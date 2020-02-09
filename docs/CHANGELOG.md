# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project does not adhere to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
This project uses MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

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