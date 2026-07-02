<div align="center">

<img src="resources/images/MSC_128x128.png" width="96" alt="MultisellCreator logo">

# MultisellCreator

A small Java desktop tool for creating **multisell XML** files for L2J **Mobius** servers.

</div>

Point it at your server datapack and it lists every item with its icon. Drag items into a
trade, set amounts, and export a clean, server-ready multisell XML - no hand-editing.

## Features

- Loads live items (with icons) straight from your datapack
- Drag-and-drop or right-click to build ingredients and products
- Multisell settings: npcs, applyTaxes, maintainEnchantment, useRate
- Native-style XML output (tab indent, inline item-name comments)
- Dark / light theme, remembers your last datapack, folder and window size
- Handles large packs (tens of thousands of items) without freezing

## Requirements

- Java 25
- [Apache Ant](https://ant.apache.org/) (to build - bundled with Eclipse, or install standalone for command-line builds)

## Build & Run

**In Eclipse:** right-click `build.xml` -> **Run As -> Ant Build**.

**From a terminal** (with Ant installed):

```bash
ant
```

Either way, the build creates a `release/` folder **next to** the project folder
(one level up), containing `MultisellCreator.jar` and a Windows launcher. Run it with:

```bash
java -jar ../release/MultisellCreator.jar
```

On Windows, just double-click `MultisellCreator.bat` in that `release/` folder.

## Usage

1. **File > Open Datapack** - select your server `game` folder (or its `data` folder).
2. **File > New** (or **Open**) a multisell.
3. Drag items into the ingredients / products of each entry and set amounts.
4. **File > Save** - enter the multisell id; it writes `<id>.xml` into the datapack.

## License

MIT - see [LICENSE](LICENSE).

## Disclaimer

Lineage 2 is a trademark of NCsoft. The bundled game icons and sounds are
property of NCsoft and are included for convenience only - they are not
covered by this project's MIT license.
