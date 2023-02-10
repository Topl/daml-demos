
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
    publicParty: string,
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
            let connectionRequest = await ledger.fetchByKey(Demo.Onboarding.ConnectionRequest, party);
            if (connectionRequest === null) {
                const firtConnectionParams: Demo.Onboarding.ConnectionRequest = {
                    user: party,
                    operator: publicParty,
                    operatorAddress: "AUANVY6RqbJtTnQS1AFTQBjXMFYDknhV8NEixHFLmeZynMxVbp64",
                    userAddress: walletAddress,
                    yesAddress: "AU9wBip3bEkFtCvamM8pTJZBr7mRvhv9JuLgozngnayP2i1HmGAT",
                    noAddress: "AUAbb91jgG4SwFSKkfa6BrjWFkzr8eFxsC16GncnCA9WYbsgk7jW",
                    changeAddress: "AUAJx3fy1YrrPb4SPNJjL1EMuLhpZWMz8guqYYkMXGSYUSNNTGTZ",
                    assetCode: {
                        version: "1",
                        issuerAddress: "AUANVY6RqbJtTnQS1AFTQBjXMFYDknhV8NEixHFLmeZynMxVbp64",
                        shortName: "Vote"
                    }
                };
                connectionRequest = await ledger.create(Demo.Onboarding.ConnectionRequest, firtConnectionParams);
                setAppState("PollState");
            } else {
                setAppState("PollState");
            }
        }
    }, [])

    useEffect(() => { checkConnectionRequest(); }, [])

    return appContainer(
        <Alert key='info' variant='info'>
            Preparing the D-application...
        </Alert>
    );
}

export default PrepareForDemoView;