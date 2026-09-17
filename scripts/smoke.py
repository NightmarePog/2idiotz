#!/usr/bin/env python3
"""Exercise the production images through Caddy, including database recovery."""
import json
import re
import subprocess
import time
import urllib.error
import urllib.request

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
            status, body = request('/api/health')
            data = json.loads(body)
            if status == expected and data['database'] == ('UP' if expected == 200 else 'DOWN'):
                return
        except (OSError, ValueError, KeyError):
            pass
        time.sleep(2)
    raise AssertionError(f'Health did not become {expected} within {timeout}s')


wait_health(200)
status, html = request('/')
assert status == 200 and 'Welcome to 2idiotz.' in html
assert 'Service status' in request('/status')[1]
assets = re.findall(r'(?:href|src)="([^" ]*\/_app/immutable/[^" ]+)"', html)
assert assets, 'No built frontend assets found'
for asset in assets:
    path = '/' + asset.lstrip('./')
    assert request(path)[0] == 200, f'Failed asset: {path}'
assert json.loads(request('/api/hello')[1])['message'] == 'Hello from Spring Boot!'
assert request('/api/does-not-exist')[0] == 404

subprocess.run(['docker', 'compose', 'stop', 'postgres'], check=True)
try:
    wait_health(503, timeout=60)
    assert request('/api/hello')[0] == 200, 'API must remain usable without the database'
    assert request('/')[0] == 200, 'Frontend must remain usable without the database'
finally:
    subprocess.run(['docker', 'compose', 'start', 'postgres'], check=True)
wait_health(200)
print('Passed: frontend, assets, API routing, PostgreSQL outage, and recovery.')
