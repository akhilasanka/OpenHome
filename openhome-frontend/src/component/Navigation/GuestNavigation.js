import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import 'react-s-alert/dist/s-alert-default.css';
import 'react-s-alert/dist/s-alert-css-effects/slide.css';
import { ACCESS_TOKEN, CLIENT_BASE_URL } from '../constants';
import Alert from 'react-s-alert';
import { timingSafeEqual } from 'crypto';
import {Nav,Navbar} from 'react-bootstrap';


class GuestNavigation extends Component {
    //call the constructor method
    constructor(props) {
        //Call the constrictor of Super class i.e The Component
        super(props);
        //maintain the state required for this component
        this.state = {
            isLoggedIn: false
        }
    }
    //Call the Will Mount to set the auth Flag to false
    componentWillMount() {
        console.log(localStorage.getItem(ACCESS_TOKEN));
        if (localStorage.getItem(ACCESS_TOKEN) != null && localStorage.getItem(ACCESS_TOKEN) != "") {
            this.setState({
                isLoggedIn: true
            });
        }
    }

    handleLogout = () => {
        localStorage.removeItem(ACCESS_TOKEN);
        localStorage.removeItem("id");
        localStorage.removeItem("role");
        localStorage.removeItem("verified")
        this.setState({
        });
        console.log("safely logged out!");
        Alert.success("You're safely logged out!");
        window.location.href= CLIENT_BASE_URL+'/';
    }

    render() {
        console.log(this.state.isLoggedIn);
        return (
            <div>
            {this.state.isLoggedIn ?
                <Navbar bg="dark" variant="dark">
                <Navbar.Brand href="/">OpenHome</Navbar.Brand>
                <Nav className="mr-auto">
                    <Nav.Link href="/stats/reservations">My Reservations</Nav.Link>
                    <Nav.Link href="/stats/billing">Billing</Nav.Link>
                    <Nav.Link href="/addpayment">Add Payment</Nav.Link>
                </Nav>
                <Nav className="mr-sm-2">
                    <Nav.Link onClick={this.handleLogout}>Logout</Nav.Link>
                </Nav>
            </Navbar>
            :
            <Navbar bg="dark" variant="dark">
                <Navbar.Brand href="/">OpenHome</Navbar.Brand>
                <Nav className="mr-auto">
                </Nav>
                <Nav className="mr-sm-2">
                    <Nav.Link href="/login">Login</Nav.Link>
                    <Nav.Link href="/signup">SignUp</Nav.Link>
                </Nav>
            </Navbar>
            }
            </div>
            
            
        )
    }
}
export default GuestNavigation;
