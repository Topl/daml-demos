// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

import React from "react";
import LoginScreen from "./LoginScreen";
import { createLedgerContext } from "@daml/react";
import DamlHub, {
  damlHubLogout,
  isRunningOnHub,
  usePublicParty,
  usePublicToken,
} from "@daml/hub-react";
import Credentials from "../Credentials";
import { authConfig } from "../config";
import { Route, Routes } from 'react-router-dom';
import MainScreen from "./MainScreen";
import appContainer from "./appContainer";

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
  // a main view developped with bootstrap
  return <Routes>
    <Route path="/" element={<MainScreen credentials={ credentials} onLogout={ () => setCredentials(undefined) } />} />
    <Route path="/login" element={<LoginScreen onLogin={ setCredentials}  credentials={credentials}  />} />
  </Routes>
};
// APP_END

export default App;
