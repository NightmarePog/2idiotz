import { SvelteDate } from 'svelte/reactivity';
import { getGreeting, getHealth } from '$lib/api/status';

export type Connection = 'checking' | 'connected' | 'unavailable';

export class ServiceStatus {
  api = $state<Connection>('checking');
  database = $state<Connection>('checking');
  loading = $state(false);
  checkedAt = $state<Date | null>(null);
  private active?: AbortController;

  async refresh() {
    this.active?.abort();
    const current = new AbortController();
    this.active = current;
    this.loading = true;
    this.api = 'checking';
    this.database = 'checking';
    const [greeting, health] = await Promise.allSettled([
      getGreeting(current.signal),
      getHealth(current.signal)
    ]);
    // Navigation or a newer request must not update this page with an old result.
    if (current.signal.aborted || this.active !== current) return;
    this.api = greeting.status === 'fulfilled' ? 'connected' : 'unavailable';
    this.database =
      health.status === 'fulfilled' && health.value.database === 'UP' ? 'connected' : 'unavailable';
    this.checkedAt = new SvelteDate();
    this.loading = false;
  }

  dispose() {
    this.active?.abort();
  }
}
