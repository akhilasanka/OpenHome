import React, { Component } from 'react';
import '../Styles/Stats.css';
import axios from 'axios';
import {API_BASE_URL, ACCESS_TOKEN } from '../constants';
import GuestNavigation from '../Navigation/GuestNavigation';
import HostNavigation from '../Navigation/HostNavigation';

class ReservationStats extends Component {
    constructor(props) {
        super(props);
        this.state = {data:null, selected_option:null};
        this.handleOptionSelect = this.handleOptionSelect.bind(this);
        this.makeTable = this.makeTable.bind(this);
    }
    handleOptionSelect(event) {
        this.setState({selected_option: event.target.value})
    }
    makeTable(dataArr) {
        let retData = <div>No reservations available.</div>;
        let tableHeader = <tr>
            <th>Property</th>
            <th>StartDate</th>
            <th>End Date</th>
            <th>Total Price</th>
            <th>Status</th>
        </tr>;
        let dataRows = dataArr.filter((elem) => {
            return this.state.selected_option === "0" ||
                elem.propertyId === parseInt(this.state.selected_option);
        })
        if(dataRows !== null && dataRows.length > 0) {
            const dataTableRows = dataRows.map(data => {
                var reservationUrl = "/reservation/view/"+data.reservationId;
                var totalPrice = data.totalPrice.toFixed(2);

                // hacky for now lol
                let reservationStatus = "";
                let statusEnum = data.status;
                if (statusEnum === 'pendingCheckIn') {
                  reservationStatus = 'Pending Check-In'
                }
                else if (statusEnum === 'checkedIn') {
                  reservationStatus = 'Checked-In'
                }
                else if (statusEnum === 'checkedOut') {
                  reservationStatus = 'Checked-Out'
                }
                else if (statusEnum === 'automaticallyCanceled') {
                  reservationStatus = 'Canceled Automatically (No Show!)'
                }
                else if (statusEnum === 'guestCanceledBeforeCheckIn' || statusEnum === 'guestCanceledAfterCheckIn') {
                  reservationStatus = 'Canceled by Guest'
                }
                else if (statusEnum === 'hostCanceledBeforeCheckIn' || statusEnum === 'hostCanceledAfterCheckIn' || statusEnum ==='pendingHostCancelation') {
                  reservationStatus = 'Canceled by Host'
                }

                console.log(data);
                return (<tr>
                    <td><a href={reservationUrl}>{data.propertyName}</a></td>
                    <td>{data.startDate}</td>
                    <td>{data.endDate}</td>
                    <td>{totalPrice}</td>
                    <td>{reservationStatus}</td>
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
        axios({
            method:'GET',
            url:API_BASE_URL + '/stats/getreservations',
            headers: {"Authorization" : "Bearer "+localStorage.getItem(ACCESS_TOKEN)}
        }).then(response => {
            console.log(response)
            this.setState({data: response.data, selected_option:"0"})
        })
    }
    render() {
        let select = null;
        let noData = <div>No reservations available.</div>;
        let pastTable = noData;
        let curTable = noData;
        let futureTable = noData;
        let headerLabel = "Your reservation Summary";
        const isGuest = localStorage.getItem("role") === "guest";
        if(this.state.data!=null && this.state.selected_option!=null) {

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
        let navigation = isGuest? <GuestNavigation/> : <HostNavigation/>;
        return(
            <div>
                {navigation}
            <div>
                <div className="header-label">{headerLabel}</div>
                <div>{select}</div>
                <div className="header-label">Past Reservations</div>
                <div>{pastTable}</div>
                <div className="header-label">Current Reservations</div>
                <div>{curTable}</div>
                <div className="header-label">Future Reservations</div>
                <div>{futureTable}</div>
            </div>
            </div>
        )
    }
}

export default ReservationStats;
