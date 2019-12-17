import React, { Component } from 'react';
import { API_BASE_URL, ACCESS_TOKEN } from '../../constants';
import { Redirect } from 'react-router-dom';
import axios from 'axios';

class OAuth2RedirectHandler extends Component {
    getUrlParameter(name) {
        name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
        var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');

        var results = regex.exec(this.props.location.search);
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    };

    render() {
        const token = this.getUrlParameter('token');
        const error = this.getUrlParameter('error');

        if(token) {
            localStorage.setItem(ACCESS_TOKEN, token);
            axios({
                method: 'get',
                url: API_BASE_URL+"/api/user/me",
                headers: {"Authorization" : `Bearer ${token}`}
            })
            .then(response => {
                if(response.data) {
                    console.log(response.data);
                    localStorage.setItem("id", response.data.id);
                    localStorage.setItem("role", response.data.role);
                    localStorage.setItem("verified", response.data.emailVerified);

                    if(response.data.role === "host") {
                        this.props.history.push("/host/properties")
                    } else {
                        this.props.history.push("/property/search")
                    }
                    window.location.reload(true)
                }
              }).catch(error => {
                console.log(error);
              });
            return <Redirect to={{
                pathname: "/home",
                state: { from: this.props.location }
            }}/>;
        } else {
            return <Redirect to={{
                pathname: "/login",
                state: {
                    from: this.props.location,
                    error: error
                }
            }}/>;
        }
    }
}

export default OAuth2RedirectHandler;
