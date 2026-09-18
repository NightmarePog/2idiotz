<script lang="ts">
  import * as Empty from '$lib/ui/empty';
  import { Button } from '$lib/ui/button';

  let {
    title,
    description,
    headingLevel = 2,
    error = false,
    onretry
  }: {
    title: string;
    description?: string;
    headingLevel?: 1 | 2;
    error?: boolean;
    onretry?: () => void;
  } = $props();
</script>

<Empty.Root
  class="items-start rounded-xl border border-solid bg-muted p-8 text-left sm:p-12"
  role={error ? undefined : 'status'}
>
  <Empty.Header class="max-w-none items-start text-left">
    <Empty.Title>
      <svelte:element this={headingLevel === 1 ? 'h1' : 'h2'} role={error ? 'alert' : undefined}
        >{title}</svelte:element
      >
    </Empty.Title>
    {#if description}<Empty.Description>{description}</Empty.Description>{/if}
  </Empty.Header>
  {#if onretry}
    <Empty.Content class="items-start">
      <Button onclick={onretry}>Zkusit znovu</Button>
    </Empty.Content>
  {/if}
</Empty.Root>
