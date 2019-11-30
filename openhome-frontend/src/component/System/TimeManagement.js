import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import { getCurrentSystemTime, addToCurrentSystemTime } from '../util/APIUtils';
import swal from 'sweetalert';

class TimeManagement extends Component {
    render() {
        if(this.props.authenticated) {
            return <Redirect
                to={{
                pathname: "/",
                state: { from: this.props.location }
            }}/>;
        }

        return (
            <div>
                <div className="container">
                    <div className="content">
                        <div className="card">
                            <h2>Time Management</h2>
                            <CurrentTimeDisplay {...this.props} />
                        </div>
                        <div className="card">
                            <TimeManagementForm {...this.props} />
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}


class CurrentTimeDisplay extends Component {
    constructor(props) {
        super(props);
        this.state = {
        }
        this.setCurrentTime();
    }

    setCurrentTime() {
      getCurrentSystemTime().then(response => {
        this.setState({
          curTime : response.toLocaleString()
        })
      });
    }

    componentDidMount() {
      setInterval( () => {
        this.setCurrentTime()
      },5000)
    }

    render() {
        return (
          <div>
              <p>
                <strong>The current* system time is:</strong> {this.state.curTime}
              </p>
              <p>
                * this time refreshes every 5 seconds
              </p>
          </div>
        );
    }
}

class TimeManagementForm extends Component {
    constructor(props) {
        super(props);
        this.state = {
          timeOffset : 1
        }
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleInputChange(event) {
        const target = event.target;
        const inputName = target.name;
        const inputValue = target.value;

        console.log(target);
        this.setState({
            [inputName] : inputValue
        });
    }

    handleSubmit = async (event) => {
        event.preventDefault();
        let validInput = true;
        var token = localStorage.getItem("accessToken");
        const addTimeRequest = Object.assign({}, this.state);
        const timeOffset = addTimeRequest['timeOffset'];

        addToCurrentSystemTime(addTimeRequest)
        .then(response => {
            swal("Success!", "You've successfully added " + timeOffset + " hour(s) to current system time!")
        }).catch(error => {
            swal("Oops!", (error && error.message) || 'Oops! Something went wrong. Please try again!', "error");
        });
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit} method="post">
                <label htmlFor="startDate" className="col-12 col-form-label">Add Time to the Current System Time</label>
                <div className="col-12">
                  <select className="form-control col-sm-6" name="timeOffset" onChange={this.handleInputChange} value={"1"}>
                      <option value="1">1 Hour</option>
                      <option value="2">2 Hours</option>
                      <option value="4">4 Hours</option>
                      <option value="8">8 Hours</option>
                      <option value="12">12 Hours</option>
                      <option value="24">24 Hours</option>
                      <option value="48">2 Days</option>
                      <option value="72">3 Days</option>
                      <option value="96">4 Days</option>
                      <option value="120">5 Days</option>
                      <option value="144">6 Days</option>
                      <option value="168">7 Days</option>
                  </select>
                </div>
                <button type="submit" className="btn btn-primary align-center">Add Time</button>
            </form>
        );
    }
}


export default TimeManagement
