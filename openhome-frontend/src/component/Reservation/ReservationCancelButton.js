import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import { cancelReservation } from '../util/APIUtils';
import swal from 'sweetalert';

class ReservationCancelButton extends Component {
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

        swal({
            title: "Caution",
            text: "Canceling the reservation can incur a penalty of 15% for up to 2 days.\n\nAre you sure?",
            icon: "warning",
            buttons: [
              'No, nevermind!',
              'Yes, I am sure!'
            ],
            dangerMode: true,
        }).then(function(isConfirm) {
            if (isConfirm){
                var cancelReservationRequest = {
                  reservationId: reservationId
                }

                cancelReservation(cancelReservationRequest)
                .then(response => {
                  swal("Success!", response.message)
                }).catch(error => {
                    swal("Oops!", (error && error.message) || 'Oops! Something went wrong. Please try again!', "error");
                });
            }
        });

    }

    render() {
        return (
            <form onSubmit={this.handleSubmit} method="post">
                <button type="submit" className="btn btn-danger align-center">Cancel</button>
            </form>
        );
    }
}

export default ReservationCancelButton
