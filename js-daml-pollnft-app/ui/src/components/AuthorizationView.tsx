
import React, { useCallback, useEffect, useState } from "react"

import appContainer from "./appContainer";
import { Alert } from "react-bootstrap";
import Button from 'react-bootstrap/Button';
import StateType from './AppState';

type AuthorizationViewProp = {
    enabled: undefined | boolean,
    walletAddress: undefined | string,
    setEnabled: (enabled: boolean | undefined) => void
    setWalletAddress: (walletAddress: string) => void,
    setAppState: (newState: StateType) => void
};

const AuthorizationView: React.FC<AuthorizationViewProp> = ({ enabled, walletAddress, setEnabled, setWalletAddress, setAppState }) => {

    const checkAuthorization = useCallback(async () => {
        if (enabled === undefined) {
            let toplAuthorizeAnswer = await topl.authorize();
            if (toplAuthorizeAnswer !== null) {
                setEnabled(true)
                setWalletAddress(toplAuthorizeAnswer.walletAddress)
                if (toplAuthorizeAnswer.walletAddress !== undefined) {
                    setAppState("AuthorizedState")
                }
            }
        }
    }, [enabled, walletAddress]);

    const revokeAuth = useCallback(async () => {
        await topl.revoke()
        setEnabled(undefined)
    }, [])


    useEffect(() => { checkAuthorization(); }, [enabled, walletAddress]);

    if (enabled == undefined) {
        return appContainer(
            <Alert key='info' variant='info'>
                Please proceed to authorize the app.
            </Alert>);
    } else if (enabled && walletAddress === undefined) {
        return appContainer(
            <>
                <Alert key='danger' variant='danger'>
                    There was a previous authorization. To restart the demo, please reauthorize
                </Alert>
                <Button variant='primary' onClick={e => revokeAuth()}>Revoke and reauthorize</Button>
            </>)
    } else {
        return appContainer(
            <>
                <Alert key='danger' variant='danger'>
                    The authorization was denied. Please reauthorize to continue.
                </Alert>
                <Button variant='primary' onClick={e => setEnabled(undefined)}>Restart demo</Button>
            </>)
    }
}

export default AuthorizationView