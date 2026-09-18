import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  testMatch: 'dev-api.test.ts',
  timeout: 60_000, // Allow Vite to compile the first page on a cold dev server.
  use: { baseURL: 'http://127.0.0.1:3302' },
  webServer: {
    command: 'pnpm dev --port 3302 --strictPort',
    url: 'http://127.0.0.1:3302/api/v1/health',
    reuseExistingServer: false
  }
});
