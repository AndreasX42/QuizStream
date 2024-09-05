from langchain_core.prompts import PromptTemplate
from langchain.docstore.document import Document

template = """You are a teacher grading a quiz.
You are given a quiz question, the true answer, three incorrect answers, the context from which the question was taken, and a short summary of the topic for which the quiz is intended. Your task is to grade the quiz question based on the following criteria:

1. **Clear and concise**: The question and answers should be easy to understand, well-formulated, and free from ambiguity.
2. **Engaging and challenging**: Incorrect answers should be plausible, making the quiz engaging and requiring thoughtful consideration.
3. **Relatively short**: Answers should be brief, typically no more than a sentence.
4. **Accurately aligned with the specified difficulty level**: The question should match the intended difficulty level. For **easy** quizzes, questions should focus on basic, easily recognizable information. For **medium** quizzes, questions should involve more complex topics requiring a deeper understanding. For **hard** quizzes, questions should require critical thinking, analysis, or interpretation of nuanced information, with particularly convincing incorrect options.
5. *Relevant to the topic**: The quiz question should be relevant to the provided topic, ensuring that it refers to the overall theme of the video. If the question does not refer to the topic as described in the provided video summary, the grade should be 0.00.
6. **Quiz question in correct language**: It is highly important that the quiz question is in the correct language, in this case the quiz language is **{language}** ('EN'=English, 'ES'=Spanish, 'DE'=German). If the question is not in the specified language, give it a grade of 0.00.

GRADE: (range from 0.00 - 4.00) - A grade of '0' means the question does not fulfill any criteria and may not reflect the provided context well. A grade of '4' means the question fully meets all criteria. The more criteria met, the higher the grade should be, up to a maximum of 4. The grade should be a floating point number with two decimal places.

Ignore issues in punctuation. Only provide the grade, no reasoning is required.

\n**QUESTION**: {question}
\n**CORRECT ANSWER**: {correct_answer}
\n**INCORRECT ANSWERS**: {incorrect_answers}
\n**DIFFICULTY**: {difficulty}
\n**CONTEXT**: {context}
\n**TOPIC SUMMARY**: {summary}
\n>>GRADE<<:"""

RELEVANCY_FILTER_PROMPT = PromptTemplate(
    input_variables=[
        "question",
        "correct_answer",
        "incorrect_answers",
        "difficulty",
        "context",
        "summary",
        "language",
    ],
    template=template,
)


def get_qa_relevancy_filter_prompt_input_dict(
    quiz_question: Document, summary: str, difficulty: str, language: str
) -> dict[str, str]:

    values = {
        "question": quiz_question.page_content,
        "correct_answer": quiz_question.metadata["answers"]["correct_answer"],
        "incorrect_answers": " ### ".join(
            quiz_question.metadata["answers"]["wrong_answers"]
        ),
        "context": quiz_question.metadata["context"],
        "summary": summary,
        "difficulty": difficulty,
        "language": language,
    }

    return values
