<script lang="ts">
  import ServiceStatus from '$lib/features/status/service-status.svelte';
</script>

<svelte:head>
  <title>Service status · 2idiotz</title>
  <meta
    name="description"
    content="Check the current availability of the 2idiotz application and database."
  />
</svelte:head>
<section class="max-w-2xl" aria-labelledby="status-title">
  <h1 id="status-title" class="text-3xl font-semibold tracking-tight sm:text-4xl">
    Service status
  </h1>
  <p class="mt-4 mb-8 leading-relaxed text-muted-foreground">
    See whether the application and database are responding. This page checks them when you open it,
    and whenever you choose to check again.
  </p>
  <ServiceStatus />
</section>
