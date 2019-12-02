import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import { checkInReservation } from '../util/APIUtils';
import swal from 'sweetalert';
import 'react-dates/initialize';
import { DateRangePicker } from 'react-dates';
import 'react-dates/lib/css/_datepicker.css';

class ReservationCheckInButton Component {
    constructor(props) {
        super(props);
        this.state = {
        }
        this.state.reservationId = props.reservationId
        if (this.state.propertyId == null){
          this.state.propertyId = 4; // hard coded for now
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleInputChange({startDate, endDate}) {
        this.setState({startDate, endDate});
        if(!startDate || !endDate) return;
        if(!startDate.isValid() || !endDate.isValid()) return;

        // calculate total cost of reservation
        const getReservationPriceRequest = Object.assign({}, this.state);
        var propertyId = this.state.propertyId;
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
                        onDatesChange={this.handleInputChange} // PropTypes.func.isRequired,
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
                        <div id="total">{this.state.totalPrice}</div>
                    </div>
                </div>
                <button type="submit" className="btn btn-primary align-center">Check-In</button>
            </form>
        );
    }
}

export default ReservationCheckInButton
