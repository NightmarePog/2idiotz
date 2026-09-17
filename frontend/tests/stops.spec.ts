import { expect, test } from '@playwright/test';
import { fileURLToPath } from 'node:url';

const stops = [
  {
    id: 12,
    name: 'Cejl',
    image_url: '/api/v1/stops-images/turingTerminal.png',
    wheelchair_accessible: true,
    has_shelter: true,
    has_ticket_machine: false
  },
  {
    id: 13,
    name: 'Ada Exchange',
    image_url: null,
    wheelchair_accessible: false,
    has_shelter: false,
    has_ticket_machine: true
  }
];

test.beforeEach(async ({ page }) => {
  await page.route('**/api/v1/stops', (route) => route.fulfill({ json: stops }));
  await page.route('**/api/v1/stops-images/turingTerminal.png', (route) =>
    route.fulfill({
      path: fileURLToPath(
        new URL(
          '../../backend/src/main/resources/seed/stopsImages/turingTerminal.png',
          import.meta.url
        )
      ),
      contentType: 'image/png'
    })
  );
});

test('lists stops with images and opens a real detail page', async ({ page }) => {
  await page.route('**/api/v1/stops/12', (route) => route.fulfill({ json: stops[0] }));
  await page.goto('/stops');
  await expect(page.getByRole('heading', { level: 1 })).toHaveText('Zastávky');
  await expect(
    page.getByRole('list', { name: 'Seznam zastávek' }).getByRole('listitem')
  ).toHaveCount(2);
  const image = page.getByRole('img', { name: 'Zastávka Cejl' });
  await expect(image).toBeVisible();
  await expect(image).toHaveJSProperty('naturalWidth', 1536);
  await expect(page.getByText('Obrázek zastávky není k dispozici')).toBeVisible();
  await page.getByRole('link', { name: 'Detail zastávky Cejl' }).click();
  await expect(page).toHaveURL(/\/stops\/12$/);
  await expect(page.getByRole('heading', { level: 1 })).toHaveText('Cejl');
  await expect(page.getByText('Bezbariérový přístup')).toBeVisible();
  await page.getByRole('link', { name: 'Všechny zastávky' }).click();
  await expect(page.getByRole('heading', { level: 1 })).toHaveText('Zastávky');
});

test('shows an understandable empty state', async ({ page }) => {
  await page.route('**/api/v1/stops', (route) => route.fulfill({ json: [] }));
  await page.goto('/stops');
  await expect(page.getByRole('status')).toContainText('Zatím tu nejsou žádné zastávky');
  await expect(page.getByRole('list', { name: 'Seznam zastávek' })).toHaveCount(0);
});

for (const failure of ['http', 'invalid', 'network']) {
  test(`handles ${failure} failure and retries without showing an empty state`, async ({
    page
  }) => {
    await page.route('**/api/v1/stops', (route) =>
      failure === 'network'
        ? route.abort()
        : route.fulfill({ status: failure === 'http' ? 503 : 200, json: {} })
    );
    await page.goto('/stops');
    await expect(page.getByRole('alert')).toHaveText('Zastávky se nepodařilo načíst');
    await expect(page.getByText('Zatím tu nejsou žádné zastávky')).toHaveCount(0);
    await page.route('**/api/v1/stops', (route) => route.fulfill({ json: stops }));
    await page.getByRole('button', { name: 'Zkusit znovu' }).click();
    await expect(page.getByRole('link', { name: 'Detail zastávky Cejl' })).toBeVisible();
  });
}

test('shows loading feedback while waiting for the API', async ({ page }) => {
  let release!: () => void;
  const pending = new Promise<void>((resolve) => (release = resolve));
  await page.route('**/api/v1/stops', async (route) => {
    await pending;
    await route.fulfill({ json: stops });
  });
  await page.goto('/stops');
  await expect(page.getByRole('status')).toHaveText('Načítáme zastávky…');
  release();
  await expect(page.getByRole('link', { name: 'Detail zastávky Cejl' })).toBeVisible();
});

test('broken images retain a usable link and layout fits narrow screens', async ({ page }) => {
  await page.route('**/api/v1/stops-images/turingTerminal.png', (route) => route.abort());
  await page.emulateMedia({ reducedMotion: 'reduce' });
  await page.setViewportSize({ width: 375, height: 812 });
  await page.goto('/stops');
  const card = page.getByRole('link', { name: 'Detail zastávky Cejl' });
  await expect(card.getByText('Obrázek zastávky není k dispozici')).toBeVisible();
  await card.focus();
  await expect(card).toBeFocused();
  expect(await card.evaluate((el) => getComputedStyle(el).outlineStyle)).toBe('solid');
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true);
});

test('a missing stop has a clear message and a way back', async ({ page }) => {
  await page.route('**/api/v1/stops/999', (route) => route.fulfill({ status: 404, json: {} }));
  await page.goto('/stops/999');
  await expect(page.getByRole('alert')).toHaveText('Zastávka nebyla nalezena');
  await expect(page.getByRole('link', { name: 'Všechny zastávky' })).toHaveAttribute(
    'href',
    '/stops'
  );
});
