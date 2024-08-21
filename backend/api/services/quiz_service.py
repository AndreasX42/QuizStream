from backend.api.schemas import QuizCreateRequestDto, QuizOutboundDto
from sqlalchemy.orm import Session
from sqlalchemy import select
from backend.quiz_generation import agenerate_quiz
from backend.commons.db import get_collection_metadata
from backend.api.models import UserToQuiz, LangchainPGCollection
from fastapi import HTTPException, status


async def create_quiz(
    quiz_data: QuizCreateRequestDto, session: Session
) -> QuizOutboundDto:

    # check quiz name is unique
    assert_quiz_name_not_exists(quiz_data.user_id, quiz_data.quiz_name, session)

    quiz_data_dict = quiz_data.model_dump()

    # transform link to string
    quiz_data_dict["youtube_url"] = str(quiz_data_dict["youtube_url"])

    collection_id, qa_ids = await agenerate_quiz(**quiz_data_dict)

    # insert the mapping into the users_quizzes table
    user_to_quiz = UserToQuiz(
        user_id=quiz_data.user_id,
        quiz_id=collection_id,
    )

    session.add(user_to_quiz)
    session.commit()

    # prepare result dto
    collection_metadata = get_collection_metadata(collection_id)
    quizResultDto = QuizOutboundDto(
        user_id=quiz_data.user_id,
        quiz_id=collection_id,
        quiz_name=quiz_data.quiz_name,
        **collection_metadata,
    )

    return quizResultDto


def assert_quiz_name_not_exists(user_id: int, quiz_name: str, session: Session):
    """Check on uniqueness of quiz name"""

    quizzes_same_name = (
        session.execute(
            select(LangchainPGCollection.uuid).where(
                LangchainPGCollection.name == quiz_name
            )
        )
        .scalars()
        .all()
    )

    existing_quiz = (
        session.execute(
            select(UserToQuiz).where(
                UserToQuiz.quiz_id.in_(quizzes_same_name),
                UserToQuiz.user_id == user_id,
            )
        )
        .scalars()
        .first()
    )

    if existing_quiz:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Quiz name '{quiz_name}' already exists for user {user_id}.",
        )
