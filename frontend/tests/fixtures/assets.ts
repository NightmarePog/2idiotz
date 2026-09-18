import type { Page } from '@playwright/test';
import { fileURLToPath } from 'node:url';

export async function mockBrandAssets(page: Page) {
  for (const [asset, contentType] of [
    ['brand/tda-logo.svg', 'image/svg+xml'],
    ['brand/tda-logo-dark.svg', 'image/svg+xml'],
    ['fonts/dosis-variable.ttf', 'font/ttf']
  ]) {
    await page.route(`**/api/v1/assets/${asset}`, (route) =>
      route.fulfill({
        path: fileURLToPath(
          new URL(`../../../backend/src/main/resources/assets/${asset}`, import.meta.url)
        ),
        contentType
      })
    );
  }
}
