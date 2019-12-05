import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import { checkOutReservation } from '../util/APIUtils';
import swal from 'sweetalert';

class ReservationCheckOutButton extends Component {
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

        var checkOutReservationRequest = {
          reservationId: reservationId
        }

        checkOutReservation(checkOutReservationRequest)
        .then(response => {
          swal("Success!", response.message).then(() => {window.location.reload(false);});
        }).catch(error => {
            swal("Oops!", (error && error.message) || 'Oops! Something went wrong. Please try again!', "error");
        });
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit} method="post">
                <button type="submit" className="btn btn-success align-center">Check-Out</button>
            </form>
        );
    }
}

export default ReservationCheckOutButton
