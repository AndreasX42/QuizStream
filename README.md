# QuizStream

# QuizStream: Create and solve quizzes of YouTube videos

**QuizStream** is a dynamic application that generates multiple-choice quizzes from YouTube video transcripts. With a streamlined Angular frontend, a robust Spring Boot API backend, and a specialized Python service for transcript processing, QuizStream enables users to create engaging quizzes with minimal effort.

## 📖 Stack

`Frontend` [Angular 18](https://angular.dev/)\
`Backend` [Java 21](https://openjdk.org/) [Python](https://www.python.org/)\
`LLM Frameworks` [LangChain](https://www.langchain.com/) [OpenAI](https://www.openai.com/) \
`API Frameworks` [Spring Boot 3](https://spring.io/projects/spring-boot) [FastAPI](https://fastapi.tiangolo.com/)\
`DBs` [PostgreSQL](https://www.postgresql.org/) [PGVector](https://github.com/pgvector/pgvector)\
`CI/CD` [Docker](https://www.docker.com/) [Kubernetes](https://kubernetes.io/) [CircleCI](https://circleci.com/) [GKE](https://cloud.google.com/kubernetes-engine)

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

## 🚀 Getting Started

- TODO: Once all services are set up, a link to the Kubernetes cluster will be provided here

## 🛠️ Development

Directory Structure

- `/app` Angular frontend.
- `/api` Spring Boot API.
- `/backend` Python backend for quiz handling using LangChain
- `/postgres` Database storing user information and embeddings
