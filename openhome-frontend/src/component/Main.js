import React, { Component } from 'react';
import { Route } from "react-router-dom";

import ListPropertiesComponent from './Properties/ListPropertiesComponent';
import HostProperty from './Properties/HostProperty';
import ViewProperty from './Properties/ViewProperty';
import Home from './Home/Home';
import Login from './Auth/login/Login';
import OAuth2RedirectHandler from './Auth/oauth2/OAuth2RedirectHandler';
import { ACCESS_TOKEN } from './constants';
import Signup from './Auth/signup/Signup';
import AddPayMethod from './Pay/AddPayMethod';
import Alert from 'react-s-alert';
import 'react-s-alert/dist/s-alert-default.css';
import 'react-s-alert/dist/s-alert-css-effects/slide.css';
import SearchProperty from './Search/SearchProperty';
import SearchResult from './Search/SearchResult';
import ReservationCreate from './Reservation/ReservationCreate'
import TimeManagement from './System/TimeManagement'
import VerifyAuthCode from './Auth/signup/VerifyAuthCode';
import EditProperty from './Properties/EditProperty';
import ReservationStats from './Stats/ReservationStats';
import BillingStats from './Stats/BillingStats';

class Main extends Component {
    render() {
        if (localStorage.getItem(ACCESS_TOKEN)!== null) {
        return (
            <div>
                <Route exact path="/"  component={Home} />
                <Route exact path="/property" exact component={ListPropertiesComponent} />
                <Route exact path="/home" component={Home} />
                <Route exact path="/login" component={Login} />
                <Route exact path="/search" component={SearchProperty} />
                <Route exact path="/property/result" component={SearchResult}/>
                <Route exact path="/property/host" exact component={HostProperty} />
                <Route exact path="/property/view/:id" exact component={ViewProperty} />
                <Route exact path="/property/host/edit/:propertyID" exact component={EditProperty} />
                <Route exact path="/addpayment" component={AddPayMethod} />
                <Route exact path="/reservation/create" component={ReservationCreate} />
                <Route exact path="/system/timeManagement" component={TimeManagement} />
                <Route exact path="/registration-confirmation" component={VerifyAuthCode} />
                <Route exact path="/stats/reservations" component={ReservationStats} />
                <Route exact path="/stats/billing" component={BillingStats} />
            </div>
        )
        }
        else{
            return(
                <div>
                    <Route exact path="/" component={Login}/>
                    <Route exact path="/login" component={Login} />
                    <Route exact path="/signup" component={Signup} />
                    <Route exact path="/home" component={Home} />
                    <Route exact path="/addpayment" component={Login} />
                    <Route path="/oauth2/redirect" component={OAuth2RedirectHandler}/>
                    <Route exact path="/registration-confirmation" component={VerifyAuthCode} />
                    <Alert stack={{limit: 3}}
                      timeout = {3000}
                      position='top-right' effect='slide' offset={65} />
                </div>
            )
        }
    }
}

export default Main;
