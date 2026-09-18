import { fakerCS_CZ as faker } from '@faker-js/faker';
import { readdir, readFile } from 'node:fs/promises';
import { resolve } from 'node:path';
import type { IncomingMessage, ServerResponse } from 'node:http';
import type { Plugin } from 'vite';
import type { Stop } from '../src/lib/api/generated/types.gen';
import { zStopInput } from '../src/lib/api/generated/zod.gen';

function json(response: ServerResponse, status: number, body?: unknown) {
  response.writeHead(status, { 'Content-Type': 'application/json', 'Cache-Control': 'no-store' });
  response.end(body === undefined ? undefined : JSON.stringify(body));
}

async function input(request: IncomingMessage) {
  let body = '';
  for await (const chunk of request) {
    body += chunk;
    if (body.length > 16_384) throw new Error('Request too large');
  }
  return zStopInput.parse(JSON.parse(body));
}

/** A development-only API: no Spring process, database, or external image service. */
export function devApi(): Plugin {
  return {
    name: 'faker-dev-api',
    apply: 'serve',
    async configureServer(server) {
      const resources = resolve(server.config.root, '../backend/src/main/resources');
      const images = (await readdir(resolve(resources, 'seed/stopsImages')))
        .filter((name) => name.endsWith('.png'))
        .sort();
      faker.seed(20260918);
      const stops: Stop[] = images.map((image, index) => ({
        id: index + 1,
        name: faker.location.street(),
        image_url: `/api/v1/stops-images/${encodeURIComponent(image)}`,
        wheelchair_accessible: faker.datatype.boolean(),
        has_shelter: faker.datatype.boolean(),
        has_ticket_machine: faker.datatype.boolean()
      }));
      let nextId = stops.length + 1;
      const team = {
        name: faker.company.name(),
        members: [faker.person.fullName(), faker.person.fullName()]
      };
      const assets = new Map<string, { path: string; type: string }>(
        images.map((name) => [
          `/api/v1/stops-images/${encodeURIComponent(name)}`,
          { path: resolve(resources, 'seed/stopsImages', name), type: 'image/png' }
        ])
      );
      for (const [name, type] of [
        ['brand/tda-logo.svg', 'image/svg+xml'],
        ['brand/tda-logo-dark.svg', 'image/svg+xml'],
        ['fonts/dosis-variable.ttf', 'font/ttf']
      ]) {
        assets.set(`/api/v1/assets/${name}`, {
          path: resolve(resources, 'assets', name),
          type
        });
      }

      server.config.logger.info('Faker dev API enabled — no backend or database required.');
      server.middlewares.use((request, response, next) => {
        const path = new URL(request.url ?? '/', 'http://localhost').pathname;
        if (!path.startsWith('/api/')) return next();

        void (async () => {
          const asset = assets.get(path);
          if (asset && (request.method === 'GET' || request.method === 'HEAD')) {
            const bytes = await readFile(asset.path);
            response.writeHead(200, { 'Content-Type': asset.type });
            response.end(request.method === 'HEAD' ? undefined : bytes);
            return;
          }
          if (request.method === 'GET') {
            if (path === '/api/v1/health') return json(response, 200, { status: 'ok' });
            if (path === '/api/v1/team') return json(response, 200, team);
            if (path === '/api/v1/stops') return json(response, 200, stops);
          }
          if (path === '/api/v1/stops' && request.method === 'POST') {
            try {
              const data = await input(request);
              const stop = { ...data, id: nextId++, image_url: data.image_url ?? null };
              stops.push(stop);
              response.setHeader('Location', `/api/v1/stops/${stop.id}`);
              return json(response, 201, stop);
            } catch {
              return json(response, 400, { message: 'Invalid stop' });
            }
          }
          const match = /^\/api\/v1\/stops\/(\d+)$/.exec(path);
          if (match) {
            const index = stops.findIndex((stop) => stop.id === Number(match[1]));
            if (index < 0) return json(response, 404, { message: 'Stop not found' });
            if (request.method === 'GET') return json(response, 200, stops[index]);
            if (request.method === 'DELETE') {
              stops.splice(index, 1);
              return json(response, 204);
            }
            if (request.method === 'PUT') {
              try {
                const data = await input(request);
                stops[index] = { ...data, id: stops[index].id, image_url: data.image_url ?? null };
                return json(response, 200, stops[index]);
              } catch {
                return json(response, 400, { message: 'Invalid stop' });
              }
            }
          }
          json(response, 404, { message: 'Unknown dev API route' });
        })().catch(next);
      });
    }
  };
}
