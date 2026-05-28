# Bloodborne Mod (Fabric 1.21.1)



A Fabric Bloodborne Mod



## Requirements



- **Java 21** (JDK 21 or newer)

- You do **not** need the Minecraft Launcher for development



## Run Minecraft with the mod (dev client)



From this folder:



```bat

.\gradlew.bat runClient

```



## Custom weapon texture



Put your texture in the **resources** tree (not in `src/main/java`). Minecraft item textures are **16×16 PNG** files.



**Add your image here:**



```text

src/main/resources/assets/simple_weapon/textures/item/vesipullo.png

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



Item id: `simple_weapon:vesipullo`



### Creative mode



1. Open the creative inventory (`E`).

2. Open the **Combat** tab.

3. Find **vesipullo**.



### Commands



```mcfunction

/give @s simple_weapon:vesipullo

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



Output: `build/libs/simple-weapon-mod-1.0.0.jar`



## Project layout



| Path | Purpose |

|------|---------|

| `src/main/java/.../ModItems.java` | Registers `vesipullo` |

| `src/main/resources/assets/simple_weapon/textures/item/` | **Your PNG goes here** |

| `src/main/resources/assets/simple_weapon/models/item/` | Item model JSON |

| `src/main/resources/assets/simple_weapon/items/` | Client item JSON |

| `src/main/resources/assets/simple_weapon/lang/` | Display name |



## IDE



Open as a Gradle project in IntelliJ IDEA or VS Code. Use the **Minecraft Client** run configuration, or `gradlew genSources` if needed.

