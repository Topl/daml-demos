
import React, { useEffect, useCallback, useRef } from "react"
import { Alert } from "react-bootstrap";
import appContainer from "./appContainer";
import StateType from './AppState';
import Ledger from "@daml/ledger";
import { Demo } from "@daml.js/js-daml-app/js-daml-app-0.1.0"

type SigningViewProp = {
    ledger: Ledger,
    party: string,
    setAppState: (newState: StateType) => void
}

const SigningView: React.FC<SigningViewProp> = ({ ledger, party, setAppState }) => {

    const timeout = useRef<number>();

    const checkDemoSignRequest = useCallback(async () => {
        let transactionToSignReady = await ledger.fetchByKey(Demo.Poll.DemoPollSignRequest, party);
        if (transactionToSignReady !== null) {
            let rawTx = JSON.parse(String(transactionToSignReady.payload?.unsignedAssetTransfer.txToSign));
            let msgToSign = transactionToSignReady.payload?.unsignedAssetTransfer.msgToSign;
            let transferDetails = {
                ...rawTx,
                messageToSign: msgToSign,
                rawTx: rawTx
            }
            let signedTx = await topl.signTransaction(transferDetails);
            let mergedSignedDetail = { ...rawTx, signatures: signedTx.signatures };
            let exerciseResult = await ledger.exerciseByKey(Demo.Poll.DemoPollSignRequest.DemoPollSignRequest_Sign, party, { signedTx: JSON.stringify(mergedSignedDetail) });
            if (exerciseResult !== null) {
                setAppState("ResultViewState")
            }
        } else {
            timeout.current = window.setTimeout(checkDemoSignRequest, 5000);
        }
    }, [ ledger, party, setAppState ])
    useEffect(() => {
        checkDemoSignRequest();
        return () => {
            window.clearTimeout(timeout.current);
        }
    }, [checkDemoSignRequest])

    return appContainer(
        <Alert key='info' variant='info'>
            Please proceed to sign the transaction...
        </Alert>
    );
}

export default SigningView