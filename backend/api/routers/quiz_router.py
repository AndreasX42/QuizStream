from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from starlette import status
from uuid import UUID

from backend.api.services import quiz_service
from backend.api.schemas import QuizOutboundDto
from backend.commons.db import get_db

router = APIRouter(
    prefix="/quizzes",
    tags=["Quiz endpoints"],
)


@router.get(
    "/{quiz_id}",
    response_model=QuizOutboundDto,
    status_code=status.HTTP_200_OK,
)
async def get_quiz_by_id(quiz_id: UUID, session: Session = Depends(get_db)):
    return await quiz_service.get_quiz_by_id(quiz_id, session)


@router.delete(
    "/{quiz_id}",
    status_code=status.HTTP_204_NO_CONTENT,
)
async def delete_quiz_by_id(
    quiz_id: UUID,
    session: Session = Depends(get_db),
):

    await quiz_service.delete_quiz_by_id(quiz_id, session)
