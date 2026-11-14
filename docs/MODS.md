# Mods

Both launchers have a **Mods** section (PC: the **Mods** tab; Quest: a game's options page, **Mods**), laid out as
Ratchet and Clank Quest VR's: the mods that come with the release, each with its switch, and the ones you add from a
file.

## Multiplayer (Jak 1), built in

[Teamruns](https://github.com/JoKronk/teamruns-jak-project)' multiplayer with this port's own games, list of games and
saves: see [MULTIPLAYER.md](MULTIPLAYER.md). It is on to start and changes nothing in Jak 1 until you host or join a
game; **Multiplayer: Off** keeps it out of the game altogether. Your name and the party server are set beside it.

## Texture packs (all three games, PC and Quest)

OpenGOAL's own kind of mod, the same zips as the OpenGOAL launcher's **Texture Packs**: pictures (PNG) that replace the
game's own, at `custom_assets/<game>/texture_replacements/<texture page>/<texture>.png` in the zip (packs with
`texture_replacements` at their top work too), with a `metadata.json` naming the pack.

- **Add a texture pack (.zip)** for a game, then turn each on or off and put them in order: where two packs change the
  same picture, the one higher in the list wins. Remove takes a pack out.
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

On PC, OpenGOAL's code mods (the OpenGOAL mod launcher's) are their own builds of the game, without VR; texture packs
are the mods this port's PC launcher takes.
