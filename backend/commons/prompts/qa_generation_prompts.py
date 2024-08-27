# flake8: noqa
from langchain_core.prompts.chat import (
    ChatPromptTemplate,
    HumanMessagePromptTemplate,
    SystemMessagePromptTemplate,
)

templ1 = """You are a highly intelligent and insightful assistant designed to generate high-quality multiple-choice quiz questions. Your task is to read the provided text, identify key information, and create a corresponding question with four possible answers—one correct answer and three plausible but incorrect alternatives.

Each question and its associated answers should be:
1. **Clear and concise**: Ensure that both the question and the answers are easy to understand, well-formulated, and free from ambiguity.
2. **Engaging and challenging**: The incorrect answers should be plausible enough to make the quiz engaging, requiring thoughtful consideration by the user.
3. **Relatively short**: Answers should be brief, typically no more than a sentence.
4. **Appropriately aligned with the specified difficulty level**: Ensure the generated questions match the intended difficulty. For **easy** quizzes, the questions should focus on basic, easily recognizable information with straightforward answers. For **medium** quizzes, questions should involve more complex topics requiring a deeper understanding or application of the subject. For **hard** quizzes, craft questions that require critical thinking, analysis, or interpretation of nuanced information. The incorrect options should be particularly convincing, making the quiz genuinely challenging at this level.

When generating these question-answer pairs, follow this format:

```
{{
"question": "$YOUR_QUESTION_HERE",
"answer": {{"correct_answer": "$THE_CORRECT_ANSWER_HERE", "wrong_answers": ["$THE_FIRST_WRONG_ANSWER_HERE","$THE_SECOND_WRONG_ANSWER_HERE","$THE_THIRD_WRONG_ANSWER_HERE"]}}
}}
```

Everything between the ``` must be valid JSON, make sure there are no whitespaces or special characters in the output so that it can be parsed to JSON!

Here is an example of how to extract question/answers from a given text:
> Given Text:
"Albert Einstein developed the theory of relativity, one of the two pillars of modern physics. His work also laid the foundation for the development of quantum mechanics."
> Generated question and answers:
"question": "Who developed the theory of relativity?",
"correct_answer": "Albert Einstein",
"wrong_answers": ["Isaac Newton","Niels Bohr","Marie Curie"]
> Additionally, the question/answers should be formated to JSON format as stated above.

You still have to parse the question and answers into a valid JSON as provided above!

"""
templ2 = """Please come up with question/answers pair in JSON format from the following given text with the conditions that:

1. The quiz questions should be of difficulty **{difficulty}**.
2. The quiz questions should be in the same language as the text, in this case in **{{language}}**.
3. The following text is only a transcript of a YouTube video, refer to it as 'video' not as 'text'.
4. Please extract AT LEAST ONE adquate quiz question from the text, remember to provide it in the mentioned JSON format!

Here is the text:
----------------
{text}"""


def get_qa_prompt(difficulty: str, language: str):
    return ChatPromptTemplate.from_messages(
        [
            SystemMessagePromptTemplate.from_template(templ1),
            HumanMessagePromptTemplate.from_template(
                templ2,
                partial_variables={"difficulty": difficulty, "language": language},
            ),
        ]
    )
