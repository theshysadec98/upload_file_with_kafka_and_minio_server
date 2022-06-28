CREATE TABLE upload_command(
    id  bigserial not null
        constraint upload_command_pk,
    bucket VARCHAR(255),
    source_url TEXT,
    file_name VARCHAR(255),
    thumbnail TEXT;
)