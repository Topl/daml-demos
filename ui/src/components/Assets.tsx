import React from 'react'
import { Form } from 'semantic-ui-react'
import { Party } from '@daml/types'
import { User } from '@daml.js/opent-app'
import { useParty, useLedger } from '@daml/react'
import { List, ListItem } from 'semantic-ui-react'
import { useStreamQuery } from '@daml/react'

const Assets: React.FC = () => {
    const sender = useParty()
    const ledger = useLedger()
    const messagesResult = useStreamQuery(User.Message)

    return (
        <List relaxed>
            {messagesResult.contracts.map(message => {
        const {sender, receiver, content} = message.payload;
        return (
          <ListItem
            className='test-select-message-item'
            key={message.contractId}>
            <strong>{sender} &rarr; {receiver}:</strong> {content}
          </ListItem>
        );
            })}
        </List>
    )
}

export default Assets