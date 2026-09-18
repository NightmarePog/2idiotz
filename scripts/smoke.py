#!/usr/bin/env python3
"""Exercise the production images through Caddy, and the health endpoint."""
import json
import re
import time
import urllib.error
import urllib.request
from urllib.parse import urlsplit

BASE = "http://localhost:8080"


def request(path):
    try:
        with urllib.request.urlopen(BASE + path, timeout=10) as response:
            return response.status, response.read().decode()
    except urllib.error.HTTPError as error:
        return error.code, error.read().decode()


def wait_health(expected, timeout=120):
    deadline = time.monotonic() + timeout
    while time.monotonic() < deadline:
        try:
            status, body = request('/api/v1/health')
            data = json.loads(body)
            if status == expected and data == {'status': 'ok'}:
                return
        except (OSError, ValueError, KeyError):
            pass
        time.sleep(2)
    raise AssertionError(f'Health did not become {expected} within {timeout}s')


wait_health(200)
status, html = request('/')
assert status == 200, f'Homepage returned HTTP {status}'
assert 'Think different Academy' in html, 'Homepage is missing the academy name'
assert 'Think diffrent Academy' in html, 'Homepage is missing the legacy compatibility name'
assert request('/status')[0] == 404
team_status, team_body = request('/api/v1/team')
team = json.loads(team_body)
assert team_status == 200 and team['name'] and team['members']
assert all(isinstance(name, str) and name.strip() for name in team['members'])
stops_status, stops_body = request('/api/v1/stops')
assert stops_status == 200
stops = json.loads(stops_body)
assert stops, 'No seeded stops found'
for stop in stops:
    image_url = stop['image_url']
    assert image_url, f'Missing seeded image: {stop["name"]}'
    image_origin = urlsplit(image_url)
    assert (image_origin.scheme, image_origin.netloc) == ('http', 'localhost:8080'), image_url
    with urllib.request.urlopen(image_url, timeout=10) as response:
        assert response.status == 200, image_url
        assert response.headers.get_content_type() == 'image/png', image_url
        assert response.read(8) == b'\x89PNG\r\n\x1a\n', image_url
assets = re.findall(r'(?:href|src)="([^" ]*\/_app/immutable/[^" ]+)"', html)
assert assets, 'No built frontend assets found'
for asset in assets:
    path = '/' + asset.lstrip('./')
    assert request(path)[0] == 200, f'Failed asset: {path}'
assert request('/api/does-not-exist')[0] == 404

print('Passed: homepage, assets, stop images, and health API routing.')
