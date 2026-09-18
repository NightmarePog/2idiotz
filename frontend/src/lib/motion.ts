import { animate } from 'motion';

export function prefersReducedMotion() {
  return (
    typeof window === 'undefined' || window.matchMedia('(prefers-reduced-motion: reduce)').matches
  );
}

/** Progressive enhancement: content remains visible without JavaScript or motion. */
export function reveal(
  node: HTMLElement,
  { delay = 0, y = 16 }: { delay?: number; y?: number } = {}
) {
  const preference = window.matchMedia('(prefers-reduced-motion: reduce)');
  // A route transition already supplies continuity; don't animate its snapshot a second time.
  if (preference.matches || document.documentElement.dataset.routeTransition) return;

  const controls = animate(
    node,
    {
      opacity: [0.35, 1],
      transform: [`translateY(${y}px)`, 'translateY(0px)']
    },
    { type: 'spring', duration: 0.65, bounce: 0, delay: Math.min(delay, 0.2) }
  );

  function finish() {
    if (preference.matches) controls.complete();
  }
  preference.addEventListener('change', finish);
  return {
    destroy() {
      preference.removeEventListener('change', finish);
      controls.cancel();
    }
  };
}
