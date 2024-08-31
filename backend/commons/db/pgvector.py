import logging
from typing import Optional, Union, Any

from langchain_postgres.vectorstores import PGVector
from langchain_core.embeddings import FakeEmbeddings
from langchain.docstore.document import Document

from sqlalchemy import MetaData, Table, select, update

from backend.commons.db import (
    SessionLocal,
    CONNECTION_STRING,
    TABLE_COLLECTION,
    TABLE_DOCS,
)

logger = logging.getLogger(__name__)


def create_collection(
    collection_name: str, documents: list[Document], video_metadata: dict[str, str]
) -> Union[str, list[str]]:
    """Creates a new collection with the provided name and metadata and upserts the given documents

    Args:
        collection_name (str): Name of collection
        documents (list[Document]): List of documents to upsert
        video_metadata (dict[str, str]): Collection metadata

    Returns:
        Union[str, list[str]]: Id of created collection and ids of upserted documents
    """

    logger.debug(
        "Creating new collection %s and upserting provided documents.", collection_name
    )

    collection = PGVector(
        collection_name=collection_name,
        connection=CONNECTION_STRING,
        embeddings=FakeEmbeddings(size=1),
        use_jsonb=True,
        collection_metadata={
            "video_metadata": video_metadata,
        },
        pre_delete_collection=False,
    )

    # fetch the corresponding id of created collection
    collection_id = get_collection_id_by_name(collection_name)

    # add qa pairs to collection
    doc_ids = collection.add_documents(documents)

    return collection_id, doc_ids


def delete_collection(name: str) -> None:
    """Deletes collection with the given name

    Args:
        name (str): Name of collection
    """

    col = PGVector(
        collection_name=name,
        connection=CONNECTION_STRING,
        embeddings=FakeEmbeddings(size=1),
    )

    col.delete_collection()


# TODO: Is there a better way to fetch the collection id after creating a new collection?
def get_collection_id_by_name(collection_name: str) -> str:
    """Gets the id of the collection with this name

    Args:
        collection_name (str): Name of collection

    Returns:
        str: Id of collection
    """

    with SessionLocal() as session:
        table = Table(TABLE_COLLECTION, MetaData(), autoload_with=session.bind)
        query = select(table.c.uuid).where(table.c.name == collection_name)
        result = session.execute(query).fetchone()

    return result[0] if result else None


def get_collection_metadata(collection_id: str) -> Optional[dict]:
    """Gets the metadata of the collection with this id

    Args:
        collection_id (str): Id of collection

    Returns:
        Optional[dict]: The collection metadata
    """

    with SessionLocal() as session:
        table = Table(TABLE_COLLECTION, MetaData(), autoload_with=session.bind)
        query = select(table.c.cmetadata).where(table.c.uuid == collection_id)
        result = session.execute(query).fetchone()

    return result[0] if result else None


def update_collection_metadata(
    collection_id: str, new_metadata: dict
) -> Optional[dict]:
    """Updates the metadata of the collection with this id with the provided new metadata

    Args:
        collection_id (str): Id of collection
        new_metadata (dict): New metadata

    Returns:
        Optional[dict]: Updated collection metadata
    """

    with SessionLocal() as session:
        table = Table(TABLE_COLLECTION, MetaData(), autoload_with=session.bind)
        query = (
            update(table)
            .where(table.c.uuid == collection_id)
            .values(cmetadata=new_metadata)
        )

        session.execute(query)
        session.commit()

    return get_collection_metadata(collection_id)


def list_collections() -> list[str]:
    """Fetches list of names of all collections

    Returns:
        list[str]: List of all collection names
    """

    with SessionLocal() as session:
        table = Table(TABLE_COLLECTION, MetaData(), autoload_with=session.bind)
        query = select(table.c["name"])
        results = session.execute(query).fetchall()

    return results


def get_by_ids(ids: list[str]) -> list[Any]:
    """Fetches all document embeddings with ids in the provided list

    Args:
        ids (list[str]): List of ids of the documents

    Returns:
        list[Any]: List of embedded documents
    """

    with SessionLocal() as session:
        table = Table(TABLE_DOCS, MetaData(), autoload_with=session.bind)
        query = select(table).where(table.c.id.in_(ids))
        results = session.execute(query).fetchall()

    return results


def get_all_by_collection_id(collection_id: str) -> list[Any]:
    """Fetches all document embeddings of corresponding collection with this id

    Args:
        collection_id (str): The collection id

    Returns:
        list[Any]: List of embedded documents
    """

    with SessionLocal() as session:
        table = Table(TABLE_DOCS, MetaData(), autoload_with=session.bind)
        query = select(table).where(table.c.collection_id == collection_id)
        results = session.execute(query).fetchall()

    return results
