# js-daml-app

This is a simple application that integrates DAML, the Ribn wallet, and the Topl blockchain node (Bifrost). This application performs the
following tasks:

- On login it authorizes the Ribn wallet with the application.
- It extracts the wallet's address and sends a certain amount of polys to the wallet's address and a vote token.
- It then allows the user to vote on a simple question.
- After the user has voted it proposes the user to send the vote token to one of the token result addresses.
- After the token is in the right address it shows the result to the user.

During all of these interactions, the user interacts with DAML contracts, a broker intercepts the calls to perform
operations on the blockchain and triggers handle the contracts created by the broker.

## Requirements

This demo was tested under Windows using Windows Subsystem for Linux (WSL) and ubuntu. The following software
is required:

- [Daml]: https://docs.daml.com
- [Node.js]: https://nodejs.dev
- The broker software (available [here](https://github.com/Topl/daml-demos/tree/main/scala-daml-broker-app)).
- Docker
- Scala
- SBT
- JVM
- The Ribn Chrome extension
- A locally published version of the [daml-bifrost-module](https://github.com/Topl/daml-bifrost-module).

## Development Quick Start

First, start the Daml components:

```bash
daml start
```

This will:

- Build you Daml code once.
- Generate JavaScript code (and TypeScript definitions) for your Daml types.
- Start a Daml sandbox gRPC server (on 6865).
- Start a Daml HTTP JSON API server (on 7575).
- Watch for the `r` key press (`r` + Enter on Windows); when pressed, rebuild
  all of the Daml code, push the new DAR to the ledger, and rerun the JS/TS
  code generation.

Under WSL the JSON API server might not start. If it is not active, run:

```bash
daml json-api --config json-api-app.conf
```

Next, start the broker. 

```bash
cd ../scala-daml-broker-app
sbt 'run 127.0.0.1 6865 keyfile.json test'
```

Next, start the local bifrost node using the following docker command:

```bash
docker run -p 9085:9085 toplprotocol/bifrost-node:1.10.2 --forge --disableAuth --seed test --debug
```

Next, start the JS dev server:

```bash
cd ui
npm install
npm start
```

This starts a server on `http://localhost:3000` which:

- Builds all of your TypeScript (or JavaScript) code (including type
  definitions from the codegen).
- Serves the result on :3000, redirecting `/v1` to the JSON API server (on
  `localhost:7575`) so API calls are on the same origin as far as your browser
  is concerned.
- Watch for changes in TS/JS code (including codegen), and immediately rebuild.

## Deploying to Daml Hub

To build everything from scratch:

```bash
daml build
daml codegen js .daml/dist/js-daml-app-0.1.0.dar -o ui/daml.js
cd ui
npm install
npm run-script build
zip -r ../js-daml-app-ui.zip build
```

Next you need to create a ledger on [Daml Hub], upload the files
`.daml/dist/js-daml-app-0.1.0.dar` (created by the `daml build` command)
and `js-daml-app-ui.zip` (created by the `zip` command based on the result
of `npm run-script build`).

[Daml Hub]: https://hub.daml.com

Once both files are uploaded, you need to tell Daml Hub to deploy them. A few
seconds later, your website should be up and running.
