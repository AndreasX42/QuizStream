FROM python:3.11

# Port the app is running on
EXPOSE 8080

# Install dependencies
WORKDIR /

COPY ./requirements.txt ./
RUN pip install --no-cache-dir --upgrade -r ./requirements.txt

# Copy all into image
COPY ./ ./

RUN chmod +x wait-for-it.sh

ENTRYPOINT ["/bin/bash", "-c", "./wait-for-it.sh postgres:5432 --timeout=60 -- uvicorn backend.api:app --host 0.0.0.0 --port 8080 --reload"]