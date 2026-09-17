<script lang="ts">
  import { Spinner } from '$lib/ui/spinner';
  let { label }: { label: string } = $props();
</script>

<p role="status" class="flex items-center gap-3 py-12 text-lg text-muted-foreground">
  <Spinner aria-hidden="true" />
  {label}
</p>
