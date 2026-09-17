<script lang="ts">
  import type { Stop } from '$lib/api/generated';
  import * as Card from '$lib/ui/card';
  import StopImage from './StopImage.svelte';

  let { stop }: { stop: Stop } = $props();
</script>

<div class="grid items-start gap-8 md:grid-cols-2 md:gap-12">
  <Card.Root class="gap-0 border py-0 shadow-none ring-0">
    <StopImage src={stop.image_url} name={stop.name} eager />
  </Card.Root>
  <div class="min-w-0">
    <p class="mb-3 font-semibold text-primary">Detail zastávky</p>
    <h1 class="text-4xl font-bold break-words sm:text-5xl">{stop.name}</h1>
    <h2 class="mt-8 text-2xl font-semibold">Vybavení zastávky</h2>
    <dl class="mt-4 divide-y border-y text-lg">
      {#each [['Bezbariérový přístup', stop.wheelchair_accessible], ['Přístřešek', stop.has_shelter], ['Automat na jízdenky', stop.has_ticket_machine]] as [label, available] (label)}
        <div class="flex justify-between gap-4 py-4">
          <dt>{label}</dt>
          <dd class="font-semibold">{available ? 'Ano' : 'Ne'}</dd>
        </div>
      {/each}
    </dl>
  </div>
</div>
