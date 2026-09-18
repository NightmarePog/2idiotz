import { sveltekit } from '@sveltejs/kit/vite';
import tailwindcss from '@tailwindcss/vite';
import { defineConfig } from 'vite';
import { devApi } from './dev/api';

export default defineConfig(({ command, mode, isPreview }) => {
  const mockApi = command === 'serve' && !isPreview && mode !== 'backend';
  return {
    plugins: [mockApi && devApi(), tailwindcss(), sveltekit()],
    server: mockApi ? {} : { proxy: { '/api': 'http://localhost:8080' } }
  };
});
