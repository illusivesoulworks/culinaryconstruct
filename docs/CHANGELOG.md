# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

## [2.0.0.3] - 2020.12.31
### Fixed
- Fixed contents being deleted when the Culinary Station is broken [#43](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/43)

## [2.0.0.2] - 2020.08.02
### Fixed
- Fixed duplication issue when shift-clicking output [#39](https://github.com/TheIllusiveC4/CulinaryConstruct/issues/39)

## [2.0.0.1] - 2020.07.05
### Fixed
- Fixed eating bowls giving back the incorrect stack

## [2.0] - 2020.06.07
### Changed
- Ported to 1.15.2

## [2.0-beta3] - 2020.04.10
### Changed
- Disabled automatic extraction from the Culinary Station due to duplication bug
### Fixed
- Fixed any item being able to be piped into the Culinary Station
- Fixed creative Food Bowl item showing an empty texture

## [2.0-beta2] - 2020.02.09
### Changed
- Renamed "bowl" tag to "bowls"

## [2.0-beta1] - 2020.02.08
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
- Removed nesting, its presence just makes things get too messy