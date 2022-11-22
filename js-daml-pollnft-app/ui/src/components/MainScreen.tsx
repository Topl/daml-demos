// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useCallback, useEffect, useState } from 'react'
import { Demo } from '@daml.js/js-daml-app/js-daml-app-0.1.0'
import { PublicParty } from '../Credentials';
import { userContext } from './App';
import Navbar from 'react-bootstrap/Navbar';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';
import { Topl } from '@daml.js/js-daml-app/c2a496eca024096f50a549241fe9cf45afd80e754ea065edb89b37b3668553c8';



type Props = {
  // onVoteYes: () => void;
  // onVoteNo: () => void; 
  getPublicParty: () => PublicParty;
}

const toAlias = (userId: string): string =>
  userId.charAt(0).toUpperCase() + userId.slice(1);

/**
 * React component for the main screen of the `App`.
 */
const MainScreen: React.FC<Props> = ({ getPublicParty }) => {
  // const user = userContext.useUser();
  const party = userContext.useParty();
  const { usePublicParty, setup } = getPublicParty();
  const setupMemo = useCallback(setup, [setup]);
  useEffect(setupMemo);
  const publicParty = "party-131b993e-3042-4a35-bf28-bfd5c8221f5f::122062b250bf674e23d6d7a5e7cc258620add3440fc310e077c7cce4ce95b2e82ab4";

  const ledger = userContext.useLedger();

  const [pollSelection, setPollSelection] = useState(true);
  const [createdDemoPoll, setCreatedDemoPoll] = useState(false);
  const [demoPollVoted, setCreatedDemoPollVoted] = useState(false);
  const [demoSignRequestReady, setDemoSignRequestReady] = useState<undefined | Demo.Poll.DemoPollSignRequest>(undefined);
  const [createdFirstConnection, setCreatedFirstConnection] = useState(false);
  const [voted, setVoted] = useState(false);
  const [walletAddress, setWalletAddress] = useState<undefined | string>(undefined)

  const PollButtons: React.FC<{ isDemo: Boolean }> = ({ isDemo }) => {
    return <>
      <Button variant={isDemo ? "primary" : "secondary"} type="submit" onClick={e => setPollSelection(true)}>
        YES!
      </Button>
      &nbsp;&nbsp;&nbsp;
      <Button variant={isDemo ? "secondary" : "danger"} type="submit" onClick={e => setPollSelection(false)}>
        NO!
      </Button>
    </>
  }

  const createFirstConection = useCallback(async () => {
    try {
      let toplAuthorizeAnswer = await topl.authorize();
      if (toplAuthorizeAnswer !== null) {
        console.log("toplAuthorizeAnswer.walletAddress: " + toplAuthorizeAnswer.walletAddress)
        setWalletAddress(toplAuthorizeAnswer.walletAddress)
      }
      let connectionRequest = await ledger.fetchByKey(Demo.Onboarding.ConnectionRequest, party);
      let userContracts = await ledger.fetchByKey(Topl.Onboarding.User, { _1: publicParty, _2: String(party) });
      if (connectionRequest === null && userContracts === null) {
        const firtConnectionParams: Demo.Onboarding.ConnectionRequest = {
          user: party,
          operator: String(publicParty),
          operatorAddress: "AUANVY6RqbJtTnQS1AFTQBjXMFYDknhV8NEixHFLmeZynMxVbp64",
          userAddress: String(toplAuthorizeAnswer.walletAddress),
          yesAddress: "AU9wBip3bEkFtCvamM8pTJZBr7mRvhv9JuLgozngnayP2i1HmGAT",
          noAddress: "AUAbb91jgG4SwFSKkfa6BrjWFkzr8eFxsC16GncnCA9WYbsgk7jW",
          changeAddress: "AUAJx3fy1YrrPb4SPNJjL1EMuLhpZWMz8guqYYkMXGSYUSNNTGTZ",
          assetCode: {
            version: "1",
            shortName: "Vote"
          }
        };
        connectionRequest = await ledger.create(Demo.Onboarding.ConnectionRequest, firtConnectionParams);
      }
      setCreatedFirstConnection(true);
    } catch (error) {
      alert(`Unknown error:\n${JSON.stringify(error)}`);
    }
  }, [ledger, party]);

  const checkDemoPoll = useCallback(async () => {
    try {
      let demoPoll = await ledger.fetchByKey(Demo.Poll.DemoPoll, party);
      if (demoPoll !== null) {
        setCreatedDemoPoll(true);
      }
      let demoPollVoted = await ledger.fetchByKey(Demo.Poll.DemoPollVoted, party);
      if (demoPollVoted !== null) {
        setCreatedDemoPollVoted(true);
      }
      let transactionToSignReady = await ledger.fetchByKey(Demo.Poll.DemoPollSignRequest, party);
      if (transactionToSignReady !== null) {
        setDemoSignRequestReady(transactionToSignReady.payload);
      }
    } catch (error) {
      alert(`Unknown error:\n${JSON.stringify(error)}`);
    }
  }, [ledger, party]);

  const wrap: (c: JSX.Element) => JSX.Element = component => (
    <>
      <Navbar bg="light">
        <Container>
          <Navbar.Brand href="#home">DAML Ribn Demo</Navbar.Brand>
        </Container>
      </Navbar>
      < Container >
        <br />
        <Row>
          <Col>
            {component}
          </Col>
        </Row>
      </Container >
    </>

  );



  const voteYes = async () => {
    console.log("Voted yes!!")
    try {
      let demoPoll = await ledger.fetchByKey(Demo.Poll.DemoPoll, party);
      if (demoPoll !== null) {
        await ledger.exerciseByKey(Demo.Poll.DemoPoll.DemoPoll_YesThisIsDemo, party, {});
      }
    } catch (error) {
      alert(`Unknown error:\n${JSON.stringify(error)}`);
    }
    setVoted(true);
  };

  const voteNo = async () => {
    console.log("Voted no!!")
    try {
      let demoPoll = await ledger.fetchByKey(Demo.Poll.DemoPoll, party);
      if (demoPoll !== null) {
        await ledger.exerciseByKey(Demo.Poll.DemoPoll.DemoPoll_NoThisIsNotDemo, party, {});
      }
    } catch (error) {
      alert(`Unknown error:\n${JSON.stringify(error)}`);
    }
    setVoted(true);
  };

  useEffect(() => { createFirstConection(); }, [createFirstConection])
  useEffect(() => { checkDemoPoll(); }, [checkDemoPoll])

  function tick() {
    checkDemoPoll();
  }

  var timerID = setInterval(() => tick(), 5000);

  // if (!(createdDemoPoll && createdFirstConnection)) {
  if (demoSignRequestReady !== undefined) {

    return wrap(<h1>Please sign with your Ribn app... {demoSignRequestReady.unsignedAssetTransfer.txToSign}</h1>);
  } else if (!createdFirstConnection && !createdDemoPoll) {
    return wrap(<h1>Setting up...</h1>);
  } else if (createdDemoPoll) {
    return wrap(
      <>
        <h1>Awesome!</h1>
        <p>Thanks for connecting {walletAddress}, we have just sent you 1000 polys on the Valhalla test net. They should show up in your wallet shortly.</p>
        <p>Now, let's dive in. This demo is going to ask you one question. If you get it right you'll receive an NFT, get it wrong and nothing happens.</p>
        <p>Question 1: Very important. Is this a demo?</p>
        <p>
          <PollButtons isDemo={pollSelection} />
        </p>
        <p>
          <Button variant="primary" type="submit" onClick={e => (pollSelection ? voteYes() : voteNo())}>
            Submit
          </Button>
        </p>
      </>
    );
  } else {
    return wrap(<h1>Waiting for the funds to arrive...</h1>);
  }
};

export default MainScreen;
