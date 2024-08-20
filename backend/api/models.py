from sqlalchemy import (
    Column,
    String,
    Integer,
    BigInteger,
    DateTime,
    Boolean,
    JSON,
    ForeignKey,
    Enum,
)
import datetime as dt

from sqlalchemy.dialects.postgresql import UUID
from pgvector.sqlalchemy import Vector
from sqlalchemy.schema import Index, UniqueConstraint
from sqlalchemy.dialects.postgresql import JSONB

from backend.commons.db import Base

from enum import Enum as PythonEnum


class Role(str, PythonEnum):
    USER = "USER"
    ADMIN = "ADMIN"


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
    id = Column(BigInteger, primary_key=True, index=True)
    username = Column(String, index=False, unique=True, nullable=False)
    password = Column(String, index=False, unique=False, nullable=False)
    email = Column(String, index=False, unique=True, nullable=False)
    date_created = Column(DateTime, default=dt.datetime.now(dt.UTC))
    is_active = Column(Boolean, default=True)
    role = Column(Enum(Role), default=Role.USER)


class UserToQuiz(Base):
    __tablename__ = "users_quizzes"
    user_id = Column(
        BigInteger, ForeignKey("users.id", ondelete="CASCADE"), primary_key=True
    )
    quiz_name = Column(
        String, ForeignKey("langchain_pg_collection.name"), primary_key=True
    )
    collection_id = Column(
        UUID(as_uuid=True),
        ForeignKey("langchain_pg_collection.uuid", ondelete="CASCADE"),
    )
    num_tries = Column(Integer, default=0)
    num_correct_answers = Column(Integer, default=0)
