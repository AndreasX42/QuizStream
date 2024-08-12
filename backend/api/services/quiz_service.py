from backend.api.schemas import QuizCreateRequest, QuizDTO

from backend.quiz_generation import agenerate_quiz
from backend.commons.db import get_collection_metadata


async def create_quiz(quiz_data: QuizCreateRequest) -> QuizDTO:
    quiz_data_dict = quiz_data.model_dump()

    # transform link to string
    quiz_data_dict["youtube_url"] = str(quiz_data_dict["youtube_url"])

    collection_id, qa_ids = await agenerate_quiz(**quiz_data_dict)

    return get_collection_metadata(collection_id)
