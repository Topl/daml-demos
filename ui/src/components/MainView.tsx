// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useMemo } from 'react';
import { Container, Grid, Header, Icon, Segment, Divider } from 'semantic-ui-react';
import { Party } from '@daml/types';
import { User } from '@daml.js/opent-app';
import { useParty, useLedger, useStreamFetchByKeys, useStreamQueries } from '@daml/react';
import UserList from './UserList';
import PartyListEdit from './PartyListEdit';
import MessageEdit from './MessageEdit';
import MessageList from './MessageList';
import Assets from './Assets';
import NewAsset from './NewAsset';

// USERS_BEGIN
const MainView: React.FC = () => {
  const username = useParty();
  const myUserResult = useStreamFetchByKeys(User.User, () => [username], [username]);
  const myUser = myUserResult.contracts[0]?.payload;
  const allUsers = useStreamQueries(User.User).contracts;
// USERS_END

  // Sorted list of users that are following the current user
  const followers = useMemo(() =>
    allUsers
    .map(user => user.payload)
    .filter(user => user.username !== username)
    .sort((x, y) => x.username.localeCompare(y.username)),
    [allUsers, username]);

  // FOLLOW_BEGIN
  const ledger = useLedger();

  const follow = async (userToFollow: Party): Promise<boolean> => {
    try {
      await ledger.exerciseByKey(User.User.Follow, username, {userToFollow});
      return true;
    } catch (error) {
      alert(`Unknown error:\n${error}`);
      return false;
    }
  }
  // FOLLOW_END

  return (
    <Container>
      <Grid centered columns={2}>
        <Grid.Row stretched>
          <Grid.Column>
            <Header as='h1' size='huge' color='blue' textAlign='center' style={{padding: '1ex 0em 0ex 0em'}}>
                {myUser ? `Welcome, ${myUser.username}!` : 'Loading...'}
            </Header>

            <Segment>
              <Header as='h2'>
                <Icon name='user' />
                <Header.Content>
                  {myUser?.username ?? 'Loading...'}
                  <Header.Subheader>Users I'm following</Header.Subheader>
                </Header.Content>
              </Header>
              <Divider />
              <PartyListEdit
                parties={myUser?.following ?? []}
                onAddParty={follow}
              />
            </Segment>
            <Segment>
              <Header as='h2'>
                <Header.Content>
                  Assets
                  <Header.Subheader>Your Topl native assets</Header.Subheader>
                </Header.Content>
              </Header>
              <Assets />
            </Segment>
            <Segment>
              <Header as='h2'>
                <Header.Content>
                  Create an asset
                  <Header.Subheader>One stop shop to create new native assets</Header.Subheader>
                </Header.Content>
              </Header>
              <NewAsset />
            </Segment>
          </Grid.Column>
        </Grid.Row>
      </Grid>
    </Container>
  );
}

export default MainView;
