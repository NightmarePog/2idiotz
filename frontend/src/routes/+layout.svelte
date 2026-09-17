<script lang="ts">
  import '../app.css';
  let { children } = $props();
</script>

<main class="mx-auto max-w-3xl space-y-6 px-6 py-16">{@render children()}</main>
