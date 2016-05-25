# Sincron

<img src="https://raw.githubusercontent.com/monixio/sincron/c4430365650296dcf9d17e7d98e3ad9af8a7f018/docs/src/site/source/assets/logo/logo-400.png"
    align="right" width="150" />

Low-level concurrency tools for Scala with equivalents for Scala.js.

This projects aims to offer higher-level APIs for JVM's concurrency tools,
along with useful and high-performance concurrent mutable data-structures.

[![Build Status](https://travis-ci.org/monixio/sincron.svg?branch=master)](https://travis-ci.org/monixio/sincron)
[![Coverage Status](https://codecov.io/github/monixio/sincron/coverage.svg?branch=master)](https://codecov.io/github/monixio/sincron?branch=master)
[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.sincron/sincron_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.sincron/sincron_2.11)

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/monixio/monix?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Usage

Sincron is by design being split in small and reusable sub-projects, to be
added as they become available. Specifying dependencies
(replace `%%` with `%%%` for targeting Scala.js):

```scala
// Atomic References
libraryDependencies += "org.sincron" %% "sincron-atomic" % "0.14"

// Everything :-)
libraryDependencies += "org.sincron" %% "sincron" % "0.14"
```

Tutorials:

- [Atomic References](https://github.com/monixio/sincron/wiki/Atomic-References)

API Documentation:

- [0.14](https://sincron.org/docs/0.14/api/)

## Maintainers

The current maintainers (people who can help you) are:

- Alexandru Nedelcu ([@alexandru](https://github.com/alexandru))
- Andrei Oprisan ([@aoprisan](https://github.com/aoprisan))

## Contributing

The Sincron project welcomes contributions from anybody wishing to
participate.  All code or documentation that is provided must be
licensed with the same license that Sincron is licensed with.

The Sincron project supports the
[Typelevel Code of Conduct](http://typelevel.org/conduct.html) when
discussing Sincron on the Github page, Gitter channel, or other venues.
We hope that our community will be respectful, helpful and kind.

Feel free to open an issue if you notice a bug, have an idea for a
feature, or have a question about the code. Pull requests are also
gladly accepted. For more information, check out the
[contributor guide](CONTRIBUTING.md).

## License

All code in this repository is licensed under the Apache License, Version 2.0.
See [LICENCE.txt](./LICENSE.txt).
