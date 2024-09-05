FROM postgres:16.4-alpine

RUN apk add --no-cache \
    postgresql-dev \
    build-base \
    git \
    clang15 \
    llvm15-dev

ENV CC=clang-15
ENV CXX=clang++-15

RUN git clone https://github.com/pgvector/pgvector.git /tmp/pgvector \
    && cd /tmp/pgvector \
    && make && make install \
    && rm -rf /tmp/pgvector

COPY docker-entrypoint-initdb.d/* /docker-entrypoint-initdb.d/

RUN chmod +x docker-entrypoint-initdb.d/*

CMD ["postgres"]
