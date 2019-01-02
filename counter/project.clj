(defproject counter "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-junit "1.1.8"]]
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.typesafe.akka/akka-actor_2.12 "2.5.19"]
                 [com.typesafe.akka/akka-remote_2.12 "2.5.19"]
                 [liu.mars/jaskell "0.1.2"]
                 [liu.mars/market-messages "0.2"]
                 [org.postgresql/postgresql "42.2.5"]
                 [clj-postgresql "0.7.0"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.clojure/core.specs.alpha "0.1.10" :exclusions [[org.clojure/clojure] [org.clojure/spec.alpha]]]
                 [org.clojure/spec.alpha "0.1.123" :exclusions [[org.clojure/clojure]]]
                 [com.fasterxml.jackson.core/jackson-core "2.9.6"]
                 [com.fasterxml.jackson.core/jackson-databind "2.9.6"]
                 [com.github.romix.akka/akka-kryo-serialization_2.12 "0.5.2"]]
  :test-paths ["src/test/clojure" "src/test/java"]
  :resource-paths ["resources/main"]
  :junit ["src/test/java"]
  :aot :all
  :uberjar-merge-with {#"\.properties$" [slurp str spit] "reference.conf" [slurp str spit]}
  :profiles {:server {:main liu.mars.market.App
                      :jvm-opts ["-Dconfig.resource=server.conf"]
                      :resource-paths ["resources/server"]}
             :test {:dependencies [[junit/junit "4.12"]
                                   [com.typesafe.akka/akka-testkit_2.12 "2.5.19"]]
                    :resource-paths ["resources/test"]
                    :java-source-paths ["src/test/java"]
                    :jvm-opts ["-Dconfig.resource=test.conf"]}})
