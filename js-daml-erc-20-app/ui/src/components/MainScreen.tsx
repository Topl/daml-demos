// Copyright (c) 2023 Topl LLC. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

import Credentials from "../Credentials";
import { Navigate, Outlet } from 'react-router-dom';
import Navbar from 'react-bootstrap/Navbar';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { Nav } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import DamlHub, {
  damlHubLogout,
  isRunningOnHub,
  usePublicParty,
  usePublicToken,
} from "@daml/hub-react";
import React from "react";
import { userContext, publicContext } from "./App";

type Props = {
  credentials: Credentials | undefined;
  onLogout: () => void
}


const MainScreen: React.FC<Props> = ({ credentials, onLogout }) => {

  if (credentials === undefined) {
    return <Navigate to="/login" />;
  } else {
    const PublicPartyLedger: React.FC = ({ children }) => {
      const publicToken = usePublicToken();
      const publicParty = usePublicParty();
      if (publicToken && publicParty) {
        return (
          <publicContext.DamlLedger
            token={publicToken.token}
            party={publicParty}>
            {children}
          </publicContext.DamlLedger>
        );
      } else {
        return <h1>Loading ...</h1>;
      }
    };
    const Wrap: React.FC = ({ children }) =>
      isRunningOnHub() ? (
        <DamlHub token={credentials.token}>
          <PublicPartyLedger>{children}</PublicPartyLedger>
        </DamlHub>
      ) : (
        <div>{children}</div>
      );
    return <Wrap>
      <userContext.DamlLedger
        token={credentials.token}
        party={credentials.party}
        user={credentials.user}>
        <Navbar bg="light" >
          <Container>
            <Navbar.Brand href="#home" >DAML - Bifrost ERC-20 Demo </Navbar.Brand>
            <Nav className='me-auto'>
              <Nav.Item>
                <Nav.Link as={Link} to="/tokens">Tokens</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link as={Link} to="/deploy">My Wallet</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link href="#" onClick={() => { damlHubLogout(); onLogout(); }}>Logout</Nav.Link>
              </Nav.Item>
            </Nav>
          </Container>
        </Navbar>
        <Container >
          <br />
          < Row >
            <Col>
              <Outlet />
            </Col>
          </Row>
        </Container >
      </userContext.DamlLedger>
    </Wrap>;

  }

}

export default MainScreen