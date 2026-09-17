<script lang="ts">
  import { onMount } from 'svelte';

  let loading = $state(true);
  let greeting = $state('Connecting to your application…');
  let api = $state('Checking');
  let database = $state('Checking');
  let checkedAt = $state('');
  let error = $state('');
  let active: AbortController | undefined;

  async function refresh() {
    active?.abort();
    const controller = new AbortController();
    active = controller;
    const timeout = setTimeout(() => controller.abort(), 10000);
    loading = true;
    error = '';
    api = 'Checking';
    database = 'Checking';
    try {
      const [helloResult, healthResult] = await Promise.allSettled([
        fetch('/api/hello', { signal: controller.signal, cache: 'no-store' }).then(async (res) => {
          if (!res.ok) throw new Error('API unavailable');
          const body = await res.json();
          if (typeof body.message !== 'string') throw new Error('Unexpected API response');
          return body.message as string;
        }),
        fetch('/api/health', { signal: controller.signal, cache: 'no-store' }).then(async (res) => {
          const body = await res.json();
          return res.ok && body.database === 'UP';
        })
      ]);
      api = helloResult.status === 'fulfilled' ? 'Connected' : 'Unavailable';
      greeting = helloResult.status === 'fulfilled' ? helloResult.value : 'Your API is currently unavailable.';
      database = healthResult.status === 'fulfilled' && healthResult.value ? 'Connected' : 'Unavailable';
      if (api !== 'Connected' || database !== 'Connected') {
        error = 'A service is unavailable. It may still be starting. Try checking again in a moment.';
      }
      checkedAt = new Date().toLocaleTimeString();
    } finally {
      clearTimeout(timeout);
      loading = false;
    }
  }

  onMount(() => {
    void refresh();
    return () => active?.abort();
  });
</script>

<svelte:head>
  <title>2idiotz · Ready to build</title>
  <meta name="description" content="The 2idiotz starter application and service status." />
</svelte:head>

<main>
  <header><a class="brand" href="/">2idiotz<span class="brand-dot">.</span></a><span class="eyebrow">THE STARTING POINT</span></header>
  <section class="intro" aria-labelledby="heading">
    <p class="eyebrow">SMALL BEGINNINGS. BIG IDEAS.</p>
    <h1 id="heading">Ready to<br /><span>build something.</span></h1>
    <p class="description">Your foundation is here. A fresh canvas for whatever comes next.</p>
  </section>

  <section class="status-panel" aria-labelledby="status-heading" aria-busy={loading}>
    <div class="panel-heading"><div><p class="eyebrow">CONNECTION CHECK</p><h2 id="status-heading">Application status</h2></div><span class="index">01 / 01</span></div>
    <div aria-live="polite" aria-atomic="true">
      <p class="greeting">{greeting}</p>
      <dl>
        <div><dt>Spring Boot API</dt><dd class:connected={api === 'Connected'}><span class="dot"></span>{api}</dd></div>
        <div><dt>PostgreSQL</dt><dd class:connected={database === 'Connected'}><span class="dot"></span>{database}</dd></div>
      </dl>
      {#if error}<p class="error">{error}</p>{/if}
    </div>
    <div class="panel-footer"><p>{checkedAt ? `Last checked at ${checkedAt}` : 'Checking your services…'}</p><button onclick={refresh} disabled={loading}>{loading ? 'Checking…' : 'Check again'}<span aria-hidden="true">↗</span></button></div>
    <noscript><p>Enable JavaScript to check the API and database connection.</p></noscript>
  </section>
  <footer><span>SVELTEKIT + SPRING BOOT</span><span>Made for what’s next.</span></footer>
</main>

<style>
  :global(*) { box-sizing: border-box; }
  :global(body) { margin: 0; background: #f5f5f0; color: #20291f; font-family: Inter, ui-sans-serif, system-ui, sans-serif; font-size: 16px; line-height: 1.5; }
  :global(:root) { --muted: #596254; --accent: #33613b; --line: #d9ded2; }
  main { max-width: 1060px; padding: 32px 32px 24px; margin: auto; }
  header, .panel-heading, .panel-footer, footer { display: flex; justify-content: space-between; align-items: center; gap: 20px; }
  header { padding-bottom: 24px; border-bottom: 1px solid var(--line); }
  .brand { color: inherit; font-weight: 850; font-size: 28px; text-decoration: none; letter-spacing: -1px; }
  .brand-dot { color: var(--accent); }
  .eyebrow, .index { font-size: 12px; font-weight: 650; letter-spacing: 1.5px; color: var(--muted); }
  .intro { padding: 70px 0 42px; }
  h1 { font-size: clamp(42px, 7vw, 76px); line-height: 1.06; letter-spacing: -3px; margin: 22px 0; font-weight: 650; }
  h1 span { color: var(--accent); }
  .description { color: var(--muted); max-width: 410px; font-size: 18px; }
  .status-panel { background: #fff; border: 1px solid var(--line); border-radius: 18px; padding: 30px; }
  .panel-heading .eyebrow { margin: 0 0 5px; }
  h2 { font-size: 22px; margin: 0; font-weight: 600; letter-spacing: -.5px; }
  .greeting { margin: 28px 0 18px; color: var(--muted); }
  dl { margin: 0; }
  dl > div { display: flex; justify-content: space-between; gap: 16px; padding: 17px 0; border-top: 1px solid var(--line); }
  dd { display: flex; align-items: center; gap: 9px; margin: 0; color: var(--muted); font-size: 14px; }
  dd.connected { color: var(--accent); }
  .dot { height: 7px; width: 7px; border-radius: 50%; background: currentColor; }
  .error { color: #914019; font-size: 14px; }
  .panel-footer { padding-top: 20px; border-top: 1px solid var(--line); }
  .panel-footer p { color: var(--muted); font-size: 13px; margin: 0; }
  button { display: flex; align-items: center; justify-content: center; gap: 24px; font: inherit; font-size: 14px; font-weight: 600; border: 0; border-radius: 8px; color: white; background: #284e30; padding: 12px 18px; min-height: 44px; cursor: pointer; }
  button:hover:not(:disabled) { background: #183b20; }
  button:disabled { opacity: .65; cursor: wait; }
  :global(:focus-visible) { outline: 3px solid #62844a; outline-offset: 4px; }
  footer { color: var(--muted); margin-top: 30px; font-size: 12px; }
  @media (max-width: 540px) { main { padding: 22px 20px; } header > .eyebrow, .index { display: none; } .intro { padding-top: 42px; } h1 { letter-spacing: -2px; } .status-panel { padding: 22px; } .panel-footer { align-items: stretch; flex-direction: column; } footer { align-items: flex-start; flex-direction: column; gap: 8px; } }
</style>
