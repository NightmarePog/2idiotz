# Skills

Give your AI assistant deep knowledge of shadcn-svelte components, patterns, and best practices.

### [Epicenter](https://github.com/EpicenterHQ/epicenter)

[Local-first, open source apps](https://github.com/EpicenterHQ/epicenter)

[Special Sponsor](https://github.com/EpicenterHQ/epicenter)

Skills give AI assistants like Claude Code project-aware context about shadcn-svelte. When installed, your AI assistant knows how to find, install, compose, and customize components using the correct APIs and patterns for your project.

For example, you can ask your AI assistant to:

- *"Add a login form with email and password fields."*  
- *"Create a settings page with a form for updating profile information."*  
- *"Build a dashboard with a sidebar, stats cards, and a data table."* The skill reads your project's `components.json` and provides the assistant with your framework, aliases, installed components, icon library, and base library so it can generate correct code on the first try.

***

## [Install](#install)

```bash
pnpm dlx skills add huntabyte/shadcn-svelte
```

```bash
npx skills add huntabyte/shadcn-svelte
```

```bash
bun x skills add huntabyte/shadcn-svelte
```

This installs the shadcn-svelte skill into your project. Once installed, your AI assistant automatically loads it when working with shadcn-svelte components.

Learn more about skills at [skills.sh](https://skills.sh).

***

## [What's Included](#whats-included)

The skill provides your AI assistant with the following knowledge:

### [Project Context](#project-context)

On every interaction, the skill reads your `components.json` to get your project's configuration: framework (SvelteKit, Vite, Astro), Tailwind version, aliases, installed components, icon library, and resolved file paths.

### [CLI Commands](#cli-commands)

Full reference for the shadcn-svelte CLI commands: `init`, `add`, and `registry build`. Includes flags and smart merge workflows.

### [Theming and Customization](#theming-and-customization)

How CSS variables, OKLCH colors, dark mode, custom colors, border radius, and component variants work. Includes guidance for both Tailwind v3 and v4.

### [Registry Authoring](#registry-authoring)

How to build and publish custom component registries: `registry.json` format, item types, file objects, dependencies, CSS variables, building, and hosting.

***

## [How It Works](#how-it-works)

1. 
   **Project detection**
    The skill activates when it finds a `components.json` file in your project.
2. 
   **Context injection**
    It reads your project configuration and injects it into the assistant's context.
3. 
   **Pattern enforcement**
    The assistant follows shadcn-svelte composition rules: using `Field.Group` for forms, namespace imports, Svelte 5 runes, and correct Bits UI APIs.
4. 
   **Component discovery**
    The assistant checks installed components and local docs before generating code.

## [Learn More](#learn-more)

- [CLI](https://shadcn-svelte.com/docs/cli)  Full CLI command reference
- [Theming](https://shadcn-svelte.com/docs/theming)  CSS variables and customization
- [Registry](https://shadcn-svelte.com/docs/registry)  Building and publishing custom registries
- [skills.sh](https://skills.sh)  Learn more about AI skills