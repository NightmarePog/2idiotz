<script lang="ts">
  import { onMount, type Snippet } from 'svelte';
  import { onNavigate } from '$app/navigation';
  import { resolve } from '$app/paths';
  import { page } from '$app/state';
  import { Moon, Sun, ArrowUpRight } from '@lucide/svelte';
  import { Button } from '$lib/ui/button';
  import { prefersReducedMotion } from '$lib/motion';
  import { cn } from '$lib/utils';
  import SiteFooter from './SiteFooter.svelte';

  let { children, footer }: { children: Snippet; footer?: Snippet } = $props();
  let dark = $state(true);
  let transition: ViewTransition | undefined;

  function syncTheme() {
    dark = document.documentElement.classList.contains('dark');
    document
      .querySelector('meta[name="theme-color"]')
      ?.setAttribute('content', dark ? '#1a1a1a' : '#ffffff');
  }

  onMount(() => {
    syncTheme();
    function handleStorage(event: StorageEvent) {
      if (event.key !== 'theme') return;
      document.documentElement.classList.toggle('dark', event.newValue !== 'light');
      syncTheme();
    }
    window.addEventListener('storage', handleStorage);
    return () => {
      window.removeEventListener('storage', handleStorage);
      transition?.skipTransition();
    };
  });

  function toggleTheme() {
    document.documentElement.classList.toggle('dark');
    syncTheme();
    try {
      localStorage.setItem('theme', dark ? 'dark' : 'light');
    } catch {
      // The current theme still works when storage is unavailable.
    }
  }

  onNavigate((navigation) => {
    transition?.skipTransition();
    if (
      !document.startViewTransition ||
      prefersReducedMotion() ||
      navigation.type === 'popstate' ||
      navigation.from?.url.pathname === navigation.to?.url.pathname
    )
      return;

    return new Promise<void>((resume) => {
      document.documentElement.dataset.routeTransition = 'true';
      const current = document.startViewTransition(async () => {
        resume();
        await navigation.complete;
      });
      transition = current;
      // Navigation must remain usable even if a browser declines to capture a snapshot.
      void current.ready.catch(resume);
      void current.finished
        .finally(() => {
          if (transition === current) delete document.documentElement.dataset.routeTransition;
        })
        .catch(() => {});
    });
  });
</script>

<a href="#main" class="sr-only z-50 bg-background p-4 focus:not-sr-only focus:absolute"
  >Přejít na obsah</a
>
<div class="flex min-h-dvh flex-col">
  <header class="border-b">
    <div
      class="mx-auto flex max-w-[74rem] items-center justify-between gap-3 px-5 py-5 sm:px-8 sm:py-6"
    >
      <div class="flex items-center gap-5">
        <a href={resolve('/')} class="brand-mark" aria-label="Think different Academy — úvod">
          <img
            src="/api/v1/assets/brand/tda-logo.svg"
            alt="Think different Academy"
            width="289"
            height="90"
            class="brand-logo brand-logo-light"
          />
          <img
            src="/api/v1/assets/brand/tda-logo-dark.svg"
            alt="Think different Academy"
            width="289"
            height="90"
            class="brand-logo brand-logo-dark"
          />
        </a>
      </div>
      <div class="flex items-center gap-2 sm:gap-5">
        <nav aria-label="Hlavní navigace" class="flex items-center gap-1 sm:gap-3">
          <a
            href={resolve('/')}
            aria-current={page.url.pathname === '/' ? 'page' : undefined}
            class={cn('nav-link home-link', page.url.pathname === '/' && 'is-active')}>Úvod</a
          >
          <a
            href={resolve('/stops')}
            aria-current={page.url.pathname.startsWith('/stops') ? 'page' : undefined}
            class={cn('nav-link', page.url.pathname.startsWith('/stops') && 'is-active')}
            >Zastávky <ArrowUpRight class="size-4" aria-hidden="true" /></a
          >
        </nav>
        <Button
          variant="outline"
          size="icon"
          onclick={toggleTheme}
          aria-label={dark ? 'Přepnout na světlý režim' : 'Přepnout na tmavý režim'}
          title={dark ? 'Světlý režim' : 'Tmavý režim'}
        >
          {#if dark}<Sun aria-hidden="true" />{:else}<Moon aria-hidden="true" />{/if}
        </Button>
      </div>
    </div>
  </header>
  <main
    id="main"
    tabindex="-1"
    class="mx-auto w-full max-w-[74rem] flex-1 px-5 py-8 outline-none sm:px-8 sm:py-10"
  >
    {@render children()}
  </main>
  <SiteFooter credits={footer} />
</div>

<style>
  .brand-mark {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 2.75rem;
    padding-block: 0.375rem;
    border-radius: 0.25rem;
    flex-shrink: 0;
  }
  .brand-logo {
    display: block;
    width: 10.5rem;
    height: auto;
  }
  .brand-logo-dark {
    display: none;
  }
  :global(.dark) .brand-logo-light {
    display: none;
  }
  :global(.dark) .brand-logo-dark {
    display: block;
  }
  .nav-link {
    display: inline-flex;
    min-height: 2.75rem;
    align-items: center;
    gap: 0.375rem;
    padding: 0.5rem 0.75rem;
    border-radius: 0.5rem;
    color: var(--muted-foreground);
    font-size: 1rem;
    font-weight: 600;
    transition:
      color var(--motion-fast),
      background-color var(--motion-fast);
  }
  .nav-link:hover {
    color: var(--foreground);
    background: var(--muted);
  }
  .nav-link.is-active {
    color: var(--primary);
  }
  @media (max-width: 639px) {
    .home-link {
      display: none;
    }
    .brand-logo {
      width: 8rem;
    }
    .nav-link {
      padding-inline: 0.5rem;
    }
  }
</style>
