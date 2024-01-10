CREATE TABLE IF NOT EXISTS "character_data"(
    "id" UUID NOT NULL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "experience" INT NOT NULL
);
