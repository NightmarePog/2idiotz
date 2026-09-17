import { expect, test } from '@playwright/test';

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
