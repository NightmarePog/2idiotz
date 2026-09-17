WITH inserted_team AS (
    INSERT INTO team (id, name)
    VALUES (1, 'app = build $ replicate 2 Idiot')
    ON CONFLICT (id) DO NOTHING
    RETURNING id
)
INSERT INTO team_members (team_id, members_order, members)
SELECT id, position, name
FROM inserted_team
CROSS JOIN (VALUES (0, 'Lukáš Erl'), (1, 'Libor Martínek')) AS member(position, name);
