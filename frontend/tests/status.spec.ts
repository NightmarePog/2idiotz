import { expect, test } from '@playwright/test';

test('homepage is readable and links to service status', async ({ page }) => {
  await page.route('**/api/hello', (route) =>
    route.fulfill({ json: { message: 'Hello from Spring Boot!' } })
  );
  await page.route('**/api/health', (route) =>
    route.fulfill({ json: { status: 'UP', database: 'UP' } })
  );
  await page.goto('/');
  await expect(page.getByRole('heading', { level: 1 })).toHaveText('Welcome to 2idiotz.');
  await expect(page.getByText('PostgreSQL')).toHaveCount(0);
  await page.getByRole('link', { name: 'View service status' }).click();
  await expect(page).toHaveURL(/\/status$/);
  await expect(page.getByText('Connected', { exact: true })).toHaveCount(2);
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true);
});

test('loading, outage, and keyboard retry are understandable', async ({ page }) => {
  let healthy = false;
  await page.route('**/api/hello', (route) => route.fulfill({ json: { message: 'Hello' } }));
  await page.route('**/api/health', async (route) => {
    await new Promise((resolve) => setTimeout(resolve, 200));
    await route.fulfill({
      status: healthy ? 200 : 503,
      json: { status: healthy ? 'UP' : 'DOWN', database: healthy ? 'UP' : 'DOWN' }
    });
  });
  await page.goto('/status');
  await expect(page.getByRole('button', { name: 'Checking…' })).toBeDisabled();
  await expect(page.getByText('A service couldn’t be reached')).toBeVisible();
  await expect(page.getByText('Unavailable', { exact: true })).toHaveCount(1);
  healthy = true;
  const retry = page.getByRole('button', { name: 'Check again' });
  await retry.focus();
  await page.keyboard.press('Enter');
  await expect(page.getByText('Connected', { exact: true })).toHaveCount(2);
  await expect(page.getByText('A service couldn’t be reached')).toHaveCount(0);
});

test('invalid API data is shown as unavailable', async ({ page }) => {
  await page.route('**/api/*', (route) => route.fulfill({ json: { unexpected: true } }));
  await page.goto('/status');
  await expect(page.getByText('Unavailable', { exact: true })).toHaveCount(2);
  await expect(page.getByRole('button', { name: 'Check again' })).toBeEnabled();
});

test('late responses cannot affect a new page', async ({ page }) => {
  let release: () => void = () => {};
  const delayed = new Promise<void>((resolve) => {
    release = resolve;
  });
  let requests = 0;
  await page.route('**/api/*', async (route) => {
    const initial = requests++ < 2;
    if (initial) await delayed;
    await route
      .fulfill({
        json: route.request().url().endsWith('/health')
          ? { status: initial ? 'DOWN' : 'UP', database: initial ? 'DOWN' : 'UP' }
          : { message: 'Hello' }
      })
      .catch(() => {});
  });
  await page.goto('/status');
  await expect.poll(() => requests).toBe(2);
  await page.getByRole('link', { name: 'Home', exact: true }).click();
  await page.getByRole('link', { name: 'View service status' }).click();
  await expect(page.getByText('Connected', { exact: true })).toHaveCount(2);
  release();
  await expect(page.getByText('Unavailable', { exact: true })).toHaveCount(0);
});
