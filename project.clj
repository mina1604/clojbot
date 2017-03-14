(defproject clojbot "0.1.0-SNAPSHOT"
  :description "Clojure sample bot framework"
  :url "https://github.com/sido378/clojbot"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.441"]
                 [org.clojure/data.json "0.2.1"]
                 [com.taoensso/timbre "4.8.0"]
                 [compojure "1.5.1"]
                 [environ "1.1.0"]
                 [http-kit "2.2.0"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-jetty-adapter "1.5.0"]]

  :plugins [[lein-ring "0.9.7"]
            [lein-environ "1.1.0"]
            [lein-kibit "0.1.3"]
            [lein-auto "0.1.3"]]

  :ring {:handler clojbot.core/app}

  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
