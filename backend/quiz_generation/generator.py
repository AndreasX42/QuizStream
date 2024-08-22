from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain.docstore.document import Document
from langchain.chains.qa_generation.base import QAGenerationChain
from langchain_openai import ChatOpenAI
from langchain_community.document_loaders import YoutubeLoader

import uuid
import asyncio
import itertools
from json import JSONDecodeError

from backend.commons.prompts import QA_GENERATION_PROMPT
from backend.commons.db import create_collection


async def agenerate_quiz(
    quiz_name: str,
    youtube_url: str,
    api_keys: dict[str, str],
):

    # get video transcript
    loader = YoutubeLoader.from_youtube_url(
        youtube_url=youtube_url, language=["en", "en-GB"], add_video_info=True
    )

    # TODO: Can result be a list of more than one element?
    transcript = loader.load()[0]

    # split transcript into chunks
    chunks, video_metadata = chunk_transcript(transcript)

    # generate question-answer set
    qa_pairs = await agenerate_qa_from_transcript(chunks, api_keys)

    # upsert into vector database
    # TODO: Are embeddings necessary in the future?
    collection_id, qa_ids = create_collection(quiz_name, qa_pairs, video_metadata)

    return collection_id, qa_ids


async def agenerate_qa_from_transcript(
    chunks: list[Document], api_keys: dict[str, str]
) -> list[dict[str, str]]:

    llm = ChatOpenAI(
        temperature=0,
        model="gpt-4o-mini",
        api_key=api_keys["OPENAI_API_KEY"],
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
