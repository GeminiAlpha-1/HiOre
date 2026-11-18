# HiOre (OreUI for Fabric)

Bedrock OreUI-style UI library for **Minecraft Java Edition 26.2** on Fabric. Client-only, pre-release.

## Status

- Phase 0: pipeline / 1px / isolation / CJK text / input validated on 26.2.
- Phase 1: seven-module skeleton and build guards (ArchUnit, verifyDocs, Checkstyle, CI) landed.
- Phase 2: complete (`oreui-core` geometry, Snap 1/2/3/Auto, ARGB, easing/Tween, CJK text geometry, pools; 55 core tests).
- Phase 3: complete (`oreui-layout` vendored taffy Flex/Block, dirty two-pass layout, ViewportClamp, Spacer; 30 layout tests).
- Phase 4: complete (`oreui-theme` 9 token enums, OreTheme + derive/override, OreTokens.DARK, 4x5 StateStyle; 19 theme tests).
- Phase 5: complete (`oreui-adapter-262` Uber SDF, render context/state isolation, text/time/resource/sound/clipboard/keybind/input/screen host; smoke screenshot).
- Phase 6: complete (`oreui-components` 10 v1 atoms/forms, ComponentTree/focus/input; component smoke screenshot).
- Phase 7: complete (`oreui-components` 8 composite/navigation/overlay controls, scroll clipping, overlay smoke screenshot).
- Phase 8: complete (`OreItemSlot`/`OreItemGrid`, adapter item submission, `OreUIGalleryScreen` with all 20 v1 controls; gallery screenshots at GUI scale 2 and 3; 24 component tests).
- Phase 9: complete (`dev.oreui.api` facade with `OreCanvas`/`OreScene`, layout budgets within 0.2/0.5ms, Maven local publishing; 34 layout tests).
- Phase 10: complete (v2 controls: SegmentedControl, Accordion, VirtualList, ContextMenu, Keybind, FluidSlot, EntityPreview, PanelFrame; v2 gallery smoke; 35 component tests).

## Requirements

| Item | Version |
| :--- | :--- |
| Minecraft Java Edition | 26.2 |
| Fabric Loader | 0.20.1 |
| Fabric API | 0.161.0+26.2 |
| Fabric Loom | 1.19.4 (`net.fabricmc.fabric-loom`) |
| Java | 25 (toolchain) |

No Yarn or Mojang mappings are used: 26.2 ships unobfuscated, so there is no `mappings` configuration.

## Modules

| Module | Role |
| :--- | :--- |
| `oreui-core` | Pure Java primitives, zero third-party dependencies |
| `oreui-layout` | taffy-java bridge, layout tree, viewport clamp |
| `oreui-theme` | Semantic tokens and built-in dark theme |
| `oreui-components` | Widgets, focus/overlay managers, platform interfaces |
| `oreui-adapter-262` | The only module allowed to reference Minecraft 26.2 / Fabric |
| `oreui-testmod` | Development-only gallery host (split client sources) |
| `oreui-consumer-test` | Compile-time consumer of the published API |

## Quick start

Coordinates (`publishToMavenLocal` publishes these artifacts):

```groovy
repositories {
    mavenLocal()
}

dependencies {
    implementation "dev.oreui:oreui-core:0.1.0-phase1"
    implementation "dev.oreui:oreui-layout:0.1.0-phase1"
    implementation "dev.oreui:oreui-theme:0.1.0-phase1"
    implementation "dev.oreui:oreui-components:0.1.0-phase1"
    implementation "dev.oreui:oreui-adapter-262:0.1.0-phase1"
}
```

Mounting onto an existing screen: the public facade is `dev.oreui.api` (`OreCanvas`/`OreScene`), and `oreui-consumer-test` guards that surface against breaking changes.

```java
OreScene scene = OreCanvas.create(SceneSize.MEDIUM);
scene.label("HiOre");
scene.button("Apply", true, () -> {
});
scene.itemSlot(1, true, 30, 100);
scene.itemGrid(9, 5, 12, 32);
scene.layout();
scene.render();
```

Adapter-level mounting (advanced, used by the adapter and testmod):

```java
Fabric26TextSource textSource = new Fabric26TextSource();
ComponentContext context = new ComponentContext(
        OreTokens.DARK, textSource,
        new Fabric26TextMeasurer(font, textSource, OreTokens.DARK),
        new Fabric26Clipboard(), new Fabric26SoundBridge(), new Fabric26KeybindBridge(),
        new Fabric26TimeSource(), null);

OreSurface root = new OreSurface(context, SurfaceVariant.PANEL);
root.style().width(300f).height(200f);

ComponentTree tree = new ComponentTree(root);
OreFabricScreenHost host = new OreFabricScreenHost(screen, OreTokens.DARK, textSource,
        (ctx, layer, delta) -> {
            if (layer == OreFabricScreenHost.RenderLayer.FOREGROUND) {
                tree.renderBackground(ctx);
                tree.renderForeground(ctx);
            }
        }, event -> tree.dispatch(event), null);
host.attach();
```

`oreui-testmod` renders `OreUIGalleryScreen` (all 20 v1 controls) for hands-on inspection.

## Build

Requires JDK 25. The Gradle 9.8.2 wrapper is included.

```bash
./gradlew --no-daemon --console=plain build
./gradlew --no-daemon --console=plain check
./gradlew --no-daemon --console=plain :oreui-testmod:runClient
```

Headless gallery smoke (screenshot lands in `oreui-testmod/run/screenshots`):

```bash
./gradlew --no-daemon --console=plain -PoruiSmoke=true -PoruiGuiScale=2 \
    -PoruiWidth=854 -PoruiHeight=480 -PoruiShot=oreui-gallery-scale2 :oreui-testmod:runClient
```

World smoke binds real `ItemStack`s and renders the item grid:

```bash
./gradlew --no-daemon --console=plain -PoruiSmoke=true -PoruiWorld=true -PoruiGuiScale=2 \
    -PoruiWidth=854 -PoruiHeight=480 -PoruiShot=oreui-gallery-world :oreui-testmod:runClient
```

## Tests

| Suite | Tests |
| :--- | :--- |
| `oreui-core` | 55 |
| `oreui-layout` | 34 (includes layout budgets) |
| `oreui-theme` | 19 |
| `oreui-components` | 35 |
| `oreui-adapter-262` | 10 |
| root (`ArchUnit`) | 17 |

## License and disclaimer

MIT. This project is not affiliated with, endorsed by, or sponsored by Mojang Studios or Microsoft. Minecraft is a trademark of Mojang Studios. No Bedrock Edition assets (`oreui`, `gui/dist/hbui` textures, fonts or audio) are extracted or bundled; all visuals are original or used under a permissive license.