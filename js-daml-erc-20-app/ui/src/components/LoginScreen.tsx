// Copyright (c) 2023 Topl LLC. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useCallback, useState } from "react";
import Credentials, { PublicParty } from "../Credentials";
import Ledger from "@daml/ledger";
import {
  DamlHubLogin as DamlHubLoginBtn,
  usePublicParty,
} from "@daml/hub-react";
import { authConfig, Insecure } from "../config";
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Navbar from 'react-bootstrap/Navbar';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Card from 'react-bootstrap/Card';
import { useNavigate } from "react-router-dom";

type Props = {
  onLogin: (credentials: Credentials) => Promise<void>,
  credentials: Credentials | undefined;
};

/**
 * React component for the login screen of the `App`.
 */
const LoginScreen: React.FC<Props> = ({ onLogin, credentials }) => {
  const navigate = useNavigate();
  const login = useCallback(
    (credentials: Credentials) => {
      onLogin(credentials);
      navigate("/");
    },
    [onLogin, navigate],
  );

  const wrap: (c: JSX.Element) => JSX.Element = component => (
    <>
      <Navbar bg="light">
        <Container>
          <Navbar.Brand href="#home">DAML ERC-20 Demo</Navbar.Brand>
        </Container>
      </Navbar>
      < Container >
        <br />
        <Row>
          <Col></Col>
          <Col><Card>
            <Card.Body><Card.Title>Please login</Card.Title>{component}</Card.Body></Card></Col>
          <Col></Col>
        </Row>
      </Container >
    </>
  );

  const InsecureLogin: React.FC<{ auth: Insecure }> = ({ auth }) => {
    const [username, setUsername] = React.useState("");
    const handleLogin = async (event: React.FormEvent) => {
      event.preventDefault();
      const token = auth.makeToken(username);
      const ledger = new Ledger({ token: token });
      const primaryParty: string = await auth.userManagement
        .primaryParty(username, ledger)
        .catch(error => {
          const errorMsg =
            error instanceof Error ? error.toString() : JSON.stringify(error);
          alert(`Failed to login as '${username}':\n${errorMsg}`);
          throw error;
        });

      const useGetPublicParty = (): PublicParty => {
        const [publicParty, setPublicParty] = useState<string | undefined>(
          undefined,
        );
        const setup = () => {
          const fn = async () => {
            const publicParty = await auth.userManagement
              .publicParty(username, ledger)
              .catch(error => {
                const errorMsg =
                  error instanceof Error
                    ? error.toString()
                    : JSON.stringify(error);
                alert(
                  `Failed to find primary party for user '${username}':\n${errorMsg}`,
                );
                throw error;
              });
            // todo stop yolowing error handling
            setPublicParty(publicParty);
          };
          fn();
        };
        return { usePublicParty: () => publicParty, setup: setup };
      };
      await login({
        user: { userId: username, primaryParty: primaryParty },
        party: primaryParty,
        token: auth.makeToken(username),
        getPublicParty: useGetPublicParty,
      });
    };

    return wrap(
      <>
        {/* FORM_BEGIN */}
        <Form>
          <Form.Group className="mb-3" controlId="formBasicEmail">
            <Form.Label>DAML User Name</Form.Label>
            <Form.Control type="text" placeholder="Enter DAML User name" onChange={v => setUsername(v.target.value)} />
          </Form.Group>
          <Button variant="primary" type="submit" onClick={handleLogin}>
            Submit
          </Button>
        </Form>
        {/* FORM_END */}
      </>,
    );
  };

  const DamlHubLogin: React.FC = () =>
    wrap(
      <DamlHubLoginBtn
        onLogin={creds => {
          if (creds) {
            login({
              party: creds.party,
              user: { userId: creds.partyName, primaryParty: creds.party },
              token: creds.token,
              getPublicParty: () => ({
                usePublicParty: () => usePublicParty(),
                setup: () => { },
              }),
            });
          }
        }}
        options={{
          method: {
            button: {
              render: () => <Button btn-primary />,
            },
          },
        }}
      />,
    );

  return authConfig.provider === "none" ? (
    <InsecureLogin auth={authConfig} />
  ) : authConfig.provider === "daml-hub" ? (
    <DamlHubLogin />
  ) : (
    <div>Invalid configuation.</div>
  );
};

export default LoginScreen;
