// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useState } from 'react'
import { userContext, publicContext } from './App';
import AuthorizationView from './AuthorizationView';
import StateType from './AppState';
import PrepareForDemoView from './PrepareForDemoView'
import PollView from './PollView';
import SigningView from './SigningView';
import ResultView from './ResultView';
import WelcomeBackView from './WelcomeBackView';
import appContainer from "./appContainer";
import { Alert } from "react-bootstrap";

/**
 * React component for the main screen of the `App`.
 */
const MainScreen: React.FC = ({ }) => {
  const party = userContext.useParty();
  const publicParty = publicContext.useParty();
  const ledger = userContext.useLedger();
  const publicLedger = publicContext.useLedger();
  
  const initialState: StateType = "InitialState"
  const authorizedState: StateType = "AuthorizedState"
  const pollState: StateType = "PollState"
  const waitingForSignatureState: StateType = "WaitingForSignatureState"
  const welcomeBackState: StateType = "WelcomeBackState"

  const [enabled, setEnabled] = useState<undefined | boolean>(undefined)
  const [walletAddress, setWalletAddress] = useState<undefined | string>(undefined)
  const [currentState, setCurrentState] = useState<StateType>(initialState)

  if (typeof topl === "undefined") {
    return appContainer(<>
        <Alert key='danger' variant='danger'>
            You need to setup your Ribn Wallet Extension to test this app.
        </Alert>
  </>);
  } else if (currentState === initialState) {
    return <AuthorizationView enabled={enabled} walletAddress={walletAddress} setEnabled={setEnabled} setWalletAddress={setWalletAddress} setAppState={setCurrentState} />
  } else if (currentState === authorizedState) {
    return <PrepareForDemoView walletAddress={String(walletAddress)} publicLedger={publicLedger} ledger={ledger} party={party} publicParty={publicParty} setAppState={setCurrentState} />;
  } else if (currentState === pollState) {
    return <PollView walletAddress={String(walletAddress)} ledger={ledger} party={party} setAppState={setCurrentState} />
  } else if (currentState === waitingForSignatureState) {
    return <SigningView ledger={ledger} party={party} setAppState={setCurrentState} />
  } else if (currentState === welcomeBackState) {
    return <WelcomeBackView ledger={ledger} party={party} setAppState={setCurrentState} />;
  } else {
    return <ResultView ledger={ledger} party={party} setAppState={setCurrentState} />;
  }
};

export default MainScreen;
