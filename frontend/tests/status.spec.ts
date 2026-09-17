import { expect, test } from '@playwright/test';

test.beforeEach(async ({ page }) => {
  await page.route('**/api/v1/team', (route) =>
    route.fulfill({ json: { name: 'Database team', members: ['Žofie', 'Jan'] } })
  );
});

test('homepage fetches health and displays OK', async ({ page }) => {
  await page.route('**/api/v1/health', (route) => route.fulfill({ json: { status: 'ok' } }));
  await page.goto('/');
  await expect(page.getByRole('heading', { level: 1 })).toHaveText('Think diffrent Academy');
  await expect(page.getByRole('status')).toHaveText('Status: OK');
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true);
});

for (const failure of ['http', 'invalid', 'wrong-value', 'network']) {
  test(`health ${failure} failure is displayed`, async ({ page }) => {
    await page.route('**/api/v1/health', (route) =>
      failure === 'network'
        ? route.abort()
        : route.fulfill({
            status: failure === 'http' ? 503 : 200,
            json:
              failure === 'http'
                ? { status: 'ok' }
                : failure === 'wrong-value'
                  ? { status: 'UP' }
                  : { unexpected: true }
          })
    );
    await page.goto('/');
    await expect(page.getByRole('status')).toHaveText('Status: Unavailable');
  });
}

test('authors show data returned by the team API', async ({ page }) => {
  await page.route('**/api/v1/health', (route) => route.fulfill({ json: { status: 'ok' } }));
  await page.goto('/');
  const authors = page.getByLabel('Authors');
  await expect(authors).toContainText('Database team');
  await expect(authors).toContainText('Žofie · Jan');
  await page.route('**/api/v1/team', (route) =>
    route.fulfill({ json: { name: 'Changed team', members: ['Eva'] } })
  );
  await page.reload();
  await expect(authors).toContainText('Changed team');
  await expect(authors).toContainText('Eva');
  await expect(authors).not.toContainText('Žofie');
});

for (const invalid of [false, true]) {
  test(`team ${invalid ? 'invalid response' : 'HTTP failure'} shows fallback`, async ({ page }) => {
    await page.route('**/api/v1/team', (route) =>
      route.fulfill({ status: invalid ? 200 : 503, json: {} })
    );
    await page.goto('/');
    await expect(page.getByLabel('Authors')).toHaveText('Team details are unavailable.');
  });
}
