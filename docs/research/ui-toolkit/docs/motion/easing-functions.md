# Easing functions

Motion's built-in easing functions and modifiers, from cubicBezier to anticipate.

Easing functions change the speed of an animation over the course of its duration. They're the difference between an animation that feels snappy and one that feels sluggish, and Motion has a full set built in: `easeInOut`, `backOut`, `circIn`, `anticipate`, `steps`, and custom `cubicBezier` curves. Easing is the curve half of a [tween](https://motion.dev/docs/tween): the duration says how long, the easing says how the movement is distributed across it.

Both the `animate`[ function](https://motion.dev/docs/animate) and Motion for React's `motion`[ component](https://motion.dev/docs/react-motion-component) have the following easing functions built-in and you can just refer to them by name.

```
// Named easing
ease: "easeIn"

// Bezier curve
ease: [0.39, 0.24, 0.3, 1]
```

But you can still import them separately to use them directly, either for use with the tiny `animate` function from `"motion/dom"` or for novel use-cases.

## Usage

All of Motion's easing functions can be imported straight from `"motion"`:

```
import { easeIn } from "motion"
```

By passing a `0`-`1` progress value to these functions, you'll receive an eased progress back.

```
const eased = easeIn(0.75)
```

## Functions

Motion provides a number of built-in easing functions:

- `cubicBezier`

- `easeIn`/`easeOut`/`easeInOut`

- `backIn`/`backOut`/`backInOut`

- `circIn`/`circOut`/`circInOut`

- `anticipate`

- `linear`

- `steps`

### `cubicBezier`

`cubicBezier` provides very precise control over the easing curve.

```
import { cubicBezier } from "motion"

const easing = cubicBezier(.35,.17,.3,.86)

const easedProgress = easing(0.5)
```

New easing curve definitions can be generated with the [Motion AI Kit](https://motion.dev/docs/ai-kit).

### `steps`

`steps` creates an easing with evenly-spaced, discrete steps. It is spec-compliant with [CSS ](https://developer.mozilla.org/en-US/docs/Web/CSS/easing-function#step-easing-function)`steps`[ easing](https://developer.mozilla.org/en-US/docs/Web/CSS/easing-function#step-easing-function).

```
import { steps } from "motion"

const easing = steps(4)

easing(0.2) // 0
easing(0.25) // 0.25
```

By default, the "steps" change at the end of a step, this can be changed by passing `"start"` to `steps`:

```
const easing = steps(4, "start")

easing(0.2) // 0.25
```

The distribution of steps is linear by default but can be adjusted by passing `progress` through another easing function first.

```
const easing = steps(4)

easing(circInOut(0.2))
```

## Modifiers

Easing modifiers can be used to create mirrored and reversed easing functions.

### `reverseEasing`

`reverseEasing` accepts an easing function and returns a new one that reverses it. For instance, an ease in function will become an ease out function.

```
import { reverseEasing } from "motion"

const powerIn = (progress) => progress * progress

const powerOut = reverseEasing(powerIn)
```

### `mirrorEasing`

`mirrorEasing` accepts an easing function and returns a new one that mirrors it. For instance, an ease in function will become an ease in-out function.

```
import { mirrorEasing } from "motion"

const powerIn = (progress) => progress * progress

const powerInOut = mirrorEasing(powerInOut)
```
