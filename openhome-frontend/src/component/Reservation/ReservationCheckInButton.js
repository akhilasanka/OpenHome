import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import { checkInReservation } from '../util/APIUtils';
import swal from 'sweetalert';

class ReservationCheckInButton extends Component {
    constructor(props) {
        super(props);
        this.state = {
          reservationId:  props.reservationId
        }

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit = async (event) => {
        event.preventDefault();
        var reservationId = this.state.reservationId;

        var checkInReservationRequest = {
          reservationId: reservationId
        }

        checkInReservation(checkInReservationRequest)
        .then(response => {
          swal("Success!", response.message)
        }).catch(error => {
            swal("Oops!", (error && error.message) || 'Oops! Something went wrong. Please try again!', "error");
        });
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit} method="post">
                <button type="submit" className="btn btn-success align-center">Check-In</button>
            </form>
        );
    }
}

export default ReservationCheckInButton
