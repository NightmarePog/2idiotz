CREATE TABLE team (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL CHECK (length(trim(name)) > 0)
);

CREATE TABLE team_member (
    team_id BIGINT NOT NULL REFERENCES team(id) ON DELETE CASCADE,
    position INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL CHECK (length(trim(name)) > 0),
    PRIMARY KEY (team_id, position)
);
