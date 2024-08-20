from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from starlette import status
import uuid

from backend.api.services import quiz_service
from backend.api.schemas import QuizDTO
from backend.commons.db import get_db

router = APIRouter(
    tags=["Quiz endpoints"],
)


@router.get(
    "/quizzes/{quiz_id}",
    response_model=QuizDTO,
    status_code=status.HTTP_200_OK,
)
async def get_quiz_by_id(quiz_id: uuid, session: Session = Depends(get_db)):
    return await quiz_service.get_quiz_by_id(quiz_id, session)


@router.delete(
    "/quizzes/{quiz_id}",
    status_code=status.HTTP_204_NO_CONTENT,
)
async def delete_quiz_by_id(
    quiz_id: uuid,
    session: Session = Depends(get_db),
):

    await quiz_service.delete_quiz_by_id(quiz_id, session)
