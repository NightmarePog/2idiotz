import { createStop } from './fixtures/stops';
import { mockBrandAssets } from './fixtures/assets';

const featuredStop = createStop(42, {
  image_url: 'http://127.0.0.1:3301/api/v1/stops-images/turingTerminal.png'
});
import { expect, test } from '@playwright/test';
import { fileURLToPath } from 'node:url';

test.beforeEach(async ({ page }) => {
  await mockBrandAssets(page);
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
  await page.route('**/api/v1/stops', (route) => route.fulfill({ json: [featuredStop] }));
  await page.route('**/api/v1/health', (route) => route.fulfill({ json: { status: 'ok' } }));
  await page.route('**/api/v1/team', (route) =>
    route.fulfill({ json: { name: 'Database team', members: ['Žofie', 'Jan'] } })
  );
});

test('homepage fetches health and displays OK', async ({ page }, testInfo) => {
  await page.goto('/');
  await expect(page.getByRole('heading', { level: 1 })).toHaveText('Zastávkyv kampusu.');
  await expect(page.getByRole('status').filter({ hasText: /^Status:/ })).toHaveText('Status: OK');
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true);
  await page.screenshot({
    path: `/tmp/tda-home-${testInfo.project.name}.png`,
    fullPage: true,
    animations: 'allow'
  });
});

test('dark mode is the default and keyboard theme choice persists', async ({ page }, testInfo) => {
  await page.emulateMedia({ colorScheme: 'light' });
  await page.goto('/');
  const root = page.locator('html');
  await expect(root).toHaveClass(/dark/);

  await expect(page.getByRole('status').filter({ hasText: /^Status:/ })).toHaveText('Status: OK');
  const lightSwitch = page.getByRole('button', { name: 'Přepnout na světlý režim' });
  await lightSwitch.focus();
  await page.keyboard.press('Enter');
  await expect(root).not.toHaveClass(/dark/);
  await expect(page.getByRole('button', { name: 'Přepnout na tmavý režim' })).toBeVisible();
  await expect(page.getByRole('status').filter({ hasText: /^Status:/ })).toHaveText('Status: OK');
  await page.screenshot({
    path: `/tmp/tda-home-light-${testInfo.project.name}.png`,
    fullPage: true,
    animations: 'allow'
  });
  await page.reload();
  await expect(root).not.toHaveClass(/dark/);

  await page.getByRole('button', { name: 'Přepnout na tmavý režim' }).click();
  await expect(root).toHaveClass(/dark/);
  await page.reload();
  await expect(root).toHaveClass(/dark/);
  await expect(page.getByRole('button', { name: 'Přepnout na světlý režim' })).toBeVisible();
});

test('reduced motion keeps the small-screen homepage readable and keyboard accessible', async ({
  page
}) => {
  await page.emulateMedia({ reducedMotion: 'reduce' });
  await page.setViewportSize({ width: 320, height: 740 });
  await page.goto('/');
  await expect(page.getByRole('heading', { level: 1 })).toBeVisible();
  await expect(page.getByRole('status').filter({ hasText: /^Status:/ })).toHaveText('Status: OK');
  await expect(page.getByLabel('Autoři')).toContainText('Žofie · Jan');
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true);
  expect(
    await page.evaluate(() =>
      document.getAnimations().some((animation) => {
        const timing = animation.effect?.getComputedTiming();
        return animation.playState === 'running' && Number(timing?.activeDuration) > 100;
      })
    )
  ).toBe(false);

  const skipLink = page.getByRole('link', { name: 'Přejít na obsah' });
  await page.keyboard.press('Tab');
  await expect(skipLink).toBeFocused();
  await expect(skipLink).toBeVisible();
  await page.keyboard.press('Enter');
  await expect(page).toHaveURL(/#main$/);
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
    await expect(page.getByRole('status').filter({ hasText: /^Status:/ })).toHaveText(
      'Status: Nedostupné'
    );
  });
}

test('authors show data returned by the team API', async ({ page }) => {
  await page.route('**/api/v1/health', (route) => route.fulfill({ json: { status: 'ok' } }));
  await page.goto('/');
  const authors = page.getByLabel('Autoři');
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
    await expect(page.getByLabel('Autoři')).toHaveText('Údaje o týmu nejsou k dispozici.');
  });
}

test('landing page has one primary action leading to the directory', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByRole('searchbox')).toHaveCount(0);
  await expect(page.getByRole('img', { name: `Zastávka ${featuredStop.name}` })).toBeVisible();
  await page.getByRole('link', { name: 'Najít zastávku', exact: true }).click();
  await expect(page).toHaveURL(/\/stops$/);
  await expect(page.getByRole('searchbox', { name: 'Hledat zastávku' })).toBeVisible();
  await expect(
    page.getByRole('link', { name: `Detail zastávky ${featuredStop.name}` })
  ).toBeVisible();
});

for (const failure of ['empty', 'api', 'image']) {
  test(`landing action remains usable with ${failure} preview failure`, async ({ page }) => {
    if (failure === 'image') {
      await page.route('**/api/v1/stops-images/**', (route) => route.abort());
    } else {
      await page.route('**/api/v1/stops', (route) =>
        route.fulfill({
          status: failure === 'api' ? 503 : 200,
          json: []
        })
      );
    }
    await page.goto('/');
    await expect(page.getByRole('link', { name: 'Najít zastávku', exact: true })).toBeVisible();
    await expect(page.getByRole('img', { name: `Zastávka ${featuredStop.name}` })).toHaveCount(0);
  });
}

test('footer links open contact and terms, and the excuse button works', async ({
  page
}, testInfo) => {
  await page.goto('/');
  await expect(page.getByRole('status').filter({ hasText: /^Status:/ })).toHaveText('Status: OK');
  const footer = page.getByRole('contentinfo', { name: 'Patička webu' });
  await expect(footer.getByRole('link', { name: 'GitHub' })).toHaveAttribute(
    'href',
    'https://github.com/NightmarePog/2idiotz'
  );
  await footer.getByRole('button', { name: 'Potřebuju výmluvu' }).click();
  await expect(footer.getByText('Čekal/a jsem na spoj. Myšlenkový.')).toBeVisible();
  await footer.screenshot({ path: `/tmp/tda-footer-${testInfo.project.name}.png` });
  await footer.getByRole('link', { name: 'Kontakt' }).click();
  await expect(page.getByRole('heading', { level: 1 })).toHaveText('Ozvěte se.');
  await expect(page.getByRole('link', { name: 'Napsat týmu' })).toHaveAttribute(
    'href',
    'https://github.com/NightmarePog/2idiotz/issues/new'
  );
  await footer.getByRole('link', { name: 'Podmínky používání' }).click();
  await expect(page.getByRole('heading', { level: 1 })).toHaveText('Podmínky. V klidu.');
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBe(true);
});
