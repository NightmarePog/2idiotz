import assert from 'node:assert/strict';
import { readFile } from 'node:fs/promises';
import test from 'node:test';
import {
  getHealth,
  getTeam,
  getStops,
  createStop,
  updateStop,
  deleteStop
} from '../src/lib/api/generated/sdk.gen';
import { ZodError } from 'zod';
import { zStop, zStopInput } from '../src/lib/api/generated/zod.gen';

const baseUrl = 'http://localhost/api/v1';

test('generated SDK validates the real Spring response and uses the public API path', async () => {
  const body = await readFile(
    new URL('../../backend/build/openapi/health.json', import.meta.url),
    'utf8'
  );
  const { data } = await getHealth({
    baseUrl,
    throwOnError: true,
    fetch: async (request) => {
      assert.ok(request instanceof Request);
      assert.equal(request.url, `${baseUrl}/health`);
      return new Response(body, { headers: { 'Content-Type': 'application/json' } });
    }
  });
  assert.deepEqual(data, { status: 'ok' });
});

for (const body of [{}, { status: 'UP' }, { status: null }, { status: 1 }, null]) {
  test(`generated SDK rejects invalid success response ${JSON.stringify(body)}`, async () => {
    await assert.rejects(
      getHealth({
        baseUrl,
        throwOnError: true,
        fetch: async () => Response.json(body)
      }),
      ZodError
    );
  });
}

test('generated SDK rejects an HTTP failure even when the body looks healthy', async () => {
  await assert.rejects(
    getHealth({
      baseUrl,
      throwOnError: true,
      fetch: async () => Response.json({ status: 'ok' }, { status: 503 })
    })
  );
});

test('generated SDK validates team data read from PostgreSQL', async () => {
  const body = await readFile(
    new URL('../../backend/build/openapi/team.json', import.meta.url),
    'utf8'
  );
  const { data } = await getTeam({
    baseUrl,
    throwOnError: true,
    fetch: async (request) => {
      assert.equal((request as Request).url, `${baseUrl}/team`);
      return new Response(body, { headers: { 'Content-Type': 'application/json' } });
    }
  });
  assert.deepEqual(data, JSON.parse(body));
  assert.ok(data.members.length > 0);
});

test('generated SDK validates stops read from PostgreSQL', async () => {
  const body = await readFile(
    new URL('../../backend/build/openapi/stops.json', import.meta.url),
    'utf8'
  );
  const { data } = await getStops({
    baseUrl,
    throwOnError: true,
    fetch: async (request) => {
      assert.equal((request as Request).url, `${baseUrl}/stops`);
      return new Response(body, { headers: { 'Content-Type': 'application/json' } });
    }
  });
  assert.deepEqual(data, JSON.parse(body));
});

const stopInput = {
  name: 'Cejl',
  wheelchair_accessible: true,
  has_shelter: true,
  has_ticket_machine: false
};
const stopResponse = { ...stopInput, id: 12, image_url: null };

test('station responses require every field and preserve safe numeric IDs', () => {
  assert.equal(
    zStop.parse({ ...stopResponse, id: Number.MAX_SAFE_INTEGER }).id,
    Number.MAX_SAFE_INTEGER
  );
  for (const id of [Number.MAX_SAFE_INTEGER + 1, 1.5, '12', null]) {
    assert.equal(zStop.safeParse({ ...stopResponse, id }).success, false);
  }
  for (const field of Object.keys(stopResponse)) {
    assert.equal(zStop.safeParse({ ...stopResponse, [field]: undefined }).success, false);
  }
});

test('generated SDK supports create, update and empty delete responses', async () => {
  const created = await createStop({
    baseUrl,
    body: stopInput,
    throwOnError: true,
    fetch: async (request) => {
      const req = request as Request;
      assert.equal(req.method, 'POST');
      assert.equal(req.url, `${baseUrl}/stops`);
      assert.deepEqual(await req.json(), stopInput);
      return Response.json(stopResponse, { status: 201 });
    }
  });
  assert.deepEqual(created.data, stopResponse);
  const updated = await updateStop({
    baseUrl,
    path: { id: 12 },
    body: { ...stopInput, image_url: null },
    throwOnError: true,
    fetch: async (request) => {
      const req = request as Request;
      assert.equal(req.method, 'PUT');
      assert.equal(req.url, `${baseUrl}/stops/12`);
      return Response.json(stopResponse);
    }
  });
  assert.deepEqual(updated.data, stopResponse);
  await deleteStop({
    baseUrl,
    path: { id: 12 },
    throwOnError: true,
    fetch: async (request) => {
      assert.equal((request as Request).method, 'DELETE');
      assert.equal((request as Request).url, `${baseUrl}/stops/12`);
      return new Response(null, { status: 204 });
    }
  });
});

for (const invalid of [
  { ...stopResponse, image_url: undefined },
  { ...stopResponse, wheelchair_accessible: null },
  { ...stopResponse, has_shelter: 'true' }
]) {
  test(`generated SDK rejects invalid stop ${JSON.stringify(invalid)}`, async () => {
    await assert.rejects(
      getStops({
        baseUrl,
        throwOnError: true,
        fetch: async () => Response.json([invalid])
      }),
      ZodError
    );
  });
}

test('station input accepts nullable image strings without a URL pattern', () => {
  for (const image_url of [
    null,
    '',
    'images/station photo.webp',
    '/uploads/cejl.svg',
    'not a URL'
  ]) {
    assert.deepEqual(zStopInput.parse({ ...stopInput, image_url }), { ...stopInput, image_url });
  }
});
