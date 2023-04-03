// Copyright (c) 2023 Topl LLC. All rights reserved.
// SPDX-License-Identifier: Apache-2.0


import React, { useCallback, useEffect } from 'react';
import { ERC20AppConfig, Standard } from '@daml.js/js-daml-erc20-app-0.1.0'
import { Card, Form, Button } from 'react-bootstrap';
import { userContext, publicContext } from "./App";

const DeployTokenScreen: React.FC = () => {
    const party = userContext.useParty();
    const ledger = userContext.useLedger();
    const publicParty = publicContext.useParty();
    const publicLedger = publicContext.useLedger();
    console.log("Party: " + party);
    const checkFirstConnection = useCallback(async () => {
        const configList = await publicLedger.query(ERC20AppConfig.ERC20AppConfig, {});
        const config = configList.at(0);
        if (config !== undefined) {
            console.log("Config exists");
            const fixedSupplyMinter = await ledger?.fetchByKey(
                Standard.ERC20FixedSupply.ERC20FixedSupplyMinter, { _1: config?.payload.operator, _2: party }
            );
            const fixedSupplyMinterCreator = await ledger?.fetchByKey(
                Standard.ERC20FixedSupply.ERC20FixedSupplyMinterCreate, party);
            if (fixedSupplyMinter === undefined && fixedSupplyMinterCreator !== undefined) {
                await ledger?.create(Standard.ERC20FixedSupply.ERC20FixedSupplyMinterCreate, {
                    operatorAddress: config.payload.operatorAddress,
                    issuer: party,
                    operator: config.payload.operator,
                    public: publicParty
                });
            } else {
                console.log("ERC20FixedSupplyMinter creator was successfully created");
            }
        } else {
            console.log("Config does not exist");
        }
    }, [ ledger, publicLedger, party, publicParty ]);

    useEffect(() => { checkFirstConnection(); }, [checkFirstConnection]);

    return <>
        <Card>
            <Card.Header>Deploy New Fixed Supply Token</Card.Header>
            <Card.Body>
                <Form>
                    <Form.Group className="mb-3" controlId="formBasicEmail">
                        <Form.Label>Token supply</Form.Label>
                        <Form.Control type="text" placeholder="10" />
                        <Form.Text className="text-muted">
                            The number of tokens to be minted.
                        </Form.Text>
                    </Form.Group>
                    <Button variant="primary" type="submit">
                        Deploy
                    </Button>
                </Form>
            </Card.Body>
        </Card>
    </>;

}

export default DeployTokenScreen;