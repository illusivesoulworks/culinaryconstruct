# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

## [1.1.1] - 2018.07.28
### Added
- Actually Additions breads support
- Cake as a valid sandwich ingredient
- Russian translation (thank you DenisMasterHerobrine)
- Korean translation (thank you SeolWha)
- Polish translation (thank you Pabilo8)
- German translation (thank you Hendrik)

## [1.1.0] - 2018.06.24
### Added
- OreDict entry for "bread"
- Added Chinese localization (thank you DYColdWind)
### Changed
- Sandwiches can no longer be nested into other sandwiches to prevent potentially long NBT tags
- All bread checks now use the new "bread" oreDict entry
### Fixed
- Sandwich output not updating after crafting [#4](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/4)
- Sandwich names not being retained when using 2 or more ingredients [#2](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/2)
- Sandwich station inventory not persisting
- Bread item shift-clicking not prioritizing the bread slot in the Sandwich Station [#5](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/5)

## [1.0.0] - 2018.06.23