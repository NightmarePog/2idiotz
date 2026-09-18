<script lang="ts">
  import { untrack } from 'svelte';
  import { resolve } from '$app/paths';
  import { page } from '$app/state';
  import { ArrowLeft } from '@lucide/svelte';
  import { getStop, type Stop } from '$lib/api/generated';
  import StopDetails from './StopDetails.svelte';
  import PageState from '$lib/components/PageState.svelte';
  import LoadingState from '$lib/components/LoadingState.svelte';
  import { Button } from '$lib/ui/button';
  import { forgetStop, rememberStop, selectedStop } from './selected-stop';

  let { id }: { id: string } = $props();

  let stop = $state<Stop | null>(untrack(() => selectedStop(Number(id))));
  let failure = $state<'missing' | 'unavailable' | null>(null);
  let attempt = $state(0);

  $effect(() => {
    const stopId = Number(id);
    void attempt;
    stop = selectedStop(stopId);
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
        else if (data) {
          stop = data;
          rememberStop(data);
        } else failure = 'unavailable';
        if (failure) {
          forgetStop(stopId);
          stop = null;
        }
      } catch {
        if (!controller.signal.aborted) {
          failure = 'unavailable';
          forgetStop(stopId);
          stop = null;
        }
      }
    }
    void loadStop();
    return () => controller.abort();
  });
</script>

<svelte:head>
  <title>{stop?.name ?? 'Detail zastávky'} | Think different Academy</title>
</svelte:head>

<Button href={`${resolve('/stops')}${page.url.search}`} variant="ghost" class="-ml-4">
  <ArrowLeft aria-hidden="true" data-icon="inline-start" /> Všechny zastávky
</Button>

{#if failure}
  <PageState
    title={failure === 'missing' ? 'Zastávka nebyla nalezena' : 'Detail se nepodařilo načíst'}
    headingLevel={1}
    error
    onretry={failure === 'unavailable' ? () => attempt++ : undefined}
  />
{:else if stop}
  <StopDetails {stop} />
{:else}
  <LoadingState label="Načítáme detail zastávky…" detail />
{/if}
