# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
### Added
- Added dynamic serialization support ([#20][i20])
- Added additional module for experimental unsigned support ([#19][i19]) - `serialization-msgpack-unsigned-support`
- Added configuration to prevent number overflows ([#17][i17])
- Added configuration for strict writing, to prevent reducing numbers into more compact format (disabled by default)

### Fixed
- Bug with never decoding `map 32` type

## [0.3.0] - 2021-09-14
**BREAKING CHANGES**:
`MsgPack.default` has been removed and `MsgPack` can be used directly instead, e.g.:

```kotlin
MsgPack.encodeToByteArray(...)
```
instead of:

```kotlin
MsgPack.default.encodeToByteArray(...)
```

### Added
- Upgraded kotlin version to 1.5.0
- Upgraded kotlinx-serialization version to 1.2.2
- Added support for timestamp extension ([#10][i10])
- Added configuration for raw (old str) type compatibility ([#12][i12])
- Added configuration for strict type mode ([#18][i18])

### Fixed
- Bug with failing to decode extension types with variable data size

## [0.2.1] - 2020-09-07
### Added
- iOS target support ([#40][p40])

## [0.2.0] - 2020-03-20
### Added
- [Code of conduct](CODE_OF_CONDUCT.md)
- [Contributing guidelines](CONTRIBUTING.md)
- Issue templates and discussions links
- Support for bin format family ([#6][i6])
- Support for ext format family ([#9][i9])
- Support for custom extension types ([#11][i11])

### Changed
- Fixed [README](README.md)
- Abstracted out reading data ([#13][i13])
- Abstracted data output ([#14][i14])

## 0.1.0 - 2020-12-20
### Added
- `serialization-msgpack` module available on Maven Central as `com.ensarsarajcic.kotlinx:serialization-msgpack:0.1.0`
- `MsgPack` as main access point to the library with static `default` instance
- `MsgPackConfiguration` as placeholder for future configuration options
- `MsgPackDynamicSerializer` as placeholder for future [contextual serializer](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md#contextual-serialization)
- Full implementation of msgpack spec excluding extension types and bin format family

[Unreleased]: https://github.com/esensar/kotlinx-serialization-msgpack/compare/0.3.0...main
[0.2.0]: https://github.com/esensar/kotlinx-serialization-msgpack/compare/0.1.0...0.2.0
[0.2.1]: https://github.com/esensar/kotlinx-serialization-msgpack/compare/0.2.0...0.2.1
[0.3.0]: https://github.com/esensar/kotlinx-serialization-msgpack/compare/0.2.1...0.3.0
[i6]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/6
[i9]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/9
[i10]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/10
[i11]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/11
[i12]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/12
[i13]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/13
[i14]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/14
[i17]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/17
[i18]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/18
[i19]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/19
[i20]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/20
[p40]: https://github.com/esensar/kotlinx-serialization-msgpack/pull/40
