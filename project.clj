(defproject specter-koans "0.1.0-SNAPSHOT"
  :description "Specter Koans"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.rpl/specter "0.13.0"]
                  [koan-engine "0.2.3"]]
  :profiles {:dev {:dependencies [[lein-koan "0.1.3"]]}}
  :repl-options {:init-ns koan-engine.runner
                 :init ^:displace (do (use '[koan-engine.core]))}
  :plugins [[lein-koan "0.1.3"]]
  :main koan-engine.runner/exec)
