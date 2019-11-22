import React, { Component } from 'react';
import { Route } from "react-router-dom";

import ListPropertiesComponent from './Properties/ListPropertiesComponent';
import Home from './Home/Home';
import Login from './Auth/login/Login';
import OAuth2RedirectHandler from './Auth/oauth2/OAuth2RedirectHandler';
import { ACCESS_TOKEN } from './constants';

class Main extends Component {
    render() {
        if (localStorage.getItem(ACCESS_TOKEN)!== null) {
        return (
            <div>
                <Route exact path="/"  component={Home} />
                <Route exact path="/property" exact component={ListPropertiesComponent} />
                <Route exact path="/home" component={Home} />
                <Route exact path="/login" component={Login} />
            </div>
        )
        }
        else{
            return(
                <div>
                    <Route exact path="/" component={Login}/>
                    <Route exact path="/login" component={Login} />
                    <Route exact path="/home" component={Home} />
                    <Route path="/oauth2/redirect" component={OAuth2RedirectHandler}/>
                    
                </div>
            )
        }
    }
}

export default Main;