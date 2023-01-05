# scala-daml-broker-app
This repository contains a simple application that uses the [daml-dopl-lib](https://github.com/Topl/daml-bifrost-module). The broker application captures the contracts in the DAML participant node. It then performs the necessary operations on the Bifrost node. The results can be captured again by the client application.

#### Prerequisites
 
 The runtime requirements are: 

- a Bifrost node instance running locally.
- a DAML participant node running.
- a `keyfile.json` file.
- the corresponding password for the `keyfile.json`.

#### How to run

The application is meant to run in the background. The command line command looks like this:

```
$ scala-daml-broker-app <HOST> <PORT> <KEYFILENAME> <KEYFILEPASSWORD>
```

The parameters refer to:

- HOST: The DAML participant node host, for example, `127.0.0.1`.
- PORT: The DAML participant node port, for example, 6865.
- KEYFILENAME: The path for the `keyfile.json`, for example, `keyfile.json`.
- KEYFILEPASSWORD: The password for the `keyfile.json` file, for example, `test`.

