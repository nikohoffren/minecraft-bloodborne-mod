# Bloodborne Mod (Fabric 1.21.1)

A Fabric Bloodborne mod for Minecraft 1.21.1.

## Requirements

- **Java 21** (JDK 21 or newer)
- You do **not** need the Minecraft Launcher for development

## Run Minecraft with the mod (dev client)

From this folder:

```bat
.\gradlew.bat runClient
```

## Texture assets (weapons & armor)

Put PNGs under `src/main/resources/assets/bloodborne/` (not in `src/main/java`). Reload with **F3+T** after adding or changing images.

### Trick weapons (inventory icon, 16×16 PNG)

| Weapon | File |
|--------|------|
| Saw Cleaver | `textures/item/saw_cleaver.png` |
| Hunter Axe | `textures/item/hunter_axe.png` |
| Threaded Cane | `textures/item/threaded_cane.png` |
| Vesipullo | `textures/item/vesipullo.png` |
| Hunter's Lantern | `textures/item/hunter_lantern.png` |

### Hunter armor — item icons (16×16 PNG)

| Piece | File |
|-------|------|
| Hunter Hat | `textures/item/hunter_hat.png` |
| Hunter Garb | `textures/item/hunter_garb.png` |
| Hunter Trousers | `textures/item/hunter_trousers.png` |
| Hunter Boots | `textures/item/hunter_boots.png` |

### Hunter armor — worn on the player (64×32 PNG, two layers)

These are drawn on the player model (not the inventory icon). On Minecraft 1.21.1 they use the classic armor layer files:

| Layer | File |
|-------|------|
| Outer (hat, chest, boots) | `textures/models/armor/hunter_layer_1.png` |
| Inner (leggings) | `textures/models/armor/hunter_layer_2.png` |

You can copy vanilla `leather_layer_1.png` / `leather_layer_2.png` as a starting template and recolor them.

Until PNGs exist, items show the purple missing-texture checkerboard.

## Custom weapon texture (legacy note)

Put your texture in the **resources** tree (not in `src/main/java`). Minecraft item textures are **16×16 PNG** files.

**Example:**

```text
src/main/resources/assets/bloodborne/textures/item/vesipullo.png
```

| File | Role |
|------|------|
| `textures/item/vesipullo.png` | The pixel art (your image) |
| `models/item/vesipullo.json` | Tells the game to use that texture on a handheld item |
| `items/vesipullo.json` | Links the item to the model (1.21+ client item format) |
| `lang/en_us.json` | In-game display name |

After adding or changing the PNG, reload assets in-game with **F3+T**, or restart `runClient`.

Until `vesipullo.png` exists, the item may show a missing-texture (purple/black) checkerboard.

## Getting vesipullo in-game

Trick weapons: `bloodborne:saw_cleaver`, `bloodborne:hunter_axe`, `bloodborne:threaded_cane`

Hunter armor: `bloodborne:hunter_hat`, `bloodborne:hunter_garb`, `bloodborne:hunter_trousers`, `bloodborne:hunter_boots`

Other: `bloodborne:vesipullo`, `bloodborne:hunter_lantern`

Vanilla swords and armor tiers are hidden from the **Combat** and **Armor** creative tabs; use the Bloodborne items instead (loot tables and mob drops still use vanilla gear until you change those later).

### Creative mode

1. Open the creative inventory (`E`).
2. Open the **Combat** tab.
3. Find **vesipullo**.

### Commands

```mcfunction
/give @s bloodborne:vesipullo
```

Cheats must be enabled in single-player (or use a commands-enabled world).

## Attack damage

On Minecraft **1.21.1**, damage is set on **`Item.Properties`**, not on a custom tier class. This matches how vanilla registers swords:

```java
new SwordItem(
    Tiers.IRON,
    new Item.Properties().attributes(
        SwordItem.createAttributes(Tiers.IRON, ATTACK_DAMAGE_MODIFIER, ATTACK_SPEED)
    )
)
```

Edit these constants in `ModItems.java`:

| Constant | Role |
|----------|------|
| `MATERIAL` | Iron/diamond/etc. — durability, enchantability, repair |
| `ATTACK_DAMAGE_MODIFIER` | Damage tuning (default `997` ≈ 1000 with iron) |
| `ATTACK_SPEED` | Swing speed (vanilla swords: `-2.4F`) |

**Formula:** shown attack damage ≈ `1 + ATTACK_DAMAGE_MODIFIER + MATERIAL.getAttackDamageBonus()`

Examples with iron material (+2 bonus):

| `ATTACK_DAMAGE_MODIFIER` | Approx. damage |
|--------------------------|----------------|
| 3 | 6 (like iron sword) |
| 997 | 1000 |
| 1997 | 2000 |

After changing values, restart the game (`.\gradlew runClient`). Reloading with F3+T is not enough for Java constants.

**Note:** Newer Minecraft versions (and current Fabric docs) use `new Item.Properties().sword(material, damage, speed)` instead. That API is **not** available on 1.21.1 — use `SwordItem.createAttributes` as above.

## Build the mod JAR

```bat
.\gradlew.bat build
```

Output: `build/libs/bloodborne-mod-1.0.0.jar`

## Project layout

| Path | Purpose |
|------|---------|
| `src/main/java/.../ModItems.java` | Registers items |
| `src/main/resources/assets/bloodborne/textures/item/` | **Your PNG goes here** |
| `src/main/resources/assets/bloodborne/models/item/` | Item model JSON |
| `src/main/resources/assets/bloodborne/items/` | Client item JSON |
| `src/main/resources/assets/bloodborne/lang/` | Display name |

## IDE

Open as a Gradle project in IntelliJ IDEA or VS Code. Use the **Minecraft Client** run configuration, or `gradlew genSources` if needed.

## WorldEdit

Download the correct WorldEdit mod from
- https://modrinth.com/plugin/worldedit/versions?l=fabric&g=1.21.1

Drop the WorldEdit mod .jar file to the `/run/mods` directory.

Added WorldEdit mod to easily construct buildings etc. to make the game look like Central Yharnam
