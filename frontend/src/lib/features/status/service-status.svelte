<script lang="ts">
  import { onMount } from 'svelte';
  import RefreshCw from '@lucide/svelte/icons/refresh-cw';
  import CircleCheck from '@lucide/svelte/icons/circle-check';
  import CircleAlert from '@lucide/svelte/icons/circle-alert';
  import { Button } from '$lib/components/ui/button';
  import { Badge } from '$lib/components/ui/badge';
  import { Skeleton } from '$lib/components/ui/skeleton';
  import * as Card from '$lib/components/ui/card';
  import * as Alert from '$lib/components/ui/alert';
  import { ServiceStatus } from './status.svelte';

  const status = new ServiceStatus();
  const rows = $derived([
    { name: 'Application', detail: 'Spring Boot API', connection: status.api },
    { name: 'Database', detail: 'PostgreSQL', connection: status.database }
  ]);
  const unavailable = $derived(status.api === 'unavailable' || status.database === 'unavailable');
  onMount(() => {
    void status.refresh();
    return () => status.dispose();
  });
</script>

<Card.Root class="shadow-none" aria-busy={status.loading}>
  <Card.Header>
    <Card.Title>Connection check</Card.Title>
    <Card.Description>A snapshot of the services behind this app.</Card.Description>
  </Card.Header>
  <Card.Content>
    <div aria-live="polite" aria-atomic="true">
      <dl class="divide-y">
        {#each rows as row (row.name)}
          <div class="flex items-center justify-between gap-3 py-5">
            <dt class="font-medium">
              {row.name}<span class="mt-1 block text-sm font-normal text-muted-foreground"
                >{row.detail}</span
              >
            </dt>
            <dd>
              {#if row.connection === 'checking'}
                <span class="sr-only">Checking</span><Skeleton class="h-6 w-24" />
              {:else}
                <Badge
                  variant={row.connection === 'connected' ? 'secondary' : 'destructive'}
                  class="gap-1.5 px-2.5 py-1"
                >
                  {#if row.connection === 'connected'}<CircleCheck
                      class="size-3.5"
                      aria-hidden="true"
                    />{:else}<CircleAlert class="size-3.5" aria-hidden="true" />{/if}
                  {row.connection === 'connected' ? 'Connected' : 'Unavailable'}
                </Badge>
              {/if}
            </dd>
          </div>
        {/each}
      </dl>
      {#if unavailable}
        <Alert.Root variant="destructive" class="mt-4">
          <CircleAlert class="size-4" aria-hidden="true" />
          <Alert.Title>A service couldn’t be reached</Alert.Title>
          <Alert.Description
            >It may still be starting. Wait a moment, then check again.</Alert.Description
          >
        </Alert.Root>
      {/if}
    </div>
    <noscript><p>Enable JavaScript to check service availability.</p></noscript>
  </Card.Content>
  <Card.Footer
    class="flex flex-col items-stretch justify-between gap-4 border-t pt-6 sm:flex-row sm:items-center"
  >
    <p class="text-sm text-muted-foreground">
      {status.checkedAt
        ? `Last checked at ${status.checkedAt.toLocaleTimeString()}`
        : 'Checking the connection…'}
    </p>
    <Button onclick={() => status.refresh()} disabled={status.loading} class="min-h-11"
      ><RefreshCw class="size-4" aria-hidden="true" />{status.loading
        ? 'Checking…'
        : 'Check again'}</Button
    >
  </Card.Footer>
</Card.Root>
