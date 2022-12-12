#!/usr/bin/sh

tmux new-session -d -s DemoSession -n damlStart  -d "/usr/bin/fish -c \"cd /home/mundacho/git/daml-demos/js-daml-pollnft-app; /home/mundacho/.daml/bin/daml start\""
sleep 30
tmux new-window -t DemoSession -n json-api -d "/usr/bin/fish -c \"cd /home/mundacho/git/daml-demos/js-daml-pollnft-app; daml json-api --config json-api-app.conf \""
tmux new-window -t DemoSession -n broker -d "/usr/bin/fish -c \"cd /home/mundacho/git/daml-demos/scala-daml-broker-app; sbt 'run 127.0.0.1 6865 keyfile.json test' \""
tmux new-window -t DemoSession -n bifrost -d "/usr/bin/fish -c \"docker run -p 9085:9085 toplprotocol/bifrost-node:1.10.2 --forge --disableAuth --seed test --debug\""
tmux new-window -t DemoSession -n triggers -d "/usr/bin/env fish -c \"cd /home/mundacho/git/daml-demos/js-daml-pollnft-app; daml trigger --dar .daml/dist/js-daml-app-0.1.0.dar --trigger-name Demo.AutoOps:initSession --ledger-host localhost --ledger-port 6865 --ledger-user public\""
