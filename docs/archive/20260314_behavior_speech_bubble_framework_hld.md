# Villager Bubble System HLD

**Date:** 2026-03-14  
**Project:** Settlements-Alpha  
**Author:** Senior engineering design draft  
**Status:** Revised draft after product/engineering review

---

## 1) Context and Product Direction

The current bubble implementation is behavior-centric (e.g., shearing), but product direction is broader:

1. Bubble events can come from **any source** (behaviors, sensors, scripted events, commands).
2. Some channels should stack (e.g., chat), others should replace (e.g., behavior indicator).
3. Stage transitions may change indicators (e.g., smoke meat overcooked -> burnt icon).

Therefore, bubble architecture should be a **global villager capability** with explicit channel policies.

---

## 2) Goals and Non-Goals

### Goals

1. Decouple bubbles from behaviors (global dispatch model).
2. Support multiple producers (behavior, sensor, system).
3. Support channel-specific policies (max active count, replacement/stacking strategy, render order).
4. Keep behavior stage-driven bubble updates simple to author.
5. Keep implementation clean, deterministic, and migration-safe.

### Non-Goals

1. Full dynamic bubble composition editor in this phase.
2. Data-driven stage map config in MVP (can be added later).
3. Rebuilding all render primitives immediately.

---

## 3) Architecture Decision Summary

Adopt a **Villager Bubble Service + Channel Policy** model:

- Server owns authoritative bubble runtime state for each villager.
- Active bubbles are ephemeral per-villager runtime state.
- Producers emit explicit commands (`UPSERT`, `PUSH`, `REMOVE_BY_ID`, `REMOVE_BY_OWNER`, `CLEAR_CHANNEL`).
- Channel behavior is defined by channel policy.
- Existing single-bubble display logic is legacy infrastructure and may be replaced or substantially refactored.

Behaviors are just one producer class. Sensors can emit transient emotes directly (e.g., "!!" + happy face for 3s).

---

## 4) Approaches Considered

### A) Behavior-owned bubble lifecycle (status quo)

**Pros**
- No framework changes.

**Cons**
- Hard coupling to behavior flow.
- Duplicate lifecycle logic.
- Cannot cleanly support non-behavior producers.

**Verdict:** Rejected.

### B) Shared utility class, still called manually by producers

**Pros**
- Reduces duplication.

**Cons**
- Lifecycle still convention-based.
- No first-class channel policy model.

**Verdict:** Transitional only.

### C) Villager bubble service + channel policy (**Recommended**)

**Pros**
- Producer-agnostic and scalable.
- Deterministic policy per channel.
- Clear architecture boundaries.

**Cons**
- Requires a stronger core contract and migration.

**Verdict:** Recommended.

---

## 5) Core Model

### 5.1 Villager bubble service

`VillagerBubbleService` (application service)

Responsibilities:
- Server-side application service; clients do not evaluate channel policy.
- Accept bubble commands from any producer.
- Resolve channel policy deterministically.
- Mutate per-villager authoritative bubble runtime state.
- Ensure expired entries are pruned on the server even when no new producer command arrives.
- Assign stable ordering metadata for active bubbles.
- Publish snapshot replication events to the client pipeline.

State ownership:
- Active bubble entries are ephemeral runtime state scoped to a villager.
- The service is the mutation entry point.
- Per-villager bubble state owns active entries and sequence assignment.
- Bubble runtime state is not persisted across saves or reloads in MVP.

Implementation note:
- Do **not** model the new runtime around the legacy single-active `BubbleManager` heap.
- Per-villager runtime state should use explicit per-channel structures with direct lookup/remove/upsert support (e.g., by `bubbleId` and `(channel, ownerKey)`).
- Active counts are intentionally small, so deterministic sorting at snapshot/render time is preferred over a heap-centric mutation model.

### 5.2 Bubble commands

#### `ownerKey`

`ownerKey` is a producer-scoped logical identity for a bubble slot within a channel.

- It is used to update or remove the same logical bubble across stage transitions.
- It is required for `UPSERT` and `REMOVE_BY_OWNER`.
- It does **not** need to be globally unique.
- It only needs to be unique within `(villager, channel)` scope.

Examples:
- `behavior:shear_sheep`
- `behavior:smoke_meat`
- `sensor:danger_emote`

`bubbleId` remains the concrete runtime instance id assigned by the service/replication pipeline.

#### Recommended MVP operations

- `UPSERT(channel, ownerKey, message)`  
  `ownerKey` is required. If a bubble exists in the channel with the same `ownerKey`, replace/update it in place. Otherwise insert a new bubble and apply channel overflow policy. This is the default operation for behavior indicators.
- `PUSH(channel, message)`  
  Always insert a new runtime bubble. `ownerKey` is absent and ignored. No dedupe is performed. Channel overflow policy decides what happens when the channel is full. This is the default operation for chat/system transient bubbles.
- `REMOVE_BY_ID(bubbleId)`  
  Remove exactly one runtime bubble instance.
- `REMOVE_BY_OWNER(channel, ownerKey)`  
  Remove the producer-owned logical bubble in the given channel, if present.
- `CLEAR_CHANNEL(channel)`

Notes:
- Producers should not need to manage transport-level packet lifecycle.
- `UPSERT` is preferred over ad-hoc "replace current behavior bubble" logic.
- `UPSERT` defines producer-owned slot semantics.
- `PUSH` defines fire-and-forget transient instance semantics.
- Current names such as `SET` / `REPLACE` are intentionally not used in MVP because they are too ambiguous.

### 5.3 Bubble channel

`BubbleChannel` is a **semantic lane**, not a raw position primitive.

Initial channels:
- `BEHAVIOR`
- `CHAT`
- `SYSTEM` (optional now)

Why needed:
- Prevent unrelated bubbles from evicting each other.
- Attach independent policy per lane.

### 5.4 Channel policy (first-class)

`ChannelPolicy` (per channel):
- `maxActive`
- `overflowPolicy` (`REPLACE_EXISTING`, `DROP_OLDEST`, `DROP_LOWEST_PRIORITY`, `REJECT_NEW`)
- `renderOrder`
- `defaultTtlCap`

Example product policies:
- `BEHAVIOR`: `maxActive=1`, `overflowPolicy=REPLACE_EXISTING`
- `CHAT`: `maxActive=3`, `overflowPolicy=DROP_OLDEST`

Operational rules:
- `UPSERT` matches only by `(channel, ownerKey)`.
- `PUSH` never dedupes.
- Expired bubbles are pruned before overflow policy is evaluated.
- Overflow policy is evaluated only against live entries in the target channel.
- Overflow eviction and tie-break behavior must be deterministic.
- Recommended eviction order for `DROP_LOWEST_PRIORITY` is:
  1. lowest `message.priority`,
  2. oldest `message.createdGameTime`,
  3. lowest `sequenceNumber`.

### 5.5 Bubble message payload

`BubbleMessage`:
- `bubbleKind` (application/shared semantic enum in MVP)
- `priority` (intra-channel)
- `ttlTicks`
- optional `extraData`
- metadata (`sourceType`, created tick)

Notes:
- `bubbleKind` must be an application/shared semantic identifier, not a rendering-infrastructure enum.
- Infrastructure maps `bubbleKind` to render assets, sprite layout, animation behavior, and renderer implementations.
- Render-specific assets, sprite layout, animation implementation, and render object construction remain infrastructure concerns.

---

## 6) Bubble Kind Strategy (Safety vs Flexibility)

Recommendation: **Hybrid path**

1. **MVP:** keep `BubbleKind` as an application/shared enum for safety and development speed.
2. **Design for extension:** use a registry/factory boundary so future composed/custom bubble payloads can be introduced without rewriting dispatcher core.
3. **Infrastructure mapping:** client/rendering infrastructure owns the mapping from `BubbleKind` to concrete renderer/asset definitions.

This balances short-term implementation confidence with long-term flexibility.

---

## 7) Producer Integration

### 7.1 Behaviors

Behavior code can dispatch directly in stage handlers:

- On stage enter: `UPSERT(BEHAVIOR, "behavior:shear_sheep", ...)`
- On stage transition/failure: `UPSERT(...)` to change indicator, or `REMOVE_BY_OWNER(BEHAVIOR, "behavior:shear_sheep")`

Concrete migration rule:
- Migrated behaviors must not reference bubble packet classes directly.
- `ShearSheepBehaviorV2` should dispatch bubble intents through the service only.

No mandatory `BehaviorSpeechBubblePolicy` abstraction in MVP.

`BehaviorSpeechBubblePolicy` can remain an **optional helper abstraction** if duplication appears later.

### 7.2 Sensors / Non-behavior producers

Sensors can dispatch transient bubbles directly:

- Example: stray emerald detected -> `PUSH(SYSTEM, EMOTE_EXCITED, ttl=3s)`

This is the major reason for the service-based architecture.

---

## 8) Client Rendering Notes (MVP)

Client rendering is infrastructure-owned and may freely evolve from the legacy single-bubble display logic.

MVP rendering requirements:

- Support deterministic rendering of all active bubbles selected by channel policy.
- Support multiple channels.
- Support stacked display for channels configured to allow multiple active bubbles.
- Avoid coupling application command semantics to fixed placement primitives.

Deterministic ordering should use a stable key:

1. `channel.renderOrder`
2. `message.priority` (higher first)
3. `message.createdGameTime`
4. service-assigned per-villager `sequenceNumber`

Exact visual layout remains an implementation detail unless product requirements later require a fixed layout contract.

Client application rules:
- A snapshot replaces the full active bubble view for that villager.
- Unknown bubble ids in local client state are ignored safely.
- Ordering is derived from replicated metadata, not local heuristics.

---

## 9) Config Strategy (Revised)

Do **not** force a universal bubble config contract on every producer.

Instead:

1. Channel policies are globally configured/defaulted.
2. Producer-specific config remains local where it adds value.
   - Example: behavior config may include `enable_bubble` or ttl defaults.
3. Stage->bubble mapping remains in behavior code for MVP (clearer ownership, easier debugging).

If designers later need data-driven stage maps, add optional config module then.

---

## 10) Clean Architecture Mapping

- **Application layer:** `VillagerBubbleService`, command handling, policy evaluation, authoritative villager bubble state.
- **Use-case producers:** behavior/sensor/etc. emit intents.
- **Infrastructure layer:** packet transport, codec DTOs, bubble registry/definition mapping, rendering implementation, client bubble view state.

This keeps dependencies pointing inward and avoids infrastructure details leaking into behavior/sensor logic.

Explicit rule:

- Domain and domain-facing interfaces must not expose rendering infrastructure types.
- Application/shared bubble semantics (e.g., `BubbleKind`) must not live under rendering infrastructure packages.
- Bubble renderers, render registries, packet DTOs, and client-side bubble stores are infrastructure concerns.

### 10.1 Replication and Tracking Synchronization

Network transport should follow the same codec-oriented style already used by the behavior UI feature.

Recommended replicated DTO direction:

- `BubbleEntrySnapshot`
  - `bubbleId`
  - `channel`
  - `bubbleKind`
  - optional `ownerKey`
  - `priority`
  - `expireGameTime`
  - `createdGameTime`
  - `sequenceNumber`
  - `extraData`

Recommended packet direction:

- `ClientBoundBubbleSnapshotPacket(villagerEntityId, entries)`

Implementation note:

- Use `StreamCodec<FriendlyByteBuf, Packet>` and dedicated codecs such as `BubbleEntrySnapshotCodec`.
- Do **not** use JSON-string serialization for the bubble packet path.
- Bound string lengths and map sizes similarly to the behavior UI codecs.
- Emit a fresh snapshot whenever authoritative active-bubble state changes for that villager.
- This includes server-side expiration pruning, not only explicit producer commands.

TODO:

- When a client begins tracking a villager after bubbles are already active, send an active-bubble snapshot for that villager. MVP may temporarily rely on re-emission or limited resync behavior.

---

## 11) Migration Plan (MVP -> Extensions)

### Phase 1 - Foundation

1. Add service API + command model.
2. Add `BubbleChannel` + `ChannelPolicy`.
3. Add villager runtime bubble state keyed by channel with direct lookup by `bubbleId` and `(channel, ownerKey)`.
4. Add server-side prune/tick integration so TTL expiration is enforced without requiring new producer commands.
5. Add codec-based bubble snapshot DTOs and packet skeletons using application/shared bubble semantics (`BubbleKind`), not rendering-infrastructure enums.
6. Add minimal client snapshot ingestion and single-bubble rendering compatibility so the new service path can be validated end-to-end.

### Phase 2 - Integrate existing behavior path

7. Migrate `ShearSheepBehaviorV2` to service commands.
8. Remove inline packet lifecycle logic from behavior.
9. Validate one complete end-to-end path: behavior -> service -> snapshot replication -> client render.

### Phase 3 - Non-behavior producer support

10. Add one sensor-driven emote example (short TTL) to validate global producer model.
11. Validate that behavior and sensor producers can coexist without cross-channel eviction bugs.

### Phase 4 - Full rendering policy rollout

12. Replace/refactor legacy single-bubble client display logic as needed.
13. Render multiple active bubbles according to channel and intra-channel ordering rules.
14. Implement stacked rendering for channels that allow multiple active entries.
15. Validate `BEHAVIOR` single-slot + `CHAT` stacked behavior.

### Phase 5 - Optional hardening/extensions

16. Optional helper abstraction for behavior boilerplate (`BehaviorSpeechBubblePolicy`) if needed.
17. Optional move to composable bubble definitions beyond enum.

---

## 12) Risks and Mitigations

1. **Duplicate bubble effects during migration**
   - Mitigation: migrate producer-by-producer; ban direct packet calls in migrated modules.

2. **Policy ambiguity**
   - Mitigation: command semantics, `ownerKey`, and channel policies are explicit and tested.

3. **Render clutter**
   - Mitigation: channel `maxActive`, TTL caps, and deterministic replacement rules.

4. **Over-abstraction early**
   - Mitigation: MVP keeps behavior stage map in code, minimal mandatory abstractions.

5. **Late-tracker desync**
   - Mitigation: add tracking-start snapshot sync after MVP if not delivered immediately.

6. **Protocol churn / packet inefficiency**
   - Mitigation: adopt typed codec-based packets from the start and avoid JSON-string payloads.

---

## 13) Testing Strategy

1. Service unit tests:
   - `UPSERT` in `BEHAVIOR` replaces prior bubble with same `ownerKey`,
   - `PUSH` in `CHAT` appends until channel max, then drops oldest,
   - `REMOVE_BY_ID` removes only the target runtime bubble,
   - `REMOVE_BY_OWNER` removes only the producer-owned bubble in that channel,
   - expired bubbles are cleaned before overflow policy is applied,
   - expired bubbles are pruned by the server lifecycle even when no new producer command arrives,
   - overflow eviction tie-breaks are deterministic,
   - equal-priority bubbles preserve stable deterministic order.

2. Producer integration tests:
   - migrated `ShearSheepBehaviorV2` dispatches bubble commands only,
   - migrated behavior code does not reference direct bubble packet classes,
   - one sensor sends a 3-second transient emote bubble.

3. Replication/codec tests:
   - codec round-trip for `BubbleEntrySnapshot`,
   - bounded string/map validation,
   - repeated snapshot application is idempotent,
   - `expireGameTime` remains the canonical expiration field in replicated snapshots,
   - snapshot ordering metadata reproduces deterministic client order.

4. Client rendering tests:
   - channel order is respected,
   - intra-channel priority is respected,
   - stacked chat rendering remains deterministic.

---

## 14) Acceptance Criteria

- [ ] Bubble system is callable by behaviors and non-behavior producers.
- [ ] Channel policy exists and is enforced (`BEHAVIOR` and `CHAT` at minimum).
- [ ] ShearSheep uses the service, not direct bubble packet lifecycle code.
- [ ] At least one sensor-based 3-second emote bubble works.
- [ ] Bubble network transport uses codec-based packets, not JSON-string serialization.
- [ ] Application/shared bubble semantics are not sourced from rendering infrastructure packages.
- [ ] Deterministic multi-bubble rendering order is validated.

---

## 15) Final Recommendation

Proceed with the **Villager Bubble Service + Channel Policy** architecture.

This best matches product direction (multi-source and multi-role bubbles), keeps implementation clean, and avoids prematurely hard-coding bubble logic into behavior-only workflows.
