<script lang="ts">
  import { cn } from '$lib/utils';

  let {
    src,
    name,
    eager = false,
    class: className,
    id,
    transition = false
  }: {
    src: string | null;
    name: string;
    eager?: boolean;
    class?: string;
    id?: number | string;
    transition?: boolean;
  } = $props();

  let failedSrc = $state<string | null>();
</script>

<div
  class={cn(
    'stop-image-frame relative flex aspect-[3/2] items-center justify-center overflow-hidden bg-muted',
    className
  )}
  style:view-transition-name={transition && id !== undefined
    ? `stop-image-${String(id).replace(/[^a-zA-Z0-9_-]/g, '-')}`
    : undefined}
>
  {#if src && src !== failedSrc}
    <img
      {src}
      alt={`Zastávka ${name}`}
      width="1536"
      height="1024"
      loading={eager ? 'eager' : 'lazy'}
      fetchpriority={eager ? 'high' : 'auto'}
      decoding="async"
      class="stop-image size-full object-cover"
      onerror={() => (failedSrc = src)}
    />
  {:else}
    <p class="px-6 text-center text-muted-foreground">Obrázek zastávky není k dispozici</p>
  {/if}
</div>
