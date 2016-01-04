# Scalax

<img src="https://raw.githubusercontent.com/monifu/scalax/8cf94d21a26e6a42d85c23dcfbc5f38b7f2d4454/logo.png" align="right" width="200" />

The missing Scala standard library. The purpose of Scalax is to
build missing interfaces and functionalities that should be standard
and to (hopefully) push these as [SLIPs](https://github.com/scala/slip).

The name `scalax` mirrors the `javax` package for Java and in
`javax` that's where extensions to an existing JRE go.

The project is broken into multiple sub-projects, so you can use just the
pieces that you want.

[![Build Status](https://travis-ci.org/monifu/scalax.svg?branch=master)](https://travis-ci.org/monifu/scalax)
[![Coverage Status](https://codecov.io/github/monifu/scalax/coverage.svg?branch=master)](https://codecov.io/github/monifu/scalax?branch=master)
[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.monifu/scalax_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.monifu/scalax_2.11)

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/monifu/scalax?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Usage

Scalax is being split in small and reusable packages. Some will provide
a Scala.js / Javascript compatible implementation (within reason) for
helping with cross compiled code.

### Atomic References (scalax-atomic)

See [Atomic References](https://github.com/monifu/scalax/wiki/Atomic-References)
in the Wiki.

Dependency for the JVM:

```scala
libraryDependencies += "org.monifu" %% "scalax-atomic" % "0.2"
```

For [Scala.js](https://github.com/monifu/scalax/wiki/Atomic-References)
/ Javascript or for cross-compiled projects:

```scala
libraryDependencies += "org.monifu" %%% "scalax-atomic" % "0.2"
```

## Maintainers

The current maintainers (people who can help you) are:

- Alexandru Nedelcu ([@alexandru](https://github.com/alexandru))
- Andrei Oprisan ([@aoprisan](https://github.com/aoprisan))

## Contributing

The Scalax project welcomes contributions from anybody wishing to participate.
All code or documentation that is provided must be licensed with the same
license that Scalax is licensed with (Apache 2.0, see LICENSE.txt).

People are expected to follow the [Typelevel Code of Conduct](http://typelevel.org/conduct.html)
when discussing Scalax on the Github page, Gitter channel, or other venues.

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
