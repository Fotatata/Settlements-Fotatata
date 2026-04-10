# Traits

## Final Trait List

**P0 — Core (Milestone 1)**

Five traits. All terrain-driven, all produce visibly different villages.

**LUMBER** — Forest biome, high tree density. Sawmill, lumber yard, carpenter workshop, logging camps at forest edge.
Lumberjack (custom NPC). Minecart rails possible for log transport in larger villages.

**MINING** — Mountains, mesa, exposed stone, OR flat ground (shaft mine). Mine entrance (into hillside or straight
down), smelter, ore storage, tool shop. Minecart rails from mine to smelter. Miner (custom NPC). Flat-ground mines get a
headframe structure over a vertical shaft.

**FARMING** — Flat terrain, grass, open sky. Farmhouses with large crop fields, barn, windmill, farmer's market. Vanilla
Farmer. Brewery compound unlocks when paired with a tavern. Winery variant in warm biomes.

**FISHING** — Adjacent water (lake, river, ocean). Docks, fish-drying racks, smokehouse, net-mending shack, boathouse.
Vanilla Fisherman. Village layout pulls toward waterline.

**DEFENSE** — Hilltops, chokepoints, hostile biome adjacency, or random chance. Walls, gates, watchtowers, barracks,
training yard, armory. Guard (custom NPC). Generates infrastructure (walls) not just buildings. Primary DEFENSE replaces
town hall with a keep.

**P1 — Economy & Social (Milestone 2-3)**

**PASTORAL** — Plains, savanna, meadow. Rancher homestead, fenced pastures, shearing station, tannery (placed far from
hub — smells), stables. Vanilla Shepherd, Leatherworker, Butcher.

**TRADE** — River, flat terrain, ocean coast. Market hall, warehouses, caravan inn, merchant guild, customs house.
Wandering Trader gets a permanent home. Wider roads. Multiplier trait — makes other traits' buildings nicer.

**HONEY** — Flower biomes, forest with bee nests. Apiary complex, honey processing shed, wax workshop, mead brewery (
cross-trait with tavern), flower gardens. Beekeeper (custom NPC).

**CRAFT** — Derived trait, scores higher when multiple resource traits present. Artisan workshops, pottery,
glassblowing, dye works. Vanilla Mason, Fletcher, Armorer, Toolsmith. Makes the village feel industrious — lots of
workstations and chimney smoke.

**SPIRITUAL** — Low base rate, boosted by special terrain features. Church/temple, monastery complex (library +
dormitory + garden), graveyard, bell tower. Vanilla Cleric. Monk (custom NPC). Primary SPIRITUAL makes the temple the
hub.

**SCHOLARLY** — Rare. Boosted by flat terrain, co-occurrence with SPIRITUAL or CRAFT. Grand library, observatory tower (
landmark), cartographer's map room, printing press. Vanilla Librarian, Cartographer. Scholar (custom NPC).

**WAYPOINT** — River crossings, road intersections, positioned between other settlements. Coaching inn, stable, small
market, signposts pointing outward. Innkeeper (custom NPC). Modest and functional, not Vegas. Cannot roll in
remote/isolated locations.

**P2 — Rare & Special (Later)**

**ARCANE** — Rare. Proximity to strongholds, ruined portals, amethyst geodes, or random low chance. Enchanter's tower,
arcane library, crystal garden, alchemy lab, summoning circle. Enchanter (custom NPC). Shifted block palette (purpur,
soul lanterns, crying obsidian accents). **Boss arena potential.**

**ANCIENT** — Proximity to vanilla structures or random chance. Excavation site with brushable blocks, relic museum,
mixed old/new architecture. Archaeologist (custom NPC). **20-30% chance of generating as abandoned** — ruined buildings,
zombie villagers, no lighting. Player can cure villagers and restore the village as a quest line.

**SEAFARING** — Ocean coastline specifically. Shipyard (partially in water), harbor, lighthouse, navigator's guild, sail
maker. Shipwright (custom NPC). Reorients entire village to face the ocean. Big sibling of FISHING.

---

## Universal Structures (not trait-specific)

These go in every village above hamlet scale, placed during the core layout phase regardless of traits:

- **Town Hall** (or Keep if DEFENSE primary, or Temple if SPIRITUAL primary)
- **Tavern** — every village. Brewery compound attaches when FARMING or HONEY present
- **Well** — hub zone, Tier 0
- **Bounty Board** — hub zone, player interaction hook, ties into reputation system
- **Houses** — fill remaining plots, 5+ variants
- **Roads** — gravel (poor), cobblestone (modest), stone brick (prosperous)

---

# Events & History

## Founding Events — "Why did we settle here?"

These roll once, always first. They set the origin story.

**FOUNDED_BY_REFUGEES**
Modifiers: DEFENSE +0.15, SPIRITUAL +0.10, TRADE -0.05
Narrative: "The founders fled conflict and sought safety."
Visual: Worn foreign banners in the town hall, a memorial stone near the hub, houses built tight together (defensive
clustering).

**FOUNDED_BY_EXPLORERS**
Modifiers: TRADE +0.15, SCHOLARLY +0.10, CRAFT +0.05
Narrative: "An expedition party discovered this location and decided to stay."
Visual: An old explorer's camp on the outskirts (tent, campfire remains, map on a lectern), a flagpole in the town
square.

**FOUNDED_BY_EXILES**
Modifiers: DEFENSE +0.10, SPIRITUAL -0.10, HOSPITALITY -0.10
Narrative: "The founders were cast out from somewhere else. They don't talk about it."
Visual: No religious structures or they're in disrepair, buildings face inward (insular layout), walls built early even
without DEFENSE trait.

**FOUNDED_BY_MONKS**
Modifiers: SPIRITUAL +0.25, SCHOLARLY +0.15, DEFENSE -0.10
Narrative: "A monastic order chose this place for contemplation."
Visual: The oldest building is the shrine/temple, gardens are well-maintained, settlement is orderly and symmetrical.

**FOUNDED_BY_PROSPECTORS**
Modifiers: MINING +0.20, TRADE +0.10, SPIRITUAL -0.05
Narrative: "Someone found valuable ore here and word spread."
Visual: An old played-out mine shaft (abandoned, different from the active one), a "first strike" monument — a gold or
diamond ore block encased in glass in the town square.

**FOUNDED_ON_ANCIENT_SITE**
Modifiers: ANCIENT +0.25, SCHOLARLY +0.10, SPIRITUAL +0.10
Narrative: "They built on top of something old. Foundations go deeper than they should."
Visual: Ancient stone brick foundations under some buildings, an exposed ruin fragment in the town square, cracked stone
bricks mixed into newer construction.

**FOUNDED_AS_OUTPOST**
Modifiers: DEFENSE +0.20, TRADE +0.10, FARMING -0.10
Narrative: "Originally a military outpost or watchtower that attracted settlers."
Visual: The oldest structure is a watchtower or small fort, the town grew around it asymmetrically, military
architecture mixed with civilian.

**FOUNDED_BY_FISHERMEN**
Modifiers: FISHING +0.20, SEAFARING +0.10, DEFENSE -0.10
Narrative: "A few families with boats found good waters and never left."
Visual: The oldest dock is weathered and repaired many times, an old overturned boat hull used as a storage shed, the
village clusters along the waterline.

---

## Disaster Events — "What nearly killed us?"

These leave scars. They subtract from some traits and often add to DEFENSE or SPIRITUAL as the village reacts.

**GREAT_FIRE**
Modifiers: LUMBER -0.20, DEFENSE +0.15, CRAFT +0.10 (learned to build with stone)
Requires: LUMBER score >= 0.3
Narrative: "A devastating fire swept through. They rebuilt, but differently."
Visual: Charred tree stumps on one side of village, one or two burnt-out building shells (blackened wood frames with no
roof), stone rebuilds where wood buildings would normally be, a fire memorial.

**PLAGUE**
Modifiers: SPIRITUAL +0.20, TRADE -0.15, HOSPITALITY -0.10
Requires: Population estimate > 30
Narrative: "A sickness came. Many didn't survive. The survivors turned to faith."
Visual: Expanded graveyard with many headstones, 2-3 abandoned houses (boarded up doors and windows), an herb garden
near the church (medicinal), the village feels slightly too large for its current population — empty plots.

**FLOOD**
Modifiers: FISHING -0.15, FARMING -0.10, DEFENSE +0.10
Requires: Water feature present
Narrative: "The river rose and took half the village with it."
Visual: A raised levee or dike along the waterline (dirt/cobblestone embankment), buildings near water are newer than
buildings further away, waterlogged ruins on the flood side (mossy cobblestone foundations with no structures above).

**FAMINE**
Modifiers: FARMING +0.15 (overcompensated), TRADE +0.10 (need imports), SPIRITUAL +0.05
Requires: FARMING score >= 0.2
Narrative: "A terrible harvest nearly ended the settlement. They swore it would never happen again."
Visual: Oversized granary/food storage (bigger than normal for village size), extra crop field diversity (multiple crop
types instead of monoculture), a rationing board near the town hall.

**MONSTER_SIEGE**
Modifiers: DEFENSE +0.30, FARMING -0.10, HOSPITALITY -0.10
Narrative: "Something attacked from the darkness. The walls went up the next day."
Visual: Repaired wall sections (mixed block types — hasty repairs), scorch marks on buildings (if fire-based mob),
trophy skulls or mob heads on fence posts at the gate, a damaged watchtower that was rebuilt taller.

**EARTHQUAKE**
Modifiers: MINING -0.15 (mines collapsed), DEFENSE +0.10, CRAFT +0.10 (rebuilding)
Requires: MINING score >= 0.2 OR elevation variance >= 0.3
Narrative: "The ground shook. The old mine caved in. They dug a new one."
Visual: A collapsed mine entrance (cobblestone and gravel pile with wooden beams sticking out), cracked foundations on
older buildings, a newer mine entrance nearby, uneven terrain that wasn't flattened (intentional — the quake did that).

**DRAGON_ATTACK**
Modifiers: DEFENSE +0.25, ARCANE +0.15, SPIRITUAL +0.10
Requires: Rare roll, maybe 5% chance
Narrative: "The elders speak of the day the sky went dark."
Visual: One large crater/scorched clearing on the village edge, an obsidian scar in the ground, dragon-themed carvings
on the rebuilt town hall, an end crystal fragment (decorative) in a display case. This village takes defense VERY
seriously.

**MINE_COLLAPSE**
Modifiers: MINING -0.25, DEFENSE +0.05, SPIRITUAL +0.15 (memorial)
Requires: MINING score >= 0.4
Narrative: "The deep shaft gave way. Good people were lost."
Visual: A sealed-off mine entrance (cobblestone wall with a memorial sign), a new mine entrance at a different location,
a memorial statue near the hub, miners carry lanterns obsessively (extra lighting everywhere).

---

## Prosperity Events — "What made us thrive?"

These are trophies. They boost traits and add aspirational structures.

**TRADE_ROUTE_DISCOVERED**
Modifiers: TRADE +0.25, FARMING +0.10, HOSPITALITY +0.10
Narrative: "A passing caravan revealed a route. Merchants followed."
Visual: Wider main road than normal, a carved signpost pointing outward with a distant location name, a caravanserai or
large stable, merchant flags/banners on market buildings.

**MASTER_CRAFTSMAN_ARRIVED**
Modifiers: Highest eligible industry trait +0.20, CRAFT +0.15, SCHOLARLY +0.05
Requires: At least one industry trait >= 0.3
Narrative: "A master [profession] settled here and elevated the craft."
Visual: One workshop building is noticeably upgraded — larger, better materials, more detailed than others. A "
masterwork" item in an item frame above the door (enchanted tool, golden item, etc). Apprentice NPCs around the
workshop.

**BOUNTIFUL_HARVEST**
Modifiers: FARMING +0.15, TRADE +0.10, HOSPITALITY +0.10, SPIRITUAL +0.05
Requires: FARMING score >= 0.3
Narrative: "A legendary harvest season brought wealth and celebration."
Visual: Oversized barn, a harvest festival ground near the hub (open area with tables, lanterns, hay bale seating),
extra crop variety, the tavern is larger than normal. General vibe of abundance.

**RICH_VEIN_STRUCK**
Modifiers: MINING +0.25, TRADE +0.15, CRAFT +0.10
Requires: MINING score >= 0.3
Narrative: "They hit a deep vein of precious ore. Wealth followed."
Visual: A reinforced mine entrance (iron bars, better timber framing), an assay office with ore samples in item frames,
stone brick buildings instead of cobblestone (wealth upgrade), a money changer or trading post.

**HERO_EMERGED**
Modifiers: DEFENSE +0.15, SPIRITUAL +0.10, SCHOLARLY +0.05
Narrative: "A great warrior or leader came from this village."
Visual: A statue in the town square (armor stand with named armor on a pedestal), a training yard where there normally
wouldn't be one, the town hall has a trophy room (item frames with weapons/armor).

**BLESSED_SPRING**
Modifiers: SPIRITUAL +0.20, FARMING +0.15, HONEY +0.05
Requires: Water feature present
Narrative: "The local spring was declared sacred. Pilgrims came."
Visual: The well or spring is ornately decorated (chiseled stone, flower pots, lanterns), a small pilgrimage shrine near
the water, a monk or cleric specifically tending the spring. Water source is treated as the spiritual center, not just
infrastructure.

**ALLIANCE_FORMED**
Modifiers: TRADE +0.15, DEFENSE +0.10, HOSPITALITY +0.10
Narrative: "A pact with a neighboring settlement brought mutual prosperity."
Visual: A diplomacy hall or meeting room in the town hall, twin banners (two different colors representing both
settlements), a maintained road leading outward toward the allied settlement's direction, a messenger NPC.

**ARCANE_DISCOVERY**
Modifiers: ARCANE +0.25, SCHOLARLY +0.15, SPIRITUAL +0.10
Requires: Rare roll, ~10% chance
Narrative: "Someone uncovered something they didn't fully understand."
Visual: An enchanting setup in an unexpected place (a farmer's basement, a back room of the tavern), glowing blocks (sea
lanterns, end rods) in unusual locations, a locked/barred building that NPCs won't enter, strange crop patterns in
nearby fields.

**ARTISTIC_RENAISSANCE**
Modifiers: CRAFT +0.20, SCHOLARLY +0.10, TRADE +0.10
Narrative: "A period of creative flourishing left its mark."
Visual: Glazed terracotta patterns on building facades, banner art on every major building, a town stage/amphitheater,
painted (colored wool/concrete) accent walls, more decorative blocks overall.

**MERCHANT_GUILD_ESTABLISHED**
Modifiers: TRADE +0.20, CRAFT +0.10, WAYPOINT +0.10
Narrative: "The merchants organized and the town's commerce formalized."
Visual: A guild hall building (large, ornate, near the market), standardized market stalls (uniform design), a trading
post with chests, guild banners.

---

## Conflict Events — "Who did we fight?"

These add narrative tension and usually boost DEFENSE while damaging something else.

**BANDIT_RAIDS**
Modifiers: DEFENSE +0.20, TRADE -0.10, FARMING -0.05
Narrative: "Raiders from the wilds plagued the settlement for years."
Visual: Reinforced gates, iron bar windows on ground-floor buildings, a bounty board with "WANTED" signs, watchtower
positioned toward the raid direction, hidden storage cellars.

**CIVIL_DISPUTE**
Modifiers: DEFENSE +0.05, TRADE -0.10, SPIRITUAL -0.05
Narrative: "The village split over a disagreement. Scars remain."
Visual: The village has a subtle divided feel — two slightly different building styles on opposite sides of the main
road, two taverns instead of one (each faction's gathering place), a damaged or vandalized structure in the middle.

**TERRITORIAL_WAR**
Modifiers: DEFENSE +0.25, FARMING -0.15, TRADE -0.10
Narrative: "War came to the borders. The village survived, but at cost."
Visual: Full walls even if DEFENSE isn't a trait, old battle damage on outer buildings (missing sections, patched
walls), a war memorial, abandoned farmland outside the walls (fields that were torched), veteran NPCs with scars (named
armor stands as memorial).

**PIRATE_RAIDS**
Modifiers: DEFENSE +0.20, SEAFARING -0.10, FISHING -0.05
Requires: Ocean or large river present
Narrative: "Sea raiders struck from the water. The harbor was fortified."
Visual: A coastal watchtower, chain across the harbor entrance (fence gates between posts), reinforced dock structures,
a captured pirate flag displayed as trophy.

**CULT_DRIVEN_OUT**
Modifiers: SPIRITUAL +0.15, DEFENSE +0.10, ARCANE -0.15
Narrative: "Dark practitioners once held influence here. They were expelled."
Visual: A burned or demolished structure on the outskirts (the old cult meeting place), protective wards (soul torches
at village entrances), the church has an aggressive/militant feel (iron doors, reinforced), villagers are suspicious of
magic.

---

## Cultural Events — "What defines who we are?"

These shape identity without necessarily being about survival.

**FESTIVAL_TRADITION**
Modifiers: HOSPITALITY +0.10, TRADE +0.10, SPIRITUAL +0.05
Narrative: "An annual festival draws visitors from far away."
Visual: A festival ground (open area with wool canopy, tables, a stage), decorative banners everywhere, lantern strings
between buildings, the tavern has an outdoor seating area.

**ISOLATIONIST_POLICY**
Modifiers: DEFENSE +0.10, TRADE -0.20, HOSPITALITY -0.15, CRAFT +0.10
Narrative: "The village decided to rely on no one but themselves."
Visual: Single narrow gate entrance, no signposts pointing outward, self-sufficient infrastructure (their own forge even
if CRAFT isn't a trait), walls or thick hedges around the perimeter, suspicious-looking guard NPCs.

**KNOWLEDGE_KEEPER**
Modifiers: SCHOLARLY +0.25, SPIRITUAL +0.10, ARCANE +0.05
Narrative: "A great library or archive was established. Knowledge became the village's currency."
Visual: Oversized library building, lecterns with written books around the village, a scholar's quarter (cluster of
small houses near the library), bookshelves visible through windows of multiple buildings.

**NATURE_PACT**
Modifiers: LUMBER -0.10, FARMING +0.10, HONEY +0.15, SPIRITUAL +0.10
Requires: Forest biome
Narrative: "The villagers swore to live with the forest, not against it."
Visual: Buildings integrated into the treeline rather than clearing it, living trees growing through/beside structures,
leaf block roofs mixed with wood, flower gardens everywhere, no sawmill even if LUMBER trait present (they harvest
carefully, not industrially). Very Tolkien-esque.

**ENGINEERING_BREAKTHROUGH**
Modifiers: CRAFT +0.15, MINING +0.10, TRADE +0.10
Narrative: "An invention changed everything — a new smelting technique, a water wheel, a better plow."
Visual: One notably advanced structure (a complex redstone-decorated watermill, an unusually large smelter, an automated
farm with water channels), the village feels slightly ahead of its time.

**SEAFARER_RETURNED**
Modifiers: SEAFARING +0.20, TRADE +0.10, SCHOLARLY +0.10
Requires: Water feature present
Narrative: "A great explorer returned with maps, goods, and stories from distant lands."
Visual: Exotic blocks mixed into architecture (jungle wood in a taiga village, sandstone in a forest village — imported
materials), a map room, a trophy hall with foreign items, a large ship or boat at the dock.

**HERMIT_SAGE**
Modifiers: ARCANE +0.15, SPIRITUAL +0.10, SCHOLARLY +0.10
Narrative: "A mysterious figure lives on the outskirts. The village has learned to respect them."
Visual: An isolated cottage far from the village (Zone 5, off the road), with an overgrown garden, enchanting table,
brewing stands. A worn footpath connects it to the village. The hermit is a unique NPC with rare trades. Player
interaction hook — befriend the hermit for unique items.

---

## Scenario Walkthroughs

**Scenario 1: Lumber + Great Fire**

Terrain: Dense forest, flat, river on the south edge.
Traits rolled: LUMBER (primary), FISHING (secondary), DEFENSE (flavor).
History: FOUNDED_BY_EXPLORERS, GREAT_FIRE, MASTER_CRAFTSMAN_ARRIVED.

The story: An expedition found this forest and set up a logging operation. It thrived — until the fire. Half the lumber
yard burned. The charred remains still stand on the east side of town. After the fire, they rebuilt with more stone than
wood, learned to respect firebreaks. Then a master carpenter arrived, drawn by the cheap lumber, and set up the finest
woodworking shop in the region. The village now exports finished furniture and building timber, not raw logs.

What generates: The main road runs toward the river (log floating route). The sawmill sits riverside. Logging camps dot
the forest edge — but on the WEST side, not the east. The east side has charred stumps and 1-2 burnt building frames (
untouched, memorial). The carpenter's workshop is notably larger and more ornate than other buildings. Stone brick
foundations appear on buildings that would normally be all-wood. A fire memorial stone sits in the town square. The
village has a palisade fence (DEFENSE flavor) — the fire made them security-conscious. A small dock with fish-drying
racks sits at the river (FISHING secondary, modest).

**Scenario 2: Mining + Earthquake + Rich Vein**

Terrain: Mesa edge, elevation changes, exposed stone, small river.
Traits rolled: MINING (primary), CRAFT (secondary), TRADE (secondary), DEFENSE (flavor).
History: FOUNDED_BY_PROSPECTORS, EARTHQUAKE, RICH_VEIN_STRUCK.

The story: Prospectors found ore in the mesa. They set up camp, then a proper mine. Then the earthquake hit — the
original shaft collapsed, killing several miners. They sealed it and dug elsewhere. The new shaft hit a rich vein of
gold ore. Wealth followed, craftsmen came to work the metal, merchants came to buy it. The village is now prosperous but
haunted by the collapse — they over-invest in structural engineering and mine safety.

What generates: Two mine entrances: one sealed with cobblestone and a memorial sign (the collapsed original), one active
with iron bar reinforcement and heavy timber framing (the new one). Minecart rails run from the active mine to a large
smelter compound. The smelter feeds an artisan workshop quarter (CRAFT secondary) where armorers and toolsmiths work the
refined metal. A trading post and small warehouse sit near the main road (TRADE secondary). The town is wealthier than
expected — stone brick buildings, the town hall has gold block accents. A monument in the square honors the miners lost
in the earthquake. Extra lanterns and torches everywhere (miners' superstition after the collapse). A palisade fence
lines the perimeter (DEFENSE flavor). The "first strike" monument — an ore block in glass — sits near the sealed mine.

**Scenario 3: Farming + Plague + Nature Pact**

Terrain: Open plains, gentle hills, flower meadow on the west side, small pond.
Traits rolled: FARMING (primary), HONEY (secondary), SPIRITUAL (flavor).
History: FOUNDED_BY_MONKS, PLAGUE, NATURE_PACT.

The story: Monks established a quiet agricultural retreat. The fields were bountiful, the bees flourished in the nearby
meadow. Then plague struck — the graveyard expanded rapidly, houses stood empty. In their grief, the surviving monks
made a pact with the land: they would farm gently, tend the bees, and never take more than needed. The village recovered
slowly, guided by spiritual principles and sustainable farming.

What generates: The hub is a modest temple, not a town hall (FOUNDED_BY_MONKS makes the shrine the oldest building,
SPIRITUAL flavor keeps it prominent). The temple has an herb garden — medicinal plants grown since the plague (PLAGUE
visual). Surrounding the temple: extensive but carefully managed crop fields (multiple crop types, not monoculture —
FAMINE prevention instinct from the plague). An expanded graveyard sits south of the temple with many headstones (PLAGUE
scar). 2-3 houses are boarded up and overgrown (PLAGUE — population never fully recovered). West side toward the meadow:
an apiary complex with flower gardens (HONEY secondary). A mead brewery sits between the apiary and the tavern. The
whole village feels gentle — flower pots on every windowsill (NATURE_PACT), living trees left standing between
buildings, leaf block accents on roofs. No heavy industry. A small roadside shrine sits at the village entrance. The
mood is peaceful but melancholy — beautiful, well-tended, but clearly scarred.

**Scenario 4: Fishing + Pirate Raids + Alliance Formed**

Terrain: Ocean coast, rocky cliff to the north, sandy beach to the south, river mouth.
Traits rolled: FISHING (primary), SEAFARING (secondary), DEFENSE (secondary), TRADE (flavor).
History: FOUNDED_BY_FISHERMEN, PIRATE_RAIDS, ALLIANCE_FORMED.

The story: Fishing families settled the coast where the river meets the sea. Good waters, sheltered cove. Then pirates
found them too. Raids burned the first dock, took their catch, terrorized the village. In desperation they reached out
to an inland settlement for help — an alliance was formed. Soldiers came, a watchtower went up, the harbor was
fortified. Now the village is a hardened fishing port: productive but vigilant, with trade connections inland.

What generates: The village faces the ocean (FISHING primary + SEAFARING secondary reorient the layout). Multiple docks
along the cove, with a chain barrier across the harbor mouth (fence gates between posts — PIRATE_RAIDS visual). A
coastal watchtower on the northern cliff (PIRATE_RAIDS + DEFENSE secondary). The oldest dock is weathered and patched
many times (FOUNDED_BY_FISHERMEN). A small shipyard sits partially in the water (SEAFARING secondary — not full scale,
but boat repairs). Fish-drying racks, a smokehouse, and a fisherman's guild cluster near the docks. A captured pirate
flag hangs on the watchtower (trophy). Stone walls protect the landward side (DEFENSE secondary). Inland: a diplomacy
hall attached to the town hall, twin banners at the gate (ALLIANCE_FORMED — the allied settlement's colors alongside
their own). A maintained road leads inland toward the ally. A modest trading post near the gate handles goods flowing
between the two settlements (TRADE flavor). The overall feel: a working fishing port that learned to defend itself, now
cautiously connected to the wider world.

**Scenario 5: The Abandoned Ancient Village**

Terrain: Dark forest edge, near a trail ruin, river.
Traits rolled: ANCIENT (primary), LUMBER (secondary), SPIRITUAL (flavor).
History: FOUNDED_ON_ANCIENT_SITE, PLAGUE, CULT_DRIVEN_OUT.
Abandoned roll: YES (30% chance triggered).

The story: They built on ancient ruins, drawn by the carved stones and buried relics. The village grew around the
excavation. Then a cult formed around the relics — worshipping whatever the ancients had worshipped. The rest of the
village drove them out, but the conflict weakened the community. When plague followed shortly after, the remaining
villagers saw it as a curse. They fled. The village stands empty now. Mostly.

What generates: The layout is fully generated — roads, plots, buildings — but everything is in decay. Buildings have
holes in roofs (stair blocks removed, leaving gaps to sky), vines crawl up walls, moss covers foundations. Tall grass
and ferns clip through floors. No torches — the village is dark, mobs spawn inside. The excavation site is intact and
overgrown — brushable blocks still present (the relics are still there, undiscovered). An ancient stone foundation is
visible under the ruined temple (FOUNDED_ON_ANCIENT_SITE). A burned structure on the north edge — the cult's meeting
place, demolished after they were expelled (CULT_DRIVEN_OUT). Protective soul torches at the village entrance, still
burning (the last act of the faithful). An overgrown graveyard with too many headstones for the village's size (PLAGUE).
Instead of villagers: zombie villagers spawn in 3-4 buildings, wearing the profession gear of their former lives (a
zombie farmer in the farmhouse, a zombie cleric in the temple). A sawmill sits at the forest edge, a massive tree
growing through its collapsed roof (LUMBER secondary, abandoned). Player opportunity: cure the zombie villagers, clear
the mobs, repair structures. The village slowly comes back to life. The excavation site yields ancient relics. The
cult's burned meeting place might still contain a hidden cellar with... something.
