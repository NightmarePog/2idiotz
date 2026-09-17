<script lang="ts">
  let {
    src,
    name,
    eager = false
  }: { src: string | null; name: string; eager?: boolean } = $props();
  let failedSrc = $state<string | null>();
</script>

<div class="flex aspect-[3/2] items-center justify-center overflow-hidden bg-muted">
  {#if src && src !== failedSrc}
    <img
      {src}
      alt={`Zastávka ${name}`}
      width="1536"
      height="1024"
      loading={eager ? 'eager' : 'lazy'}
      decoding="async"
      class="h-full w-full object-cover"
      onerror={() => (failedSrc = src)}
    />
  {:else}
    <p class="px-6 text-center text-muted-foreground">Obrázek zastávky není k dispozici</p>
  {/if}
</div>
