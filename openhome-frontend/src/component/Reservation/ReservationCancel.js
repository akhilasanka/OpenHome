import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import swal from 'sweetalert';
import 'react-dates/initialize';
import { DateRangePicker } from 'react-dates';
import 'react-dates/lib/css/_datepicker.css';
import ReservationCancelButton from "../Reservation/ReservationCancelButton";

class ReservationCancel extends Component {
    constructor(props) {
        super(props);
        this.state = {
          reservationId: this.props.match.params.id
        }
    }

    render() {
        return (
            <div className="container">
                <div className="content">
                <ReservationCancelButton reservationId={this.state.reservationId}/>
                </div>
            </div>
        );
    }
}

export default ReservationCancel
