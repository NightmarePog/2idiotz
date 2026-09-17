import js from '@eslint/js';
import ts from 'typescript-eslint';
import svelte from 'eslint-plugin-svelte';
import globals from 'globals';
import svelteConfig from './svelte.config.js';

export default ts.config(
  {
    ignores: [
      'build/**',
      '.svelte-kit/**',
      'playwright-report/**',
      'test-results/**',
      'src/lib/api/generated/**'
    ]
  },
  js.configs.recommended,
  ...ts.configs.recommended,
  ...svelte.configs['flat/recommended'],
  { languageOptions: { globals: { ...globals.browser, ...globals.node } } },
  {
    files: ['src/**/*.{ts,js,svelte}'],
    rules: {
      'no-restricted-globals': [
        'error',
        { name: 'fetch', message: 'Use the generated API SDK for backend requests.' }
      ]
    }
  },
  {
    files: ['src/lib/ui/**/*.{ts,js,svelte}'],
    rules: {
      // Primitives receive resolved URLs from callers and also support external links.
      'svelte/no-navigation-without-resolve': 'off',
      'no-restricted-imports': [
        'error',
        { patterns: ['$lib/components/*', '$lib/features/*', '$lib/api/*', '$app/*'] }
      ]
    }
  },
  {
    files: ['src/lib/components/**/*.{ts,js,svelte}'],
    rules: {
      'no-restricted-imports': ['error', { patterns: ['$lib/features/*', '$lib/api/*'] }]
    }
  },
  {
    files: ['**/*.svelte', '**/*.svelte.ts', '**/*.svelte.js'],
    languageOptions: { parserOptions: { parser: ts.parser, svelteConfig } }
  }
);
