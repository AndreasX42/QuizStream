from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from starlette import status

from backend.api.services import quiz_service
from backend.api.schemas import QuizCreateRequest, QuizDTO
from backend.commons.db import get_db

router = APIRouter(
    prefix="/users",
    tags=["User endpoints"],
)


@router.post(
    "/{user_id}/quizzes",
    response_model=QuizDTO,
    status_code=status.HTTP_201_CREATED,
)
async def create_quiz(
    user_id: int, quiz_data: QuizCreateRequest, session: Session = Depends(get_db)
):
    return await quiz_service.create_quiz(user_id, quiz_data, session)


@router.get(
    "/{user_id}/quizzes",
    response_model=list[QuizDTO],
    status_code=status.HTTP_200_OK,
)
async def get_quizzes_by_user_id(user_id: int, session: Session = Depends(get_db)):
    return await quiz_service.get_quizzes_by_user_id(user_id, session)
