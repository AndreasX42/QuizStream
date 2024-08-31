# QuizStream

[![CircleCI](https://dl.circleci.com/status-badge/img/circleci/6FfqBzs4fBDyTPvBNqnq5x/8HU8omXUEUaEgrpWMj271K/tree/main.svg?style=shield&circle-token=545d0058e25f4566f54a9282ef976f6a8a77b327)](https://app.circleci.com/pipelines/circleci/6FfqBzs4fBDyTPvBNqnq5x/WCAab585ez56Du7MgmTwE1)

# QuizStream: Create and solve quizzes of YouTube videos

**QuizStream** is a dynamic application that generates multiple-choice quizzes from YouTube video transcripts. With a streamlined Angular frontend, a robust Spring Boot API backend, and a specialized Python service for transcript processing, QuizStream enables users to create engaging quizzes with minimal effort.

## 📖 Stack

`Frontend` [Angular 18](https://angular.dev/)\
`Backend` [Java 21](https://openjdk.org/) [Python](https://www.python.org/)\
`LLM Frameworks` [LangChain](https://www.langchain.com/) [OpenAI](https://www.openai.com/) \
`API Frameworks` [Spring Boot 3](https://spring.io/projects/spring-boot) [FastAPI](https://fastapi.tiangolo.com/)\
`DBs` [PostgreSQL](https://www.postgresql.org/) [PGVector](https://github.com/pgvector/pgvector)\
`CI/CD` [Docker](https://www.docker.com/) [Kubernetes](https://kubernetes.io/) [CircleCI](https://circleci.com/) [GKE](https://cloud.google.com/kubernetes-engine)

## 🚀 Getting Started

- CircleCI pushes the Docker images after each successful build to
  - https://hub.docker.com/u/andreasx42
- Google Kubernetes Engine cluster could be available on
  - https://quizstream.app
- Checkout repository
  - Start application with ‘docker-compose -f docker-compose_all.yaml up --build’
    - Application should be available on localhost:4200.
    - Backend API documentation is available on localhost:9090/swagger-ui/index.html
  - Use Kubernetes with 'kubectl apply -f ./k8s' to deploy locally
    - Application should be available directly on localhost/
    - For backend API access we use nginx routing with localhost/api/v1/\*
    - Be aware to check deployment configs for image versions

## 🌟 Features

- **Automatic Quiz Generation**: Generate multiple-choice quizzes based on YouTube video links.
- **Support for Various Question Types**: Create quizzes with various question formats, including multiple choice.
- **User-friendly Interface**: Intuitive and easy-to-use Angular frontend.
- **Scalable Backend**: Spring Boot API to manage quiz generation, storage, and retrieval.
- **Advanced Transcript Processing**: Python service for processing YouTube transcripts and converting them into quiz questions.

## 🌐 Architecture

QuizStream is built using a microservices architecture, with the following components:

1. **Frontend**:

   - **Angular** application for user interaction and quiz creation.
   - Communicates with the backend via RESTful APIs.

2. **Backend API**:

   - **Spring Boot** application that handles user requests, manages data, and communicates with the Python service.
   - Provides RESTful endpoints for quiz generation and management.

3. **Python Service**:

   - Specialized backend service for processing YouTube video transcripts.
   - Utilizes **LangChain** to generate high-quality, multiple-choice quiz questions.

4. **Database**:
   - Persistent storage for quizzes, user data, and other relevant information.
   - Uses **PGVector** extension to handle embeddings and quiz data.

## 🛠️ Development

Directory Structure

- `/app` Angular frontend.
- `/api` Spring Boot API.
- `/backend` Python backend for quiz handling using LangChain
- `/postgres` Database storing user information and embeddings
