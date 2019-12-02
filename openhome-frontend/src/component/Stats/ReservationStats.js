import React, { Component } from 'react';
import axios from 'axios';
import {API_BASE_URL, ACCESS_TOKEN } from '../constants';
import Alert from 'react-s-alert';

class ReservationStats extends Component {
    constructor(props) {
        super(props);
        // sample response to test things out.
        // changes in BE. Add "all":"All properties" to validProperties
        this.state = {data:null, selected_option:null};
        this.handleOptionSelect = this.handleOptionSelect.bind(this);
    }
    handleOptionSelect(event) {
        this.setState({selected_option: event.target.value})
    }
    componentDidMount() {
        this.setState({
            data:{"success":true,
            "validProperties":{"all": "All Properties", "4":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California", "5":"Townhouse (Private Room) at 123 Redwood Dr,Petaluma,California"},
            "past":[
                {"reservationId":1,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":2,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":3,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":5,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":6,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":5},
                {"reservationId":7,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":5}],
            "current":[{"reservationId":8,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4}],
            "future":[{"reservationId":9,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":5},
                {"reservationId":10,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Jan 2020","endDate":"Jan 2020","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":11,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":12,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":13,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":14,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":15,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":5},
                {"reservationId":16,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":5},
                {"reservationId":17,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":18,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":19,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Jan 2020","endDate":"Jan 2020","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":22,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4}]}
                ,selected_option:"all"
        })
    }
    render() {
        let select = null;
        if(this.state.data!=null && this.state.selected_option!=null) {
            const isGuest = localStorage.getItem("role") === "guest";
            
            if(!isGuest) {
                let options = Object.keys(this.state.data.validProperties).map(key => {
                        if(this.state.selected_option === key) {
                            return <option value={key} selected> {this.state.data.validProperties[key]}</option>
                        } else {
                            return <option value={key}> {this.state.data.validProperties[key]}</option>
                        }
                    }
                );
                select = <select onChange={this.handleOptionSelect} 
                    class="custom-select my-1 mr-sm-2" id="inlineFormCustomSelectPref">
                    {options}
                </select>
            }
        }
        return(
            <form class="form-inline">
                <label class="my-1 mr-2" for="inlineFormCustomSelectPref">Your reservation Summary for</label>
                {select}
                {this.state.selected_option}
            </form>
        )
    }
}

export default ReservationStats;
