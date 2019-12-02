import React, { Component } from 'react';
import axios from 'axios';
import {API_BASE_URL, ACCESS_TOKEN } from '../constants';
import Alert from 'react-s-alert';

class BillingStats extends Component {
    constructor(props) {
        super(props);
        // sample response to test things out.
        // changes in BE. Add 0:"All properties" to validProperties
        // change key in validProperties to Long/Integer.
        this.state = {data:null, selected_option:null, selected_month: null};
        this.handleOptionSelect = this.handleOptionSelect.bind(this);
        this.handleMonthSelect = this.handleMonthSelect.bind(this);
        this.makeTable = this.makeTable.bind(this);

    }
    handleOptionSelect(event) {
        this.setState({selected_option: parseInt(event.target.value)});
    }
    handleMonthSelect(event) {
        this.setState({selected_month: event.target.value});
    }

    componentDidMount() {
        this.setState({data:{"success":true,
            "validProperties":{0:"All Properties", 5:"one more prop", 4:"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California"},
            "allMonths":["Jan `19","Feb `19","Mar `19","Apr `19","May `19","Jun `19","Jul `19","Aug `19","Sep `19","Oct `19","Nov `19","Dec `19"],
            "lineItems":[{"transactionMonth":"Nov `19","transactionId":1,"reservationId":1,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":-10.0,"chargedDate":"29 Nov `19","type":"Change/Cancel Penalty","card":"HOST"},
            {"transactionMonth":"Nov `19","transactionId":2,"reservationId":1,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":-10.0,"chargedDate":"29 Nov `19","type":"Change/Cancel Penalty","card":"HOST"},
            {"transactionMonth":"Nov `19","transactionId":3,"reservationId":1,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":-10.0,"chargedDate":"29 Nov `19","type":"Change/Cancel Penalty","card":"HOST"},
            {"transactionMonth":"Nov `19","transactionId":4,"reservationId":2,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":100.0,"chargedDate":"29 Nov `19","type":"Guest Change/Cancel Credit","card":"4444"},
            {"transactionMonth":"Nov `19","transactionId":5,"reservationId":3,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":500.0,"chargedDate":"29 Nov `19","type":"Guest Check-in credit","card":"4444"},
            {"transactionMonth":"Nov `19","transactionId":6,"reservationId":1,"propertyId":4,"propertyName":"Townhouse (Private Room) at 5805 Charlotte dr apt 270,San jose,California","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":-10.0,"chargedDate":"29 Nov `19","type":"Change/Cancel Penalty","card":"HOST"},
            {"transactionMonth":"Nov `19","transactionId":7,"reservationId":2,"propertyId":5,"propertyName":"One More Property","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":100.0,"chargedDate":"29 Nov `19","type":"Guest Change/Cancel Credit","card":"4444"},
            {"transactionMonth":"Nov `19","transactionId":8,"reservationId":3,"propertyId":5,"propertyName":"One More Property","startDate":"29 Nov `19","endDate":"29 Nov `19","amount":500.0,"chargedDate":"29 Nov `19","type":"Guest Check-in credit","card":"4444"}],
            
            }, selected_option:0, selected_month: "Jan `19"})
    }

    makeTable(dataArr, isGuest) {
        let retData = <div>There were no charges for {this.state.selected_month}</div>; 
        let tableHeader = <tr>
            <td>Property</td>
            <td>StartDate</td>
            <td>End Date</td>
            <td>Amount</td>
            <td>Memo</td>
            <td>Charged on</td>
        </tr>;
        if(isGuest) {
            tableHeader = <tr>
                <td>Property</td>
                <td>StartDate</td>
                <td>End Date</td>
                <td>Amount</td>
                <td>Memo</td>
                <td>Charged on</td>
                <td>Card Used</td>
            </tr>;
        }
        let dataRows = dataArr.filter((elem) => {
            return (this.state.selected_option === 0 || 
                elem.propertyId === this.state.selected_option) && 
                elem.transactionMonth === this.state.selected_month;
        })
        if(dataRows !== null && dataRows.length > 0) {
            const numReservations = dataRows.length;
            const totalCredits = dataRows.filter(elem => {
                return parseFloat(elem.amount) > 0.0
            }).map(elem => parseFloat(elem.amount)).reduce((total, elem) => {
                return total + elem;
            })
            const totalCharges = -1.0* (dataRows.filter(elem => {
                return parseFloat(elem.amount) < 0.0
            }).map(elem => parseFloat(elem.amount)).reduce((total, elem) => {
                return total + elem;
            }));
            const dataTableRows = dataRows.map(data => {
                let cardData = null;
                if(isGuest)
                    cardData = <td>...{data.card}</td>;

                return (<tr>
                    <td>{data.propertyName}</td>
                    <td>{data.startDate}</td>
                    <td>{data.endDate}</td>
                    <td>{data.amount}</td>
                    <td>{data.type}</td>
                    <td>{data.chargedDate}</td>
                    {cardData}
                </tr>);
            });
            retData = 
            <div>
                <table className="table">
                    <thead>{tableHeader}</thead>
                    <tbody>{dataTableRows}</tbody>
                </table>
                <div>In {this.state.selected_month} a total of {numReservations} reservations were made,
                you were credited {totalCredits} and charged {totalCharges}.</div>
            </div>
        }
        return retData;
    }


    render() {
        let select = null;
        let monthselect = null;
        let noData = <div>There were no charges for {this.state.selected_month}</div>; 
        let pastTable = noData;
        let headerLabel = "Your billing Summary";
        
        if(this.state.data!=null && this.state.selected_option!=null 
            && this.state.selected_month!=null) {
            const isGuest = localStorage.getItem("role") === "guest";
            pastTable = this.makeTable(this.state.data.lineItems, isGuest);
            if(!isGuest) {
                headerLabel = "Your billing Summary for";
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
            let monthOptions = this.state.data.allMonths.map(month => {
                if(this.state.selected_month === month)
                    return (<option value={month} selected>{month}</option>);
                else 
                    return (<option value={month}>{month}</option>);
            })
            monthselect = <select onChange={this.handleMonthSelect} 
                    class="custom-select my-1 mr-sm-2" id="inlineFormCustomSelectPref">
                    {monthOptions}
                </select>;
        }
        return(
            <div>
                <div className="header-label">{headerLabel}</div>
                <div>{select}</div>
                <div>{monthselect}</div>
                <div className="header-label">Billing line items</div>
                <div>{pastTable}</div>
            </div>
        )

    }
}

export default BillingStats;