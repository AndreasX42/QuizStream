FROM postgres:16.4-alpine

# Install required packages
RUN apt-get update && apt-get install -y \
    postgresql-server-dev-16 \
    build-essential \
    git \
    && rm -rf /var/lib/apt/lists/*

# Clone and install pgvector
RUN git clone https://github.com/pgvector/pgvector.git /tmp/pgvector \
    && cd /tmp/pgvector \
    && make && make install \
    && rm -rf /tmp/pgvector

# Set the default command to run PostgreSQL
CMD ["postgres"]
