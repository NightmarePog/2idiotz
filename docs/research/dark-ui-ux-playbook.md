**Dark UI, distinctive visual design, and advanced interaction: research playbook**

Researched 18 September 2026. Scope: an attractive web interface that defaults to dark, with sophisticated animation and a recognizable identity. Includes an application to this repository. This is research and design guidance; no interface changes or runtime usability tests were performed.

Follow-up: the [Motion and UI toolkit pack](ui-toolkit/README.md) adds Motion.dev research, component-library comparisons, downloaded official documentation, and installed Motion/shadcn-svelte skill provenance.

The recommended direction is a strong typographic composition, carefully layered dark surfaces, real product content, and motion that connects actions to outcomes. Give the product one recognizable visual motif and one signature interaction. Make repeated tasks feel immediate. Use more expressive choreography where people are exploring or learning.

**What the evidence actually supports.** “AI slop” is a subjective description, not a measurable design category or a reliable way to identify how an interface was made. A font, gradient, card, or component library cannot establish authorship. The useful question is whether decisions express the product's purpose and survive real interaction.

| Evidence | Finding relevant to this brief | Practical limit |
| --- | --- | --- |
| [Microsoft Research / University of Washington: Interrogating Design Homogenization in Web Vibe Coding, March 2026](https://arxiv.org/html/2603.13036v1) | Examines how accepting generated defaults can narrow design expression; proposes deliberate reflection and alternatives during creation. | Conceptual risk analysis, literature review, and walkthroughs of six tools. It does not establish the prevalence of particular visual clichés or a universal detection method. |
| [NN/g: AI Prototyping in Real Design Contexts, reviewed August 2026](https://www.nngroup.com/articles/ai-prototyping/) | In a real profile-page redesign evaluation, explicit requirements improved fit; generated results still missed contextual hierarchy, grouping, and contrast. | Heuristic evaluation of a particular design problem, not a controlled conversion experiment or permanent ranking of models. |
| [CHI 2024: The Effects of Generative AI on Design Fixation and Divergent Thinking](https://arxiv.org/abs/2403.11164) | In a 60-participant experiment, AI-image assistance increased fixation on an initial example and reduced idea variety relative to the baseline. | The task was visual chatbot-avatar ideation. Applying the finding to website composition is an inference, not a tested result of this study. |
| [Linear: March 2026 interface refresh](https://linear.app/now/behind-the-latest-design-refresh) | The team reduced competing navigation, icon treatments, and separators while preserving useful information density. | First-party account of design intent. It provides a useful precedent, not proof that its exact styling suits every product. |
| [W3C WCAG 2.2](https://www.w3.org/TR/WCAG22/) | Defines testable accessibility requirements, including contrast, keyboard operation, and alternatives to certain interactions. | A conformance baseline does not by itself establish beauty, clarity, or usability for the intended audience. |

The design prescriptions below are a synthesis and proposed starting points. Timing ranges, density choices, and limits on decorative effects are recommendations to prototype, not scientific constants.

**Patterns that make a polished interface feel generic.** Evaluate combinations and context. A useful card grouping or a purposeful gradient can belong in an excellent design.

| Anti-pattern | What goes wrong | Better design decision |
| --- | --- | --- |
| Interchangeable hero: tiny pill, enormous vague claim, two buttons, floating mockup | The top of the page could describe dozens of products; the useful action is buried. | Put the actual task or outcome in the opening composition. Use a real artifact, example, or working preview. |
| Every section becomes three equal cards | Unrelated information receives identical weight; reading becomes repetitive. | Choose a form that fits the content: comparison table, annotated image, list, timeline, or a single prominent example. |
| Glow, blur, gradient, particles, and perspective all compete | Decorative effects consume the attention needed for content and controls. | Select one coherent material or motif. Concentrate emphasis at a meaningful focal point. |
| Every label gets a badge, icon, or colored background | Secondary information competes with the next decision. | Reserve strong accents for action and selection; let spacing and typography group ordinary information. |
| All information is small gray text because the UI is “premium” | Visual restraint becomes poor legibility. | Create hierarchy through size, weight, spacing, and position while retaining adequate contrast. |
| A new display font is the whole identity | Surface novelty cannot compensate for generic composition or content. | Establish a consistent typography system and distinctive image treatment tied to the brand. Familiar fonts remain valid. |
| Uniform scroll reveals on every heading and row | Reading waits for choreography; repeated use becomes tiring. | Keep routine content present. Reserve entrance motion for a small, deliberate composition. |
| Fake dashboard numbers, invented testimonials, or stock feature copy | The interface looks finished while the product's actual value remains unproven. | Show real data and product states. Label illustrative material clearly. Omit unsupported claims. |
| Magnetic buttons, custom cursors, and hidden hover actions everywhere | Stable controls become harder to target; touch and keyboard users lose functionality. | Keep hit areas stable. Treat pointer effects as optional decoration and expose essential actions directly. |
| Endless motion as a substitute for personality | Nothing feels important because nothing comes to rest. | Connect motion to a state change, then settle. Design the resting frame carefully. |
| Asymmetry or brutalism applied indiscriminately to avoid looking generated | Navigation and task completion become less predictable. | Put expressive asymmetry in editorial composition; keep repeated task controls consistent. |
| Only the ideal desktop screenshot is designed | Slow requests, long names, empty results, narrow screens, and errors expose unfinished behavior. | Design the complete state model and test transitions between states. |

These are diagnostic recommendations, informed by the sources above and interface engineering judgment. They are not a blacklist of styles associated with AI.

There is direct usability evidence against delaying readable text for a scroll reveal: NN/g observed people waiting and interpreting the delay as loading. This supports keeping headings, explanations, and actions available while placing expressive motion in supporting visuals. It does not rule out all scroll-linked storytelling. [NN/g scroll-animation observations](https://www.nngroup.com/articles/scroll-animations/)

**Study references for a specific design decision.** The following are primary product pages or accounts from their designers. They are inspiration and documented intent, not independently validated examples of conversion gains. Some use light backgrounds; the transferable lesson is composition, identity, or interaction rather than their exact theme.

| Reference | What to examine | What to transfer into this brief |
| --- | --- | --- |
| [Linear's interface refresh](https://linear.app/now/behind-the-latest-design-refresh) | Relative prominence of navigation, content, icons, and separators. | A dark interface can feel rich while peripheral structure recedes. Keep action placement predictable. |
| [Pentagram's Isomorphic Labs](https://www.pentagram.com/work/isomorphic-labs) | Reconfiguring cubes, typography, gradients, and grid derived from a digital/biological relationship. | One meaningful concept can organize an entire visual and motion system. Gradients and 3D remain valid when they express that concept. |
| [Teenage Engineering's Field System](https://teenage.engineering/products/field-system) | Actual devices, their connections, detailed capabilities, and concrete use cases. | Make the product itself fascinating through close imagery and useful detail. Give photography a clear informational role. |
| [Raycast](https://www.raycast.com/) | Keyboard imagery, specific tasks, and product-interface demonstrations. | Build the memorable visual around an object people associate with using the product. |
| [Stripe's accessible color-system process](https://stripe.com/blog/accessible-color-systems) | Systematic control of contrast and perceived color weight, tested in components. | Engineer color relationships, then verify them in the actual dark surfaces and states. |

For a broader reference board, include transport signage, editorial photography, and the existing academy identity alongside software examples. This reduces dependence on a single fashionable website and gives the design material specific to its subject.

**Build the dark system around hierarchy.** Distinguish the page canvas, content surfaces, and elevated overlays. Carbon's dark themes become lighter as surfaces rise; this is a useful model for making depth legible without relying on shadows. Use semantic tokens such as `surface`, `surface-raised`, `text-secondary`, `control-border`, `focus`, and `selected`, so a color's purpose remains clear. [Carbon color structure](https://carbondesignsystem.com/elements/color/overview/)

Start with three surface levels and two main text levels. Separate subtle decorative dividers from boundaries needed to identify a control. Use chromatic accents selectively, while ensuring the brand is visible beyond a single button. Glass can clarify a floating overlay; applying translucent panels everywhere makes background-dependent contrast harder to control. This is a proposed design direction, not a requirement to use a particular shade of black.

Dark should be the initial default requested in this brief. Offer a persistent explicit light/system choice where appropriate, and respect that choice on subsequent visits. Define the precedence clearly: saved selection first; otherwise dark. If someone selects system, follow the operating-system preference. Set the theme before first paint and style native controls consistently. Dark appearance also needs deliberate image and icon handling; changing the background alone is insufficient. [Apple dark-mode guidance](https://developer.apple.com/design/human-interface-guidelines/dark-mode)

Normal text needs at least 4.5:1 contrast under WCAG AA; qualifying large text needs 3:1. Large means at least 24 CSS pixels regular or approximately 18.67 CSS pixels bold. Thin letterforms can remain difficult even when their specified colors pass. Use stronger weights and comfortable sizes for dark UI labels. [W3C text contrast](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html)

Required visual information that identifies controls and their states generally needs 3:1 against adjacent colors. This does not mean every card border must meet 3:1. Test focus indicators, selected states, chart marks, and essential input boundaries in their actual context. Avoid communicating availability or errors through color alone. [W3C non-text contrast](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast.html)

For typography, a useful prototype starts with 16–18px body text, comfortable line spacing, and distinct heading roles. Use fluid display sizes with sensible bounds, and test Czech diacritics and long names for this project. Keep uppercase tracking for short labels rather than paragraphs. Prefer purposeful line breaks and coherent alignment to filling every empty area. These values are design starting points, not accessibility thresholds.

**Make motion a consistent interaction language.** Use three levels: immediate feedback on controls, spatial continuity during state changes, and occasional expressive sequences. Routine controls should respond as soon as the user acts; the animation can continue after that acknowledgment. A spring should settle without excessive overshoot, and a new interaction should interrupt or retarget the current movement.

| Pattern | Proposed behavior and starting timing | Reduced-motion / fallback behavior |
| --- | --- | --- |
| Tactile press | Immediate state feedback; approximately 1–2% compression over 80–120ms, release over 120–180ms. Keep the hit area stable. | Change surface, border, or icon without scaling. |
| Traveling selection marker | Move an underline or selection surface between related choices over 160–240ms. Update semantic selection immediately. | Show the selected marker in its new position immediately. |
| Shared element between list and detail | Carry one selected image or object identity into the next view over 220–340ms. Other content stays quiet. | Ordinary navigation or a brief fade; preserve history and focus. |
| Filter/reorder continuity | Move existing results to their new positions over 180–280ms using stable IDs. Avoid a long stagger. | Instant update, stable input focus, and an appropriate result-count announcement. |
| Contextual disclosure | Open supporting information near its trigger over 180–260ms; close over 140–200ms. | Show/hide directly; retain explicit open/close controls. |
| Signature SVG line | Trace a meaningful connection once over 350–550ms, then leave the final shape visible. | Show the complete shape. Any route-like graphic must represent real data or be clearly decorative. |
| Meaningful data update | Briefly highlight a value that actually changed over 160–240ms; retain stable numeric space. | Update the true value directly. Never count through fictitious intermediate values. |
| Short editorial entrance | Introduce a title and illustration as a composition over 350–500ms, with small offsets between a few elements. | Render the final composition immediately; content and actions never depend on the entrance. |
| Scroll-linked explanation | Connect a diagram to two or three explanatory steps using ordinary scroll position. | Show the diagram and steps in normal reading order. |
| Optional image or 3D inspection | Let users deliberately explore an object; keep controls visible and movement bounded. | Static image plus equivalent information and ordinary controls. |

For the signature effect, choose a product-specific action: a stop photograph expanding into its detail view, a selected relationship becoming visible, or a useful comparison responding to input. An effect earns its place when the movement helps someone recognize what changed or understand what they are exploring.

Native View Transitions can support continuity between views, but support for one API does not imply support for every related feature. Feature-detect `document.startViewTransition`, keep navigation functional without it, and handle cancelled or rapid navigation. Avoid scaling long text blocks as images if it produces blurry or distorted typography. [MDN View Transitions](https://developer.mozilla.org/en-US/docs/Web/API/Document/startViewTransition)

Scroll-driven animation is appropriate for an explanation that benefits from a relationship between text and graphics. Preserve native scrolling and a readable static version. Feature-detect the specific timeline capabilities in use; compositor-friendly animation properties still matter. Avoid making a visitor traverse a pinned scene to reach a routine task. [Chrome scroll-driven animations](https://developer.chrome.com/docs/css-ui/scroll-driven-animations)

More elaborate options—image masks, variable-font animation, pointer-responsive lighting, and WebGL scenes—are conditional. Use masks on imagery without masking access to text; confine variable-font effects to short display content; keep pointer lighting off essential information; provide a useful static poster for 3D. Prototype one effect and profile it before expanding it. Large animated blur and paint-heavy surfaces can be expensive. Prefer transform and opacity for frequent motion, and use `will-change` only when measurement justifies it. [web.dev animation performance](https://web.dev/articles/animations-guide)

Treat reduced motion as a designed variant: remove travel, parallax, scale, and nonessential loops while retaining information and feedback. WCAG's interaction-animation criterion is AAA; supporting this preference remains a product requirement proposed here. Automatically moving content lasting more than five seconds alongside other content can require pause/stop/hide controls under Level A; auto-updating content has a separate clause. [W3C interaction animation](https://www.w3.org/WAI/WCAG22/Understanding/animation-from-interactions.html), [W3C pause/stop/hide](https://www.w3.org/WAI/WCAG22/Understanding/pause-stop-hide.html)

**Advanced UX should make the product easier to operate.** The following patterns add depth beyond visual effects. Their value depends on actual content and user tasks.

| Pattern | Behavior worth designing | Common failure to prevent |
| --- | --- | --- |
| Progressive disclosure | Show the information needed for the current decision; reveal supporting details in context. | Hiding the primary action or essential facts to achieve a sparse screenshot. |
| Search with visible refinements | Keep the query editable, expose active filters, explain result counts, and provide a clear reset. | Invisible filter state or unexpectedly losing the query after visiting a detail page. |
| URL-backed view state | Preserve meaningful search/filter state in the URL; make browser Back restore a useful context. | Treating navigation as animation only and losing history, scroll position, or selection. |
| Optional command palette | Give frequent users fast actions with searchable labels and visible conventional alternatives. | Adding a shortcut system to a tiny app with no real action complexity. |
| Contextual detail panel | Support quick comparison on wide screens; offer a focused detail route or sheet on narrow screens. | Nested modals, broken direct links, or a panel that obscures the list's useful content. |
| Reversible immediate actions | Reflect low-risk actions promptly; expose pending, failure, rollback, and undo where meaningful. | Showing an irreversible operation as successful before the server confirms it. |
| Stable progressive loading | Reserve image dimensions, keep existing useful results during refresh, distinguish empty from loading and failure. | Endless shimmering skeletons, invented progress percentages, or a blank screen on every refresh. |
| Designed errors and empty states | Preserve entered information, explain what happened, and offer the next useful action. | Generic apologetic copy with no recovery or an error styled as a decorative card. |

Autocomplete requires coherent keyboard behavior, not just a visually positioned dropdown. A modal needs sensible initial focus, contained keyboard navigation, Escape handling, and focus return. Use the relevant established pattern and test it rather than combining arbitrary ARIA attributes. [W3C combobox pattern](https://www.w3.org/WAI/ARIA/apg/patterns/combobox/), [W3C modal-dialog pattern](https://www.w3.org/WAI/ARIA/apg/patterns/dialog-modal/)

Keep touch controls comfortably targetable; 44–48 CSS pixels is a useful design target. WCAG 2.2 AA specifies 24×24 CSS pixels or its documented exceptions, so do not present 44px as the universal AA minimum. Sticky bars should leave keyboard focus visible. Any custom drag interaction needs an appropriate non-drag pointer alternative in addition to keyboard operation. [W3C target size](https://www.w3.org/WAI/WCAG22/Understanding/target-size-minimum.html), [W3C focus not obscured](https://www.w3.org/WAI/WCAG22/Understanding/focus-not-obscured-minimum.html), [W3C dragging movements](https://www.w3.org/WAI/WCAG22/Understanding/dragging-movements.html)

**Apply the research to Think different Academy / Transit.** The following findings come from static inspection of this repository, rather than a running-browser audit. The [brand guide](../brand/README.md) requires Dosis, recognizable green and blue, official logo assets, and the existing Svelte/Tailwind/shadcn-svelte stack. These provide useful specificity for a distinctive interface.

| Current finding | Consequence for the design |
| --- | --- |
| [The HTML document](../../frontend/src/app.html) has no dark class; [the theme](../../frontend/src/app.css) defines a white root and separate `.dark` overrides. | Dark tokens exist, but the initial source-defined appearance is light. A later implementation must establish dark before first paint and persist explicit preferences. |
| Dosis and brand colors are already installed. | Build on them through weight, spacing, imagery, and composition. No generic font swap is necessary. |
| [The stop list](../../frontend/src/lib/features/stops/StopsPage.svelte) already keys results by stop ID. | There is a suitable identity foundation for deliberate filtering/reordering motion if those features are added. |
| List/detail views use real stop images and amenity data. | Make photographs, names, and useful facilities the dominant content. A shared image transition is a plausible signature. |
| Current public stop responses expose names, images, and three facility/accessibility booleans. | Route traces, live departures, distance sorting, and live vehicle motion require additional verified data and feature work. Do not imply that these already exist. |
| CSS includes a global reduced-motion rule. | It is a useful baseline, but future JavaScript, scroll-driven, or canvas motion still needs explicit reduced-motion behavior. |

The local set of 15 stop PNGs totals approximately 36.84 MiB, and the image component uses the source without responsive `srcset` variants. These are filesystem observations, not measured transfer sizes or page-load metrics. Before adding elaborate image motion, prepare appropriately sized derivatives and preserve the existing reserved image proportions, eager/lazy loading decisions, and fallback behavior. Avoid modifying immutable migration seed assets; generate new delivery assets separately.

Contrast calculations below use the WCAG sRGB relative-luminance formula with fully opaque token colors. Ratios are rounded for display; assessments used unrounded values. Runtime overlays, opacity, and states still need separate checking.

| Pair from the brand/theme | Calculated ratio | Interpretation |
| --- | --- | --- |
| Brand green `#91F5AD` / dark `#1A1A1A` | 13.15:1 | Strong text pairing; also suitable as a bright action fill with dark text. |
| Brand blue `#0070BB` / dark `#1A1A1A` | 3.34:1 | Fails AA for normal text. Keep this blue in appropriate larger graphics or filled components; choose a tested lighter link token for small text. |
| White `#FFFFFF` / brand blue `#0070BB` | 5.20:1 | Passes AA for normal text. A blue filled region can retain brand presence. |
| Border `#4B6373` / dark `#1A1A1A` | 2.76:1 | Insufficient if this is a required identifying control boundary. A decorative separator can be subtler. |
| Border `#4B6373` / card `#242F37` | 2.17:1 | Same contextual limitation; separate decorative borders from essential control borders. |

Three directions worth comparing with the same real content:

| Direction | Visual composition | Signature motion | Fit |
| --- | --- | --- | --- |
| **Editorial transit** | Dark canvas, confident Dosis headings, prominent real photographs, blue information areas, green actions, a recurring line/stop motif. | Selected image connects the list to detail. | Recommended starting point: strong identity with a clear browsing task. |
| Compact transit utility | More rows, smaller imagery, high information density, persistent search and filters. | Selection and filtering remain spatially understandable. | Better if observation shows frequent, time-sensitive lookup. Requires the corresponding features. |
| Expressive academy showcase | Larger branded illustration, varied composition, short explanatory sequences. | A small SVG story or illustrated response to user input. | Useful for a separate introduction or educational page; keep the stop directory immediately usable. |

For the recommended direction, the first viewport should show where users are and offer a useful path to stops. A later search/filter feature could sit directly above the existing list. On desktop, an editorial introduction can sit beside a genuine stop photograph; on mobile, retain the task and a compact visual. Use a single shared image transition when opening a stop, then display its facilities plainly. Preserve missing-image, loading, empty, and retry states with the same care as the ideal screen.

The homepage currently emphasizes API health and team metadata. Before redesigning it, check the repository's documented health/smoke contract and retain any required information. The intended public-facing hierarchy should emphasize the product's actual purpose while presenting technical status only where it helps.

**Use the existing stack deliberately.** Start with CSS transitions and Svelte's built-in primitives. `fade`, `fly`, `crossfade`, and SVG `draw` cover many small transitions. `animate:flip` is intended for index changes in keyed each blocks; additions/removals need their own transitions. [Svelte transitions](https://svelte.dev/docs/svelte/svelte-transition), [Svelte animation directive](https://svelte.dev/docs/svelte/animate)

Svelte also provides `Spring`, `Tween`, and `prefersReducedMotion`. Use springs where an interaction can retarget quickly, and explicitly choose the static state under reduced motion. For route changes, SvelteKit documents invoking View Transitions through `onNavigate`; a slowly resolved promise can stall navigation. Preserve its focus management and route announcements. [Svelte motion](https://svelte.dev/docs/svelte/svelte-motion), [SvelteKit View Transitions](https://svelte.dev/docs/kit/faq#How-do-I-use-the-view-transitions-API), [SvelteKit accessibility](https://svelte.dev/docs/kit/accessibility)

Add GSAP only when a specific timeline or scroll sequence needs its capabilities. Its `matchMedia` utility can collect and revert animations across breakpoint and reduced-motion conditions. Keep setup and cleanup tied to component lifetime. One library should own a given animated property so transitions do not fight. [GSAP matchMedia](https://gsap.com/docs/v3/GSAP/gsap.matchMedia()/)

**Review quality in use.** Proposed implementation order: establish the dark default and readable tokens; refine real content and hierarchy; complete interaction states; add control feedback and one shared transition; evaluate an expressive effect only after the core experience works well.

1. Compare at least two structurally different compositions using identical content. Explain the tradeoff in task clarity and identity before choosing one.
2. Remove the logo temporarily. Assess whether typography, imagery, color roles, and composition still suggest a specific product.
3. Inspect the page with motion disabled. The resting composition must remain attractive and understandable.
4. Test narrow screens, long Czech stop names, zoom/reflow, touch input, keyboard operation, and visible focus. Inspect missing-image, no-results, retry, and slow-request states.
5. Repeatedly open/close and navigate during animation. Back, direct links, scroll restoration, and input focus must remain predictable.
6. Profile the actual effect on a representative midrange phone. Investigate long tasks, dropped frames, broad paint regions, unnecessary animation offscreen, and resource use when idle.
7. Track LCP ≤2.5s, INP ≤200ms, and CLS ≤0.1 at the 75th percentile, separated by mobile and desktop. These are established Web Vitals targets; a Lighthouse load test alone cannot establish field INP. [web.dev Web Vitals](https://web.dev/articles/vitals)
8. Run a small formative study with representative users: ask them to find a stop, inspect facilities, and return to their prior context. Record completion, hesitation, wrong turns, and whether motion helped them understand the change. A five-second impression exercise can reveal perceived purpose, but cannot replace task testing.

Measure attractiveness through both first impressions and repeat use. Record perceived clarity and memorability alongside task success. Keep effects that contribute something observable; revise effects that repeatedly draw attention away from the user's intended action.
