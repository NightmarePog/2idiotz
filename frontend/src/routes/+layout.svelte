<script lang="ts">
  import { resolve } from '$app/paths';
  import '../app.css';
  import { page } from '$app/state';
  import { Separator } from '$lib/components/ui/separator';
  let { children } = $props();
</script>

<a
  href="#main"
  class="absolute top-3 left-3 z-50 -translate-y-24 rounded-md bg-card px-4 py-3 focus:translate-y-0"
  >Skip to content</a
>
<div class="mx-auto flex min-h-screen max-w-5xl flex-col px-5 sm:px-8">
  <header class="flex min-h-24 items-center justify-between gap-6">
    <a href={resolve('/')} class="text-2xl font-bold tracking-tight" aria-label="2idiotz home"
      >2idiotz<span class="text-primary">.</span></a
    >
    <nav aria-label="Main navigation" class="flex gap-2 text-sm">
      <a
        href={resolve('/')}
        aria-current={page.url.pathname === '/' ? 'page' : undefined}
        class="rounded-md px-3 py-3 font-medium hover:bg-secondary">Home</a
      >
      <a
        href={resolve('/status')}
        aria-current={page.url.pathname === '/status' ? 'page' : undefined}
        class="rounded-md px-3 py-3 font-medium hover:bg-secondary">Service status</a
      >
    </nav>
  </header>
  <Separator />
  <main id="main" class="flex-1 py-12 sm:py-20" tabindex="-1">{@render children()}</main>
  <footer class="border-t py-6 text-sm text-muted-foreground">2idiotz · A place to start.</footer>
</div>
