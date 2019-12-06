import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import { createReservation, getReservationPrice } from '../util/APIUtils';
import swal from 'sweetalert';
import 'react-dates/initialize';
import { DateRangePicker } from 'react-dates';
import 'react-dates/lib/css/_datepicker.css';

class ReservationCreateButton extends Component {
    constructor(props) {
        super(props);
        this.state = {
        }
        this.state.totalPrice = "Select a date range!";
        this.state.propertyId = props.propertyId;
        this.state.startDate = props.startDate;
        this.state.startDateFormatted = new Date(props.startDate).toDateString()
        this.state.endDate =  props.endDate;
        this.state.endDateFormatted = new Date(props.endDate).toDateString()
        this.state.enabled= props.enabled;
        this.state.redirectAfterBooking = false;

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    componentDidMount() {
        // get total cost of reservation
        var propertyId = this.state.propertyId;
        var startDate = this.state.startDate;
        var endDate = this.state.endDate;
        var payload = {
          startDate: startDate,
          endDate: endDate,
          propertyId: propertyId
        }

        getReservationPrice(payload)
        .then(response => {
          this.state.totalPrice = "$ " +  response;
          this.forceUpdate()
        }).catch(error => {
            swal("Oops!", (error && error.message) || 'Oops! Something went wrong fetching the total price. Please try again!', "error");
        });

    }

    handleSubmit = async (event) => {
        event.preventDefault();
        let validInput = true;

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
            swal("Success!", "You've successfully created the reservation!").then(() => {this.setState({redirectAfterBooking: true});});

          }).catch(error => {
              swal("Oops!", (error && error.message) || 'Oops! Something went wrong. Please try again!', "error");
          });
        }
    }

    render() {
        if (this.state.redirectAfterBooking) {
            return <Redirect to="/stats/reservations" />;
        }

        let buttonOrWarningElement = <button type="submit" className="btn btn-primary align-center">Reserve</button>;
        if(localStorage.verified && localStorage.verified === "false") {
            buttonOrWarningElement = <div className="alert alert-warning"> Please verify your email id. If you have already verified. Logout and login back again to make reservations!</div>
        }
        else if (!this.state.enabled){
            buttonOrWarningElement = <div className="alert alert-warning"> To reserve you must add a payment method! <a href='/addpayment'>Click Here!</a> </div>
        }

        return (
            <form onSubmit={this.handleSubmit} method="post">
                <div className="form-group row">
                    <div className="col-12 col-form-label">
                        Dates:
                    </div>
                    <div className="col-12">
                        <div id="dates">{this.state.startDateFormatted} - {this.state.endDateFormatted}</div>
                    </div>
                </div>
                <div className="form-group row">
                    <div className="col-12 col-form-label">
                        Total:
                    </div>
                    <div className="col-12">
                        <div id="total">{this.state.totalPrice}</div>
                    </div>
                </div>
                {buttonOrWarningElement}
            </form>
        );
    }
}

export default ReservationCreateButton
