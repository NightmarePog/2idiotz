<script lang="ts">
  import { onMount, untrack } from 'svelte';
  import { afterNavigate, replaceState } from '$app/navigation';
  import { page } from '$app/state';
  import { Accessibility, House, Search, SearchX, Ticket, X } from '@lucide/svelte';
  import { getStops, type Stop } from '$lib/api/generated';
  import StopCard from './StopCard.svelte';
  import PageState from '$lib/components/PageState.svelte';
  import LoadingState from '$lib/components/LoadingState.svelte';
  import { Button } from '$lib/ui/button';
  import { Input } from '$lib/ui/input';
  import * as Empty from '$lib/ui/empty';
  import * as Field from '$lib/ui/field';
  import * as ToggleGroup from '$lib/ui/toggle-group';

  const amenities = [
    {
      value: 'accessible',
      field: 'wheelchair_accessible',
      label: 'Bezbariérový přístup',
      icon: Accessibility
    },
    { value: 'shelter', field: 'has_shelter', label: 'Přístřešek', icon: House },
    { value: 'tickets', field: 'has_ticket_machine', label: 'Automat na jízdenky', icon: Ticket }
  ] as const;

  let stops = $state<Stop[] | null>(null);
  let failed = $state(false);
  const controller = new AbortController();

  function readFilters(url: URL) {
    const requested = (url.searchParams.get('filter') ?? '').split(',');
    return amenities.filter(({ value }) => requested.includes(value)).map(({ value }) => value);
  }

  // Shallow replaceState changes the address bar, not page.url. Keep controls and
  // outgoing links reactive locally, then restore them on navigation/history.
  const initialUrl = untrack(() => page.url);
  let query = $state(initialUrl.searchParams.get('q') ?? '');
  let filters = $state(readFilters(initialUrl));
  let directorySearch = $state(initialUrl.search);
  const hasFilters = $derived(query.length > 0 || filters.length > 0);

  afterNavigate(() => {
    const url = new URL(window.location.href);
    query = url.searchParams.get('q') ?? '';
    filters = readFilters(url);
    directorySearch = url.search;
  });

  function normalize(value: string) {
    return value
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLocaleLowerCase('cs')
      .trim();
  }

  const visibleStops = $derived(
    (stops ?? []).filter(
      (stop) =>
        normalize(stop.name).includes(normalize(query)) &&
        amenities.every(({ value, field }) => !filters.includes(value) || stop[field])
    )
  );

  function updateFilters(nextQuery: string, nextFilters: string[]) {
    const url = new URL(window.location.href);
    if (nextQuery) url.searchParams.set('q', nextQuery);
    else url.searchParams.delete('q');
    const selected = amenities
      .filter(({ value }) => nextFilters.includes(value))
      .map(({ value }) => value);
    if (selected.length) url.searchParams.set('filter', selected.join(','));
    else url.searchParams.delete('filter');
    query = nextQuery;
    filters = selected;
    directorySearch = url.search;
    // The browser URL already includes the configured base; do not resolve it twice.
    // eslint-disable-next-line svelte/no-navigation-without-resolve
    replaceState(url, page.state);
  }

  function resetFilters() {
    updateFilters('', []);
    // The reset control disappears; keep keyboard focus in the search workflow.
    document.getElementById('stop-search')?.focus();
  }

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
    content="Najděte svou zastávku v kampusu. Fotografie, bezbariérový přístup, přístřešky a automaty na jízdenky na jednom místě."
  />
</svelte:head>

<header class="directory-heading">
  <h1>Zastávky</h1>
</header>

{#if failed}
  <PageState title="Zastávky se nepodařilo načíst" error onretry={loadStops} />
{:else if stops === null}
  <LoadingState label="Načítáme zastávky…" />
{:else if stops.length === 0}
  <PageState title="Zatím tu nejsou žádné zastávky" />
{:else}
  <section class="directory-controls" aria-label="Vyhledávání a filtrování zastávek">
    <Field.Group class="gap-6 lg:flex-row lg:items-end lg:gap-10">
      <Field.Field class="lg:max-w-sm">
        <Field.Label for="stop-search"
          ><Search aria-hidden="true" size={16} /> Hledat zastávku</Field.Label
        >
        <Input
          id="stop-search"
          type="search"
          autocomplete="off"
          value={query}
          oninput={(event) => updateFilters(event.currentTarget.value, filters)}
          class="h-12"
        />
      </Field.Field>
      <Field.Set class="min-w-0 gap-3">
        <Field.Legend variant="label">Vybavení</Field.Legend>
        <ToggleGroup.Root
          type="multiple"
          variant="outline"
          size="lg"
          spacing={2}
          value={filters}
          onValueChange={(value) => updateFilters(query, value)}
          aria-label="Vybavení zastávky"
          class="w-full flex-wrap justify-start"
        >
          {#each amenities as amenity (amenity.value)}
            <ToggleGroup.Item value={amenity.value} aria-label={amenity.label} class="min-h-11">
              <amenity.icon aria-hidden="true" />
              {amenity.label}
            </ToggleGroup.Item>
          {/each}
        </ToggleGroup.Root>
      </Field.Set>
    </Field.Group>
  </section>

  <div class="results-heading">
    <p role="status" aria-live="polite" aria-atomic="true">
      <span class="result-number">{visibleStops.length}</span>
      <span
        >{hasFilters
          ? `z ${stops.length} zastávek`
          : stops.length === 1
            ? 'zastávka'
            : stops.length < 5
              ? 'zastávky'
              : 'zastávek'}</span
      >
    </p>
    {#if hasFilters}
      <Button variant="ghost" onclick={resetFilters}
        ><X aria-hidden="true" data-icon="inline-start" /> Zrušit filtry</Button
      >
    {/if}
  </div>

  {#if visibleStops.length === 0}
    <Empty.Root class="min-h-80 border">
      <Empty.Header>
        <Empty.Media variant="icon"><SearchX aria-hidden="true" /></Empty.Media>
        <Empty.Title><h2>Takovou zastávku tu nemáme</h2></Empty.Title>
        <Empty.Description>Zkuste jiný název nebo zrušte filtry.</Empty.Description>
      </Empty.Header>
      <Empty.Content
        ><Button variant="outline" onclick={resetFilters}>Zobrazit všechny zastávky</Button
        ></Empty.Content
      >
    </Empty.Root>
  {:else}
    <ul class="stop-grid" aria-label="Seznam zastávek">
      {#each visibleStops as stop, index (stop.id)}
        <li class="min-w-0">
          <StopCard {stop} eager={index < 3} search={directorySearch} />
        </li>
      {/each}
    </ul>
  {/if}
{/if}

<style>
  .directory-heading h1 {
    font-size: clamp(2.25rem, 4vw, 3rem);
    font-weight: 650;
    line-height: 1.08;
    letter-spacing: -0.025em;
  }
  .directory-controls {
    padding-block: 1.5rem;
  }
  .results-heading {
    display: flex;
    min-height: 3.5rem;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
    border-bottom: 1px solid var(--border);
  }
  .results-heading > p {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: var(--muted-foreground);
  }
  .result-number {
    color: var(--foreground);
    font-weight: 650;
    font-variant-numeric: tabular-nums;
  }
  .stop-grid {
    display: grid;
    grid-template-columns: minmax(0, 1fr);
  }
</style>
