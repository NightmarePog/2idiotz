#!/usr/bin/env python3
"""Exercise the production images through Caddy, and the health endpoint."""
import json
import re
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
assert status == 200 and 'Think diffrent Academy' in html
assert request('/status')[0] == 404
assets = re.findall(r'(?:href|src)="([^" ]*\/_app/immutable/[^" ]+)"', html)
assert assets, 'No built frontend assets found'
for asset in assets:
    path = '/' + asset.lstrip('./')
    assert request(path)[0] == 200, f'Failed asset: {path}'
assert request('/api/does-not-exist')[0] == 404

print('Passed: homepage, assets, and health API routing.')
