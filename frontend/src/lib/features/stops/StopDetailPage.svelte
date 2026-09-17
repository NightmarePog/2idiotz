<script lang="ts">
  import { resolve } from '$app/paths';
  import { ArrowLeft } from '@lucide/svelte';
  import { getStop, type Stop } from '$lib/api/generated';
  import StopDetails from './StopDetails.svelte';
  import PageState from '$lib/components/PageState.svelte';
  import LoadingState from '$lib/components/LoadingState.svelte';
  import { Button } from '$lib/ui/button';

  let { id }: { id: string } = $props();

  let stop = $state<Stop | null>(null);
  let failure = $state<'missing' | 'unavailable' | null>(null);
  let attempt = $state(0);

  $effect(() => {
    const stopId = Number(id);
    void attempt;
    stop = null;
    failure = null;
    if (!Number.isSafeInteger(stopId) || stopId < 1) {
      failure = 'missing';
      return;
    }
    const controller = new AbortController();
    async function loadStop() {
      try {
        const { data, response } = await getStop({
          path: { id: stopId },
          signal: AbortSignal.any([controller.signal, AbortSignal.timeout(10_000)])
        });
        if (controller.signal.aborted) return;
        if (response?.status === 404) failure = 'missing';
        else if (data) stop = data;
        else failure = 'unavailable';
      } catch {
        if (!controller.signal.aborted) failure = 'unavailable';
      }
    }
    void loadStop();
    return () => controller.abort();
  });
</script>

<svelte:head>
  <title>{stop?.name ?? 'Detail zastávky'} | Think different Academy</title>
</svelte:head>

<Button href={resolve('/stops')} variant="ghost" class="-ml-4 text-primary">
  <ArrowLeft aria-hidden="true" /> Všechny zastávky
</Button>

{#if failure}
  <PageState
    title={failure === 'missing' ? 'Zastávka nebyla nalezena' : 'Detail se nepodařilo načíst'}
    description={failure === 'missing'
      ? 'Tato zastávka neexistuje nebo již byla odstraněna.'
      : 'Zkuste to prosím znovu za chvíli.'}
    headingLevel={1}
    error
    onretry={failure === 'unavailable' ? () => attempt++ : undefined}
  />
{:else if stop}
  <StopDetails {stop} />
{:else}
  <LoadingState label="Načítáme detail zastávky…" />
{/if}
