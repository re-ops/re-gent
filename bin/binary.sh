LEIN_SNAPSHOTS_IN_RELEASE=1 lein uberjar
cat bin/stub.sh target/re-gent-0.6.5-standalone.jar > target/re-gent && chmod +x target/re-gent
