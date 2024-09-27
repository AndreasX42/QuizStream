from backend.api.schemas import QuizCreateRequestDto, QuizCreateResultDto
from sqlalchemy.orm import Session
from sqlalchemy import select
from backend.quiz_generation import agenerate_quiz
from backend.api.models import UserToQuiz, LangchainPGCollection
from fastapi import HTTPException, status

import datetime as dt
from uuid import UUID


async def create_quiz(
    quiz_data: QuizCreateRequestDto, session: Session
) -> QuizCreateResultDto:

    # check quiz name is unique
    assert_quiz_name_not_exists(quiz_data.user_id, quiz_data.quiz_name, session)

    collection_id, qa_ids = await agenerate_quiz(
        quiz_name=quiz_data.quiz_name,
        youtube_url=str(quiz_data.youtube_url),
        language=quiz_data.language,
        difficulty=quiz_data.difficulty,
        api_keys=quiz_data.api_keys,
    )

    # insert the mapping into the users_quizzes table
    user_to_quiz = UserToQuiz(
        user_id=quiz_data.user_id,
        quiz_id=collection_id,
        num_questions=len(qa_ids),
        language=quiz_data.language.name,
        type=quiz_data.type,
        difficulty=quiz_data.difficulty,
        date_created=dt.datetime.now(dt.UTC).strftime("%Y-%m-%dT%H:%M:%SZ"),
    )

    session.add(user_to_quiz)
    session.commit()

    # prepare result dto
    quizResultDto = QuizCreateResultDto(
        user_id=quiz_data.user_id, quiz_id=collection_id, quiz_name=quiz_data.quiz_name
    )

    return quizResultDto


def assert_quiz_name_not_exists(user_id: UUID, quiz_name: str, session: Session):
    """Check on uniqueness of quiz name"""

    quizzes_with_same_name = (
        session.execute(
            select(LangchainPGCollection.uuid).where(
                LangchainPGCollection.name == quiz_name
            )
        )
        .scalars()
        .all()
    )

    if not quizzes_with_same_name:
        return

    matchWithUserQuizzes = (
        session.execute(
            select(UserToQuiz).where(
                UserToQuiz.user_id == user_id,
                UserToQuiz.quiz_id.in_(quizzes_with_same_name),
            )
        )
        .scalars()
        .first()
    )

    if matchWithUserQuizzes:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={
                "error_external": f"Quiz with name '{quiz_name}' already exists.",
                "error_internal": f"Quiz name '{quiz_name}' already exists for user {user_id}.",
            },
        )
