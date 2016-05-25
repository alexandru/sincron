## 0.14 (May 25, 2016)

- The design choice in Issue #6 was a mistake, as it doesn't work out
  well and it was reverted. Sorry for the incovenience.

## 0.13 (May 21, 2016)

- -[Issue #6](https://github.com/monixio/sincron/issues/6) - in `AtomicBuilder`
  make the return `R` type param a type member, which should make it possible
  to force an input `T` when building a new instance with `Atomic.apply`- 

## 0.12 (May 10, 2016)

- Add support for Scala 2.12.0-M4
- Upgrade Scala.js version
- Drop scoverage from build on master
- Drop tut from build
- Fix timeouts in tests
