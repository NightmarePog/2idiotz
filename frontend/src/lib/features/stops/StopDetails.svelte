<script lang="ts">
  import { Accessibility, Check, House, Ticket, Minus } from '@lucide/svelte';
  import type { Stop } from '$lib/api/generated';
  import { Badge } from '$lib/ui/badge';
  import StopImage from './StopImage.svelte';

  let { stop }: { stop: Stop } = $props();

  const amenities = $derived([
    {
      label: 'Bezbariérový přístup',
      available: stop.wheelchair_accessible,
      icon: Accessibility
    },
    {
      label: 'Přístřešek',
      available: stop.has_shelter,
      icon: House
    },
    {
      label: 'Automat na jízdenky',
      available: stop.has_ticket_machine,
      icon: Ticket
    }
  ]);
</script>

<article class="stop-detail">
  <header class="detail-heading">
    <h1>{stop.name}</h1>
  </header>

  <div class="detail-layout">
    <figure class="detail-image">
      <div class="detail-image-frame">
        <StopImage src={stop.image_url} name={stop.name} id={stop.id} transition eager />
      </div>
    </figure>

    <section class="detail-amenities" aria-labelledby="amenities-title">
      <h2 id="amenities-title">Vybavení</h2>
      <dl>
        {#each amenities as amenity (amenity.label)}
          <div class="amenity-row">
            <dt>
              <span class="amenity-icon" aria-hidden="true"
                ><amenity.icon size={21} strokeWidth={1.5} /></span
              >
              <span class="amenity-label">{amenity.label}</span>
            </dt>
            <dd>
              <Badge variant={amenity.available ? 'default' : 'outline'}>
                {#if amenity.available}<Check aria-hidden="true" />{:else}<Minus
                    aria-hidden="true"
                  />{/if}
                {amenity.available ? 'Ano' : 'Ne'}
              </Badge>
            </dd>
          </div>
        {/each}
      </dl>
    </section>
  </div>
</article>

<style>
  .detail-heading {
    margin: 1.5rem 0 2rem;
    padding: 1.5rem;
    border-radius: var(--radius-md);
    border-inline-end: 0.5rem solid var(--sign-marker);
    background: var(--brand);
    color: var(--brand-foreground);
  }
  h1 {
    font-size: clamp(2.25rem, 4vw, 3rem);
    font-weight: 650;
    line-height: 1.08;
    overflow-wrap: anywhere;
  }
  .detail-layout {
    display: grid;
    grid-template-columns: minmax(0, 1.55fr) minmax(0, 1fr);
    align-items: start;
    gap: clamp(2rem, 5vw, 5rem);
  }
  .detail-image-frame {
    overflow: hidden;
    border: 1px solid var(--border);
    border-radius: var(--radius-md);
  }
  h2 {
    font-size: 1.5rem;
    font-weight: 600;
    line-height: 1.15;
    letter-spacing: -0.025em;
  }
  dl {
    margin-top: 1rem;
    border-top: 1px solid var(--border);
  }
  .amenity-row {
    display: flex;
    min-width: 0;
    align-items: center;
    justify-content: space-between;
    gap: 0.75rem;
    padding: 1rem 0;
    border-bottom: 1px solid var(--border);
  }
  dt {
    display: flex;
    min-width: 0;
    align-items: center;
    gap: 1rem;
  }
  .amenity-icon {
    display: grid;
    width: 2.75rem;
    height: 2.75rem;
    flex-shrink: 0;
    place-items: center;
    color: var(--muted-foreground);
  }
  .amenity-label,
  .amenity-label {
    font-size: 1.1rem;
    font-weight: 600;
  }
  @media (max-width: 1023px) {
    .detail-layout {
      grid-template-columns: minmax(0, 1fr);
      gap: 2rem;
    }
  }
  @media (max-width: 639px) {
    .detail-heading {
      margin: 1.5rem 0 2rem;
      padding-bottom: 2rem;
      gap: 1rem;
    }
    .amenity-row {
      padding: 1.25rem 0;
    }
  }
</style>
