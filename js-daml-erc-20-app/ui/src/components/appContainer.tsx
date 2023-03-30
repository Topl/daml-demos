
import React from 'react';
import Navbar from 'react-bootstrap/Navbar';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { Nav } from 'react-bootstrap';

const appContainer: (c: JSX.Element) => JSX.Element = component => (
    <>
        <Navbar bg="light" >
            <Container>
                <Navbar.Brand href="#home" >DAML - Bifrost ERC-20 Demo </Navbar.Brand>
        <Nav className='me-auto'>
            <Nav.Item>
                <Nav.Link href="/tokens">Tokens</Nav.Link>
            </Nav.Item>
            <Nav.Item>
                <Nav.Link href="/tokens">My Wallet</Nav.Link>
            </Nav.Item>
            <Nav.Item>
                <Nav.Link href="/logout">Logout</Nav.Link>
            </Nav.Item>
        </Nav>
            </Container>
        </Navbar>
        <Container >
            <br />
            < Row >
                <Col>
                    {component}
                </Col>
            </Row>
        </Container >
    </>

);

export default appContainer;