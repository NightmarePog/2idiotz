<script lang="ts">
  import '../app.css';
  import { page } from '$app/state';
  import { resolve } from '$app/paths';
  import ProjectCredits from '$lib/features/about/ProjectCredits.svelte';
  import AppShell from '$lib/components/AppShell.svelte';
  let { children } = $props();
</script>

{#snippet footer()}
  {#if page.url.pathname === resolve('/')}
    <ProjectCredits />
  {:else}
    <p>Vytvořeno pro cesty po kampusu. A občas i oklikou.</p>
  {/if}
{/snippet}

<AppShell {footer}>{@render children()}</AppShell>
