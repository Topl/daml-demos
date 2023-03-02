
import React, { useCallback, useEffect } from "react"
import appContainer from "./appContainer"
import { Alert } from "react-bootstrap";
import Ledger from "@daml/ledger"
import { Demo } from '@daml.js/js-daml-app/js-daml-app-0.1.0/lib'
import StateType from './AppState';

type PrepareForDemoViewProp = {
    walletAddress: string
    ledger: Ledger,
    party: string,
    publicParty: string|undefined,
    setAppState: (newState: StateType) => void
}

const PrepareForDemoView: React.FC<PrepareForDemoViewProp> = ({ walletAddress, ledger, party, publicParty, setAppState }) => {
    const checkConnectionRequest = useCallback(async () => {
        let demoPoll = await ledger.fetchByKey(Demo.Poll.DemoPoll, party);
        let demoPollProcessedResults = await ledger.fetchByKey(Demo.Poll.DemoPollProcessedResults, party);
        if (demoPoll !== null) {
            setAppState("PollState");
        } else if (demoPollProcessedResults !== null) {
            await ledger.exerciseByKey(Demo.Poll.DemoPollProcessedResults.DemoPollProcessedResults_Reprocess, party, {});
            setAppState("WelcomeBackState");
        } else {
            let onboardingList = await ledger.query(Demo.Onboarding.PollAppOnboarding, {})
            let onboardingContract = onboardingList.at(0);
            if (onboardingContract === undefined) {
                console.log("Undefined onboarding?")
                setAppState("PollState");
            } else {
                console.log("Defined onboarding")
                let contractId = onboardingContract.contractId;
                let connectionRequest = await ledger.exercise(
                    Demo.Onboarding.PollAppOnboarding.ConnectToApp, 
                    contractId, {
                        userParty: party,
                        userAddress: walletAddress
                    });
                    if (connectionRequest !== null) {
                        console.log("Connection request != null")
                        setAppState("PollState");
                    } else {
                        console.log("Connection request == null")
                        // TODO fix this
                        setAppState("PollState");
                    }
                }
                
            }
    }, [ ledger, party, walletAddress, setAppState])

    useEffect(() => { checkConnectionRequest(); }, [ checkConnectionRequest])

    return appContainer(
        <Alert key='info' variant='info'>
            Preparing the D-application...
        </Alert>
    );
}

export default PrepareForDemoView;