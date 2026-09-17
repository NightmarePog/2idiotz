import { defineConfig } from '@hey-api/openapi-ts';

export default defineConfig({
  input: process.env.API_INPUT ?? 'openapi.json',
  output: process.env.API_OUTPUT ?? 'src/lib/api/generated',
  plugins: [
    '@hey-api/typescript',
    '@hey-api/client-fetch',
    { name: '@hey-api/sdk', validator: 'zod' }
  ]
});
