# Scalax

<img src="https://raw.githubusercontent.com/monifu/scalax/8cf94d21a26e6a42d85c23dcfbc5f38b7f2d4454/logo.png" align="right" width="200" />

The missing Scala standard library. The purpose of Scalax is to
build missing interfaces and functionalities that should be standard
and to (hopefully) push these as [SLIPs](https://github.com/scala/slip).

The name `scalax` mirrors the `javax` package for Java and in
`javax` that's where extensions to an existing JRE go.

[![Build Status](https://travis-ci.org/monifu/scalax.svg?branch=master)](https://travis-ci.org/monifu/scalax)
[![Coverage Status](https://codecov.io/github/monifu/scalax/coverage.svg?branch=master)](https://codecov.io/github/monifu/scalax?branch=master)
[![License](https://img.shields.io/github/license/monifu/scalax.svg)](https://opensource.org/licenses/BSD-3-Clause)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.monifu/scalax_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.monifu/scalax_2.11)

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/monifu/monix?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Usage

Scalax is being split in small and reusable packages. Some will provide
a Scala.js / Javascript compatible implementation (within reason) for
helping with cross compiled code.

### Atomic References (scalax-atomic)

See [Atomic References](https://github.com/monifu/scalax/wiki/Atomic-References)
in the Wiki.

Dependency for the JVM:

```scala
libraryDependencies += "org.monifu" %% "scalax-atomic" % "0.3"
```

For [Scala.js](http://www.scala-js.org/) / Javascript or for cross-compiled projects:

```scala
libraryDependencies += "org.monifu" %%% "scalax-atomic" % "0.3"
```

### Cancelable (scalax-cancelable)

Dependency for the JVM:

```scala
libraryDependencies += "org.monifu" %% "scalax-cancelable" % "0.3"
```

For [Scala.js](http://www.scala-js.org/) / Javascript or for cross-compiled projects:

```scala
libraryDependencies += "org.monifu" %%% "scalax-cancelable" % "0.3"
```

### Scheduler (scalax-scheduler)

Dependency for the JVM:

```scala
libraryDependencies += "org.monifu" %% "scalax-cancelable" % "0.3"
```

For [Scala.js](http://www.scala-js.org/) / Javascript or for cross-compiled projects:

```scala
libraryDependencies += "org.monifu" %%% "scalax-cancelable" % "0.3"
```

### Future Utils (scalax-future)

Dependency for the JVM:

```scala
libraryDependencies += "org.monifu" %% "scalax-future" % "0.3"
```

For [Scala.js](http://www.scala-js.org/) / Javascript or for cross-compiled projects:

```scala
libraryDependencies += "org.monifu" %%% "scalax-future" % "0.3"
```

### Everything (scalax)

Dependency for the JVM:

```scala
libraryDependencies += "org.monifu" %% "scalax" % "0.3"
```

For [Scala.js](http://www.scala-js.org/) / Javascript or for cross-compiled projects:

```scala
libraryDependencies += "org.monifu" %%% "scalax" % "0.3"
```

## Maintainers

The current maintainers (people who can help you) are:

- Alexandru Nedelcu ([@alexandru](https://github.com/alexandru))
- Andrei Oprisan ([@aoprisan](https://github.com/aoprisan))

## Contributing

The Scalax project welcomes contributions from anybody wishing to
participate.  All code or documentation that is provided must be
licensed with the same license that Scalax is licensed with
(see LICENSE.txt) and to sign Scala's Contributor License
Agreement (see the [contributor guide](CONTRIBUTING.md) for details).

People are expected to follow the
[Typelevel Code of Conduct](http://typelevel.org/conduct.html) when
discussing Scalax on the Github page, Gitter channel, or other venues.

We hope that our community will be respectful, helpful, and kind. If
you find yourself embroiled in a situation that becomes heated, or
that fails to live up to our expectations, you should disengage and
contact one of the project maintainers in private. We hope to avoid
letting minor aggressions and misunderstandings escalate into larger
problems.

Feel free to open an issue if you notice a bug, have an idea for a
feature, or have a question about the code. Pull requests are also
gladly accepted. For more information, check out the
[contributor guide](CONTRIBUTING.md).

## License

All code in this repository is licensed under the BSD 3-Clause license.
See [LICENCE.txt](./LICENSE.txt).
