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
import Button from 'react-bootstrap/Button';
import { Topl } from '@daml.js/js-daml-app/c2a496eca024096f50a549241fe9cf45afd80e754ea065edb89b37b3668553c8';

/**
 * React component for the main screen of the `App`.
 */
const MainScreen: React.FC = ({ }) => {
  const party = userContext.useParty();
  const publicParty = "party-419bf17d-fa3c-4b5e-ba97-b103b3d58400::1220ca6cd6c1d92be26531427cf7c47998952ca4884e5e6ca7d9b25e206e98a7a3e1";

  const ledger = userContext.useLedger();
  const initialState: StateType = "InitialState"
  const authorizedState: StateType = "AuthorizedState"
  const pollState: StateType = "PollState"
  const waitingForSignatureState: StateType = "WaitingForSignatureState"

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
  } else {
    return appContainer(<>
      <Alert key='danger' variant='danger'>
        NOT IMPLEMENTED.
      </Alert>
    </>)
  }
};

export default MainScreen;
