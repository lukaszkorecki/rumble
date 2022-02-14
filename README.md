# Rumble

A set of helpers and tools for a smoother development REPL workflow.

## Usage

Best to alias it:

```clojure
(require '[rumble.repl :as R])
```

From there you can run `(R/help)` and get all of the info:

```
;; in ns rumble.repl
;; rumble.repl/c - ([component-name]) Pul out a compont from a running system, pass keyword for the component name
;; rumble.repl/clear-aliases - ([] [an-ns]) Reset aliases for given ns or current if no args given
;; rumble.repl/find-ns - ([re]) Find namespace vars by a regex
;; rumble.repl/find-test-ns - ([pattern]) Find test namespace vars by a regex
;; rumble.repl/help - ([& _n]) null
;; rumble.repl/list-ns - ([root] []) Return list of symbols of namespaces found in src dir. Default: ./src
;; rumble.repl/pp - ([thing]) Alias for pprint, but returns passed in data
;; rumble.repl/refresh - ([]) Refresh changed namespaces, only if its safe
;; rumble.repl/refresh-all - ([]) Refresh everything, only if its safe
;; rumble.repl/restart-system! - ([]) Restarts the system with an optiona reload
;; rumble.repl/safe-to-refresh? - ([]) Check if refresh is safe, by verifying that application system is not running
;; rumble.repl/start-system! - ([] [an-ns]) Given a namespace, usually some-service, do the following:
  - find some-service.user namespace (by convention)
  - refresh
  - require the user ns e.g. some-service.user
  - start  system, invoking somer-service.user/start
  Warning: best if the system is not running, or things will go south

  Example: (rumble.repl/start-system! 'foo.user)
;; rumble.repl/stop-system! - ([] [an-ns]) Given a namespace, usually some-service.user, stop the system. If not passed, stops currently running system
;; rumble.repl/sys - ([]) Pull out the system for passing around
;; rumble.repl/t - ([] [ns-list]) Run tests via kaocha - either all or a list of vars. WILL NOT REFRESH
;; rumble.repl/t! - ([] [& ns-list]) Run tests via kaocha, but refresh first - runs all tests or a list (or one) of ns vars
;; rumble.repl/tap-log-get - ([]) Return tap logged data
;; rumble.repl/tap-log-init! - ([]) Initialize a tap> listener and store the ref to it
;; rumble.repl/tap-log-reset! - ([]) Clear the log
;; rumble.repl/tap-log-stop! - ([]) Clear tap log and remove the listener
```


## Managing application lifecycle during development

Rumble assumes a couple of things:

- you're using Component
- your dev system lives in `<app name>.user` namespace by default

These assumptions make for typing fewer lines, because you can pass your own namespace symbol and ensure that the ns exports `start` and `stop` functions to manage the system lifecycle, it *might* work.

From there, you have a lot sorts of tools to pull out components out of the running system, get a reference to it and all of that good stuff.

## Tests

Depends on Kaocha. Usage is as simple as:

- `(R/t!)` to run all tests
- `(R/t! (R/find-test-ns #.*bananas.*"))` to run only a matching subset

Tests will not refresh changed namespaces if the dev system is running!
If you have a custom config for Kaocha, set `KAOCHA_CONFIG` environment variable to point to it.

## Taps

A couple of helpers to get data out of taps defined in the code.

## License

Copyright © 2019 - 2022  Łukasz Korecki

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
