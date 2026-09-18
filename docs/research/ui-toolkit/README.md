**Motion, component libraries, and AI tooling**

Downloaded and checked on 18 September 2026, extending the [dark UI research](../dark-ui-ux-playbook.md). This pack contains official documentation snapshots, a React skill for reference, and installation provenance. Application dependencies and components were not changed.

**Installed for Codex.** Two official skills are installed in the user's skills directory and will be available on the next turn:

| Skill | Installed location | Upstream |
| --- | --- | --- |
| Motion | `~/.codex/skills/motion` | [motiondivision/ai-kit](https://github.com/motiondivision/ai-kit/tree/1140efe9ad5e03c689ea6bb19d9d3850a4dae5f7/plugins/motion/skills/motion), revision `1140efe9ad5e03c689ea6bb19d9d3850a4dae5f7` |
| shadcn-svelte | `~/.codex/skills/shadcn-svelte` | [huntabyte/shadcn-svelte](https://github.com/huntabyte/shadcn-svelte/tree/cca0797342bb6cca361a97e61cfab90e68167c04/skills/shadcn-svelte), revision `cca0797342bb6cca361a97e61cfab90e68167c04` |

The Motion skill supplies animation guidance plus documentation-search, transition, and audit workflows. shadcn-svelte supplies Svelte component composition, CLI, theming, and reference rules. Both are original upstream skills; their advice must be applied in the context of the repository's brand guide, existing components, and installed versions. The project's documented `pnpm exec shadcn-svelte` workflow remains relevant; generic upstream initialization or update instructions do not call for resetting this existing app. [Motion AI Kit](https://motion.dev/docs/ai-kit), [shadcn-svelte skills](https://www.shadcn-svelte.com/docs/skills)

The [official React shadcn skill](reference-skills/shadcn/SKILL.md) was downloaded with its supporting files and license at revision `a87a63b2ca25143d26c8bd0903e4e9bc77b3f824`. It lives outside agent skill-discovery directories because its React APIs and broad `components.json` trigger overlap with the Svelte project. It remains available for comparing patterns or future React work. [Official shadcn skill documentation](https://ui.shadcn.com/docs/skills)

**Motion's live documentation is connected.** The global Codex configuration now registers `motion` at `https://mcp.motion.dev`. Verification completed an MCP handshake, listed the public tools, executed a JavaScript documentation search, and read six documentation resources. This confirms the endpoint works; the already-running conversation may need a session reload before the newly configured MCP tools appear. [Motion setup](https://motion.dev/docs/ai-kit-install), [Codex MCP configuration](https://developers.openai.com/codex/mcp)

The tested free endpoint exposed `search-motion-docs`. Premium example/component source, MotionScore audits, and the visual editor require Motion+; those features were not activated. The public website and installer skill differ on whether CSS easing generation is free, and that tool was absent from the verified free tool list, so this setup does not promise it. The current setup uses the hosted endpoint rather than the retired local server/API-token approach. [Motion AI Kit access](https://motion.dev/docs/ai-kit)

**Local documentation to read first.** Snapshots are useful offline; check the linked official pages when implementation depends on a current API. Full source URLs, retrieval time, revisions, and SHA-256 hashes are in [manifest.json](manifest.json).

| Topic | Downloaded document | Purpose |
| --- | --- | --- |
| Motion index | [llms.txt](docs/motion/llms.txt) | Official JS, React, Vue, and AI documentation map |
| DOM animation | [animate](docs/motion/animate.md) | Mini/hybrid APIs, keyframes, sequences, animation controls |
| Scroll choreography | [scroll](docs/motion/scroll.md) | Scroll-linked animation and progress |
| Spring behavior | [spring](docs/motion/spring.md) | Spring parameters and lower-level generation |
| Timing curves | [easing functions](docs/motion/easing-functions.md) | Available easing functions and composition |
| Sequencing | [stagger](docs/motion/stagger.md) | Delays between related elements |
| Rendering cost | [performance](docs/motion/performance.md) | Browser rendering and animation-property choices |
| Svelte component map | [llms.txt](docs/shadcn-svelte/llms.txt) | Official Markdown pages for individual components |
| Svelte AI workflow | [skills](docs/shadcn-svelte/skills.md) | Official skill setup and usage |
| Semantic theme | [theming](docs/shadcn-svelte/theming.md) | Theme tokens and customization |
| Overlays | [dialog](docs/shadcn-svelte/dialog.md) | Actual Svelte API, titles, triggers, and composition |
| Selection/navigation | [tabs](docs/shadcn-svelte/tabs.md) | Tab structure and binding |
| Control feedback | [button](docs/shadcn-svelte/button.md) | Variants, states, and composition |
| React shadcn map | [llms.txt](docs/shadcn-react/llms.txt) | Reference for the React ecosystem |
| Aceternity catalog | [llms.txt](docs/aceternity-react/llms.txt) | Links to visual effects, examples, and component docs |

These are focused snapshots and indexes, not a download of every component or premium example. The six Motion articles were fetched as Markdown resources from its official MCP server; appending `.md` to Motion's ordinary documentation URLs was not a working download route during verification.

**Where Motion fits this Svelte app.** Use Motion's framework-neutral JavaScript API through `motion` or `motion/mini`. `motion/react` and React hooks belong to React applications. Svelte already provides transitions, springs, tweens, and keyed-list animation, so choose Motion when its sequence, SVG, or scroll APIs solve a specific interaction better. [Motion animate documentation](https://motion.dev/docs/animate), [Svelte motion](https://svelte.dev/docs/svelte/svelte-motion)

When implementing later, scope DOM access to the client and to the component's own elements; clean up animation controls, observers, and scroll subscriptions on disposal. Preserve readable server-rendered content. Decide which system owns each animated property to avoid CSS, Svelte, and Motion overwriting one another. Apply reduced-motion behavior explicitly; React configuration examples are not a Svelte integration. These are integration recommendations, not additional package requirements.

Do not assume that the free JavaScript package has every React layout feature. At the time of research, Motion's separate JavaScript layout-animation API was documented as Motion+ alpha. The existing Svelte FLIP and native View Transition options remain relevant for list and page continuity. [Motion JavaScript layout documentation](https://motion.dev/docs/layout-animations)

**Libraries worth studying.** Use their documentation and demos to evaluate interaction behavior, keyboard/touch support, reduced motion, and resting appearance. Popularity and a polished demo do not guarantee suitability for this app.

| Library | Useful patterns to investigate | Compatibility and decision |
| --- | --- | --- |
| [Motion Primitives](https://motion-primitives.com/docs) | Morphing dialogs, shared selection backgrounds, restrained enter/exit behavior | React + Motion + Tailwind; useful mechanics to study. Its [repository](https://github.com/ibelick/motion-primitives) identifies it as beta. |
| [Animate UI](https://animate-ui.com/docs/installation) | Animated primitive components, tabs, sheets, icons | React; its [compatibility guidance](https://animate-ui.com/docs/troubleshooting) lists React, Motion, and Tailwind requirements. Use as a behavior reference here. |
| [Magic UI](https://magicui.design/docs/installation) | Selected hero details and product visualizations | React ecosystem, with free components and separate Pro offerings. Its [MCP integration](https://magicui.design/docs/mcp) is an option for future React work. |
| [Aceternity UI](https://ui.aceternity.com/llms.txt) | Expandable cards, image interactions, advanced promotional effects | React/Next.js. The downloaded official catalog provides a broad reference; adapt only a small number of purposeful effects. |
| [Motion UI](https://motion.dev/ui) | Coordinated component motion and shared timing/theme values | Official premium Motion catalog, primarily React. Premium source is supplied through authenticated Motion+ tooling. |
| [shadcn-svelte](https://www.shadcn-svelte.com/docs) | Accessible Svelte composition, overlays, navigation, and forms | Direct fit for the installed stack; the official skill and docs are installed/downloaded. |

For this project, the strongest first experiment is still stop-card image continuity into the detail page, with responsive press feedback and meaningful filtered-list movement if filtering is added. The React libraries expand the reference pool; they do not require replacing the Svelte architecture.

**Suggested requests for future work:**

- “Use Motion's JavaScript docs to prototype a short stop-image transition in Svelte, with reduced-motion support.”
- “Use shadcn-svelte to build an accessible filter dialog, preserving our brand tokens and `$lib/ui` aliases.”
- “Compare the behavior of Motion Primitives' morphing dialog with a Svelte dialog and native View Transition; keep ordinary navigation and focus working.”

The [manifest](manifest.json) records the original installed files. The shadcn projects' MIT licenses are retained both in the pack and alongside their installed/downloaded skills. Motion's downloaded npm installer metadata declares MIT; its source revision and package version are recorded separately. The installer version is not the application's animation-library version.
