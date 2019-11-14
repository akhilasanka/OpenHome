import React, { Component } from 'react';
import { Route } from "react-router-dom";

import ListPropertiesComponent from './Properties/ListPropertiesComponent';
import Signin from './Auth/Signin';

class Main extends Component {
    render() {
        return (
            <div>
                <Route path="/" exact component={ListPropertiesComponent} />
                <Route path="/signin" exact component={Signin} />
            </div>
        )
    }
}

export default Main;