import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import '../Styles/Signin.css';
import 'react-s-alert/dist/s-alert-default.css';
import 'react-s-alert/dist/s-alert-css-effects/slide.css';
import { ACCESS_TOKEN } from '../constants';

class Home extends Component {
    //call the constructor method
    constructor(props) {
        //Call the constrictor of Super class i.e The Component
        super(props);
        //maintain the state required for this component
        this.state = {
        }
    }
    //Call the Will Mount to set the auth Flag to false
    componentWillMount() {
    }

    handleLogout = () => {
        localStorage.removeItem(ACCESS_TOKEN);
        this.setState({
        });
        console.log("safely logged out!");
      }

    render() {
        let redirectVar = '';
        if (!localStorage.getItem(ACCESS_TOKEN)) {
            redirectVar = <Redirect to="/login" />
        }
        return (
            
            <div>
            {redirectVar}
                <div className="row" style={{marginLeft:"0em"}}>
                    <input type="button" className="btn btn-primary btn-sm" onClick={this.handleLogout} style={{margin:"1em"}} value="Logout"/>
                </div>
                <div className="container">
                    <h1> Welcome home! Succesfully logged in!</h1>
                </div>
            </div>
            
        )
    }
}
export default Home;
