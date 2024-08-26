from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain.docstore.document import Document
from langchain.chains.qa_generation.base import QAGenerationChain
from langchain_core.prompts import PromptTemplate
from langchain.chains.llm import LLMChain
from langchain_openai import ChatOpenAI
from langchain_community.document_loaders import YoutubeLoader

from fastapi import HTTPException, status

import uuid
import asyncio
import itertools
from json import JSONDecodeError

from backend.commons.prompts import QA_GENERATION_PROMPT
from backend.commons.db import create_collection


async def agenerate_quiz(
    quiz_name: str,
    youtube_url: str,
    translation_language: str,
    api_keys: dict[str, str],
):

    # get video transcript
    loader = YoutubeLoader.from_youtube_url(
        youtube_url=youtube_url,
        language=["en", "es", "de", "fr"],
        translation=translation_language,
        add_video_info=True,
    )

    try:
        transcript = loader.load()[0]
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error fetching video transcript for {youtube_url}.",
        )

    # split transcript into chunks
    chunks, video_metadata = chunk_transcript(transcript)

    # create summary of video
    await asummarize_video(video_metadata, api_keys)

    # generate question-answer set
    qa_pairs = await agenerate_qa_from_transcript(chunks, api_keys)

    # upsert into vector database
    # TODO: Are embeddings necessary in the future?
    collection_id, qa_ids = create_collection(quiz_name, qa_pairs, video_metadata)

    return collection_id, qa_ids


async def asummarize_video(video_metadata: dict[str, str], api_keys: dict[str, str]):

    prompt_template = """Write a concise summary of the following text and in the same language:
        "{text}"
    CONCISE SUMMARY:"""

    prompt = PromptTemplate.from_template(prompt_template)

    try:
        llm_chain = LLMChain(
            llm=ChatOpenAI(
                temperature=0, model="gpt-4o-mini", api_key=api_keys["OPENAI_API_KEY"]
            ),
            prompt=prompt,
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Invalid API key provided." + str(e),
        )

    doc_to_summarize = Document(page_content=video_metadata["transcript"])

    video_metadata["description"] = llm_chain.run(doc_to_summarize)


async def agenerate_qa_from_transcript(
    chunks: list[Document], api_keys: dict[str, str]
) -> list[dict[str, str]]:

    try:
        llm = ChatOpenAI(
            temperature=0,
            model="gpt-4o-mini",
            api_key=api_keys["OPENAI_API_KEY"],
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Invalid API key provided." + str(e),
        )

    qa_generator_chain = QAGenerationChain.from_llm(
        llm=llm, prompt=QA_GENERATION_PROMPT
    )

    tasks = [get_qa_from_chunk(chunk, qa_generator_chain) for chunk in chunks]

    qa_pairs = await asyncio.gather(*tasks)
    qa_pairs = list(itertools.chain.from_iterable(qa_pairs))

    return qa_pairs


async def get_qa_from_chunk(
    chunk: Document,
    qa_generator_chain: QAGenerationChain,
) -> list[Document]:
    try:
        # return list of qa pairs
        qa_pairs = qa_generator_chain.run(chunk.page_content)

        processed_pairs = []

        # attach chunk metadata to qa_pair
        for qa_pair in qa_pairs:

            qa_pair["page_content"] = qa_pair.pop("question")
            qa_pair["metadata"] = dict(**chunk.metadata)
            qa_pair["metadata"].update(
                {
                    "answer": qa_pair.pop("answer"),
                    "id": str(uuid.uuid4()),
                    "context": chunk.page_content,
                }
            )

            qa_pair = Document(
                page_content=qa_pair["page_content"], metadata=qa_pair["metadata"]
            )

            processed_pairs.append(qa_pair)

        return processed_pairs

    except JSONDecodeError:
        return []


def chunk_transcript(transcript: Document) -> list[Document]:

    text_splitter = RecursiveCharacterTextSplitter(
        chunk_size=len(transcript.page_content)
        // 7,  # TODO: target should be 7 questions
        chunk_overlap=100,
        length_function=len,
        add_start_index=True,
    )

    doc = Document(page_content=transcript.page_content, metadata={})

    video_metadata = dict(**transcript.metadata)
    video_metadata.update({"transcript": transcript.page_content})

    chunks = text_splitter.split_documents([doc])

    # add end_index
    for chunk in chunks:
        chunk.metadata["end_index"] = chunk.metadata["start_index"] + len(
            chunk.page_content
        )

    return chunks, video_metadata
