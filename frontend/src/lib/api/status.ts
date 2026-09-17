import { z } from 'zod';
import { request } from './client';

const greetingSchema = z.object({ message: z.string() });
const healthSchema = z.object({ status: z.enum(['UP', 'DOWN']), database: z.enum(['UP', 'DOWN']) });
export type Health = z.infer<typeof healthSchema>;
export type Greeting = z.infer<typeof greetingSchema>;

export const getGreeting = (signal?: AbortSignal) =>
  request('/api/hello', greetingSchema, { signal });
export const getHealth = (signal?: AbortSignal) =>
  request('/api/health', healthSchema, { signal, acceptedStatuses: [503] });
