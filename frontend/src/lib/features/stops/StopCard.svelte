<script lang="ts">
  import { resolve } from '$app/paths';
  import { ArrowUpRight } from '@lucide/svelte';
  import type { Stop } from '$lib/api/generated';
  import * as Card from '$lib/ui/card';
  import StopImage from './StopImage.svelte';

  let { stop, eager = false }: { stop: Stop; eager?: boolean } = $props();
</script>

<a
  href={resolve('/stops/[id]', { id: String(stop.id) })}
  aria-label={`Detail zastávky ${stop.name}`}
  class="group block h-full rounded-xl"
>
  <Card.Root
    class="h-full gap-0 border py-0 shadow-none ring-0 transition-colors group-hover:border-primary group-active:bg-muted"
  >
    <StopImage src={stop.image_url} name={stop.name} {eager} />
    <Card.Content class="flex flex-row items-center justify-between gap-4 p-5">
      <div class="min-w-0">
        <Card.Title class="text-2xl font-semibold break-words group-hover:text-primary"
          ><h2>{stop.name}</h2></Card.Title
        >
        <Card.Description class="mt-1 text-base">Detail zastávky</Card.Description>
      </div>
      <span
        aria-hidden="true"
        class="flex size-11 shrink-0 items-center justify-center rounded-full bg-secondary text-secondary-foreground"
        ><ArrowUpRight size={20} /></span
      >
    </Card.Content>
  </Card.Root>
</a>
