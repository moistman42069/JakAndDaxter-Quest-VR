# Mods

Both launchers have a **Mods** section (PC: the **Mods** tab; Quest: a game's options page, **Mods**), laid out as
Ratchet and Clank Quest VR's: what works here, the mods that come with the release (each with its switch), the ones you
added (each with On / Off, and **Update** when its list has a newer one), and **Browse mods**.

## What works here, and what does not

- **Multiplayer (Jak 1)** comes built in.
- **Texture packs** (all three games, PC and Quest): new pictures in place of the game's own.
- **Mod packages built for the Quest** (Quest only).
- **OpenGOAL's own mods** (the ones in the OpenGOAL launcher's Mods and on [jakmods.dev](https://jakmods.dev): new
  gameplay, levels, randomizers, speedrun tools) are whole games of their own: each has its own program (gk) and its own
  compiled game code, made for a flat screen. They cannot run in VR, so this port cannot add them; Browse mods lists
  them as **Flat screen only**, with their page, so players can see what there is. (A mod made for VR from one of them
  would be built on this port's own source, as Multiplayer was from Teamruns.)

## Browse mods

Both launchers read two lists, both in OpenGOAL's own mod list format (the OpenGOAL launcher's
`schemas/mod-source/v1`):

- **This project's list**, [`mods/mods.json`](../mods/mods.json) in this repository: texture packs and the Quest's mod
  packages made or checked for VR.
- **jakmods.dev's list** (`https://jakmods.dev/mods.json`), the one the OpenGOAL launcher's Mods reads: its texture
  packs can be added; its other mods show as Flat screen only.

A list is read when you press **Browse mods** (or **Check again**), and as the launcher starts while automatic update
checks are on (PC: the Play tab's Updates; Quest: the launcher's own). The last good copy is kept and shown, with a note,
when a list cannot be read. For each game, Browse mods shows each mod's name, version, authors, what it is here and
where it is from: **Add** for a texture pack (and on the Quest, a mod package), **Update to ...** once one you added
has a newer version in its list, **Added** when you have the newest, and **Its page**. Files are downloaded only from
GitHub over HTTPS (at most 2 GB), then added exactly as a file you chose would be.

### Adding a mod to this project's list

An entry in `mods/mods.json` is the OpenGOAL format's: under `texturePacks` (a texture pack) or `mods` (a Quest mod
package, its version's asset named `quest`), keyed by a short id, with `displayName`, `description`, `authors` (a
list), `tags`, `supportedGames` (`jak1`, `jak2`, `jak3`) and `versions`, each version with `version`, `publishedDate`
and `assets` (a texture pack's zip under `any`; a Quest package's under `quest`), and, this project's own, the file's
`size` (bytes) and `sha256`, which the launchers check. The newest version (by number) is the one offered; its file
must be a GitHub release's. Only add a pack whose licence lets it be shared.

```json
"texturePacks": {
  "example-pack": {
    "displayName": "Example pack", "description": "What it changes.", "authors": ["Someone"], "tags": [],
    "supportedGames": ["jak1"],
    "versions": [{"version": "1.0.0", "publishedDate": "2026-11-15",
      "assets": {"any": "https://github.com/someone/example-pack/releases/download/v1.0.0/example-pack.zip"},
      "size": 12345678, "sha256": "<64 hex digits>"}]
  }
}
```

(`launcher/common/src/dev/jakquest/vr/ModList.java`, shared by both launchers.)

## Multiplayer (Jak 1), built in

[Teamruns](https://github.com/JoKronk/teamruns-jak-project)' multiplayer with this port's own games, list of games and
saves: see [MULTIPLAYER.md](MULTIPLAYER.md). It is on to start and changes nothing in Jak 1 until you host or join a
game; **Multiplayer: Off** keeps it out of the game altogether. Your name and the party server are set beside it.

## Texture packs (all three games, PC and Quest)

OpenGOAL's own kind of mod, the same zips as the OpenGOAL launcher's **Texture Packs**: pictures (PNG) that replace the
game's own, at `custom_assets/<game>/texture_replacements/<texture page>/<texture>.png` in the zip (packs with
`texture_replacements` at their top work too), with a `metadata.json` naming the pack.

- **Add** one from Browse mods, or **Add a texture pack (.zip)** for a game from a file, then turn each on or off and
  put them in order: where two packs change the same picture, the one higher in the list wins. Remove takes a pack out;
  **Update to ...** (a pack from a list) puts its newer version in its place, on or off as it was.
- The pictures go into the game when it is built from your disc, so a change of packs means building the game again:
  - **PC:** the pack's card offers **Build the game again with them**, and the Play tab says so too. It uses the disc
    extracted before (no disc needed) and takes a few minutes.
  - **Quest:** **Prepare again** takes you to the launcher's disc step; choose your disc image again. Your saves and
    settings stay.
- Only pictures under `texture_replacements` are taken from a zip; pictures for another of the games are left out,
  and a path that would leave the folder is refused. A pack may unpack to 3 GB at most.
- The launcher owns `custom_assets/<game>/texture_replacements` in the game's data folder: it is made again from the
  packs on before each build.

Kept in `%LOCALAPPDATA%\JakQuestVR\texture-packs` on PC and in the app's own files on the Quest
(`launcher/common/src/dev/jakquest/vr/TexturePacks.java`, shared by both launchers).

## Mod packages (Quest)

Precursor's mod packages: a zip with a `mod.json` and the game's files built for the headset (ARM64) on a PC with
Precursor's `scripts/android-port/mod_recipe.py` and goalc. **Import a mod (.zip)**, then enable or disable it with the
game closed; enabling backs up the files it replaces and disabling puts them back. Two mods that replace the same file
cannot be on together.

A package's `mod.json` may give its `version`; Browse mods then offers **Update to ...** when this project's list has a
newer one (disable it first, as for any change of a mod).
