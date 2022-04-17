# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

## [1.18.2-4.3.1.0] - 2022.04.17
### Added
- Added AppleSkin integration [#34](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/34)
- Added `showNutritionInfo` configuration option for showing nutrition and saturation information in tooltips for
  sandwiches and food bowls

## [1.18.2-4.3.0.0] - 2022.04.13
### Changed
- Updated to and requires Forge 40.0.47+
- Mods can now retrieve the proper nutrition and saturation modifier amounts from sandwiches through `IForgeItem#getFoodProperties(ItemStack, LivingEntity)`

## [1.18.2-4.2.0.2] - 2022.03.03
### Changed
- Updated to Minecraft 1.18.2
- Culinary Station added to the `minecraft:mineable/axe` block tag, allowing axe tools to mine it faster [#59](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/59)

## [1.18.1-4.2.0.1] - 2022.01.26
### Fixed
- Fixed Culinary Station not retaining inventory on load

## [1.18.1-4.2.0.0] - 2022.01.20
### Changed
- Updated to Minecraft 1.18.1
- Updated to Forge 38.0+

## [1.17.1-4.1.0.0] - 2022.01.20
### Changed
- Updated to Minecraft 1.17.1
- Updated to Forge 37.0+

## [1.16.5-4.0.0.7] - 2022.01.19
### Added
- Added `es_es.json` localization (thanks albertosaurio65!) [#53](https://github.com/TheIllusiveC4/CulinaryConstruct/pull/53)
### Fixed
- Fixed Suspicious Stew not applying effects when used as an ingredient [#51](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/51)

## [1.16.5-4.0.0.6] - 2021.02.25
### Fixed
- Fixed some bugs using container ingredient stacks [#49](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/49)

## [1.16.5-4.0.0.5] - 2021.02.21
### Added
- Added Diet integration

## [1.16.5-4.0.0.4] - 2021.02.08
### Fixed
- Fixed potion effects using global state [#48](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/48)

## [1.16.5-4.0.0.3] - 2021.01.18
### Changed
- Updated to Minecraft 1.16.5
### Fixed
- Fixed buckets and potions being consumed on crafting [#47](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/47)

## [1.16.4-4.0.0.2] - 2020.12.31
### Changed
- Updated to Minecraft 1.16.4
### Fixed
- Fixed contents being deleted when the Culinary Station is broken [#43](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/43)

## [1.16.3-4.0.0.1] - 2020.09.27
### Changed
- Updated to Minecraft 1.16.3

## [1.16.2-4.0.0.0] - 2020.09.08
### Changed
- Updated to Minecraft 1.16.2

## [3.0.0.2] - 2020.08.11
### Changed
- Updated to Forge 32.0.107 [#40](https://github.com/TheIllusiveC4/CulinaryConstruct/pull/40)

## [3.0.0.1] - 2020.08.02
### Fixed
- Fixed duplication issue when shift-clicking output [#39](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/39)

## [3.0] - 2020.07.06
### Changed
- Ported to 1.16.1 Forge