import React, { Component } from 'react';
import axios from 'axios';
import { ACCESS_TOKEN, API_BASE_URL } from '../../constants';
import swal from 'sweetalert';

class VerifyAuthCode extends Component {
    //call the constructor method
    constructor(props) {
        //Call the constrictor of Super class i.e The Component
        super(props);
        //maintain the state required for this component
        this.state = {
            verified: false
        }
    }
    //Call the Will Mount to set the auth Flag to false
    async componentWillMount() {
        var token = localStorage.getItem("accessToken");
        var authcodeStr = this.props.location.search;
        var authcode = authcodeStr.replace('?token=', '');
        console.log(authcode);
        var data = { authcode: authcode };
        await axios({
            method: 'post',
            url: API_BASE_URL + '/api/auth/verify',
            data: data,
            config: { headers: { 'Content-Type': 'multipart/form-data' } },
            headers: { "Authorization": `Bearer ${token}` }
        })
            .then((response) => {
                if (response.status >= 500) {
                    throw new Error("Bad response from server");
                }
                return response.data;
            })
            .then((responseData) => {
                function sleep(ms) {
                    return new Promise(resolve => setTimeout(resolve, ms));
                }

                if (responseData == "verified") {
                    swal("Yayy!", "Sucessfully Verified", "success");
                    this.setState({
                        verified: true
                    });
                }
                else {
                    swal("Oops!", "Unable to verify with the given code. Please contact admin", "error");
                }
            }).catch(function (err) {
                swal("Oops!", "Unable to verify with the given code. Please contact admin.", "error");
                console.log(err)
            });
    }

    render() {
        return (

            <div>

                {this.state.verified ?
                    <div className="container" style={{ marginTop: "3em" }}>
                        <h3> Sucessfully verified. Please proceed with next steps! </h3>
                    </div>
                    :
                    <div className="container" style={{ marginTop: "3em" }}>
                        <h1> Verifying Auth Code. . . </h1>
                    </div>
                }

            </div>

        )
    }
}
export default VerifyAuthCode;
