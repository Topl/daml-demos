
import React, { useEffect, useCallback, useRef, useState } from "react"
import appContainer from "./appContainer";
import Button from 'react-bootstrap/Button';
import Ledger from "@daml/ledger"
import { Demo } from '@daml.js/js-daml-app/js-daml-app-0.1.0/lib'
import StateType from './AppState';
import { Alert } from "react-bootstrap";

type PollViewProp = {
    walletAddress: string,
    ledger: Ledger,
    party: string,
    setAppState: (newState: StateType) => void
}

const PollView: React.FC<PollViewProp> = ({ walletAddress, ledger, party, setAppState }) => {

    const [isDemo, setIsDemo] = useState(true);
    const [waitingForPoll, setWaitingForPoll] = useState(true)


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
        setAppState("WaitingForSignatureState");
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
        setAppState("WaitingForSignatureState");
    };

    const timeout = useRef<number>();

    const checkDemoPollExists = useCallback(async () => {
        if (waitingForPoll) {
            let demoPoll = await ledger.fetchByKey(Demo.Poll.DemoPoll, party);
            if (demoPoll !== null) {
                setWaitingForPoll(false);
            } else {
                setWaitingForPoll(true);
                timeout.current = window.setTimeout(checkDemoPollExists, 5000);
            }
        }
    }, [])


    useEffect(() => {
        checkDemoPollExists();
        return () => {
            window.clearTimeout(timeout.current);
        }
    }, [checkDemoPollExists])

    if (waitingForPoll) {
        return appContainer(
            <Alert key='info' variant='info'>
                Waiting for funds to arrive...
            </Alert>
        );
    } else {
        return appContainer(
            <>
                <h1>Awesome!</h1>
                <p>Thanks for connecting {walletAddress}, we have just sent you 1000 polys on the Valhalla test net. They should show up in your wallet shortly.</p>
                <p>Now, let's dive in. This demo is going to ask you one question. If you get it right you'll receive an NFT, get it wrong and nothing happens.</p>
                <p>Question 1: Very important. Is this a demo?</p>
                <p>
                    <Button variant={isDemo ? "primary" : "secondary"} type="submit" onClick={e => setIsDemo(true)}>
                        YES!
                    </Button>
                    &nbsp;&nbsp;&nbsp;
                    <Button variant={isDemo ? "secondary" : "danger"} type="submit" onClick={e => setIsDemo(false)}>
                        NO!
                    </Button>
                </p>
                <p>
                    <Button variant="primary" type="submit" onClick={e => (isDemo ? voteYes() : voteNo())}>
                        Submit
                    </Button>
                </p>
            </>
        )
    }
}

export default PollView;