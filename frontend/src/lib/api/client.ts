import { z } from 'zod';

export class ApiError extends Error {
  constructor(
    message: string,
    readonly status?: number
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

export async function request<T>(
  path: string,
  schema: z.ZodType<T>,
  options: { signal?: AbortSignal; acceptedStatuses?: number[]; timeoutMs?: number } = {}
): Promise<T> {
  const timeout = AbortSignal.timeout(options.timeoutMs ?? 10_000);
  const signal = options.signal ? AbortSignal.any([options.signal, timeout]) : timeout;
  const response = await fetch(path, { signal, cache: 'no-store' });
  if (!response.ok && !options.acceptedStatuses?.includes(response.status)) {
    throw new ApiError('The service could not be reached.', response.status);
  }
  let json: unknown;
  try {
    json = await response.json();
  } catch {
    throw new ApiError('The service returned an unreadable response.', response.status);
  }
  const result = schema.safeParse(json);
  if (!result.success) {
    throw new ApiError('The service returned an unexpected response.', response.status);
  }
  return result.data;
}
