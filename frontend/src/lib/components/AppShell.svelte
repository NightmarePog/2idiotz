<script lang="ts">
  import { resolve } from '$app/paths';
  import { page } from '$app/state';
  import { Button } from '$lib/ui/button';
  let { children } = $props();
</script>

<a href="#main" class="sr-only z-50 bg-background p-4 focus:not-sr-only focus:absolute"
  >Přejít na obsah</a
>
<header class="border-b bg-background">
  <div class="mx-auto flex max-w-6xl items-center justify-between gap-4 px-5 py-5 sm:px-8">
    <a
      href={resolve('/')}
      class="rounded-lg bg-white p-3"
      aria-label="Think different Academy — úvod"
    >
      <img
        src="/brand/tda-logo.svg"
        alt="Think different Academy"
        class="h-12 w-36 object-contain sm:w-44"
      />
    </a>
    <nav aria-label="Hlavní navigace">
      <Button
        href={resolve('/stops')}
        variant="ghost"
        class="text-primary"
        aria-current={page.url.pathname.startsWith('/stops') ? 'page' : undefined}>Zastávky</Button
      >
    </nav>
  </div>
</header>
<main id="main" class="mx-auto max-w-6xl space-y-6 px-5 py-10 sm:px-8 sm:py-14">
  {@render children()}
</main>
