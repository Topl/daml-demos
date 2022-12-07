// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useCallback, useEffect, useState } from 'react'
import { Demo } from '@daml.js/js-daml-app/js-daml-app-0.1.0'
import { PublicParty } from '../Credentials';
import { Alert } from "react-bootstrap";
import { userContext } from './App';
import AuthorizationView from './AuthorizationView';
import appContainer from "./appContainer";
import StateType from './AppState';
import PrepareForDemoView from './PrepareForDemoView'
import PollView from './PollView';
import SigningView from './SigningView';
import ResultView from './ResultView';
import WelcomeBackView from './WelcomeBackView'

/**
 * React component for the main screen of the `App`.
 */
const MainScreen: React.FC = ({ }) => {
  const party = userContext.useParty();
  const publicParty = "party-4d2f70b5-3930-45bb-afa2-1fb853a8c2aa::12202d9c0ebe063ea76ea3107a2e1968b062020e26468bae7e06b67c4dba6624a2e3";

  const ledger = userContext.useLedger();
  const initialState: StateType = "InitialState"
  const authorizedState: StateType = "AuthorizedState"
  const pollState: StateType = "PollState"
  const waitingForSignatureState: StateType = "WaitingForSignatureState"
  const welcomeBackState: StateType = "WelcomeBackState"

  const [enabled, setEnabled] = useState<undefined | boolean>(undefined)
  const [walletAddress, setWalletAddress] = useState<undefined | string>(undefined)
  const [currentState, setCurrentState] = useState<StateType>(initialState)


  if (currentState == initialState) {
    return <AuthorizationView enabled={enabled} walletAddress={walletAddress} setEnabled={setEnabled} setWalletAddress={setWalletAddress} setAppState={setCurrentState} />
  } else if (currentState == authorizedState) {
    return <PrepareForDemoView walletAddress={String(walletAddress)} ledger={ledger} party={party} publicParty={publicParty} setAppState={setCurrentState} />;
  } else if (currentState == pollState) {
    return <PollView walletAddress={String(walletAddress)} ledger={ledger} party={party} setAppState={setCurrentState} />
  } else if (currentState == waitingForSignatureState) {
    return <SigningView ledger={ledger} party={party} setAppState={setCurrentState} />
  } else if (currentState == welcomeBackState) {
    return <WelcomeBackView ledger={ledger} party={party} setAppState={setCurrentState} />;
  } else {
    return <ResultView ledger={ledger} party={party} setAppState={setCurrentState} />;
  }
};

export default MainScreen;
