from fastapi import APIRouter, Depends
from starlette import status

from backend.api.services import quiz_service
from backend.api.schemas import QuizCreateRequest, QuizDTO
from backend.commons.db import get_db

router = APIRouter(
    tags=["Quiz endpoints"],
)


@router.post(
    "/quizzes",
    response_model=QuizDTO,
    status_code=status.HTTP_201_CREATED,
)
async def create_quiz(
    quiz_data: QuizCreateRequest,
):
    return await quiz_service.create_quiz(quiz_data)
