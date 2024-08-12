from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base, sessionmaker
import os


DRIVER = os.environ.get("POSTGRES_DRIVER")
HOST = os.environ.get("POSTGRES_HOST")
PORT = os.environ.get("POSTGRES_PORT")
DB = os.environ.get("POSTGRES_DATABASE")
USER = os.environ.get("POSTGRES_USER")
PWD = os.environ.get("POSTGRES_PASSWORD")

DATABASE_URL = f"postgresql+{DRIVER}://{USER}:{PWD}@{HOST}/{DB}"

# Creating the engine
engine = create_engine(DATABASE_URL)

# Creating the session factory
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()


def get_db():
    db_session = SessionLocal()
    try:
        yield db_session
    finally:
        db_session.close()
