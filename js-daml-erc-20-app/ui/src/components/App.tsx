// Copyright (c) 2023 Topl LLC. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

import React from "react";
import LoginScreen from "./LoginScreen";
import { createLedgerContext } from "@daml/react";
import Credentials from "../Credentials";
import { Route, Routes } from 'react-router-dom';
import MainScreen from "./MainScreen";
import DeployTokenScreen from "./DeployTokenScreen";
import { isRunningOnHub } from "@daml/hub-react";


// Context for the party of the user.
export const userContext = createLedgerContext();
// Context for the public party used to query user aliases.
// On Daml hub, this is a separate context. Locally, we have a single
// token that has actAs claims for the user’s party and readAs claims for
// the public party so we reuse the user context.
export const publicContext = isRunningOnHub()
  ? createLedgerContext()
  : userContext;



/**
 * React component for the entry point into the application.
 */
// APP_BEGIN
const App: React.FC = () => {
  const [credentials, setCredentials] = React.useState<
    Credentials | undefined
  >();

  // const [initialized, setInitialized] = React.useState(false);

  // a main view developped with bootstrap
  return <Routes>
    <Route path="/" element={<MainScreen
      credentials={credentials}
      onLogout={() => setCredentials(undefined)} />}>
      <Route path="deploy" element={<DeployTokenScreen />} />
    </Route>
    <Route path="/login" element={<LoginScreen onLogin={async (credentials) => {setCredentials(credentials);  }} credentials={credentials} />} />
  </Routes>
};
// APP_END

export default App;
