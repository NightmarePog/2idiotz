<script lang="ts">
  import { resolve } from '$app/paths';
  import ArrowUpRight from '@lucide/svelte/icons/arrow-up-right';
  import { Button } from '$lib/components/ui/button';
  import * as Card from '$lib/components/ui/card';
</script>

<svelte:head>
  <title>Welcome · 2idiotz</title>
  <meta name="description" content="Welcome to 2idiotz. A starting point for our next idea." />
</svelte:head>
<section aria-labelledby="welcome" class="max-w-2xl">
  <p class="mb-5 text-sm font-semibold text-primary">Hello, and welcome.</p>
  <h1
    id="welcome"
    class="text-4xl leading-tight font-semibold tracking-tight text-balance sm:text-6xl"
  >
    Welcome to 2idiotz.
  </h1>
  <p class="mt-6 max-w-lg text-lg leading-relaxed text-muted-foreground">
    This is our starting point. There aren’t any features to explore yet, but the app is ready for
    its first idea.
  </p>
  <Button href={resolve('/status')} class="mt-8 min-h-11"
    >View service status <ArrowUpRight class="size-4" aria-hidden="true" /></Button
  >
</section>
<Card.Root class="mt-14 max-w-2xl shadow-none">
  <Card.Header>
    <Card.Title>Something not working?</Card.Title>
    <Card.Description
      >The status page checks whether the app’s services can be reached. You can check again
      whenever you need to.</Card.Description
    >
  </Card.Header>
</Card.Root>
