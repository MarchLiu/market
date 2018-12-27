(defproject sequences "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.typesafe.akka/akka-actor_2.12 "2.5.19"]
                 [liu.mars/jaskell "0.1.1"]
                 [liu.mars/market-messages "0.1"]
                 [org.postgresql/postgresql "42.2.5"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.clojure/core.specs.alpha "0.1.10" :exclusions [[org.clojure/clojure] [org.clojure/spec.alpha]]]
                 [org.clojure/spec.alpha "0.1.123" :exclusions [[org.clojure/clojure]]]]
  :aot :all
  :main liu.mars.market.App)
