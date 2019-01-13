(defproject liu.mars/market-messages "0.2"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.github.romix.akka/akka-kryo-serialization_2.12 "0.5.2"]
                 [com.taoensso/nippy "2.14.0"]
                 [com.fasterxml.jackson.core/jackson-core "2.9.6"]
                 [com.fasterxml.jackson.core/jackson-databind "2.9.6"]])
