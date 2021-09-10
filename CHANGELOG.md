# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
- Upgraded kotlin version to 1.5.0
- Upgraded kotlinx-serialization version to 1.2.2

## [0.2.1] - 2020-09-07
### Added
- iOS target support ([#40][[p40])

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

[Unreleased]: https://github.com/esensar/kotlinx-serialization-msgpack/compare/0.2.1...main
[0.2.0]: https://github.com/esensar/kotlinx-serialization-msgpack/compare/0.1.0...0.2.0
[0.2.1]: https://github.com/esensar/kotlinx-serialization-msgpack/compare/0.2.0...0.2.1
[i6]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/6
[i9]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/9
[i11]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/11
[i13]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/13
[i14]: https://github.com/esensar/kotlinx-serialization-msgpack/issues/14
[p40]: https://github.com/esensar/kotlinx-serialization-msgpack/pull/40
