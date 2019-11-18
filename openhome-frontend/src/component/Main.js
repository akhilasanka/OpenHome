import React, { Component } from 'react';
import { Route } from "react-router-dom";

import ListPropertiesComponent from './Properties/ListPropertiesComponent';
import Signin from './Auth/Signin';
import Login from './Auth/login/Login';

class Main extends Component {
    render() {
        return (
            <div>
                <Route path="/" exact component={ListPropertiesComponent} />
                <Route path="/signin" exact component={Signin} />
                <Route path="/login" exact component={Login} />
            </div>
        )
    }
}

export default Main;