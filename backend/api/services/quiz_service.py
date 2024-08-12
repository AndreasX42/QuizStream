from backend.api.schemas import QuizCreateRequest, QuizDeleteRequest, QuizDTO
from sqlalchemy.orm import Session
from sqlalchemy import select, delete
from backend.quiz_generation import agenerate_quiz
from backend.commons.db import get_collection_metadata
from backend.api.models import UserToQuiz, LangchainPGCollection
from fastapi import HTTPException, status


async def create_quiz(
    user_id: int, quiz_data: QuizCreateRequest, session: Session
) -> QuizDTO:

    # check quiz name is unique
    assert_quiz_name_not_exists(user_id, quiz_data.quiz_name, session)

    quiz_data_dict = quiz_data.model_dump()

    # transform link to string
    quiz_data_dict["youtube_url"] = str(quiz_data_dict["youtube_url"])

    collection_id, qa_ids = await agenerate_quiz(**quiz_data_dict)

    # insert the mapping into the users_quizzes table
    new_mapping = UserToQuiz(
        user_id=user_id,
        quiz_name=quiz_data.quiz_name,
        collection_id=collection_id,
    )

    session.add(new_mapping)
    session.commit()

    return get_collection_metadata(collection_id)


async def get_quizzes_by_user_id(user_id: int, session: Session) -> list[QuizDTO]:

    collection_ids = (
        session.execute(
            select(UserToQuiz.collection_id).where(UserToQuiz.user_id == user_id)
        )
        .scalars()
        .all()
    )

    if not collection_ids:
        return []

    quiz_metadata = (
        session.execute(
            select(LangchainPGCollection.cmetadata).where(
                LangchainPGCollection.uuid.in_(collection_ids)
            )
        )
        .scalars()
        .all()
    )

    return [
        QuizDTO.model_validate(quiz, from_attributes=True) for quiz in quiz_metadata
    ]


async def delete_quiz(user_id: int, data: QuizDeleteRequest, session: Session) -> None:

    result = session.execute(
        select(UserToQuiz).where(
            UserToQuiz.user_id == user_id, UserToQuiz.quiz_name == data.quiz_name
        )
    )

    user_quiz_mapping = result.scalars().first()

    if not user_quiz_mapping:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Quiz {data.quiz_name} not found for user {user_id}.",
        )

    session.execute(
        delete(UserToQuiz).where(
            UserToQuiz.user_id == user_id, UserToQuiz.quiz_name == data.quiz_name
        )
    )

    session.execute(
        delete(LangchainPGCollection).where(
            LangchainPGCollection.uuid == user_quiz_mapping.collection_id
        )
    )

    session.commit()


def assert_quiz_name_not_exists(user_id: int, quiz_name: str, session: Session):
    """Check on uniqueness of quiz name"""
    existing_quiz = (
        session.execute(
            select(UserToQuiz).where(
                UserToQuiz.user_id == user_id,
                UserToQuiz.quiz_name == quiz_name,
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
