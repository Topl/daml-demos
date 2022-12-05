
import React, { useState, useRef, useEffect, useCallback } from "react"
import appContainer from "./appContainer";
import Button from 'react-bootstrap/Button';
import Ledger from "@daml/ledger"
import { Alert } from "react-bootstrap";
import StateType from './AppState';
import { Demo } from '@daml.js/js-daml-app/js-daml-app-0.1.0'
import Image from 'react-bootstrap/Image'
import { Bool } from "@daml/types";

type ResultViewProp = {
    ledger: Ledger,
    party: string,
    setAppState: (newState: StateType) => void
}

const ResultView: React.FC<ResultViewProp> = ({ ledger, party, setAppState }) => {
    const [yesPercentage, setYesPercentage] = useState<undefined | number>(undefined)
    const [noPercentage, setNoPercentage] = useState<undefined | number>(undefined)
    const [hasImage, setHasImage] = useState<undefined | boolean>(undefined)

    const timeout = useRef<number>();

    const checkBalances = useCallback(async () => {
        let demoPollProcessedResults = await ledger.fetchByKey(Demo.Poll.DemoPollProcessedResults, party);
        if (demoPollProcessedResults !== null) {
            setYesPercentage(Number(demoPollProcessedResults.payload.yesPercent))
            setNoPercentage(Number(demoPollProcessedResults.payload.noPercent))
            setHasImage(demoPollProcessedResults.payload.hasNFT)
        } else {
            timeout.current = window.setTimeout(checkBalances, 5000);
        }
    }, [])
    const resetDemo = useCallback(async () => {
        await ledger.exerciseByKey(Demo.Poll.DemoPollProcessedResults.DemoPollProcessedResults_Archive, party, {});
        setAppState("InitialState")
    }, [])

    useEffect(() => {
        checkBalances();
        return () => {
            window.clearTimeout(timeout.current);
        }
    }, [checkBalances])

    const imageUrl = (hasNFT: Boolean) => {
        if (hasNFT) {
            return "/img/toplnft.png"
        } else {
            return "/img/nonft.png"
        }
    }

    if (yesPercentage === undefined && noPercentage === undefined) {
        return appContainer(<Alert key='info' variant='info'>
            Waiting for the results...
        </Alert>)
    } else {
        return appContainer(<>
            <h1>Congratulations!</h1>
            <p>You get a Topl Testnet demo NFT! There were no right or wrong answers,
                but just in case you answered no, this was a demo. Here is how everyone voted.</p>
            <p>Question: Is this a demo?</p>
            <div className="mb-2">
                <Button variant="success" size="lg">
                    {yesPercentage} %
                </Button>{' '}
                <Button variant="danger" size="lg">
                    {noPercentage} %
                </Button>
            </div>
            <div className="mb-2">
                <Button variant="success" size="lg">
                    YES
                </Button>{' '}
                <Button variant="danger" size="lg">
                    NO
                </Button>
            </div>
            <p>Here is your NFT</p>
            <Image className="float-right" src={imageUrl(Boolean(hasImage))} />
            <div>
                <Button variant="primary" type="submit" onClick={e => resetDemo()}>
                    Reset demo
                </Button>
            </div>
        </>);

    }
}

export default ResultView;