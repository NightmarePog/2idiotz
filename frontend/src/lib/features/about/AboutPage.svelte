<script lang="ts">
  import { resolve } from '$app/paths';
  import { ArrowLeft, ArrowUpRight } from '@lucide/svelte';
  import { Button } from '$lib/ui/button';
  let { kind }: { kind: 'contact' | 'terms' } = $props();
</script>

<svelte:head>
  <title>{kind === 'contact' ? 'Kontakt' : 'Podmínky používání'} — Think different Academy</title>
</svelte:head>

<article class="about-page">
  <Button href={resolve('/')} variant="ghost" class="-ml-4"
    ><ArrowLeft data-icon="inline-start" aria-hidden="true" /> Zpět na úvod</Button
  >
  {#if kind === 'contact'}
    <h1>Ozvěte se.</h1>
    <p class="intro">Chyba v aplikaci? Nápad na zlepšení? Tady je správná zastávka.</p>
    <section>
      <h2>Potkáme se na GitHubu</h2>
      <p>
        Napište nám přes Issues v repozitáři projektu. Přidejte, co se stalo, na které stránce a co
        jste čekali. Snímek obrazovky pomůže taky.
      </p>
      <Button href="https://github.com/NightmarePog/2idiotz/issues/new"
        >Napsat týmu <ArrowUpRight data-icon="inline-end" aria-hidden="true" /></Button
      >
      <p class="note">
        K odeslání potřebujete účet na GitHubu. Issues jsou veřejné, osobní údaje si nechte pro
        sebe.
      </p>
    </section>
    <section>
      <h2>Chcete se podívat pod kapotu?</h2>
      <p>
        Kód i prostor pro vaše nápady najdete v <a href="https://github.com/NightmarePog/2idiotz"
          >repozitáři projektu</a
        >. Kafe si přineste vlastní.
      </p>
    </section>
  {:else}
    <h1>Podmínky. V klidu.</h1>
    <p class="intro">Pár pravidel společné jízdy. Žádná zkouška na konci.</p>
    <p class="note">
      Tohle je neformální průvodce používáním studentského projektu, ne právní smlouva.
    </p>
    <section>
      <h2>Hledejte zastávky, ne kličky.</h2>
      <p>
        Aplikace pomáhá najít zastávky v kampusu a jejich vybavení. Neplánuje jízdní řády a
        nezaručí, že stihnete přednášku v osm.
      </p>
    </section>
    <section>
      <h2>Mapa má fantazii.</h2>
      <p>
        Animované schéma na úvodu je ilustrace. Skutečné spoje a odjezdy si ověřte u dopravce;
        vybavení na místě se může změnit.
      </p>
    </section>
    <section>
      <h2>Buďte fajn spolucestující.</h2>
      <p>
        Nerozbíjejte aplikaci ostatním. Když najdete chybu, <a href={resolve('/contact')}
          >dejte nám vědět</a
        >. Hlášení „nefunguje to“ bereme, ale s podrobnostmi dojedeme dál.
      </p>
    </section>
    <section>
      <h2>Výmluvy nejsou omluvenky.</h2>
      <p>
        Generátor v patičce je pro pobavení. Studijní oddělení za něj kredity nedává. Pravděpodobně.
      </p>
    </section>
  {/if}
</article>

<style>
  .about-page {
    max-width: 42rem;
    padding-bottom: 2rem;
  }
  h1 {
    margin-top: 2rem;
    font-size: clamp(2.5rem, 5vw, 3.5rem);
    line-height: 1.1;
    font-weight: 650;
    letter-spacing: -0.025em;
  }
  .intro {
    font-size: 1.25rem;
    margin-block: 1.25rem;
  }
  section {
    margin-top: 2.5rem;
  }
  h2 {
    font-size: 1.5rem;
    font-weight: 650;
    margin-bottom: 0.75rem;
  }
  p {
    line-height: 1.65;
    color: var(--muted-foreground);
    margin-bottom: 1rem;
  }
  .note {
    font-size: 0.875rem;
    margin-top: 1rem;
  }
  p a {
    color: var(--primary);
    text-decoration: underline;
    text-underline-offset: 0.2em;
  }
</style>
