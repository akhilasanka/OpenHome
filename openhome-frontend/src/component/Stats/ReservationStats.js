import React, { Component } from 'react';
import '../Styles/Stats.css';
import axios from 'axios';
import {API_BASE_URL, ACCESS_TOKEN } from '../constants';
import Alert from 'react-s-alert';

class ReservationStats extends Component {
    constructor(props) {
        super(props);
        // sample response to test things out.
        // changes in BE. Add 0:"All properties" to validProperties
        // change key in validProperties to Long/Integer.
        // startdate & end date needs to have dayOfMonth.

        this.state = {data:null, selected_option:null};
        this.handleOptionSelect = this.handleOptionSelect.bind(this);
        this.makeTable = this.makeTable.bind(this);
    }
    handleOptionSelect(event) {
        this.setState({selected_option: parseInt(event.target.value)})
    }
    makeTable(dataArr) {
        let retData = <div>No reservations available.</div>;
        let tableHeader = <tr>
            <td>Property</td>
            <td>StartDate</td>
            <td>End Date</td>
            <td>Weekday Price</td>
            <td>Weekend Price</td>
        </tr>;
        let dataRows = dataArr.filter((elem) => {
            return this.state.selected_option === 0 || elem.propertyId === this.state.selected_option;
        })
        if(dataRows !== null && dataRows.length > 0) {
            const dataTableRows = dataRows.map(data => {
                return (<tr>
                    <td>{data.propertyName}</td>
                    <td>{data.startDate}</td>
                    <td>{data.endDate}</td>
                    <td>{data.weekdayPrice}</td>
                    <td>{data.weekendPrice}</td>
                </tr>);
            });
            retData = <table className="table">
            <thead>{tableHeader}</thead>
            <tbody>{dataTableRows}</tbody>
            </table>
        }
        return retData;
    }
    componentDidMount() {
        this.setState({
            data:{"success":true,
            "validProperties":{0: "All Properties", 4:"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California", "5":"Townhouse (Private Room) at 123 Redwood Dr,Petaluma,California"},
            "past":[
                {"reservationId":1,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":2,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":3,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":5,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":6,"propertyName":"Townhouse (Private Room) at 123 Redwood Dr,Petaluma,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":5},
                {"reservationId":7,"propertyName":"Townhouse (Private Room) at 123 Redwood Dr,Petaluma,California","startDate":"Nov 2019","endDate":"Nov 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":5}],
            "current":[{"reservationId":8,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Nov 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4}],
            "future":[{"reservationId":9,"propertyName":"Townhouse (Private Room) at 123 Redwood Dr,Petaluma,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":5},
                {"reservationIdlet ":10,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Jan 2020","endDate":"Jan 2020","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationIdlet ":11,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationIdlet ":12,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationIdlet ":13,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationIdlet ":14,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationIdlet ":15,"propertyName":"Townhouse (Private Room) at 123 Redwood Dr,Petaluma,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":5},
                {"reservationId":16,"propertyName":"Townhouse (Private Room) at 123 Redwood Dr,Petaluma,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":5},
                {"reservationId":17,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":18,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":19,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Jan 2020","endDate":"Jan 2020","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4},
                {"reservationId":22,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"Dec 2019","endDate":"Dec 2019","weekdayPrice":1.0,"weekendPrice":0.0,"propertyId":4}]}
                ,selected_option:0
        })
    }
    render() {
        let select = null;
        let noData = <div>No reservations available.</div>; 
        let pastTable = noData;
        let curTable = noData;
        let futureTable = noData;
        let headerLabel = "Your reservation Summary";
        
        if(this.state.data!=null && this.state.selected_option!=null) {
            const isGuest = localStorage.getItem("role") === "guest";
            pastTable = this.makeTable(this.state.data.past);
            curTable = this.makeTable(this.state.data.current);
            futureTable = this.makeTable(this.state.data.future);
            if(!isGuest) {
                headerLabel = "Your reservation Summary for";
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
            <div>
                <div className="header-label">{headerLabel}</div>
                <div>{select}</div>
                <div className="header-label">Past Reservations</div>
                <div>{pastTable}</div>
                <div className="header-label">Current Reservations</div>
                <div></div>
                <div className="header-label">Future Reservations</div>
                <div></div>
            </div>
        )
    }
}

export default ReservationStats;
