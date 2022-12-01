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
  const publicParty = "party-2f7bb3a8-7cad-4c36-9d3e-677e50116a77::1220092026ef19d5186bd1cc612d01893cff876aed548831dd7e817a0dd29b466cba";

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
