import { defineConfig } from '@hey-api/openapi-ts';

export default defineConfig({
  input: process.env.API_INPUT ?? 'openapi.json',
  output: process.env.API_OUTPUT ?? 'src/lib/api/generated',
  plugins: [
    '@hey-api/typescript',
    '@hey-api/client-fetch',
    {
      name: 'zod',
      $resolvers: {
        number: (ctx) => {
          // JSON integers stay JS numbers; Zod's integer validator rejects unsafe values.
          if (ctx.schema.type === 'integer' && ctx.schema.format === 'int64') {
            ctx.schema = { ...ctx.schema, format: undefined };
          }
          return undefined;
        }
      }
    },
    { name: '@hey-api/sdk', validator: 'zod' }
  ]
});
