# Asterix

<img src="https://raw.githubusercontent.com/monifu/asterix/8cf94d21a26e6a42d85c23dcfbc5f38b7f2d4454/logo.png" align="right" width="200" />

Low level primitives and building blocks for Scala and Scala.js

> "Ils sont fous ces Romains!" -- Asterix the Gaul

[![Build Status](https://travis-ci.org/monifu/asterix.png?branch=master)](https://travis-ci.org/monifu/asterix)
[![Coverage Status](https://coveralls.io/repos/monifu/asterix/badge.svg?branch=master&service=github)](https://coveralls.io/github/monifu/asterix?branch=master)
[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.monifu/asterix_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.monifu/asterix_2.11)

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/monifu/monix?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Usage

Asterix is being split in small and reusable packages. Some will provide
a Scala.js / Javascript compatible implementation (within reason) for
helping with cross compiled code.

### Atomic References (asterix-atomic)

See [Atomic References](https://github.com/monifu/asterix/wiki/Atomic-References)
in the Wiki.

Dependency for the JVM:

```scala
libraryDependencies += "org.monifu" %% "asterix-atomic" % "0.1"
```

For [Scala.js](https://github.com/monifu/asterix/wiki/Atomic-References)
/ Javascript or for cross-compiled projects:

```scala
libraryDependencies += "org.monifu" %%% "asterix-atomic" % "0.1"
```

## Maintainers

The current maintainers (people who can help you) are:

- Alexandru Nedelcu ([@alexandru](https://github.com/alexandru))
- Andrei Oprisan ([@aoprisan](https://github.com/aoprisan))

## Contributing

The Asterix project welcomes contributions from anybody wishing to participate.
All code or documentation that is provided must be licensed with the same
license that Asterix is licensed with (Apache 2.0, see LICENSE.txt).

People are expected to follow the [Typelevel Code of Conduct](http://typelevel.org/conduct.html)
when discussing Asterix on the Github page, Gitter channel, or other venues.

We hope that our community will be respectful, helpful, and kind. If you find
yourself embroiled in a situation that becomes heated, or that fails to live up
to our expectations, you should disengage and contact one of the project maintainers
in private. We hope to avoid letting minor aggressions and misunderstandings
escalate into larger problems.

Feel free to open an issue if you notice a bug, have an idea for a feature, or
have a question about the code. Pull requests are also gladly accepted. For more information,
check out the [contributor guide](CONTRIBUTING.md).

## License

All code in this repository is licensed under the Apache License, Version 2.0.
See [LICENCE.txt](./LICENSE.txt).
