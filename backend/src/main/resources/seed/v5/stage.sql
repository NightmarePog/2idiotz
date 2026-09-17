CREATE TEMP TABLE seed_station (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL CHECK (length(trim(name)) > 0),
    lines TEXT,
    is_transfer BOOLEAN NOT NULL,
    transfer_lines TEXT,
    x NUMERIC NOT NULL,
    y NUMERIC NOT NULL,
    wheelchair_accessible BOOLEAN NOT NULL,
    has_shelter BOOLEAN NOT NULL,
    has_bench BOOLEAN NOT NULL,
    has_ticket_machine BOOLEAN NOT NULL,
    has_display BOOLEAN NOT NULL,
    image_url VARCHAR(255) NOT NULL
) ON COMMIT DROP;
