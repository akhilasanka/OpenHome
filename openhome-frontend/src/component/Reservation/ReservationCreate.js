import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import { createReservation, getCurrentSystemTime } from '../util/APIUtils';
import swal from 'sweetalert';
import 'react-dates/initialize';
import { DateRangePicker } from 'react-dates';
import 'react-dates/lib/css/_datepicker.css';

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
            <div className="card">
                <div className="container">
                    <div className="content">
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
                    <label htmlFor="startDate" className="col-12 col-form-label">Dates</label>
                    <div className="col-12">
                      <DateRangePicker
                        startDate={this.state.startDate} // momentPropTypes.momentObj or null,
                        startDateId="startDate" // PropTypes.string.isRequired,
                        endDate={this.state.endDate} // momentPropTypes.momentObj or null,
                        endDateId="endDate" // PropTypes.string.isRequired,
                        onDatesChange={({ startDate, endDate }) => this.setState({ startDate, endDate })} // PropTypes.func.isRequired,
                        focusedInput={this.state.focusedInput} // PropTypes.oneOf([START_DATE, END_DATE]) or null,
                        onFocusChange={focusedInput => this.setState({ focusedInput })} // PropTypes.func.isRequired,
                        small={true}
                      />
                    </div>
                </div>
                <div className="form-group row">
                    <div className="col-12 col-form-label">
                        Total:
                    </div>
                    <div className="col-12">
                        <div id="total">Select a date range!</div>
                    </div>
                </div>
                <button type="submit" className="btn btn-primary align-center">Reserve</button>
            </form>
        );
    }
}

export default ReservationCreate
