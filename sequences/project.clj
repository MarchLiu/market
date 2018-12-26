(defproject sequences "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.typesafe.akka/akka-actor_2.12 "2.5.19"]]
  :aot :all
  :main liu.mars.market.App)
