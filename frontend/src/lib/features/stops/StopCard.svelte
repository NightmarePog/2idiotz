<script lang="ts">
  import { resolve } from '$app/paths';
  import { Accessibility, ArrowRight, House, Ticket } from '@lucide/svelte';
  import type { Stop } from '$lib/api/generated';
  import { Badge } from '$lib/ui/badge';
  import { rememberStop } from './selected-stop';
  import StopImage from './StopImage.svelte';

  let {
    stop,
    eager = false,
    search = ''
  }: { stop: Stop; eager?: boolean; search?: string } = $props();
</script>

<!-- eslint-disable svelte/no-navigation-without-resolve -- The route is already resolved; preserve the query verbatim after it. -->
<a
  href={`${resolve('/stops/[id]', { id: String(stop.id) })}${search}`}
  aria-label={`Detail zastávky ${stop.name}`}
  class="stop-card group"
  onclick={() => rememberStop(stop)}
>
  <div class="stop-card-image">
    <StopImage
      src={stop.image_url}
      name={stop.name}
      id={stop.id}
      transition
      {eager}
      class="aspect-auto h-24 w-full sm:h-28 [&_p]:min-w-0 [&_p]:px-2 [&_p]:text-xs"
    />
  </div>
  <div class="stop-card-body">
    <div class="flex items-center justify-between gap-4">
      <h2>{stop.name}</h2>
      <span class="stop-card-arrow" aria-hidden="true"><ArrowRight size={22} /></span>
    </div>
    <div class="stop-card-amenities">
      {#if stop.wheelchair_accessible}
        <Badge variant="outline"><Accessibility aria-hidden="true" /> Bez bariér</Badge>
      {/if}
      {#if stop.has_shelter}
        <Badge variant="outline"><House aria-hidden="true" /> Přístřešek</Badge>
      {/if}
      {#if stop.has_ticket_machine}
        <Badge variant="outline"><Ticket aria-hidden="true" /> Jízdenky</Badge>
      {/if}
      {#if !stop.wheelchair_accessible && !stop.has_shelter && !stop.has_ticket_machine}
        <p class="text-sm text-muted-foreground">Bez uvedeného vybavení</p>
      {/if}
    </div>
  </div>
</a>

<!-- eslint-enable svelte/no-navigation-without-resolve -->

<style>
  .stop-card {
    display: grid;
    grid-template-columns: 10.5rem minmax(0, 1fr);
    align-items: center;
    gap: 1.5rem;
    min-width: 0;
    padding-block: 1.25rem;
    border-bottom: 1px solid var(--border);
  }
  .stop-card-image {
    overflow: hidden;
    border-radius: var(--radius-md);
  }
  .stop-card-body {
    min-width: 0;
  }
  h2 {
    min-width: 0;
    font-size: 1.5rem;
    font-weight: 650;
    line-height: 1.2;
    overflow-wrap: anywhere;
  }
  .stop-card-arrow {
    color: var(--muted-foreground);
    flex-shrink: 0;
  }
  .stop-card-amenities {
    display: flex;
    flex-wrap: wrap;
    align-items: flex-start;
    gap: 0.5rem;
    margin-top: 0.75rem;
  }
  .stop-card:hover h2,
  .stop-card:focus-visible h2 {
    color: var(--primary);
  }
  @media (max-width: 639px) {
    .stop-card {
      grid-template-columns: 5.5rem minmax(0, 1fr);
      align-items: start;
      gap: 1rem;
    }
    h2 {
      font-size: 1.25rem;
    }
    .stop-card-arrow {
      display: none;
    }
  }
</style>
