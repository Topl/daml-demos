// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useCallback, useEffect, useState } from 'react'
import { PublicParty } from '../Credentials';
import LoginScreen from './LoginScreen';
import Credentials from "../Credentials";
import appContainer from './appContainer';
import { Navigate } from 'react-router-dom';
import Navbar from 'react-bootstrap/Navbar';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { Nav } from 'react-bootstrap';

type Props = {
  credentials: Credentials | undefined;
  onLogout: () => void
}

const toAlias = (userId: string): string =>
  userId.charAt(0).toUpperCase() + userId.slice(1);

const MainScreen: React.FC<Props> = ({ credentials, onLogout }) => {

  if (credentials === undefined) {
    return <Navigate to="/login" />;
  } else {
    return <>
      <Navbar bg="light" >
        <Container>
          <Navbar.Brand href="#home" >DAML - Bifrost ERC-20 Demo </Navbar.Brand>
          <Nav className='me-auto'>
            <Nav.Item>
              <Nav.Link href="/tokens">Tokens</Nav.Link>
            </Nav.Item>
            <Nav.Item>
              <Nav.Link href="/tokens">My Wallet</Nav.Link>
            </Nav.Item>
            <Nav.Item>
              <Nav.Link href="#" onClick={ () => { onLogout(); } }>Logout</Nav.Link>
            </Nav.Item>
          </Nav>
        </Container>
      </Navbar>
      <Container >
        <br />
        < Row >
          <Col>
            <p>Hello!</p>
          </Col>
        </Row>
      </Container >
    </>;
  }

}

export default MainScreen