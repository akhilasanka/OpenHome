import React, { Component } from 'react';
import axios from 'axios';
import '../Styles/Signin.css';

class Login extends Component {
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

    render() {
        //redirect based on successful login
        return (
            <div>
                <div>
                    <div className="container">
                        <div className="row justify-content-center align-items-center" style={{ height: '75vh' }}>
                            <div className="col-4">
                                <div className="card">
                                    <h5 className="card-title text-center border-bottom"><span className="appName">Open Home</span></h5>
                                    <div className="card-body">
                                        <form  method="post" autoComplete="off">
                                            <div className="form-group text-center">
                                                <span className="" style={{ color: '#5e5e5e', fontWeight: '600' }}>Sign In</span>
                                            </div>
                                            <div className="form-group">
                                                <input type="email" className="form-control" name="email" id="uname"
                                                    placeholder="Email" required />
                                            </div>
                                            <div className="form-group">
                                                <input type="password" className="form-control" name="password" id="pwd"
                                                    placeholder="Password" required />
                                            </div>
                                            <div className="form-group">
                                                <div className="col text-center">
                                                    <input type="submit" className="btn btn-primary align-items-center" value="Sign In"
                                                        style={{ width: '100%', backgroundColor: '#005a8b', borderColor: '#004b75' }} />
                                                </div>
                                            </div>
                                            <div className="form-group">
                                                <small> <span className="label">New user? Click <a href="/signup">here</a> to sign up</span>
                                                </small>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

//export Login Component
export default Login;
