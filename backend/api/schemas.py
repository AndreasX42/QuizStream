from pydantic import BaseModel, Field, HttpUrl, field_validator
from datetime import datetime, timezone
from typing import Any
import re


class QuizCreateRequest(BaseModel):
    quiz_name: str = Field(min_length=3, description="Name of quiz")
    user_id: str = Field(min_length=1, description="User id")
    api_keys: dict[str, str] = Field(description="Dictionary of API keys.")
    youtube_url: HttpUrl

    @field_validator("youtube_url")
    @classmethod
    def validate_youtube_url(cls, value: HttpUrl) -> HttpUrl:

        youtube_regex = (
            r"(https?://)?(www\.)?"
            r"(youtube|youtu|youtube-nocookie)\.(com|be)/"
            r"(watch\?v=|embed/|v/|.+\?v=)?([^&=%\?]{11})"
        )

        youtube_pattern = re.compile(youtube_regex)

        if not youtube_pattern.match(str(value)):
            raise ValueError("Invalid YouTube video URL")

        return value


class QuizDTO(BaseModel):
    date_created: str = Field(
        default_factory=lambda: datetime.now(timezone.utc).strftime(
            "%Y-%m-%dT%H:%M:%SZ"
        ),
        description="Date when quiz was created",
    )
    num_tries: int = Field(min=0, description="Number of attempts to solve the quiz")
    accuracy: float = Field(min=0.0, description="Rate of correctly solved questions")
    video_metadata: dict[str, Any] = Field(
        description="Additional information about video."
    )