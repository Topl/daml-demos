# Topl DAML Demos

This is a set of DAML smart contracts and various tests and scenarios to explore the full functionality of DAML with Topl. Any contracts or examples found here are for testing purposes only. There is no guarantee that these demos will continue to be updated or supported in the future, and will be clearly marked with the DAML SDK and Bifrost Node version that they were written for and support.

## How to use these demos

### Typescript/Javascript Daml Applications

These applications consist of a set of Daml smart contracts paired with a UI layer written in React with Typescript or Javascript. The application frontend communicates with a running ledger through the Ledger API and uses JS bindings generated from the Daml contract(s) to create, update, and archive the new contracts.

### Prerequisites

In order to compile the smart contracts and use the resulting JS bindings in the React application, the Daml SDK and NPM need to be installed:

1. Download and install the latest [DAML SDK](https://github.com/digital-asset/daml/releases)
2. Recommended - Use [NVM](https://github.com/nvm-sh/nvm) to manage NPM & Node versions or alternatively:
     - [NPM](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm)
     - [Yarn](https://yarnpkg.com/getting-started/install)

Using the Daml CLI tool, build and generate the Javascript bindings:

```
daml build
daml codegen js .daml/dist/<app-name>.dar -o ui/daml.js
```

Next, navigate to the `ui` directory and install the dependencies and build the app by running

Under the `ui` directory of the demo, install the dependencies:

```
cd ui
npm install
```

A Daml application needs a ledger to communicate with to create and update contracts. A simple sandbox ledger can be started using:

```
daml start
```

The ledger needs to be running continuously while interacting with the Daml application to record state updates.

Finally the application can be started with:

```
npm start
```

A browser window should open at http://localhost:3000 with the starting page of the application.
