import { afterEach, describe, expect, it, vi } from 'vitest';
import { z } from 'zod';
import { request } from './client';
import { getHealth } from './status';

const schema = z.object({ message: z.string() });
afterEach(() => vi.unstubAllGlobals());

describe('API client', () => {
  it('validates response data', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(Response.json({ message: 'hello' })));
    await expect(request('/api/hello', schema)).resolves.toEqual({ message: 'hello' });
  });
  it('rejects malformed responses', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(Response.json({ message: 42 })));
    await expect(request('/api/hello', schema)).rejects.toThrow('unexpected response');
  });
  it('accepts the documented unavailable health response', async () => {
    vi.stubGlobal(
      'fetch',
      vi
        .fn()
        .mockResolvedValue(Response.json({ status: 'DOWN', database: 'DOWN' }, { status: 503 }))
    );
    await expect(getHealth()).resolves.toEqual({ status: 'DOWN', database: 'DOWN' });
  });
  it('rejects proxy failures', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response('Bad gateway', { status: 502 })));
    await expect(getHealth()).rejects.toThrow('could not be reached');
  });
  it('cancels requests when the caller aborts', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn(
        (_path, { signal }) =>
          new Promise((_resolve, reject) => {
            signal.addEventListener('abort', () => reject(signal.reason), { once: true });
          })
      )
    );
    const controller = new AbortController();
    const result = request('/api/hello', schema, { signal: controller.signal });
    controller.abort();
    await expect(result).rejects.toMatchObject({ name: 'AbortError' });
  });
  it('times out a stalled service', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn(
        (_path, { signal }) =>
          new Promise((_resolve, reject) => {
            signal.addEventListener('abort', () => reject(signal.reason), { once: true });
          })
      )
    );
    await expect(request('/api/hello', schema, { timeoutMs: 10 })).rejects.toMatchObject({
      name: 'TimeoutError'
    });
  });
});
