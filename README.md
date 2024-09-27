# QuizStream

[![CircleCI](https://dl.circleci.com/status-badge/img/circleci/6FfqBzs4fBDyTPvBNqnq5x/WCAab585ez56Du7MgmTwE1/tree/master.svg?style=shield)](https://dl.circleci.com/status-badge/redirect/circleci/6FfqBzs4fBDyTPvBNqnq5x/WCAab585ez56Du7MgmTwE1/tree/master)

# QuizStream: Create and solve quizzes of YouTube videos

**QuizStream** is aimed at making learning more engaging by turning YouTube videos into interactive quizzes. Whether you're a teacher, a content creator or just someone who loves to learn, QuizStream simplifies the process of creating quizzes based on video content. All you need is a YouTube video that you'd like to turn into a quiz. The application does the heavy lifting for you, automatically generating questions from the video's content. You can customize the quiz additionally by setting the difficulty level and by choosing the language.

## 📖 Stack

`Frontend` [Angular 18](https://angular.dev/)\
`Backend` [Java 21](https://openjdk.org/) [Python](https://www.python.org/)\
`LLM Frameworks` [LangChain](https://www.langchain.com/) [OpenAI](https://www.openai.com/) \
`API Frameworks` [Spring Boot 3](https://spring.io/projects/spring-boot) [FastAPI](https://fastapi.tiangolo.com/)\
`DBs` [PostgreSQL](https://www.postgresql.org/) [PGVector](https://github.com/pgvector/pgvector)\
`CI/CD` [Docker](https://www.docker.com/) [Kubernetes](https://kubernetes.io/) [CircleCI](https://circleci.com/) [GKE](https://cloud.google.com/kubernetes-engine)

## 🚀 Getting Started

- Google Kubernetes Engine cluster available at
  - https://quizstream.app
- CircleCI pushes the Docker images after each successful build to
  - https://hub.docker.com/u/andreasx42
- Checkout repository
  - Start application with ‘docker-compose -f docker-compose_dev_all.yaml up --build’
    - Frontend should be available on localhost:4200.
  - Use Kubernetes with 'kubectl apply -f ./k8s' to deploy locally
    - Frontend should be available directly on localhost/
    - For backend API access we use nginx routing with localhost/api/v1/\*
    - Be aware to check deployment configs for Docker image versions

## 🌐 Architecture

QuizStream is built using a microservices architecture, with the following components:

![image](https://github.com/user-attachments/assets/b0ab92db-9f66-4dff-8143-7bafe7115499)

## 🌟 Features

- **Easy-to-Use Interface**: Our sleek Angular-based frontend makes navigating and using QuizStream a breeze.
- **Powerful Backend**: We use Spring Boot to handle all the data and requests behind the scenes.
- **Smart AI Processing**: Our Python-based backend converts video content into quiz questions effortlessly.
- **Reliable Data Storage**: Your quizzes and user information are safely stored in a PostgreSQL database.

## 🛠️ Development

Directory Structure

- `/app` Angular frontend
- `/api` Spring Boot API
- `/backend` Python backend
- `/postgres` Database for user and quiz data
