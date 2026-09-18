<script lang="ts">
  import { Pause, Play } from '@lucide/svelte';
  import { Button } from '$lib/ui/button';

  let paused = $state(false);
  const routes = [
    {
      id: 'A',
      color: 'var(--brand)',
      path: 'M 48 144 H 160 Q 176 144 188 156 L 280 248 Q 292 260 310 260 H 424 Q 444 260 444 280 V 432'
    },
    {
      id: 'B',
      color: 'var(--sign-marker)',
      path: 'M 116 440 V 352 Q 116 334 130 320 L 276 174 Q 290 160 308 160 H 484'
    },
    {
      id: 'C',
      color: 'var(--chart-3)',
      path: 'M 302 64 V 340 Q 302 360 322 360 H 510'
    }
  ];
  const stations = [
    { x: 100, y: 144 },
    { x: 160, y: 144 },
    { x: 222, y: 190 },
    { x: 302, y: 260, interchange: true },
    { x: 370, y: 260 },
    { x: 444, y: 330 },
    { x: 444, y: 392 },
    { x: 116, y: 394 },
    { x: 156, y: 294 },
    { x: 241, y: 209, interchange: true },
    { x: 302, y: 160, interchange: true },
    { x: 360, y: 160 },
    { x: 432, y: 160 },
    { x: 302, y: 114 },
    { x: 302, y: 320 },
    { x: 372, y: 360 }
  ];
</script>

<figure class="campus-map" class:paused>
  <div class="map-heading">
    <span class="map-title">Kampus v pohybu</span>
    <div class="motion-control">
      <Button
        variant="ghost"
        size="icon"
        onclick={() => (paused = !paused)}
        aria-label={paused ? 'Spustit animaci mapy' : 'Pozastavit animaci mapy'}
      >
        {#if paused}<Play aria-hidden="true" />{:else}<Pause aria-hidden="true" />{/if}
      </Button>
    </div>
  </div>
  <svg viewBox="0 0 560 500" role="img" aria-labelledby="campus-map-title campus-map-description">
    <title id="campus-map-title">Ilustrační mapa spojení v kampusu</title>
    <desc id="campus-map-description"
      >Tři barevné linky propojují zastávky u fakult, kolejí a dalších míst v kampusu. Jde o
      ilustraci, nikoli skutečné dopravní trasy.</desc
    >
    <defs>
      <pattern id="campus-map-grid" width="28" height="28" patternUnits="userSpaceOnUse">
        <circle cx="1" cy="1" r="1" fill="currentColor" />
      </pattern>
    </defs>
    <rect width="560" height="500" fill="url(#campus-map-grid)" class="map-grid" />
    <g class="districts">
      <text x="56" y="80">VÝUKA</text>
      <text x="416" y="90">VÝZKUM</text>
      <text x="48" y="476">UBYTOVÁNÍ</text>
    </g>
    {#each routes as route, index (route.id)}
      <path d={route.path} stroke={route.color} class="route-base" />
      <path
        d={route.path}
        stroke={route.color}
        pathLength="100"
        class="route-draw"
        style:animation-delay={`${index * 0.35}s`}
      />
      <path
        d={route.path}
        pathLength="100"
        class="connection"
        style:animation-delay={`${index * -3}s`}
      />
    {/each}
    <g class="stations">
      {#each stations as station, index (index)}
        <circle
          cx={station.x}
          cy={station.y}
          r={station.interchange ? 9 : 4.5}
          class:interchange={station.interchange ?? false}
        />
        {#if station.interchange}<circle
            cx={station.x}
            cy={station.y}
            r="3"
            class="station-center"
          />{/if}
      {/each}
    </g>
    <g class="station-labels">
      <text x="94" y="123">Koleje</text>
      <text x="320" y="106">Fakulta</text>
      <text x="226" y="240">Menza</text>
      <text x="320" y="241" class="central-label">Knihovna</text>
      <text x="364" y="140">Laboratoře</text>
      <text x="355" y="390">Sportoviště</text>
      <text x="137" y="399">Aula</text>
    </g>
    <g class="line-terminal">
      <rect x="31" y="127" width="34" height="34" rx="8" fill="var(--brand)" />
      <text x="48" y="150" fill="var(--brand-foreground)">A</text>
      <rect x="467" y="143" width="34" height="34" rx="8" fill="var(--sign-marker)" />
      <text x="484" y="166" fill="var(--sign-marker-foreground)">B</text>
      <rect x="493" y="343" width="34" height="34" rx="8" fill="var(--chart-3)" />
      <text x="510" y="366" fill="var(--sign-marker-foreground)">C</text>
    </g>
  </svg>
  <figcaption>
    <span class="legend"><span aria-hidden="true"></span> Fakulty, koleje a vše mezi nimi.</span
    ><span>Ilustrační schéma</span>
  </figcaption>
</figure>

<style>
  .campus-map {
    min-width: 0;
  }
  .map-heading {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
  }
  .map-title {
    color: var(--muted-foreground);
    font-size: 0.875rem;
    font-weight: 600;
    letter-spacing: 0.1em;
    text-transform: uppercase;
  }
  svg {
    display: block;
    width: 100%;
    height: auto;
    overflow: visible;
  }
  .map-grid {
    color: var(--border);
    opacity: 0.35;
  }
  .districts {
    fill: var(--muted-foreground);
    font-size: 11px;
    letter-spacing: 2px;
  }
  .route-base,
  .route-draw,
  .connection {
    fill: none;
    stroke-width: 8;
    stroke-linecap: round;
    stroke-linejoin: round;
  }
  .route-base {
    opacity: 0.2;
  }
  .route-draw {
    stroke-dasharray: 100;
    stroke-dashoffset: 0;
    animation: connect 2.5s ease-out both;
  }
  .connection {
    stroke: var(--background);
    stroke-width: 3;
    stroke-dasharray: 2 98;
    animation: travel 10s linear infinite;
  }
  .stations circle {
    fill: var(--background);
    stroke: var(--foreground);
    stroke-width: 2;
  }
  .stations .interchange {
    stroke-width: 3;
  }
  .stations .station-center {
    fill: var(--foreground);
    stroke: none;
  }
  .station-labels {
    fill: var(--foreground);
    font-size: 15px;
    font-weight: 500;
    paint-order: stroke;
    stroke: var(--background);
    stroke-width: 5;
    stroke-linejoin: round;
  }
  .central-label {
    font-size: 21px;
    font-weight: 700;
  }
  .line-terminal text {
    text-anchor: middle;
    font-weight: 700;
    font-size: 19px;
  }
  figcaption {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    gap: 0.75rem;
    margin-top: 0.75rem;
    color: var(--muted-foreground);
    font-size: 0.8rem;
  }
  .legend {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
  }
  .legend > span {
    width: 0.5rem;
    height: 0.5rem;
    border: 2px solid var(--primary);
    border-radius: 50%;
  }
  .paused .route-draw,
  .paused .connection {
    animation-play-state: paused;
  }
  @keyframes connect {
    from {
      stroke-dashoffset: 100;
    }
    to {
      stroke-dashoffset: 0;
    }
  }
  @keyframes travel {
    to {
      stroke-dashoffset: -100;
    }
  }
  @media (prefers-reduced-motion: reduce) {
    .route-draw,
    .connection {
      animation: none;
    }
    .connection,
    .motion-control {
      display: none;
    }
  }
</style>
