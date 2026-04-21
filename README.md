# Create: Gravitated

![Create: Gravitated icon](./aeronautics-addon/neoforge/src/main/resources/create_gravitated.png)

Create: Gravitated is a NeoForge addon for Create Aeronautics that adds control blocks for easier ship building.

It is aimed at the "I want my contraption to behave" part of the experience:

- `Gravitite` cancels gravity for the contraption it is attached to.
- `Stabilite` levels a contraption toward the horizon without freezing normal motion.
- A steering wheel placed on top of Stabilite can drive ship yaw directly.
- In-game Ponder scenes explain Gravitite, Stabilite, steering wheel yaw, redstone control, and the new crystal behavior more clearly.
- Side tip: Honey Glue range can be configured with `/creategravitated honey_glue_range`.

## Features

### Gravitite

- Zero-gravity support for Create Aeronautics contraptions
- Redstone-controlled strength
- Obtainable by crystallizing Levitite Blend with shulker boxes on the four horizontal sides and soul fire
- White shimmer particles, ambient hum, and animated blue visuals

### Stabilite

- Horizon stabilization with redstone-scaled rigidity
- Obtainable by crystallizing Levitite Blend with amethyst buds or clusters on the four horizontal sides and normal fire
- Purple visuals and particles
- Helm yaw input when a steering wheel is mounted above the block

## Obtaining the crystals

### Gravitite

1. Place a source block of Levitite Blend.
2. Put shulker boxes on the north, south, east, and west sides of the fluid.
3. Ignite the blend with soul fire, or use a Levitite Soul Catalyst item.

### Stabilite

1. Place a source block of Levitite Blend.
2. Put amethyst buds or amethyst clusters on the north, south, east, and west sides of the fluid.
3. Ignite the blend with normal fire, or use a Levitite Catalyst item.

## Steering wheel controls

- Mount a Steering Wheel on top of Stabilite.
- Right-click the wheel to take control.
- Press `A` and `D` to steer left and right.
- Press `Shift` to stop steering.
- While steering, the player stays held in front of the wheel while the camera remains free.

### Building tweaks

- Configurable Honey Glue range with `/creategravitated honey_glue_range`
- New Ponder tutorials for both addon control blocks
- Gravitite now has a more visible Ponder menu entry under Flight Crystals
- Gravitite and Stabilite no longer visually blend into each other when used side by side
- Tooltip styling and polished presentation assets for the new blocks

## Requirements

- Minecraft `1.21.1`
- NeoForge `21.1.x`
- [Create](https://modrinth.com/mod/create)
- [Create Aeronautics](https://modrinth.com/mod/create-aeronautics)

The release jar is built as an addon. It is not a replacement for Create Aeronautics.

## Building

From the repository root:

```powershell
.\gradlew :aeronautics-addon:neoforge:build
```

Built jars land in:

```text
aeronautics-addon/neoforge/build/libs/
```

## Release

Current release line:

- `1.0.4`

Primary jar name:

- `create-gravitated-neoforge-1.21.1-1.0.4.jar`

## License

This repository uses the Simulated Project license layout:

- code is MIT
- project assets are All Rights Reserved

See [LICENSE.md](./LICENSE.md) for the exact terms.
