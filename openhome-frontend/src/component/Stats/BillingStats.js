import React, { Component } from 'react';
import axios from 'axios';
import {API_BASE_URL, ACCESS_TOKEN } from '../constants';
import GuestNavigation from '../Navigation/GuestNavigation';
import HostNavigation from '../Navigation/HostNavigation';

class BillingStats extends Component {
    constructor(props) {
        super(props)
        this.state = {data:null, selected_option:null, selected_month: null};
        this.handleOptionSelect = this.handleOptionSelect.bind(this);
        this.handleMonthSelect = this.handleMonthSelect.bind(this);
        this.makeTable = this.makeTable.bind(this);

    }
    handleOptionSelect(event) {
        this.setState({selected_option: event.target.value});
    }
    handleMonthSelect(event) {
        this.setState({selected_month: event.target.value});
    }

    componentDidMount() {
        axios({
            method:'GET',
            url:API_BASE_URL + '/api/stats/getbillingsummary',
            headers: {"Authorization" : "Bearer "+localStorage.getItem(ACCESS_TOKEN)}
        }).then(response => {
            console.log(response)
            this.setState({data: response.data, selected_option:"0",
                selected_month: response.data.allMonths[0]})
        })
    }

    makeTable(dataArr, isGuest) {
        let retData = <div>There were no charges for {this.state.selected_month}</div>;
        let cardUsedHeaderCol = null
        if(isGuest) {
            cardUsedHeaderCol = <th>Card Used</th>;
        }
        let tableHeader = <tr>
            <th>Property</th>
            <th>StartDate</th>
            <th>End Date</th>
            <th>Amount</th>
            <th>Memo</th>
            <th>Charged on</th>
            {cardUsedHeaderCol}
        </tr>;
        let dataRows = dataArr.filter((elem) => {
            return (this.state.selected_option === "0" ||
                elem.propertyId === parseInt(this.state.selected_option)) &&
                elem.transactionMonth === this.state.selected_month;
        })
        if(dataRows !== null && dataRows.length > 0) {
            const numReservations = dataRows.length;

            const totalCreditsArr = dataRows.filter(elem => {
                return parseFloat(elem.amount) > 0.0
            }).map(elem => parseFloat(elem.amount))
            const totalCredits = totalCreditsArr.length > 0 ? totalCreditsArr.reduce((total, elem) => {
                return total + elem;
            }) : 0.0;

            const totalChargesArr = dataRows.filter(elem => {
                return parseFloat(elem.amount) < 0.0
            }).map(elem => parseFloat(elem.amount));
            const totalCharges = totalChargesArr.length>0? -1.0*(totalChargesArr.reduce((total, elem) => {
                return total + elem;
            })):0.0;

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
                    <thead class="thead-dark">{tableHeader}</thead>
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
        const isGuest = localStorage.getItem("role") === "guest";
        if(this.state.data!=null && this.state.selected_option!=null
            && this.state.selected_month!=null) {
            pastTable = this.makeTable(this.state.data.lineItems, isGuest);
            if(!isGuest) {
                headerLabel = "The billing Summary for your properties";
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
        let navigation = isGuest? <GuestNavigation/> : <HostNavigation/>;
        return(
            <div>
                {navigation}
            <div>
                <div className="header-label main-header-sum">{headerLabel}</div>
                <div>{select}</div>
                <div>{monthselect}</div>
                <div className="header-label">Billing line items</div>
                <div>{pastTable}</div>
            </div>
            </div>
        )

    }
}

export default BillingStats;
