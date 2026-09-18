import { expect, test } from '@playwright/test';
import { zStop, zTeamResponse } from '../src/lib/api/generated/zod.gen';

test('Faker dev API serves the app and assets without backend dependencies', async ({ page }) => {
  const response = await page.request.get('/api/v1/stops');
  expect(response.status()).toBe(200);
  const stops = zStop.array().parse(await response.json());
  expect(stops.length).toBeGreaterThan(0);
  zTeamResponse.parse(await (await page.request.get('/api/v1/team')).json());
  for (const stop of stops) {
    const image = await page.request.get(stop.image_url!);
    expect(image.status()).toBe(200);
    expect(image.headers()['content-type']).toBe('image/png');
  }
  await page.goto('/stops');
  await expect(
    page.getByRole('list', { name: 'Seznam zastávek' }).getByRole('listitem')
  ).toHaveCount(stops.length);
  const image = page.getByRole('img', { name: `Zastávka ${stops[0].name}`, exact: true });
  await expect
    .poll(() => image.evaluate((el: HTMLImageElement) => el.naturalWidth))
    .toBeGreaterThan(0);
  const logo = page.locator('.brand-mark img:visible');
  await expect
    .poll(() => logo.evaluate((el: HTMLImageElement) => el.naturalWidth))
    .toBeGreaterThan(0);
  expect((await page.request.get('/api/v1/assets/fonts/dosis-variable.ttf')).status()).toBe(200);
  await page.getByRole('link', { name: `Detail zastávky ${stops[0].name}`, exact: true }).click();
  await expect(page.getByRole('heading', { level: 1 })).toHaveText(stops[0].name);
});

test('dev stop mutations persist in memory and validate inputs', async ({ request }) => {
  const input = {
    name: 'Test stop',
    wheelchair_accessible: true,
    has_shelter: false,
    has_ticket_machine: true
  };
  expect((await request.post('/api/v1/stops', { data: {} })).status()).toBe(400);
  const created = await request.post('/api/v1/stops', { data: input });
  expect(created.status()).toBe(201);
  const stop = zStop.parse(await created.json());
  const path = `/api/v1/stops/${stop.id}`;
  expect((await request.get(path)).status()).toBe(200);
  const updated = await request.put(path, { data: { ...input, name: 'Renamed' } });
  expect(zStop.parse(await updated.json()).name).toBe('Renamed');
  expect(zStop.parse(await (await request.get(path)).json()).name).toBe('Renamed');
  expect((await request.delete(path)).status()).toBe(204);
  expect((await request.get(path)).status()).toBe(404);
  expect((await request.get('/api/v1/stops-images/unknown.png')).status()).toBe(404);
});

test('dev API returns contract errors for invalid input and IDs', async ({ request }) => {
  const input = {
    name: 'Cejl',
    wheelchair_accessible: true,
    has_shelter: false,
    has_ticket_machine: true
  };
  const created = await request.post('/api/v1/stops', { data: input });
  const path = `/api/v1/stops/${(await created.json()).id}`;
  try {
    for (const data of [
      { ...input, extra: 1 },
      { ...input, image_url: '/image.png' },
      { ...input, has_shelter: 'true' },
      { name: 'Cejl' }
    ]) {
      for (const response of [
        await request.post('/api/v1/stops', { data }),
        await request.put(path, { data })
      ]) {
        expect(response.status()).toBe(400);
        expect(await response.json()).toEqual({ error: expect.any(String) });
      }
    }
    for (const method of ['GET', 'PUT', 'DELETE']) {
      for (const id of ['0', '-1', '1.5', 'abc']) {
        const response = await request.fetch(`/api/v1/stops/${id}`, {
          method,
          data: method === 'PUT' ? input : undefined
        });
        expect(response.status()).toBe(400);
        expect(await response.json()).toEqual({ error: expect.any(String) });
      }
      const response = await request.fetch('/api/v1/stops/9999999', {
        method,
        data: method === 'PUT' ? input : undefined
      });
      expect(response.status()).toBe(404);
      expect(await response.json()).toEqual({ error: 'Stop not found' });
    }
  } finally {
    await request.delete(path);
  }
});
