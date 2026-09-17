<script lang="ts">
  import { onMount } from 'svelte';
  import { getHealth, getTeam, type TeamResponse } from '$lib/api/generated';
  import { Badge } from '$lib/ui/badge';

  let team = $state<TeamResponse>();
  let teamError = $state(false);

  let status = $state('Loading…');

  onMount(() => {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 10_000);

    async function checkHealth() {
      try {
        const { data } = await getHealth({
          signal: controller.signal,
          cache: 'no-store',
          throwOnError: true
        });
        status = data.status.toUpperCase();
      } catch {
        status = 'Unavailable';
      }
    }

    async function loadTeam() {
      try {
        const { data } = await getTeam({
          signal: controller.signal,
          cache: 'no-store',
          throwOnError: true
        });
        team = data;
      } catch {
        teamError = true;
      }
    }

    void Promise.all([checkHealth(), loadTeam()]).finally(() => clearTimeout(timeout));
    return () => {
      controller.abort();
      clearTimeout(timeout);
    };
  });
</script>

<svelte:head>
  <title>Think different Academy</title>
</svelte:head>

<h1 class="text-3xl font-semibold tracking-tight sm:text-5xl">Think different Academy</h1>
<p role="status">
  <Badge variant="secondary" class="h-auto px-3 py-1 text-sm">Status: {status}</Badge>
</p>

<footer
  class="border-t pt-6 text-sm text-muted-foreground"
  aria-label="Authors"
  aria-busy={!team && !teamError}
>
  {#if team}
    <p class="font-medium text-foreground">{team.name}</p>
    <p>{team.members.join(' · ')}</p>
  {:else if teamError}
    <p>Team details are unavailable.</p>
  {:else}
    <p>Loading team details…</p>
  {/if}
</footer>
