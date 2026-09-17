ALTER TABLE team_member RENAME TO team_members;
ALTER TABLE team_members RENAME COLUMN name TO members;
ALTER TABLE team_members RENAME COLUMN position TO members_order;
