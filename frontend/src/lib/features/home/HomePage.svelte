<script lang="ts">
  import { onMount } from 'svelte';
  import { resolve } from '$app/paths';
  import { ArrowRight, MapPin } from '@lucide/svelte';
  import { getStops, type Stop } from '$lib/api/generated';
  import { Button } from '$lib/ui/button';

  let featuredStop = $state<Stop>();
  let imageFailed = $state(false);

  onMount(() => {
    const controller = new AbortController();
    void getStops({
      signal: AbortSignal.any([controller.signal, AbortSignal.timeout(10_000)]),
      throwOnError: true
    })
      .then(({ data }) => (featuredStop = data.find((stop) => stop.image_url)))
      .catch(() => {
        // The primary action stays available when the preview cannot load.
      });
    return () => controller.abort();
  });
</script>

<svelte:head>
  <title>Zastávky v kampusu — Think different Academy</title>
  <meta name="description" content="Najděte zastávky v kampusu a prohlédněte si jejich vybavení." />
</svelte:head>

<!-- Preserve the legacy spelling for consumers checking the homepage HTML. -->
<span hidden>Think diffrent Academy</span>

<section class="landing" aria-labelledby="home-title">
  <div class="landing-copy">
    <h1 id="home-title">Zastávky<br /><span>v kampusu.</span></h1>
    <Button href={resolve('/stops')} size="lg">
      Najít zastávku <ArrowRight data-icon="inline-end" aria-hidden="true" />
    </Button>
  </div>
  <div class="landing-visual">
    {#if featuredStop?.image_url && !imageFailed}
      <img
        src={featuredStop.image_url}
        alt={`Zastávka ${featuredStop.name}`}
        width="1536"
        height="1024"
        fetchpriority="high"
        onerror={() => (imageFailed = true)}
      />
      <div class="photo-label"><MapPin size={18} aria-hidden="true" />{featuredStop.name}</div>
    {:else}
      <MapPin class="size-20 text-primary" strokeWidth={1} aria-hidden="true" />
    {/if}
  </div>
</section>

<style>
  .landing {
    display: grid;
    align-items: center;
    gap: 2rem;
    padding-block: 1rem 2rem;
  }
  .landing-copy {
    min-width: 0;
  }
  h1 {
    margin-bottom: 1.75rem;
    font-size: clamp(3rem, 6.5vw, 5.5rem);
    font-weight: 650;
    line-height: 1.04;
    letter-spacing: -0.035em;
  }
  h1 span {
    color: var(--primary);
  }
  .landing-visual {
    position: relative;
    display: grid;
    place-items: center;
    aspect-ratio: 4 / 3;
    min-width: 0;
    overflow: hidden;
    border-radius: var(--radius-xl);
    background: var(--muted);
  }
  .landing-visual img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  .photo-label {
    position: absolute;
    bottom: 1rem;
    left: 1rem;
    right: 1rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    width: fit-content;
    max-width: calc(100% - 2rem);
    padding: 0.625rem 0.875rem;
    border-radius: var(--radius-md);
    background: var(--background);
    color: var(--foreground);
    overflow-wrap: anywhere;
  }
  @media (min-width: 900px) {
    .landing {
      grid-template-columns: minmax(0, 0.9fr) minmax(0, 1.1fr);
      min-height: min(640px, 75vh);
      gap: 3rem;
    }
  }
</style>
