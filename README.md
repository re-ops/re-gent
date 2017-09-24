# Intro

A Zeromq based agent for [re-mote](https://github.com/re-ops/re-mote)

[![Build Status](https://travis-ci.org/re-ops/re-gent.png)](https://travis-ci.org/re-ops/re-gent)


# Get running

Build the single binary agent file:

```bash
$ ./bin/binary.sh
```

Now from a [re-mote](https://github.com/re-ops/re-mote) seassion:

```clojure

[re-mote]λ: (deploy develop "re-gent/target/re-gent") ; agent binary path

Run summary:

  ✔ re-a
  ✔ re-e


Run summary:

  ✔ re-a
  ✔ re-e


Run summary:

  ✔ re-a
  ✔ re-e

[#re_mote.repl.base.Hosts {:auth {:user "vagrant"} :hosts ("re-a" "re-e")}
 {:failure {} :hosts ("re-a" "re-e") :success [{:code 0 :host "re-a" :uuid "d2687d896054430ea84df44ae54d5b92"} {:code 0 :host "re-e" :uuid "d52e9260043c4eb787526eaebba16c11"}]}]

[re-mote]λ: (registered-hosts)
                re-a   000A-0019
                re-e   0000-001B
nil

```

# Prerequisite

* JDK 8 and lein.

# Copyright and license

Copyright [2017] [Ronen Narkis]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
