<script lang="ts">
  import type { Snippet } from 'svelte';
  let { credits }: { credits?: Snippet } = $props();

  import { resolve } from '$app/paths';
  import { ArrowUpRight, Code, Coffee } from '@lucide/svelte';
  import { Button } from '$lib/ui/button';

  const excuses = [
    'Čekal/a jsem na spoj. Myšlenkový.',
    'Šel/šla jsem zkratkou. Byla vzdělávací.',
    'Káva měla zpoždění. Nemohl/a jsem ji tam nechat.',
    'Zastávku jsem našel/našla. Jen na opačné straně kampusu.'
  ];
  let excuse = $state(-1);
</script>

<footer class="site-footer" aria-label="Patička webu">
  <div class="footer-inner">
    <div class="footer-main">
      <div class="signoff">
        <p class="terminal"><span aria-hidden="true"></span> Think different Academy</p>
        <h2>Konečná.<br /><span>Zatím.</span></h2>
        <p>Od kolejí po poslední přednášku.<br />Uvidíme se na zastávce.</p>
      </div>
      <nav aria-label="O projektu">
        <h3>Za zastávkou</h3>
        <a href="https://github.com/NightmarePog/2idiotz"
          ><Code class="size-4" aria-hidden="true" /> GitHub <ArrowUpRight
            class="size-4"
            aria-hidden="true"
          /></a
        >
        <a href={resolve('/contact')}>Kontakt <span aria-hidden="true">↗</span></a>
        <a href={resolve('/terms')}>Podmínky používání <span class="link-note">(v klidu)</span></a>
      </nav>
      <div class="extra">
        <h3>Pro případ zpoždění</h3>
        <p>Správnou zastávku najdeme.<br />Omluvenku už necháme na vás.</p>
        <Button variant="outline" onclick={() => (excuse = (excuse + 1) % excuses.length)}>
          <Coffee data-icon="inline-start" aria-hidden="true" /> Potřebuju výmluvu
        </Button>
        <p class="excuse" aria-live="polite" aria-atomic="true">
          {excuse >= 0 ? excuses[excuse] : 'Používejte s nadhledem. A radši vyrazte včas.'}
        </p>
      </div>
    </div>
    <div class="footer-bottom">
      {#if credits}
        {@render credits()}
      {:else}
        <p>Vytvořeno pro cesty po kampusu. A občas i oklikou.</p>
      {/if}
      <a href="#main">Zpátky nahoru <span aria-hidden="true">↑</span></a>
    </div>
  </div>
</footer>

<style>
  .site-footer {
    background: var(--muted);
    border-top: 1px solid var(--border);
    margin-top: 2rem;
  }
  .footer-inner {
    max-width: 74rem;
    margin-inline: auto;
    padding: 3rem 2rem 1rem;
  }
  .footer-main {
    display: grid;
    grid-template-columns: 1.25fr 0.8fr 1fr;
    gap: 3rem;
  }
  .terminal {
    display: flex;
    align-items: center;
    gap: 0.625rem;
    font-size: 0.875rem;
    font-weight: 600;
  }
  .terminal > span {
    width: 0.75rem;
    height: 0.75rem;
    border: 3px solid var(--primary);
    border-radius: 50%;
  }
  h2 {
    font-size: 3rem;
    line-height: 1;
    font-weight: 650;
    letter-spacing: -0.025em;
    margin-block: 1.25rem;
  }
  h2 span {
    color: var(--primary);
  }
  h3 {
    font-size: 1rem;
    font-weight: 650;
    margin-bottom: 1rem;
  }
  .signoff > p:last-child,
  .extra > p,
  .footer-bottom {
    color: var(--muted-foreground);
    line-height: 1.6;
  }
  nav a {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    min-height: 2.75rem;
    width: fit-content;
  }
  a:hover {
    color: var(--primary);
    text-decoration: underline;
    text-underline-offset: 0.25em;
  }
  .link-note {
    color: var(--muted-foreground);
    font-size: 0.875rem;
  }
  .extra > p {
    margin-bottom: 1rem;
  }
  .extra .excuse {
    margin-top: 0.875rem;
    min-height: 4.5em;
    font-size: 0.875rem;
    max-width: 30ch;
  }
  .footer-bottom {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    justify-content: space-between;
    gap: 0.5rem 2rem;
    margin-top: 2rem;
    padding-top: 1rem;
    border-top: 1px solid var(--border);
    font-size: 0.875rem;
  }
  .footer-bottom a {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    min-height: 2.75rem;
  }
  @media (max-width: 899px) {
    .footer-main {
      grid-template-columns: 1fr 1fr;
      gap: 2rem;
    }
    .signoff {
      grid-column: 1 / -1;
    }
  }
  @media (max-width: 539px) {
    .footer-inner {
      padding: 2rem 1.25rem 1rem;
    }
    .footer-main {
      grid-template-columns: minmax(0, 1fr);
    }
  }
</style>
