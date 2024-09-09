from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from backend.api.services import quiz_service
from backend.api.schemas import QuizCreateRequestDto, QuizCreateResultDto
from backend.commons.db import get_db

import logging

logger = logging.getLogger(__name__)

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

    try:
        return await quiz_service.create_quiz(quiz_data, session)

    except HTTPException as http_exc:
        # re-raise HTTPException
        raise http_exc

    except Exception as e:
        logger.error("Unexpected error occurred: %s", str(e))

        raise HTTPException(
            status_code=500,
            detail={
                "error_external": f"{type(e).__name__}: An unexpected error occurred.",
                "error_internal": f"{type(e).__name__}: {str(e)}.",
            },
        ) from e
