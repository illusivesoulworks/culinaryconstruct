# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
Prior to version 5.0.0, this project used MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

## [5.1.1+1.19.4] - 2023.10.23
### Changed
- Updated to SpectreLib 0.12.6
- [Fabric] Requires Fabric Loader >=0.14.23

## [5.1.0+1.19.4] - 2023.03.23
### Added
- Added Quilt support
### Changed
- Updated to Minecraft 1.19.4

## [5.0.0+1.19.2] - 2023.03.21
### Added
- Added AppleSkin integration [#66](https://github.com/illusivesoulworks/culinaryconstruct/issues/66)
- Added Meal API integration [#65](https://github.com/illusivesoulworks/culinaryconstruct/issues/65)
### Changed
- Updated SpectreLib to 0.12.4+1.19.2
- Naming text box for the Culinary Station now only focuses when clicked on [#63](https://github.com/illusivesoulworks/culinaryconstruct/issues/63)

## [5.0.0-beta.1+1.19.2] - 2022.12.06
### Added 
- [Fabric] Added Fabric version using the [MultiLoader template](https://github.com/jaredlll08/MultiLoader-Template)
### Changed
- Configuration system is now provided by SpectreLib
- Configuration file is now located in the root folder's `configs` folder
- Changed to [Semantic Versioning](http://semver.org/spec/v2.0.0.html)
- Updated to Minecraft 1.19.2
- [Forge] Updated to Forge 43+

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

## [1.16.1-3.0.0.2] - 2020.08.11
### Changed
- Updated to Forge 32.0.107 [#40](https://github.com/TheIllusiveC4/CulinaryConstruct/pull/40)

## [1.16.1-3.0.0.1] - 2020.08.02
### Fixed
- Fixed duplication issue when shift-clicking output [#39](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/39)

## [1.16.1-3.0.0.0] - 2020.07.06
### Changed
- Ported to 1.16.1 Forge

## [1.15.2-2.0.0.3] - 2020.12.31
### Fixed
- Fixed contents being deleted when the Culinary Station is broken [#43](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/43)

## [1.15.2-2.0.0.2] - 2020.08.02
### Fixed
- Fixed duplication issue when shift-clicking output [#39](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/39)

## [1.15.2-2.0.0.1] - 2020.07.05
### Fixed
- Fixed eating bowls giving back the incorrect stack

## [1.15.2-2.0.0.0] - 2020.06.07
### Changed
- Ported to 1.15.2

## [1.14.4-2.0.0.0-beta3] - 2020.04.10
### Changed
- Disabled automatic extraction from the Culinary Station due to duplication bug
### Fixed
- Fixed any item being able to be piped into the Culinary Station
- Fixed creative Food Bowl item showing an empty texture

## [1.14.4-2.0.0.0-beta2] - 2020.02.09
### Changed
- Renamed "bowl" tag to "bowls"

## [1.14.4-2.0.0.0-beta1] - 2020.02.08
### Added
- Bowls of food can now be crafted at the Culinary Station using bowls as a base.
    - Buckets of fluid, Milk Buckets, and Potions can be added to bowls
    - Bowls will always only output one item
- Added "bowl" forge tag that is used for identifying valid bowls
### Changed
- Updated to Forge 1.14.4
- Sandwich Station renamed to Culinary Station now that bowls of food are also possible
- Advancement renamed to Culinarian now that bowls of food are possible
- Moved previous "bread" ore dictionary entry to "bread" forge tag for identifying valid breads
### Removed
- Removed nesting of sandwiches

## [1.12.2-1.3.4.0] - 2020.04.21
### Added
- Added Bread Blacklist config option [#37](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/37)

## [1.12.2-1.3.3.2] - 2020.02.09
### Added
- Roots Wildewheet Bread support

## [1.12.2-1.3.3.1] - 2019.09.05
### Added
- Gobber Glob Bread support
### Changed
- Foods with container items will now get that container back when crafting sandwiches

## [1.12.2-1.3.3.0] - 2019.06.08
### Added
- Sandwich nesting (thank you Alwinfy) [#29](https://github.com/TheIllusiveC4/CulinaryConstruct/pull/29)

## [1.12.2-1.3.2.1] - 2019.04.10
### Fixed
- Fixed Sandwich Station breaking instantly and silently [#28](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/28)

## [1.12.2-1.3.2.0] - 2019.02.03
### Added
- Nutrition support [#25](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/25)

## [1.12.2-1.3.1.0] - 2018.12.31
### Added
- Config option for additional bread items [#24](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/24)

## [1.12.2-1.3.0.1] - 2018.12.26
### Changed
- Updated Polish translation (thank you Pabilo8)

## [1.12.2-1.3.0.0] - 2018.12.15
### Added
- Sandwich Advancement
- Config option for max sandwich food value per sandwich [#22](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/22)
- Compatibility for 10pal's Plant Mega Pack Cornbread
- Integration with AppleCore's IEdible [#22](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/22)
- [API] ICulinaryIngredient API interface [#22](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/22)

## [1.12.2-1.2.0.1] - 2018.09.26
### Changed
- Updated Russian translation (thank you kellixon)
### Fixed
- Server crashing [#20](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/20)

## [1.12.2-1.2.0.0] - 2018.09.26
### Changed
- Default name for sandwiches now lists ingredients used
### Fixed
- Syncing errors
- Item piping not respecting slot restrictions [#7](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/7)

## [1.12.2-1.1.2.0] - 2018.09.12
### Added
- Reliquary breads support
- Ashenwheat breads support
- Config options to control blacklisting ingredients [#10](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/10)
### Fixed
- Updated Russian translation (thank you AlekseiVoronin and Prosta4okua)

## [1.12.2-1.1.1.0] - 2018.07.28
### Added
- Actually Additions breads support
- Cake as a valid sandwich ingredient
- Russian translation (thank you DenisMasterHerobrine)
- Korean translation (thank you SeolWha)
- Polish translation (thank you Pabilo8)
- German translation (thank you Hendrik)

## [1.12.2-1.1.0.0] - 2018.06.24
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

## [1.12.2-1.0.0.0] - 2018.06.23
- Initial release
