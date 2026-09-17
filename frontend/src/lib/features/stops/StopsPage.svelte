<script lang="ts">
  import { onMount } from 'svelte';
  import { getStops, type Stop } from '$lib/api/generated';
  import StopCard from './StopCard.svelte';
  import PageState from '$lib/components/PageState.svelte';
  import LoadingState from '$lib/components/LoadingState.svelte';
  import { Badge } from '$lib/ui/badge';

  let stops = $state<Stop[] | null>(null);
  let failed = $state(false);
  const controller = new AbortController();

  async function loadStops() {
    failed = false;
    try {
      const { data } = await getStops({
        throwOnError: true,
        signal: AbortSignal.any([controller.signal, AbortSignal.timeout(10_000)])
      });
      stops = data;
    } catch {
      if (!controller.signal.aborted) failed = true;
    }
  }

  onMount(() => {
    void loadStops();
    return () => controller.abort();
  });
</script>

<svelte:head>
  <title>Zastávky | Think different Academy</title>
  <meta
    name="description"
    content="Prohlédněte si všechny zastávky, jejich fotografie a dostupné vybavení."
  />
</svelte:head>

<div class="mb-10 flex flex-wrap items-end justify-between gap-5 border-b pb-8">
  <div>
    <p class="mb-3 font-semibold tracking-wide text-primary">Think different Transit</p>
    <h1 class="text-4xl font-bold tracking-tight sm:text-6xl">Zastávky</h1>
    <p class="mt-4 max-w-xl text-lg text-muted-foreground">
      Najděte svou zastávku. Prohlédněte si její podobu a zjistěte, jaké nabízí vybavení.
    </p>
  </div>
  {#if stops && stops.length > 0}
    <Badge variant="secondary" class="h-auto px-4 py-2 text-base">Celkem: {stops.length}</Badge>
  {/if}
</div>

{#if failed}
  <PageState
    title="Zastávky se nepodařilo načíst"
    description="Zkuste to prosím znovu za chvíli."
    error
    onretry={loadStops}
  />
{:else if stops === null}
  <LoadingState label="Načítáme zastávky…" />
{:else if stops.length === 0}
  <PageState
    title="Zatím tu nejsou žádné zastávky"
    description="V systému zatím není evidována žádná zastávka. Jakmile přibudou, najdete je zde."
  />
{:else}
  <ul class="grid gap-6 sm:grid-cols-2 lg:grid-cols-3" aria-label="Seznam zastávek">
    {#each stops as stop, index (stop.id)}
      <li class="min-w-0"><StopCard {stop} eager={index < 3} /></li>
    {/each}
  </ul>
{/if}
