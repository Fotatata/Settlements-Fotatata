# Villager Stats UI — Design & Implementation Plan

## Context
Players need a go-to screen to inspect villager stats: identity, health, genetics, inventory, current activity, and more. This is the primary "character sheet" for any villager in the Settlements mod. The UI uses the existing layout-based framework (`LayoutScreen`, `LinearLayout`, `Elements`, etc.) and follows the same snapshot-based server sync pattern established by `BehaviorControllerScreen`.

## UI Element Design

### Layout: Two-Column + Footer
- **Left panel (Weighted 7 / ~35%)**: Identity, appearance, vitals, genetics, reputation
- **Right panel (Weighted 13 / ~65%)**: Activity, status, inventory
- **Footer (full width)**: System info, navigation buttons
- Panel sizing: 85%/80% of screen, clamped to 420–540w, 280–400h (wider/taller than behavior screen to fit two columns + hex chart)

### Left Panel Elements (top to bottom)

1. **Villager Name** — centered text, `theme.textColor()`. If villager has no custom name, display "Villager". Font: default Minecraft font.

2. **Expertise + Profession** — centered text below name, `theme.subtleTextColor()`. Format: `"Journeyman Farmer"`. Expertise from `Expertise.fromLevel(villagerData.getLevel())`, profession from `villagerData.getProfession()` registry name, title-cased. If profession is `none` or `nitwit`, displays `"Unemployed"` instead of the expertise+profession combo.

3. **Villager 3D Model** — `Elements.custom()` renderer within a subtle bordered container (`Elements.rect().color(theme.rowColor()).border(theme.borderDark())` background). Renders the actual `Villager` entity model by delegating to vanilla's `InventoryScreen.renderEntityInInventory()` with proper `Quaternionf` pose setup, scissoring, and scale adjustment. Shows profession outfit, biome skin variant. Head/body follows mouse cursor position. Fixed height ~50-60px. Implemented inline in the screen class (no separate `Elements.entityModel()` needed — not worth the abstraction for a single use site).
   - **Performance note**: Entity model rendering (vertex upload, texture binding, lighting, pose stack) is the most expensive element in the framework. For v1, render every frame (vanilla does this in inventory screen). Add a code comment noting this is a candidate for framebuffer caching if profiling shows issues.
   - **Graceful degradation**: If entity is null (unloaded chunk, dead), render a placeholder icon or gray silhouette.

4. **HP Bar + Fraction** — `Elements.custom()` renderer. Horizontal bar with background (dark gray), filled portion colored by HP percentage (green >50%, yellow 25-50%, red <25%). Text overlay: `"18/20"` in white, right-aligned or centered. Bar width fills left panel, height ~7px. Text beside or below bar.

5. **Hexagonal Genetics Chart** — `Elements.custom()` renderer. **Not fixed size** — radius scales proportionally to `min(availableWidth, availableHeight)` with margin for labels. Text elements (name, expertise, HP) have hard pixel minimums; the hex chart is the element that adapts to fill remaining space. Details:
   - 6 axes: STR (top), CON (top-right), AGI (bottom-right), INT (bottom), WIL (bottom-left), CHA (top-left)
   - 4 concentric hexagons at 25%, 50%, 75%, 100% radius — mark D/C/B/A grade boundaries. Drawn with translucent lines (`0x30FFFFFF`)
   - 3 axis lines through center connecting opposite vertices (`0x20FFFFFF`)
   - Filled data polygon showing actual gene values — translucent green fill (`0x6055FF55`), solid green outline (`theme.successColor()`)
   - Small dots (3x3 fill) at each data vertex
   - Labels at outer edge: 3-letter abbreviation (STR, CON, AGI, INT, WIL, CHA) in `theme.subtleTextColor()`. No grade letters displayed on the chart — grades are only shown on hover.
   - **Grade thresholds (even quartiles)**: A: 0.75–1.0, B: 0.50–0.74, C: 0.25–0.49, D: 0.00–0.24 (used for hover tooltip only)
   - **Hover interaction**: Detect which axis the mouse is closest to (angle from center, 60-degree sectors). Show detail text via tooltip (i18n): e.g., `Component.translatable("ui.settlements.stats.gene.strength.detail", "0.72", "B")` rendering as `"Strength: 0.72 (B) — Affects melee damage and carry weight"`. All user-visible strings must use translation keys, not hardcoded English.
   - **Rendering approach**: Use `Tesselator` + `BufferBuilder` for all hex chart geometry:
     - Lines (axes, concentric hexagons, data polygon outline): `VertexFormat.Mode.DEBUG_LINES` for 1px OpenGL lines, or rotated quads via `Matrix4f` for thicker lines
     - Filled data polygon: `TRIANGLE_FAN` mode from center to each data vertex
     - **No Bresenham** — avoid per-pixel `graphics.fill()` calls which destroy batching
   - **Caching**: Gene data rarely changes between snapshots. Cache the computed vertex positions and only recalculate when gene values actually change (compare against previous snapshot's gene map). The `Tesselator` draw calls themselves are cheap, but vertex computation + polygon fill can be cached into a pre-built vertex list

6. **Reputation** — text element: `"Reputation: Friend of the Village"`. No number shown in the label — the number is revealed on hover (tooltip: `"+20"`). Text color reflects standing:
   - Major negative → red (`theme.errorColor()`)
   - Around zero → yellow (`theme.warningColor()`)
   - Major positive → lime/green (`theme.successColor()`)
   - Title mapping (placeholder tiers, to be finalized with `ReputationUtil`): Hostile, Unfriendly, Neutral, Friendly, Honored, Exalted, etc.

### Right Panel Elements (top to bottom)

1. **Current Activity** — horizontal layout with icon + text.
   - If a behavior is running: show behavior's `iconItemId` as 16x16 `itemIcon` + translated `displayNameKey` + `currentStageLabel` (e.g., `[Crafting Table] Harvest Crops — Harvesting`) in `theme.successColor()`
   - If no behavior running: show `"None"` in `theme.subtleTextColor()`. Avoids incorrect mappings for edge cases like panicking, fleeing, or trading which don't map to schedule phases.
   - Data source: filter snapshot's behavior fields (`activeBehaviorNameKey` non-null → active, else "None")

2. **Home/Workstation Status** — vertical layout with 2 rows, each row is icon + text:
   - Row 1: Bed icon (`Items.RED_BED`) + coordinates text. e.g., `[Red Bed icon] Home: 10, 58, 1832`. If no bed claimed: `[Red Bed icon] Homeless`
   - Row 2: Profession workstation icon (resolved at render time from profession key) + coordinates text. e.g., `[Composter icon] Work: 38, 60, 1800`. If unemployed (`none`/`nitwit` profession): `[Barrier icon] Unemployed`. If employed but no job site claimed yet: `[Workstation icon] Unemployed`. Fallback icon for unknown professions is `Items.BARRIER`.
   - Coordinates sourced from `MemoryModuleType.HOME` and `MemoryModuleType.JOB_SITE` `GlobalPos` values in villager brain
   - "None" text in `theme.subtleTextColor()`, coordinate text in `theme.textColor()`

3. **Reserved Space** — `Elements.flexSpacer()`. Empty area that grows to fill remaining vertical space. Future features (skills, mood, relationships) go here.

4. **Inventory Section** (anchored at bottom of right panel):
   - Header text: `Component.translatable("ui.settlements.stats.inventory", usedSlots, backpackSize)` → `"Inventory (12/40):"` — count of non-empty slots / `backpackSize`
   - **Only render non-empty slots** — for a potentially 40-slot backpack, showing all empty slots creates excessive visual noise. The `"12/40"` count label already communicates capacity. Non-empty items render as 20x20px `itemIcon` elements (16px icon + 2px border).
   - Number of columns = `floor(availablePanelWidth / 20)`, calculated once during `buildRoot()` (on init and resize only).
   - **Snapshot updates must NOT trigger full layout rebuild**. Inventory uses a `ScrollableList` with a `rowFactory` supplier. When a new inventory snapshot arrives, only `ScrollableList.rebuildRows()` is called — this rebuilds just the inventory rows, not the entire screen element tree. The full `buildRoot()` is never triggered by inventory updates.
   - Each item has vanilla tooltip on hover (using `tooltipStackSupplier` parameter of `itemIcon`)
   - Inventory section uses `ScrollableList` (row height = 20px) so large inventories scroll naturally with mouse wheel. Scroll indicators (▲/▼) appear when content exceeds visible area.

### Footer Elements (full width)

1. **Connection Status** — `Elements.custom()` renderer showing `"Connection:"` (white) followed by status. Since `CustomRenderer` doesn't propagate `renderOverlay()`, hover tooltip must be handled manually within the custom renderer by checking mouse bounds and calling `graphics.renderTooltip()` directly.
   - Normal: `"OK"` in lime (`theme.successColor()`). Hover: `"Server connection active"` (via translation key)
   - Stale/lost: `"Disconnected"` in red (`theme.errorColor()`). Hover: `"Connection lost — close and reopen the UI"` (via translation key)

2. **Buttons** — horizontal layout, right-aligned:
   - "Close" button — closes the screen (sends close packet, clears session)
   - (Behaviors button removed — that's a debug-only feature)

## Data Flow

### Snapshot Record
```java
// application/ui/stats/model/VillagerStatsSnapshot.java
@Builder
public record VillagerStatsSnapshot(
    long gameTime,
    int villagerEntityId,
    @Nonnull String villagerName,
    @Nonnull String professionKey,         // from villagerData.getProfession().unwrapKey()
    int expertiseLevel,                    // 1-5 int, decoded via Expertise.fromLevel() — avoids string fragility
    float currentHealth,
    float maxHealth,
    @Nonnull double[] geneValues,          // indexed by GeneType.ordinal(), 6 entries — avoids Map autoboxing
    @Nullable BlockPos homePos,            // bed position, null if none
    @Nullable BlockPos workstationPos,     // job site position, null if none
    @Nullable String activeBehaviorNameKey,   // translation key, null when idle
    @Nullable String activeBehaviorStage,     // stage label, null when idle
    @Nullable String activeBehaviorIconId,    // ResourceLocation string, null when idle
    @Nonnull SchedulePhase schedulePhase,     // enum, not String — type safety
    int reputation                            // placeholder, 0 for now
) {}
```

**Design decisions:**
- `mainHand`/`offHand` removed — the 3D villager model already renders equipped items visually
- `expertiseLevel` as int (not String) — decoded via `Expertise.fromLevel()` which already handles int→enum. Avoids `IllegalArgumentException` on unknown strings from version mismatches
- `geneValues` as `double[]` (not `Map<GeneType, Double>`) — eliminates autoboxing (6 values × every 10 ticks), reduces allocation, simplifies codec (just write 6 doubles in ordinal order). Access via `geneValues[GeneType.STRENGTH.ordinal()]`
- All user-visible strings use translation keys, not hardcoded English. Formatting (title-casing, combining expertise+profession) happens in the **screen class** (presentation layer), not the snapshot builder

### Inventory Snapshot (separate record)
```java
// application/ui/stats/model/VillagerInventorySnapshot.java
@Builder
public record VillagerInventorySnapshot(
    int backpackSize,
    @Nonnull List<ItemStack> nonEmptyItems   // only non-empty stacks
) {}
```

### Server Sync
- Follow the same session-based pattern as `BehaviorControllerScreen`
- Server builds snapshot from `BaseVillager` every ~10 ticks while session is open
- **Sectioned change detection**: Use separate version counters for stats vs inventory. Stats snapshot sent every ~10 ticks (~0.5s). **Inventory snapshot sent every ~60 ticks (~3s)** and only when inventory has actually changed. This avoids serializing up to 40 `ItemStack` objects (with NBT/components) frequently.
- **No event-driven sync** — slight delays when villager picks up items quickly are acceptable. Wait for next snapshot interval to bulk update.
- **Inventory change detection**: Use a simple version counter (`int inventoryVersion`) on `VillagerInventory`. Increment on any `addItem()`, `consume()`, `setMainHand()`, `setOffHand()` call. Session service compares `lastSentInventoryVersion` to current — only rebuilds and sends inventory snapshot when they differ.
- Packets: Open, Close, Heartbeat, HeartbeatAck, StatsSnapshot, InventorySnapshot, Unavailable

### UI Framework Extensions Needed

1. **Villager model rendering** — Implemented inline in `VillagerStatsScreen` using `Elements.custom()` that delegates to vanilla's `InventoryScreen.renderEntityInInventory()`. Not extracted to a separate `Elements.entityModel()` element — a single use site doesn't justify the abstraction. Resolves entity via `Minecraft.getInstance().level.getEntity(entityId)` on the client. **Graceful degradation**: if entity is null (unloaded chunk, dead), renders a "?" placeholder instead of crashing.

2. **Dynamic grid layout** — Compute column count in the screen class during `buildRoot()` (called on init + resize). Build `LinearLayout.vertical` of `LinearLayout.horizontal` rows. Item data comes from suppliers that reference the latest snapshot, so the grid structure stays fixed while item contents update.

3. **`GuiLineDrawing` utility** — Static utility for `Tesselator`-based line drawing and polygon fill. Methods:
   - `drawLine(GuiGraphics, x1, y1, x2, y2, color)` — OpenGL line via `DEBUG_LINES` mode
   - `drawPolygonOutline(GuiGraphics, float[][] vertices, color)` — line strip
   - `fillConvexPolygon(GuiGraphics, float[][] vertices, color)` — `TRIANGLE_FAN` from centroid
   - `drawHexagon(GuiGraphics, cx, cy, radius, color)` — convenience for concentric rings

## File List

### New Files

**Application layer:**
- `application/ui/stats/model/VillagerStatsSnapshot.java` — snapshot record
- `application/ui/stats/snapshot/VillagerStatsSnapshotBuilder.java` — extracts snapshot from BaseVillager
- `application/ui/stats/session/VillagerStatsSession.java` — server-side session
- `application/ui/stats/session/VillagerStatsSessionService.java` — session lifecycle management

**Presentation layer:**
- `presentation/ui/stats/VillagerStatsScreen.java` — main screen (extends LayoutScreen)
- `presentation/ui/stats/VillagerStatsClientState.java` — client-side state singleton
- `presentation/ui/stats/HexChartRenderer.java` — genetics hex chart rendering logic

**Key bindings:**
- `presentation/ui/keybindings/SettlementsKeyMappings.java` — central registry for all key mappings

**Infrastructure layer (networking):**
- `infrastructure/network/features/ui/stats/packet/ServerBoundOpenVillagerStatsPacket.java`
- `infrastructure/network/features/ui/stats/packet/ServerBoundCloseVillagerStatsPacket.java`
- `infrastructure/network/features/ui/stats/packet/ServerBoundHeartbeatVillagerStatsPacket.java`
- `infrastructure/network/features/ui/stats/packet/ClientBoundOpenVillagerStatsPacket.java`
- `infrastructure/network/features/ui/stats/packet/ClientBoundVillagerStatsSnapshotPacket.java`
- `infrastructure/network/features/ui/stats/packet/ClientBoundVillagerInventorySnapshotPacket.java`
- `infrastructure/network/features/ui/stats/packet/ClientBoundHeartbeatAckVillagerStatsPacket.java`
- `infrastructure/network/features/ui/stats/packet/ClientBoundVillagerStatsUnavailablePacket.java`
- `infrastructure/network/features/ui/stats/codec/VillagerStatsSnapshotCodec.java`
- `infrastructure/network/features/ui/stats/codec/VillagerInventorySnapshotCodec.java`
- 8 handler classes (1:1 with packets, following existing handler pattern)

**UI framework utilities:**
- `presentation/ui/framework/GuiLineDrawing.java` — Tesselator-based line/polygon drawing utility

### Modified Files
- `infrastructure/network/core/PacketRegistry.java` — register all new packets
- `presentation/ui/framework/Elements.java` — add `renderItemDecorations()` call to `ItemIconElement` for item count display
- `domain/genetics/GeneType.java` — add display abbreviation and description translation key fields:
  ```java
  STRENGTH("STR", "ui.settlements.gene.strength.desc"),
  CONSTITUTION("CON", "ui.settlements.gene.constitution.desc"),
  // ... etc
  ```
  No conflict with existing serialization — `GeneType` is only serialized by name in NBT tags (via `EnumMap`), adding fields doesn't change serialization.
- `domain/inventory/VillagerInventory.java` — add `@Getter int inventoryVersion` field, increment on mutating operations (`addItem`, `consume`, `setMainHand`, `setOffHand`)

### Test Files
Note: Project uses JUnit 5 only (no Mockito). Tests follow existing patterns from `BehaviorSnapshotCodecTest` — using `FriendlyByteBuf` with `Unpooled` for codec round-trip tests, and direct instantiation for pure logic tests.

- `test/.../presentation/ui/stats/HexChartRendererTest.java` — unit tests for vertex computation, grade calculation (A/B/C/D from gene value), hover sector detection (angle → gene index). Pure geometry math, no rendering calls needed.
- `test/.../infrastructure/network/features/ui/stats/codec/VillagerStatsSnapshotCodecTest.java` — round-trip encode/decode. Verify all fields survive serialization including nullable behavior fields and nullable BlockPos.
- `test/.../infrastructure/network/features/ui/stats/codec/VillagerInventorySnapshotCodecTest.java` — **integration test** (requires `RegistryFriendlyByteBuf` with full MC registry bootstrap). Round-trip encode/decode for inventory snapshot with varying item counts.
- `test/.../presentation/ui/framework/GuiLineDrawingTest.java` — unit tests for vertex computation utilities (hexagon vertices, polygon centroid). No actual GL calls.

## Implementation Phases

### Phase 1: Snapshot + Networking
Build the data pipeline: snapshot record, builder, codec, all packets/handlers, session management. No UI yet. Verify data flows correctly via logging.

### Phase 2: Screen Shell
Create `VillagerStatsScreen` with the two-column layout structure, header, footer, placeholder rects for the hex chart and villager model areas. Wire up open/close flow.

### Phase 3: Left Panel Content
Implement name, expertise+profession, HP bar, reputation stub. Add the villager 3D model renderer (extends `Elements` with `entityModel()`).

### Phase 4: Hex Chart
Implement `HexChartRenderer` with `Tesselator`-based line drawing (`DEBUG_LINES` mode), `TRIANGLE_FAN` polygon fill, concentric hexagons, data polygon, labels, grades, hover detection.

### Phase 5: Right Panel Content
Implement current activity display, home/workstation status, dynamic inventory grid with tooltips.

### Phase 6: Polish
Connection status display with hover tooltip, edge cases (dead villager, unloaded chunk, entity model degradation), visual polish, i18n translation keys.

## Verification
- Open the villager stats screen by pressing R while looking at a villager (within 15 blocks)
- Verify all left panel elements render: name, profession, 3D model with mouse-follow, HP bar, hex chart with correct gene values, reputation stub
- Hover over hex chart axes and verify gene detail text appears
- Verify right panel: current activity updates when villager starts/stops a behavior, home/workstation icons reflect actual state
- Verify inventory grid: items display correctly, tooltips work on hover, grid reflows on window resize, syncs when villager picks up items
- Verify connection status shows "OK" (lime) normally, "Disconnected" (red) when server stops sending updates, with correct hover tooltips
- Test with villagers of different professions, expertise levels, and biome variants
