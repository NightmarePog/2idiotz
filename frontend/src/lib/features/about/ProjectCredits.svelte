<script lang="ts">
  import { onMount } from 'svelte';
  import { getHealth, getTeam, type TeamResponse } from '$lib/api/generated';
  import { Badge } from '$lib/ui/badge';
  let team = $state<TeamResponse>();
  let teamError = $state(false);

  let status = $state('Načítání…');

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
        status = 'Nedostupné';
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

<div class="project-credits" aria-label="Informace o projektu">
  <div aria-label="Autoři" aria-busy={!team && !teamError}>
    {#if team}
      <p class="font-semibold">{team.name}</p>
      <p>{team.members.join(' · ')}</p>
    {:else if teamError}
      <p>Údaje o týmu nejsou k dispozici.</p>
    {:else}
      <p>Načítáme údaje o týmu…</p>
    {/if}
  </div>
  <p role="status">
    <Badge variant={status === 'Nedostupné' ? 'destructive' : 'outline'}>Status: {status}</Badge>
  </p>
</div>

<style>
  .project-credits {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 0.5rem 1.5rem;
    min-width: 0;
  }
  [aria-label='Autoři'] {
    display: flex;
    flex-wrap: wrap;
    gap: 0.25rem 0.75rem;
    min-width: 0;
    overflow-wrap: anywhere;
  }
</style>
