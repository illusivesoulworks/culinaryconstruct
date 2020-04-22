# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

## [1.3.4] - 2020.04.21
### Added
- Added Bread Blacklist config option [#37](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/37)

## [1.3.3.2] - 2020.02.09
### Added
- Roots Wildewheet Bread support

## [1.3.3.1] - 2019.09.05
### Added
- Gobber Glob Bread support
### Changed
- Foods with container items will now get that container back when crafting sandwiches

## [1.3.3] - 2019.06.08
### Added
- Sandwich nesting (thank you Alwinfy) [#29](https://github.com/TheIllusiveC4/CulinaryConstruct/pull/29)

## [1.3.2.1] - 2019.04.10
### Fixed
- Fixed Sandwich Station breaking instantly and silently [#28](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/28)

## [1.3.2] - 2019.02.03
### Added
- Nutrition support [#25](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/25)

## [1.3.1] - 2018.12.31
### Added
- Config option for additional bread items [#24](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/24)

## [1.3.0.1] - 2018.12.26
### Changed
- Updated Polish translation (thank you Pabilo8)

## [1.3.0] - 2018.12.15
### Added
- Sandwich Advancement
- Config option for max sandwich food value per sandwich [#22](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/22)
- Compatibility for 10pal's Plant Mega Pack Cornbread
- Integration with AppleCore's IEdible [#22](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/22)
- [API] ICulinaryIngredient API interface [#22](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/22)

## [1.2.0.1] - 2018.09.26
### Changed
- Updated Russian translation (thank you kellixon)
### Fixed
- Server crashing [#20](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/20)

## [1.2.0] - 2018.09.26
### Changed
- Default name for sandwiches now lists ingredients used
### Fixed
- Syncing errors
- Item piping not respecting slot restrictions [#7](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/7)

## [1.1.2] - 2018.09.12
### Added
- Reliquary breads support
- Ashenwheat breads support
- Config options to control blacklisting ingredients [#10](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/10)
### Fixed
- Updated Russian translation (thank you AlekseiVoronin and Prosta4okua)

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