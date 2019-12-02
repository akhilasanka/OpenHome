import React, { Component } from 'react';
import '../Styles/Stats.css';
import axios from 'axios';
import {API_BASE_URL, ACCESS_TOKEN } from '../constants';
import Alert from 'react-s-alert';

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
            <th>Weekday Price</th>
            <th>Weekend Price</th>
        </tr>;
        let dataRows = dataArr.filter((elem) => {
            return this.state.selected_option === "0" || 
                elem.propertyId === parseInt(this.state.selected_option);
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
                <div>{curTable}</div>
                <div className="header-label">Future Reservations</div>
                <div>{futureTable}</div>
            </div>
        )
    }
}

export default ReservationStats;
