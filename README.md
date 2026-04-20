# Create: Gravitated

![Create: Gravitated icon](./aeronautics-addon/neoforge/src/main/resources/create_gravitated.png)

Adds a command to configure honey glue range and changes default range to 64 blocks.

## Command

Use the server command below:

```text
/creategravitated honey_glue_range
/creategravitated honey_glue_range get
/creategravitated honey_glue_range set <blocks>
/creategravitated honey_glue_range reset
```

The configured value is stored in:

```text
config/create-gravitated.properties
```

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

- `1.0.3-gravitated.2`

Primary jar name:

- `create-gravitated-neoforge-1.21.1-1.0.3-gravitated.2.jar`

## License

This repository uses the Simulated Project license layout:

- code is MIT
- project assets are All Rights Reserved

See [LICENSE.md](./LICENSE.md) for the exact terms.
