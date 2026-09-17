INSERT INTO station (id, name, image_url, is_transfer, x, y,
    wheelchair_accessible, has_shelter, has_bench, has_ticket_machine, has_display)
SELECT id, name, image_url, is_transfer, x, y,
    wheelchair_accessible, has_shelter, has_bench, has_ticket_machine, has_display
FROM seed_station
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('station', 'id'),
    GREATEST((SELECT MAX(id) FROM station), (SELECT last_value FROM station_id_seq)));
