# Change Log

## [Unreleased]


## [1.2.1] - 2024-01-02

### Fixed

 - Support dependency trees with a single root dependency.


## [1.2.0] - 2020-09-16

### Fixed

 - Include the final node of the tree when parsing. Previously it was being dropped which would lead to incorrect diffs when the new tree added a dependency at the end.


## [1.1.0] - 2020-08-25

### Added

 - `-h` and `--help` flags now recognized.

### Changed

 - Dependency trees which are unchanged beneath a changed node are no longer displayed.
 - Group changed nodes in a tree by their Maven coordinate.


## [1.0.0] - 2020-08-24

Initial release


[Unreleased]: https://github.com/JakeWharton/dependency-tree-diff/compare/1.2.1...HEAD
[1.2.1]: https://github.com/JakeWharton/dependency-tree-diff/releases/tag/1.2.1
[1.2.0]: https://github.com/JakeWharton/dependency-tree-diff/releases/tag/1.2.0
[1.1.0]: https://github.com/JakeWharton/dependency-tree-diff/releases/tag/1.1.0
[1.0.0]: https://github.com/JakeWharton/dependency-tree-diff/releases/tag/1.0.0
