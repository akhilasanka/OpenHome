import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import 'react-s-alert/dist/s-alert-default.css';
import 'react-s-alert/dist/s-alert-css-effects/slide.css';
import { ACCESS_TOKEN } from '../constants';
import Alert from 'react-s-alert';


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
        localStorage.removeItem("id");
        localStorage.removeItem("role");
        localStorage.removeItem("verified")
        this.setState({
        });
        console.log("safely logged out!");
        Alert.success("You're safely logged out!");
      }

    render() {
        let redirectVar = '';
        if (!localStorage.getItem(ACCESS_TOKEN)) {
            redirectVar = <Redirect to="/login" />
        }
        if(localStorage.role && localStorage.role === "host") {
            redirectVar = <Redirect to="/host/properties" />
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
