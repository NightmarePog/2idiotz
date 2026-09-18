<script lang="ts">
  import { Spinner } from '$lib/ui/spinner';
  import { Skeleton } from '$lib/ui/skeleton';
  let { label, detail = false }: { label: string; detail?: boolean } = $props();
</script>

<p role="status" class="flex items-center gap-3 py-12 text-lg text-muted-foreground">
  <Spinner aria-hidden="true" />
  {label}
</p>

{#if detail}
  <div class="grid gap-8 lg:grid-cols-[1.55fr_1fr]" aria-hidden="true">
    <Skeleton class="aspect-[3/2] w-full rounded-xl" />
    <div class="flex flex-col gap-6">
      <Skeleton class="h-10 w-2/3" />
      {#each [0, 1, 2] as item (item)}
        <Skeleton class="h-16 w-full" />
      {/each}
    </div>
  </div>
{:else}
  <div class="grid gap-6" aria-hidden="true">
    {#each [0, 1, 2] as item (item)}
      <div
        class="grid grid-cols-[5.5rem_minmax(0,1fr)] items-center gap-4 sm:grid-cols-[10.5rem_minmax(0,1fr)]"
      >
        <Skeleton class="aspect-[3/2] w-full rounded-xl" />
        <div class="flex flex-col gap-3">
          <Skeleton class="h-7 w-2/3" />
          <Skeleton class="h-4 w-1/2" />
        </div>
      </div>
    {/each}
  </div>
{/if}
