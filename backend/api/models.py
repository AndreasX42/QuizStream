from sqlalchemy import (
    Column,
    String,
    Integer,
    DateTime,
    Boolean,
    JSON,
    ForeignKey,
)
import datetime as dt

from sqlalchemy.dialects.postgresql import UUID
from pgvector.sqlalchemy import Vector
from sqlalchemy.schema import Index, UniqueConstraint
from sqlalchemy.dialects.postgresql import JSONB

from backend.commons.db import Base


class LangchainPGCollection(Base):
    __tablename__ = "langchain_pg_collection"

    uuid = Column(UUID(as_uuid=True), primary_key=True, nullable=False)
    name = Column(String, nullable=False, unique=True)
    cmetadata = Column(JSON, nullable=True)

    __table_args__ = (
        UniqueConstraint("name", name="langchain_pg_collection_name_key"),
    )


class LangchainPGEmbedding(Base):
    __tablename__ = "langchain_pg_embedding"

    id = Column(String, primary_key=True, nullable=False)
    collection_id = Column(
        UUID(as_uuid=True),
        ForeignKey("langchain_pg_collection.uuid", ondelete="CASCADE"),
    )
    embedding = Column(Vector, nullable=True)
    document = Column(String, nullable=True)
    cmetadata = Column(JSONB, nullable=True)

    __table_args__ = (
        Index("ix_langchain_pg_embedding_id", "id", unique=True),
        Index(
            "ix_cmetadata_gin",
            "cmetadata",
            postgresql_using="gin",
            postgresql_ops={"cmetadata": "jsonb_path_ops"},
        ),
    )


class User(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, index=True, unique=True)
    hashed_password = Column(String, index=False, unique=False)
    email = Column(String, index=True, unique=True)
    date_created = Column(DateTime, default=dt.datetime.now(dt.UTC))
    is_active = Column(Boolean, default=True)
    role = Column(String, default="user", index=True)


class UserToQuiz(Base):
    __tablename__ = "users_quizzes"
    user_id = Column(
        Integer, ForeignKey("users.id", ondelete="CASCADE"), primary_key=True
    )
    quiz_name = Column(
        String, ForeignKey("langchain_pg_collection.name"), primary_key=True
    )
    collection_id = Column(
        UUID(as_uuid=True),
        ForeignKey("langchain_pg_collection.uuid", ondelete="CASCADE"),
    )
