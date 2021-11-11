// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useCallback } from 'react'
import { Button, Form, Grid, Header, Image, Segment } from 'semantic-ui-react'
import Credentials, { computeCredentials } from '../Credentials';
import Ledger from '@daml/ledger';
import { User } from '@daml.js/opent-app';
import { DeploymentMode, deploymentMode, ledgerId, httpBaseUrl} from '../config';
import { useEffect } from 'react';

type Props = {
  onLogin: (credentials: Credentials) => void;
}

/**
 * React component for the login screen of the `App`.
 */
const LoginScreen: React.FC<Props> = ({onLogin}) => {
  const [username, setUsername] = React.useState('');
  const [walletAddress, setWalletAddress] = React.useState('');

  const login = useCallback(async (credentials: Credentials) => {
    try {
      const ledger = new Ledger({token: credentials.token, httpBaseUrl});
      let userContract = await ledger.fetchByKey(User.User, credentials.party);
      if (userContract === null) {
        const user = {username: credentials.party, following: []};
        userContract = await ledger.create(User.User, user);
      }
      onLogin(credentials);
    } catch(error) {
      alert(`Unknown error:\n${error}`);
    }
  }, [onLogin]);

  const handleLogin = async (event: React.FormEvent) => {
    event.preventDefault();
    const credentials = computeCredentials(username);
    await login(credentials);
  }

  const handleDamlHubLogin = () => {
    window.location.assign(`https://login.projectdabl.com/auth/login?ledgerId=${ledgerId}`);
  }

  const getCookieValue = (name: string): string => (
    document.cookie.match('(^|;)\\s*' + name + '\\s*=\\s*([^;]+)')?.pop() || ''
  )

  useEffect(() => {
    const url = new URL(window.location.toString());
    const party = url.searchParams.get('party');
    if (party === null) {
      return;
    }
    url.search = '';
    window.history.replaceState(window.history.state, '', url.toString());
    const token = getCookieValue('DAMLHUB_LEDGER_ACCESS_TOKEN');
    login({token, party, ledgerId});
  }, [login]);

  return (
    <Grid textAlign='center' style={{ height: '100vh' }} verticalAlign='middle'>
      <Grid.Column style={{ maxWidth: 450 }}>
        <Header as='h1' textAlign='center' size='huge' style={{color: '#223668'}}>
          <Header.Content>
            OpenT.io
          </Header.Content>
          <Header.Subheader>
            The "T" stands for Topl
          </Header.Subheader>
        </Header>
        <Form size='large' className='test-select-login-screen'>
          <Segment>
            {deploymentMode !== DeploymentMode.PROD_DAML_HUB
            ? <>
                {/* FORM_BEGIN */}
                <Form>
                  <Form.Input
                    fluid
                    icon='user'
                    iconPosition='left'
                    placeholder='Username'
                    value={username}
                    className='test-select-username-field'
                    onChange={e => setUsername(e.currentTarget.value)}
                  />
                  <Form.Input 
                    fluid
                    icon='key'
                    iconPosition='left'
                    placeholder='Wallet Address'
                    value={walletAddress}
                    className='test-select-wallet-address-field'
                    onChange={e => setWalletAddress(e.currentTarget.value)}
                    style={{margin: '0 0 16px 0'}}
                  />
                </Form>
                <Button
                  primary
                  fluid
                  className='test-select-login-button'
                  onClick={handleLogin}>
                  Log in
                </Button>
                {/* FORM_END */}
              </>
            : <Button primary fluid onClick={handleDamlHubLogin}>
                Log in with Daml Hub
              </Button>
            }
          </Segment>
        </Form>
      </Grid.Column>
    </Grid>
  );
};

export default LoginScreen;
