# Bloodborne Mod (Fabric 1.21.1)

A Fabric mod that brings Bloodborne-style hunting to Minecraft 1.21.1: trick weapons, hunter gear, a firearm, permanent night, and Yharnam-inspired visuals for worlds you build (e.g. with WorldEdit).

## What the mod includes

| Category | Content |
|----------|---------|
| **Trick weapons** | Saw Cleaver, Hunter Axe, Threaded Cane (melee, tuned damage/speed) |
| **Firearm** | Hunter Pistol (offhand, **G** to fire, ammo bar) |
| **Gear** | Hunter armor set (4 pieces), Hunter's Lantern |
| **Atmosphere** | Permanent night; dark foliage and water; join fade + "Central Yharnam" title |
| **World look** | Grass and related blocks render as deepslate-style cobble/brick (no block replacement in the world) |
| **Mobs** | Slimes are removed |
| **Creative** | Vanilla swords and armor hidden from Combat tab; Bloodborne items added instead |

---

## Quick install (play with the built JAR)

1. Install **Minecraft 1.21.1**.
2. Install **Fabric Loader** for 1.21.1 from [fabricmc.net](https://fabricmc.net/use/).
3. Download **Fabric API** for 1.21.1 from [Modrinth](https://modrinth.com/mod/fabric-api) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api).
4. Build the mod (see below) or use a release JAR if you have one.
5. Copy into your Minecraft **mods** folder:
   - `fabric-api-….jar`
   - `bloodborne-mod-1.0.0.jar` (from `build/libs/` after `.\gradlew.bat build`)
6. Launch the **Fabric 1.21.1** profile and create or open a world.

Cheats/commands are optional but useful for testing (`/give`, see below).

---

## Quick start (development)

**Requirements:** Java **21** (JDK 21+). You do not need the Minecraft Launcher for Gradle runs.

From the project root:

```bat
.\gradlew.bat runClient
```

First run downloads Minecraft and dependencies; later runs are faster.

**Reload textures/models in-game:** **F3+T**  
**After changing Java** (`ModItems.java`, damage, new item classes): restart `runClient` (F3+T is not enough).

---

## Getting items in-game

### Creative mode

Open inventory (**E**) → **Combat** tab. Bloodborne weapons, pistol, lantern, and armor are listed there. Vanilla swords and armor tiers are hidden in that tab only (loot tables can still use vanilla gear until you change them).

### Commands

```mcfunction
/give @s bloodborne:saw_cleaver
/give @s bloodborne:hunter_axe
/give @s bloodborne:threaded_cane
/give @s bloodborne:hunter_pistol
/give @s bloodborne:hunter_lantern
/give @s bloodborne:hunter_hat
/give @s bloodborne:hunter_garb
/give @s bloodborne:hunter_trousers
/give @s bloodborne:hunter_boots
```

Enable cheats in single-player, or use a server/world that allows commands.

### Hunter Pistol

- Hold the pistol in your **offhand**.
- Press **G** (keybind: *Fire Hunter Pistol*, category *Bloodborne*).
- Uses quicksilver-style ammo (durability bar on the item).

---

## Adding a new trick weapon

A standard melee weapon needs **Java registration**, **creative tab entry**, **assets**, and a **display name**. It is not only `ModItems` + JSON + PNG, but those are the main pieces.

### Checklist

| Step | File / location | What to do |
|------|-----------------|------------|
| 1 | `src/main/java/.../ModItems.java` | Register the item with `BloodborneWeaponItem`, set damage and attack speed, call `register("your_id", …)`. Use the same `snake_case` id everywhere. |
| 2 | `src/main/java/.../ModCreativeTabs.java` | Add `entries.accept(ModItems.YOUR_ITEM);` in the Combat tab handler so it appears in creative. |
| 3 | `src/main/resources/assets/bloodborne/lang/en_us.json` | Add `"item.bloodborne.your_id": "Display Name"`. |
| 4 | `assets/bloodborne/textures/item/your_id.png` | **16×16** inventory texture (PNG). |
| 5 | `assets/bloodborne/models/item/your_id.json` | Item model (usually `handheld` parent pointing at your texture). |
| 6 | `assets/bloodborne/items/your_id.json` | Links the item to the model (1.21+ item definition format). |
| 7 | — | Restart the dev client if you changed Java; use **F3+T** if you only changed assets. |

### Example: copy an existing weapon

Use **Saw Cleaver** as a template:

- **Java:** duplicate the `SAW_CLEAVER` block in `ModItems.java` (new constants + `register("ludwigs_holy_blade", …)`).
- **Model:** copy `models/item/saw_cleaver.json`, change `layer0` to `bloodborne:item/ludwigs_holy_blade`.
- **Item definition:** copy `items/saw_cleaver.json`, change the model path to `bloodborne:item/ludwigs_holy_blade`.
- **Texture:** add `textures/item/ludwigs_holy_blade.png`.
- **Lang:** add the translation key `item.bloodborne.ludwigs_holy_blade`.

In-game id: `bloodborne:ludwigs_holy_blade` (namespace `bloodborne`, path = register name).

### Special items (not plain swords)

| If the weapon should… | Use |
|------------------------|-----|
| Behave like a normal melee trick weapon | `BloodborneWeaponItem` + `SwordItem.createAttributes(...)` (same as existing three weapons) |
| Shoot, use ammo, custom keybind | Custom item class (see `HunterPistolItem` + client/network code) |
| Emit light, custom use action | Custom item class (see `HunterLanternItem`) |

For a first weapon, stick to `BloodborneWeaponItem` until you need extra behavior.

### Asset file reference (one weapon)

```text
src/main/resources/assets/bloodborne/
  textures/item/your_id.png          ← pixel art
  models/item/your_id.json           ← handheld model
  items/your_id.json                 ← item → model link
  lang/en_us.json                    ← display name
```

**Model** (`models/item/your_id.json`):

```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "bloodborne:item/your_id"
  }
}
```

**Item definition** (`items/your_id.json`):

```json
{
  "model": {
    "type": "minecraft:model",
    "model": "bloodborne:item/your_id"
  }
}
```

Until the PNG exists, the item shows the purple missing-texture checkerboard.

---

## Attack damage (trick weapons)

On **1.21.1**, damage is set on `Item.Properties` via `SwordItem.createAttributes` in `ModItems.java`:

| Weapon | Damage modifier | Attack speed |
|--------|-----------------|--------------|
| Saw Cleaver | 8 | -2.8 |
| Hunter Axe | 5 | -2.4 |
| Threaded Cane | 4 | -1.8 |

Shared material: `WEAPON_MATERIAL` (`Tiers.IRON`) — affects durability, enchantability, repair.

**Shown attack damage** ≈ `1 + DAMAGE_MODIFIER + material bonus` (iron bonus is +2).

Examples with iron:

| Damage modifier | ≈ Total damage |
|-----------------|----------------|
| 3 | 6 (like iron sword) |
| 8 | 11 (Saw Cleaver) |

After changing Java constants, **restart** the game; F3+T does not reload code.

---

## Texture assets (weapons & armor)

Put PNGs under `src/main/resources/assets/bloodborne/`. Reload with **F3+T** after image-only changes.

### Trick weapons (16×16 item icons)

| Weapon | File |
|--------|------|
| Saw Cleaver | `textures/item/saw_cleaver.png` |
| Hunter Axe | `textures/item/hunter_axe.png` |
| Threaded Cane | `textures/item/threaded_cane.png` |
| Hunter Pistol | `textures/item/hunter_pistol.png` |
| Hunter's Lantern | `textures/item/hunter_lantern.png` |

### Hunter armor — item icons (16×16)

| Piece | File |
|-------|------|
| Hunter Hat | `textures/item/hunter_hat.png` |
| Hunter Garb | `textures/item/hunter_garb.png` |
| Hunter Trousers | `textures/item/hunter_trousers.png` |
| Hunter Boots | `textures/item/hunter_boots.png` |

### Hunter armor — worn on the player (64×32)

| Layer | File |
|-------|------|
| Outer (hat, chest, boots) | `textures/models/armor/hunter_layer_1.png` |
| Inner (leggings) | `textures/models/armor/hunter_layer_2.png` |

Copy vanilla `leather_layer_1.png` / `leather_layer_2.png` as a starting point if needed.

---

## Yharnam terrain (visual overrides)

The mod overrides **vanilla block models** under `src/main/resources/assets/minecraft/models/block/` so existing worlds look darker and stonier without replacing blocks in the save:

| Block | Appearance |
|-------|------------|
| Grass block (incl. snowy) | Deepslate bricks |
| Podzol, mycelium | Deepslate bricks |
| Dirt path | Bricks on top/sides, cobbled deepslate below |
| Short grass, tall grass, fern, large fern | Cobbled deepslate (cross models) |

Reload with **F3+T** after editing JSON. Block **behavior** stays vanilla (drops, tools, etc.); only **rendering** changes.

Custom art: add PNGs under `assets/bloodborne/textures/block/` and point model `textures` at `bloodborne:block/your_texture`.

---

## Build the mod JAR

```bat
.\gradlew.bat build
```

Output: `build/libs/bloodborne-mod-1.0.0.jar` (version from `gradle.properties`).

---

## Project layout

| Path | Purpose |
|------|---------|
| `src/main/java/.../ModItems.java` | Registers items |
| `src/main/java/.../ModCreativeTabs.java` | Creative inventory entries |
| `src/main/java/.../BloodborneMod.java` | Server init (night, networking, etc.) |
| `src/client/java/.../BloodborneModClient.java` | Client intro, colors, HUD |
| `src/main/resources/assets/bloodborne/` | Textures, models, items, lang |
| `src/main/resources/assets/minecraft/` | Vanilla overrides (terrain, splashes) |

---

## WorldEdit (optional)

For building Central Yharnam-style areas:

1. Download WorldEdit for **Fabric 1.21.1**: [Modrinth](https://modrinth.com/plugin/worldedit/versions?l=fabric&g=1.21.1)
2. Place the WorldEdit JAR in `run/mods/` when using `runClient`, or in your normal `mods` folder when playing from a built install.

---

## IDE

Open the folder as a **Gradle** project in IntelliJ IDEA or VS Code. Run **Minecraft Client** or `.\gradlew.bat runClient`. Run `.\gradlew.bat genSources` if you need decompiled Minecraft sources for navigation.
