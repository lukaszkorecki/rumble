# Rumble

A set of helpers and tools for a smoother development REPL workflow.

<img alt="Rubmle" height="300" align="right" src="https://w7.pngwing.com/pngs/998/690/png-transparent-rumble-transformers-the-game-shockwave-soundwave-transformers-fictional-character-transformers-the-movie-transformers-the-game.png" />

## Installation

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.lukaszkorecki/rumble.svg)](https://clojars.org/org.clojars.lukaszkorecki/rumble)

### Lein

Add it to `~/.lein/profiles.clj`:

```clojure
{
 :user {:dependencies [[org.clojars.lukaszkorecki/rumble "RELEASE"]]}
 :repl {:dependencies [[org.clojars.lukaszkorecki/rumble "RELEASE"]]}
}
```

### `deps.edn`

This is more flexible approach, as you'd set up a `:dev/rumble` alias and then include it in the REPL-launching command.


## Usage

Best to alias it in some way:

```clojure
(ns scratch) ;; or whatever
(require 'r)
```


From there you can run `(r/help)` and get all of the info


## Managing application lifecycle during development

Rumble assumes a couple of things:

- you're using Component
- your dev system lives in `<app name>.user` namespace by default

These assumptions make for typing fewer lines, because you can pass your own namespace symbol and ensure that the ns exports `start` and `stop` functions to manage the system lifecycle, it *might* work.

From there, you have a lot sorts of tools to pull out components out of the running system, get a reference to it and all of that good stuff.

## Tests

Depends on Kaocha. Usage is as simple as:

- `(r/t!)` to run all tests
- `(r/t! (r/tests #.*bananas.*"))` to run only a matching subset

Tests will not refresh changed namespaces if the dev system is running!
If you have a custom config for Kaocha, set `KAOCHA_CONFIG` environment variable to point to it.

## Taps

A couple of helpers to get data out of taps defined in the code.


## License

Copyright © 2019 - 2023  Łukasz Korecki

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
