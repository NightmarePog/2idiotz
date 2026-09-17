<script lang="ts">
  import { onMount } from 'svelte';
  import { getHealth } from '$lib/api/generated';
  import { Badge } from '$lib/components/ui/badge';

  let status = $state('Loading…');

  onMount(() => {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 10_000);

    async function checkHealth() {
      try {
        const { data } = await getHealth({
          signal: controller.signal,
          cache: 'no-store',
          throwOnError: true
        });
        status = data.status.toUpperCase();
      } catch {
        status = 'Unavailable';
      } finally {
        clearTimeout(timeout);
      }
    }

    void checkHealth();
    return () => {
      controller.abort();
      clearTimeout(timeout);
    };
  });
</script>

<svelte:head>
  <title>Think diffrent Academy</title>
</svelte:head>

<h1 class="text-3xl font-semibold tracking-tight sm:text-5xl">Think diffrent Academy</h1>
<p role="status">
  <Badge variant="secondary" class="h-auto px-3 py-1 text-sm">Status: {status}</Badge>
</p>
