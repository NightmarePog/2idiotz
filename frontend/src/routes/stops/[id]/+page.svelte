<script lang="ts">
  import { page } from '$app/state';
  import StopDetailPage from '$lib/features/stops/StopDetailPage.svelte';
</script>

<StopDetailPage id={page.params.id ?? ''} />
