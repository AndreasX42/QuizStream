from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from starlette import status

from backend.api.services import quiz_service
from backend.api.schemas import QuizCreateRequestDto, QuizCreateResultDto
from backend.commons.db import get_db

router = APIRouter(
    prefix="/quizzes",
    tags=["Quiz endpoints"],
)


@router.post(
    "/new",
    response_model=QuizCreateResultDto,
    status_code=status.HTTP_201_CREATED,
)
async def create_quiz(
    quiz_data: QuizCreateRequestDto, session: Session = Depends(get_db)
):

    return await quiz_service.create_quiz(quiz_data, session)
