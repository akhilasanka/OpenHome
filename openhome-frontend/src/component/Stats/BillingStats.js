import React, { Component } from 'react';
import axios from 'axios';
import {API_BASE_URL, ACCESS_TOKEN } from '../constants';
import Alert from 'react-s-alert';

class BillingStats extends Component {
    constructor(props) {
        super(props);
        // sample response to test things out.
        this.state = {"success":true,
        "validProperties":{"4":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California"},
        "allMonths":["Jan `19","Feb `19","Mar `19","Apr `19","May `19","Jun `19","Jul `19","Aug `19","Sep `19","Oct `19","Nov `19","Dec `19"],
        "lineItems":[{"transactionMonth":"Nov `19","transactionId":1,"reservationId":1,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":-10.0,"chargedDate":"29 Nov `19","type":"Change/Cancel Penalty","card":"HOST"},
        {"transactionMonth":"Nov `19","transactionId":2,"reservationId":1,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":-10.0,"chargedDate":"29 Nov `19","type":"Change/Cancel Penalty","card":"HOST"},
        {"transactionMonth":"Nov `19","transactionId":3,"reservationId":1,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":-10.0,"chargedDate":"29 Nov `19","type":"Change/Cancel Penalty","card":"HOST"},
        {"transactionMonth":"Nov `19","transactionId":4,"reservationId":2,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":100.0,"chargedDate":"29 Nov `19","type":"Guest Change/Cancel Credit","card":"4444"},
        {"transactionMonth":"Nov `19","transactionId":5,"reservationId":3,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":500.0,"chargedDate":"29 Nov `19","type":"Guest Check-in credit","card":"4444"},
        {"transactionMonth":"Nov `19","transactionId":6,"reservationId":1,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":-10.0,"chargedDate":"29 Nov `19","type":"Change/Cancel Penalty","card":"HOST"},
        {"transactionMonth":"Nov `19","transactionId":7,"reservationId":2,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":100.0,"chargedDate":"29 Nov `19","type":"Guest Change/Cancel Credit","card":"4444"},
        {"transactionMonth":"Nov `19","transactionId":8,"reservationId":3,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":500.0,"chargedDate":"29 Nov `19","type":"Guest Check-in credit","card":"4444"}]}
    }
    render() {
        var role = localStorage.getItem("role");

        return(
            <form class="form-inline">
                <label class="my-1 mr-2" for="inlineFormCustomSelectPref">Your reservation Summary for</label>
                <select class="custom-select my-1 mr-sm-2" id="inlineFormCustomSelectPref">
                    <option value="1" selected>One</option>
                    <option value="2">Two</option>
                    <option value="3">Three</option>
                </select>
            </form>
        )
    }
}

export default BillingStats;