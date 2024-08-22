from langchain_postgres import PGVector
from langchain_postgres.vectorstores import PGVector
from langchain_core.embeddings import FakeEmbeddings
from langchain.docstore.document import Document

from sqlalchemy import MetaData, Table, select, update
from typing import Optional

from backend.commons.db import (
    SessionLocal,
    CONNECTION_STRING,
    TABLE_COLLECTION,
    TABLE_DOCS,
)

import uuid


def create_collection(
    collection_name: str, documents: list[Document], video_metadata: dict[str, str]
) -> [uuid, [str]]:
    """Creates a new collection with the provided name, documents and video metadata"""

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
    """Deletes collection with this name"""
    col = PGVector(
        collection_name=name,
        connection=CONNECTION_STRING,
        embeddings=FakeEmbeddings(size=1),  # TODO: are embeddings needed?
    )

    col.delete_collection()


# TODO: Is there a better way to fetch the collection id after creating a new collection?
def get_collection_id_by_name(collection_name: str) -> str:
    """Fetch the collection id for the given name"""

    with SessionLocal() as session:
        table = Table(TABLE_COLLECTION, MetaData(), autoload_with=session.bind)
        query = select(table.c.uuid).where(table.c.name == collection_name)
        result = session.execute(query).fetchone()

    return result[0] if result else None


def get_collection_metadata(collection_id: str) -> Optional[dict]:
    """Fetch the collection metadata for the given id"""

    with SessionLocal() as session:
        table = Table(TABLE_COLLECTION, MetaData(), autoload_with=session.bind)
        query = select(table.c.cmetadata).where(table.c.uuid == collection_id)
        result = session.execute(query).fetchone()

    return result[0] if result else None


def update_collection_metadata(
    collection_id: str, new_metadata: dict
) -> Optional[dict]:
    """Updates the metadata of the collection"""

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
    """Returns list of all collections in vector store"""

    with SessionLocal() as session:
        table = Table(TABLE_COLLECTION, MetaData(), autoload_with=session.bind)
        query = select(table.c["name"])
        results = session.execute(query).fetchall()

    return results


def get_by_ids(ids: list[str]) -> list[str]:
    """Returns all documents with provided ids"""

    with SessionLocal() as session:
        table = Table(TABLE_DOCS, MetaData(), autoload_with=session.bind)
        query = select(table).where(table.c.id.in_(ids))
        results = session.execute(query).fetchall()

    return results


def get_all_by_collection_id(collection_id: str):
    """Returns all documents of collection"""

    with SessionLocal() as session:
        table = Table(TABLE_DOCS, MetaData(), autoload_with=session.bind)
        query = select(table).where(table.c.collection_id == collection_id)
        results = session.execute(query).fetchall()

    return results
