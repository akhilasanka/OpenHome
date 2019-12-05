import React, { Component } from 'react';
import axios from 'axios';
import {API_BASE_URL, ACCESS_TOKEN } from '../constants';
import Alert from 'react-s-alert';
import GuestNavigation from '../Navigation/GuestNavigation';
import HostNavigation from '../Navigation/HostNavigation';

class AddPayMethod extends Component {
    constructor(props) {
        super(props);
        this.state = {
            cardnumber: '',
            expiryMonth: '',
            expiryYear:'',
            cvv:'',
            minyear:'19'
        };
        this.handleCardNumberChange = this.handleCardNumberChange.bind(this);
        this.handleCardExpiryMonthChange = this.handleCardExpiryMonthChange.bind(this);
        this.handleCardExpiryYearChange = this.handleCardExpiryYearChange.bind(this);
        this.handleCardCVCChange = this.handleCardCVCChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(event) {
      event.preventDefault();
        axios(
            {
                method:'post',
                url:API_BASE_URL + '/pay/addpaymethod',
                data:{
                    userid:parseInt(localStorage.getItem("id")),
                    cardNumber:this.state.cardnumber,
                    expiryMonth: parseInt(this.state.expiryMonth),
                    expiryYear: parseInt(this.state.expiryYear)+2000,
                    cvv: this.state.cvv
                },
                headers: {"Authorization" : "Bearer "+localStorage.getItem(ACCESS_TOKEN)}
            }
        ).then((response)=>{
            Alert.success("Payment method added!");
            this.props.history.push("/home");
        })
        .catch(Alert.error("Failed to add payment method"))
    }

    handleCardCVCChange(event) {
        console.log(event.target.value);
        this.setState({cvv: event.target.value});
    }

    handleCardNumberChange(event) {
        console.log(event.target.value);
        this.setState({cardnumber: event.target.value});
    }

    handleCardExpiryMonthChange(event) {
        console.log(event.target.value);
        this.setState({expiryMonth: event.target.value});
    }

    handleCardExpiryYearChange(event) {
      console.log(event.target.value);
      this.setState({expiryYear: event.target.value});
  }

    render() {
        const tabContent = {
            "width": "500px",
            "margin": "auto",
            "margin-top": "30px"
        }
        const isGuest = localStorage.getItem("role") === "guest";
        let navigation = isGuest? <GuestNavigation/> : <HostNavigation/>;
        return (
          <div>
            {navigation}
            <div className="tab-content" style={tabContent}>
              <form role="form" onSubmit={this.handleSubmit}>
              <div className="form-group">
                <label for="cardNumber">Card number</label>
                <div className="input-group">
                  <input onChange={this.handleCardNumberChange} type="text" name="cardNumber" placeholder="Your 16 digit card number" 
                    className="form-control" pattern="[0-9]{16}" maxLength="16" required/>
                  <div className="input-group-append">
                    <span className="input-group-text text-muted">
                                                <i className="fa fa-cc-visa mx-1"></i>
                                                <i className="fa fa-cc-amex mx-1"></i>
                                                <i className="fa fa-cc-mastercard mx-1"></i>
                                            </span>
                  </div>
                </div>
              </div>
              <div className="row">
                <div className="col-sm-8">
                  <div className="form-group">
                    <label><span className="hidden-xs">Expiration</span></label>
                    <div className="input-group">
                      <input onChange={this.handleCardExpiryMonthChange} type="number" placeholder="MM" name="expirymonth" min="1" max="12" className="form-control" required/>
                      <input onChange={this.handleCardExpiryYearChange} type="number" placeholder="YY" name="expiryyear" min={this.state.minyear} max="99" className="form-control" required/>
                    </div>
                  </div>
                </div>
                <div className="col-sm-4">
                  <div className="form-group mb-4">
                    <label data-toggle="tooltip" title="Three-digits code on the back of your card">CVV
                                                <i className="fa fa-question-circle"></i>
                                            </label>
                    <input type="text"  onChange={this.handleCardCVCChange} placeholder="000" pattern="[0-9]{3}" maxLength="3" name="cvv" required className="form-control"/>
                  </div>
                </div>



              </div>
              <input type="submit" className="subscribe btn btn-primary btn-block rounded-pill shadow-sm" value="Add"/>

            </form>

            </div>
          </div>
        );
    }
}

export default AddPayMethod;