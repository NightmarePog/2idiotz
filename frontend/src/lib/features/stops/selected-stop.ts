import { browser } from '$app/environment';
import type { Stop } from '$lib/api/generated';

// A single browser-only preview preserves image continuity during navigation.
// The detail request still runs and its response always takes precedence.
let selected: Stop | null = null;

export function rememberStop(stop: Stop) {
  if (browser) selected = stop;
}

export function selectedStop(id: number): Stop | null {
  return browser && selected?.id === id ? selected : null;
}

export function forgetStop(id: number) {
  if (selected?.id === id) selected = null;
}
