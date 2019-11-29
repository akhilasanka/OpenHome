import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import { createReservation } from '../util/APIUtils';
import swal from 'sweetalert';

class ReservationCreate extends Component {
    render() {
        if(this.props.authenticated) {
            return <Redirect
                to={{
                pathname: "/",
                state: { from: this.props.location }
            }}/>;
        }

        return (
            <div className="container">
                <div className="content">
                <h1 className="title">Create Reservation</h1>
                  <div className='col-6'>
                    <ReservationCreateForm {...this.props} />
                  </div>
                </div>
            </div>
        );
    }
}

class ReservationCreateForm extends Component {
    constructor(props) {
        super(props);
        this.state = {
            startDate: '',
            endDate: ''
        }

        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleInputChange(event) {
        const target = event.target;
        const inputName = target.name;
        const inputValue = target.value;

        this.setState({
            [inputName] : inputValue
        });
    }

    handleSubmit = async (event) => {
        event.preventDefault();

        console.log(event.target);
        let validInput = true;
        var token = localStorage.getItem("accessToken");

        const createReservationRequest = Object.assign({}, this.state);
        var startDate = new Date(createReservationRequest["startDate"]);
        var endDate = new Date(createReservationRequest["endDate"]);

        if (endDate < startDate) {
            swal("Oops!", "Start Date must be before End Date", "error");
            validInput = false;
        }

        if (validInput) {

          createReservation(createReservationRequest)
          .then(response => {
              swal("Success!", "You've successfully created the reservation!")
          }).catch(error => {
              swal("Oops!", (error && error.message) || 'Oops! Something went wrong. Please try again!', "error");
          });
        }
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit} method="post">
                <div className="form-group row">
                    <label htmlFor="startDate" className="col-sm-4 col-form-label">Start Date:*</label>
                    <div className="col-sm-8">
                        <input name="startDate" type="date" className="form-control" onChange={this.handleInputChange}  required />
                    </div>
                </div>
                <div className="form-group row">
                    <label htmlFor="endDate" className="col-sm-3 col-form-label">End Date:*</label>
                    <div className="col-sm-9">
                        <input name="endDate" type="date" className="form-control" onChange={this.handleInputChange} required />
                    </div>
                </div>
                <div className="form-group row">
                  <button type="submit" className="btn btn-primary align-center">Reserve</button>
                </div>
            </form>
        );
    }
}

export default ReservationCreate
