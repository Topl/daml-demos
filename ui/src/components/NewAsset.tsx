
import React from 'react'
import { Form, Button } from 'semantic-ui-react'
import { Party } from '@daml/types'
import { User, AssetCreation } from '@daml.js/opent-app'
import { useParty, useLedger } from '@daml/react'
import { List, ListItem } from 'semantic-ui-react'
import { useStreamQuery } from '@daml/react'


const NewAsset: React.FC = () => {
    const sender = useParty()
    const [id, setName] = React.useState("");
    const [quantity, setAmount] = React.useState("0");
    const [isSubmitting, setIsSubmitting] = React.useState(false);
    const ledger = useLedger()

    const createAsset = async (event: React.FormEvent) => {
      try {
        event.preventDefault()
        const receiver = sender
        setIsSubmitting(true)
        await ledger.exerciseByKey(AssetCreation.User.CreateAsset, receiver, {receiver, id, quantity})
        setName("")
        setAmount("")
      } catch (error) {
        alert(`Error creating asset:\n${JSON.stringify(error)}`)
      } finally {
        setIsSubmitting(false)
      }
    }

    return (
      <Form onSubmit={createAsset}>
        <Form.Input
          className='test-select-message-content'
          placeholder="Asset name"
          value={id}
          onChange={event => setName(event.currentTarget.value)}
        />
        <Form.Input
          className='test-select-message-content'
          placeholder="Asset quantity"
          value={quantity}
          onChange={event => setAmount(event.currentTarget.value)}
        />
        <Button
          fluid
          className='test-select-message-send-button'
          type="submit"
          disabled={isSubmitting || id === "" || quantity === "0"}
          loading={isSubmitting}
          content="Send"  
        />
      </Form>
    )
}

export default NewAsset