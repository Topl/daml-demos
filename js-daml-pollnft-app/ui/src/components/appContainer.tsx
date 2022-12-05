
import React from 'react';
import Navbar from 'react-bootstrap/Navbar';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

const appContainer: (c: JSX.Element) => JSX.Element = component => (
    <>
        <Navbar bg="light" >
            <Container>
                <Navbar.Brand href="#home" > DAML Ribn Demo </Navbar.Brand>
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