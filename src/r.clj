(ns r
  (:require
    [potemkin]
    [rumble.repl]))


(potemkin/import-vars
  [rumble.repl
   c
   clear-aliases
   find-ns
   find-test-ns
   list-ns
   pp
   refresh
   refresh-all
   restart-system!
   safe-to-refresh?
   start-system!
   stop-system!
   sys
   t
   t!
   tap-log-get
   tap-log-init!
   tap-log-reset!
   tap-log-stop!
   help])
