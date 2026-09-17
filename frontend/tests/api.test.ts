import assert from 'node:assert/strict';
import { readFile } from 'node:fs/promises';
import test from 'node:test';
import { getHealth } from '../src/lib/api/generated/sdk.gen';
import { ZodError } from 'zod';

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
