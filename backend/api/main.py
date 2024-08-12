from fastapi import FastAPI

from backend.api.routers import quiz_router

app = FastAPI(
    title="QuizManager Documentation",
    version="0.01",
    description="""API for the backend component managing the quizzes.""",
    # root path should only be "/api" in production
    # root_path="" if os.environ.get("EXECUTION_CONTEXT") in ["DEV", "TEST"] else "/api",
)

app.include_router(quiz_router.router)
