(defproject sequences "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :plugins [[lein-junit "1.1.8"]]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.typesafe.akka/akka-actor_2.12 "2.5.19"]
                 [com.typesafe.akka/akka-remote_2.12 "2.5.19"]
                 [liu.mars/jaskell "0.1.2"]
                 [liu.mars/market-messages "0.1"]
                 [org.postgresql/postgresql "42.2.5"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.clojure/core.specs.alpha "0.1.10" :exclusions [[org.clojure/clojure] [org.clojure/spec.alpha]]]
                 [org.clojure/spec.alpha "0.1.123" :exclusions [[org.clojure/clojure]]]
                 [com.taoensso/nippy "2.14.0"]
                 [cheshire "5.8.1"]
                 [com.github.romix.akka/akka-kryo-serialization_2.12 "0.5.2"]]
  :aot :all
  :test-paths ["src/test/clojure" "src/test/java"]
  :junit ["src/test/java"]
  :profiles {:server {:main liu.mars.market.App
                      :jvm-opts ["-Dconfig.resource=junit.conf"]}
             :client {:main liu.mars.Client
                      :jvm-opts ["-Dconfig.resource=client.conf"]}
             :junit {:dependencies [[junit/junit "4.12"]
                                    [com.typesafe.akka/akka-testkit_2.12 "2.5.19"]]
                     :java-source-paths ["src/test/java"]
                     :jvm-opts ["-Dconfig.resource=junit.conf"]}})
