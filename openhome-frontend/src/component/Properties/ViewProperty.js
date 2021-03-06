import React, {Component} from 'react';
// import DatePicker from 'react-datepicker';
// import 'react-datepicker/dist/react-datepicker.css';
import {Redirect} from 'react-router';
import '../Styles/HostProperty.css'
import axios from 'axios';
import {ACCESS_TOKEN, API_BASE_URL} from "../constants";
import ReservationCreateButton from "../Reservation/ReservationCreateButton";
import { getQueryStringValue } from '../util/URLUtils';
import { hasValidPaymentMethod } from '../util/APIUtils';
import {Link} from "react-router-dom";
import { confirmAlert } from 'react-confirm-alert'; // Import
import 'react-confirm-alert/src/react-confirm-alert.css';
import HostNavigation from "../Navigation/HostNavigation";
import GuestNavigation from "../Navigation/GuestNavigation"; // Import css
import Alert from "react-s-alert";
import swal from 'sweetalert';

class PropertyDisplay extends Component {

    constructor(props) {
        super(props);

        this.state = {
            arrivalDate: new Date(),
            departureDate: new Date(),
            propertyDetails: {},
            photos: [],
            bookingStartDate: "",
            bokingEndDate: "",
            guests: 2,
            totalCost: 0,
            weekdays: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
            weekends: ['Saturday', 'Sunday'],
            errorRedirect: false,

            startDate: getQueryStringValue('startDate'),
            endDate: getQueryStringValue('endDate'),
            hasValidPaymentMethod: false,
            redirect : false
        }

        //Bind
        this.submitBooking = this.submitBooking.bind(this);
        this.handleArrivalDateChange = this.handleArrivalDateChange.bind(this);
        this.handleDepartureDateChange = this.handleDepartureDateChange.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleDeleteProperty = this.handleDeleteProperty.bind(this);
    }

    componentDidMount() {
        axios.defaults.withCredentials = true;

        var data = {
            PropertyId: this.props.match.params.id
        }
        console.log('Data: ', data);

        axios(
            {
                method: 'get',
                url: API_BASE_URL + '/api/property/' + this.props.match.params.id,
                headers: {"Authorization": "Bearer " + localStorage.getItem(ACCESS_TOKEN)}
            }
        ).then(response => {
            if (response.status === 200) {
                console.log('Result: ', response.data);
                var propertyDetails = response.data;
                this.setState({
                    propertyDetails: propertyDetails,
                    photos: JSON.parse(propertyDetails.photosArrayJson)
                }, console.log(this.state));
            }
        }).catch((err) => {
            if (err) {
                this.setState({
                    errorRedirect: true
                })
            }
        });

        hasValidPaymentMethod()
        .then(response => {
          console.log("has valid");
          this.setState({hasValidPaymentMethod: response.hasPayMethod});
          this.forceUpdate();
        }).catch(error => {
          console.log(error);
        });
    }

    submitBooking = (e) => {

        axios.defaults.withCredentials = true;
        var data = {
            PropertyId: this.props.match.params.id,
            Bookingstartdate: this.state.bookingStartDate,
            Bookingenddate: this.state.bookingEndDate,
            Guests: this.state.guests,
            Totalcost: e.target.value
        }

        axios.post('http://localhost:3001/submit-booking', data)
            .then(response => {
                if (response.status === 200) {
                    console.log('Booking Successful!');
                }
            }).catch((err) => {
            if (err) {
                this.setState({
                    errorRedirect: true
                })
            }
        });

    }

    handleArrivalDateChange(date) {

        var month = date.toString().split(' ')[1];
        var day = date.toString().split(' ')[2];

        this.setState({
            arrivalDate: date,
            bookingStartDate: month + ' ' + day
        });
    }

    handleDepartureDateChange(date) {

        var month = date.toString().split(' ')[1];
        var day = date.toString().split(' ')[2];

        this.setState({
            departureDate: date,
            bookingEndDate: month + ' ' + day
        });
    }

    handleInputChange = (event) => {

        const target = event.target;
        const name = target.name;
        const value = target.value;


        this.setState({
            [name]: value
        });
    }

    handleDeleteProperty = () => {
        var propertyID = this.props.match.params.id;
        let redirect = false;
        console.log(API_BASE_URL + '/hosts/' + localStorage.id + '/property/' + propertyID + '/delete');
        axios(
            {
                method: 'post',
                url: API_BASE_URL + '/api/hosts/' + localStorage.id + '/property/' + propertyID + '/delete',
                params: { "isPenalityApproved": false },
                headers: { "Authorization": "Bearer " + localStorage.getItem(ACCESS_TOKEN) }
            }
        ).then((response) => {
            console.log("*******************");
            console.log(response);
            if(response.data.status=="NeedsApproval"){
                swal({
                    title: "Caution",
                    text: "Changes affecting existing reservations. 15% of reservation amount will be charged as PENALITY for reservations within a week. Are you sure you want to proceed?",
                    icon: "warning",
                    buttons: [
                      'No, cancel it!',
                      'Yes, I am sure!'
                    ],
                    dangerMode: true,
                  }).then(function(isConfirm) {
                    if (isConfirm) {
                        axios(
                            {
                                method: 'post',
                                url: API_BASE_URL + '/api/hosts/' + localStorage.id + '/property/' + propertyID + '/delete',
                                params: { "isPenalityApproved": true },
                                headers: { "Authorization": "Bearer " + localStorage.getItem(ACCESS_TOKEN) }
                            }
                        ).then((response) => {
                           if(response.data.status=="EditSuccessful"){
                            redirect = true;
                            swal({
                                title: 'OK',
                                text: 'Updated with penality charged!',
                                icon: 'success'
                              });
                           }
                           else if(response.data.status=="EditError"){
                                swal("Oops!","Failed to make changes. Please try again! "+response.data.message,"error");
                           }
                        });
                    } else {
                      swal("Cancelled", "No changes have been made :)", "success");
                    }
                  })
            }
            else if(response.data.status=="EditSuccessful"){
                swal("Sucessfully deleted property!");
                redirect = true;

            }
            if(redirect){
                this.setState({
                    redirect: true
                })
            }
        })
            .catch(Alert.error("Failed to delete property. Please try again."))
    }

    render() {
        // let redrirectVar = null;
        // if (!cookie.load('cookie')) {
        //     redrirectVar = <Redirect to="/login" />
        // }
        // if (this.state.errorRedirect === true) {
        //     redrirectVar = <Redirect to="/error" />
        // }
        let redirectDiv = null;
        if(this.state.redirect === true){
            redirectDiv = <Redirect to="/host/properties"/>;
        }

        var totalCost = 0;

        if (this.state.propertyDetails.Baserate) {


            const startDate = this.state.arrivalDate;
            const timeEnd = this.state.departureDate;
            const diff = 1;
            const diffDuration = 1;
            totalCost = (diffDuration._data.days + 1) * this.state.propertyDetails.Baserate.substring(1);

        }

        let carousalBlock = this.state.photos.map(function (item, index) {

            return (
                <div className={index == 0 ? "carousel-item active" : "carousel-item"} key={index}>
                    <img className=" carousel-img property-display-img" src={item} alt="property-image"/>
                </div>
            )
        });

        let carousalIndicator = this.state.photos.map(function (item, index) {

            return (
                <li data-target="#myCarousel" data-slide-to={index} className={index == 0 ? "active" : ""}
                    key={index}></li>
            )
        });

        let availableDays = ""
        let adString = this.state.propertyDetails.availableDays
        let adArray = []
        if (adString && adString !== "") {
            if (adString.startsWith("M")) {
                adArray.push("Monday")
                adString = adString.substr(1)
            }
            if (adString.startsWith("TU")) {
                adArray.push("Tuesday")
                adString = adString.substr(2)
            }
            if (adString.startsWith("W")) {
                adArray.push("Wednesday")
                adString = adString.substr(1)
            }
            if (adString.startsWith("TH")) {
                adArray.push("Thursday")
                adString = adString.substr(2)
            }
            if (adString.startsWith("F")) {
                adArray.push("Friday")
                adString = adString.substr(1)
            }
            if (adString.startsWith("SA")) {
                adArray.push("Saturday")
                adString = adString.substr(2)
            }
            if (adString.startsWith("SU")) {
                adArray.push("Sunday")
                adString = adString.substr(2)
            }
        }


        let bathRooms = ""
        if(this.state.sharingType === 'Private Room') {
            bathRooms = <div className="form-group">
                <label htmlFor="privateBathroomAvailable"  className="col-sm-3">Private Bathroom avaiable?</label>
                <select className="form-control"  name="privateBathroomAvailable" id="privateBathroomAvailable" onChange={this.handleInputChange}>
                    <option value="" selected disabled hidden>-- Please Select --</option>
                    <option>Yes</option>
                    <option>No</option>
                </select>
            </div>
        }

        let privateBathShower = ""
        if(this.state.propertyDetails.hasPrivateBathroom === 'No') {
            privateBathShower =
                <tr>
                    <th scope="row">Private Bath/Shower</th>
                    <td>{this.state.propertyDetails.hasPrivateBathroom}</td>
                </tr>
        }

        let parkingFee = ""
        if(this.state.propertyDetails.parkingAvailability === 'Yes') {
            parkingFee =
                <tr>
                    <th scope="row">Daily Parking Fee</th>
                    <td> Free </td>
                    </tr>
            if(this.state.propertyDetails.dailyParkingFee !== 0) {
            parkingFee =
                <tr>
                    <th scope="row">Daily Parking Fee</th>
                    <td>$ {this.state.propertyDetails.dailyParkingFee.toFixed(2)}</td>
                </tr>
            }
        }

        let ownerName = ""
        if(this.state.propertyDetails.owner) {
            ownerName = this.state.propertyDetails.owner.name
        }

        let weekdayRentPrice = ""
        if(this.state.propertyDetails.weekdayPrice > 0) {
            weekdayRentPrice =
                <div><h6>Weekdays: <strong>${this.state.propertyDetails.weekdayPrice.toFixed(2)}</strong><span> per night</span></h6></div>
        }

        let weekendRentPrice = ""
        if(this.state.propertyDetails.weekendPrice > 0) {
            weekendRentPrice =
                <div><h6>Weekends: <strong>${this.state.propertyDetails.weekendPrice.toFixed(2)}</strong><span> per night</span></h6></div>
        }

        let parkingPrice = ""
        if(this.state.propertyDetails.dailyParkingFee && this.state.propertyDetails.dailyParkingFee !== 0) {
            parkingPrice =
              <div><h6>Parking: <strong>${this.state.propertyDetails.dailyParkingFee.toFixed(2)}</strong><span> per day</span></h6></div>
        }

        let reservationOrEditDiv = ""
        if(this.state.propertyDetails.owner)
            if(this.state.propertyDetails.owner.id.toString() === localStorage.id.toString()) {
            console.log("Owner view")
            let propertyEditLink = "/property/host/edit/" + this.props.match.params.id
                reservationOrEditDiv =
                <div>
                    <a href={propertyEditLink} className="btn btn-primary align-center mb-3"> Edit Details </a>
                    <br />
                    <button type="button" className="btn btn-danger align-center mb-3" onClick={this.handleDeleteProperty}>Delete Property</button>
                </div>
        } else {

            reservationOrEditDiv = <ReservationCreateButton propertyId={this.props.match.params.id} startDate={this.state.startDate} endDate={this.state.endDate} enabled={this.state.hasValidPaymentMethod}/>
        }

        let navigation = <GuestNavigation />
        if(localStorage.role == "host") {
            navigation = <HostNavigation />
        }

        return (
            <div>


            {redirectDiv}
            <div>
                {navigation}
                <div className=" container property-display-content border">
                    <div className="row">
                        <div className="details-content-headline-text col-12 "><br/><h4>
                            <strong>{this.state.propertyDetails.headline}</strong></h4>
                            <p>{this.state.propertyDetails.addressStreet}, {this.state.propertyDetails.addressCity} {this.state.propertyDetails.addressState} {this.state.propertyDetails.addressZipcode}</p>
                        </div>
                    </div>
                    <div className="row">
                        <div className="property-display-img-content col-12">
                            <div id="myCarousel" className="carousel slide" data-ride="carousel">


                                <ul className="carousel-indicators">
                                    {carousalIndicator}
                                </ul>

                                <div className="carousel-inner">
                                    {carousalBlock}
                                </div>

                                <a className="carousel-control-prev" href="#myCarousel" data-slide="prev">
                                    <span className="carousel-control-prev-icon"></span>
                                </a>
                                <a className="carousel-control-next" href="#myCarousel" data-slide="next">
                                    <span className="carousel-control-next-icon"></span>
                                </a>
                            </div>
                        </div>
                    </div>

                    <div className="row">
                        <div className="property-display-details-content col-6">
                            <div className="property-description-content">
                                <br/>
                                <br/>
                                <h3>
                                    <strong>{this.state.propertyDetails.numBedroom} bedroom &bull; {this.state.propertyDetails.sharingType}</strong>
                                </h3>
                                <br />
                                <h5>
                                    <p> {this.state.propertyDetails.description} </p>
                                </h5>
                                <div className="desc-content">
                                    {this.state.propertyDetails.Description}
                                </div>
                            </div>
                            <div className="property-details-description">
                                <br/>
                                <table className="table table-borderless">
                                    <tbody>
                                    <tr>
                                        <th scope="row">Property Type</th>
                                        <td>{this.state.propertyDetails.propertyType}</td>
                                    </tr>

                                    <tr>
                                        <th scope="row">Available days</th>
                                        <td>{adArray.join(", ")}</td>
                                    </tr>

                                    <tr>
                                        <th scope="row">Square Footage</th>
                                        <td>{this.state.propertyDetails.squareFootage} sqft</td>
                                    </tr>

                                    <tr>
                                        <th scope="row">Private Bathroom</th>
                                        <td>{this.state.propertyDetails.hasPrivateBathroom}</td>
                                    </tr>

                                    {privateBathShower}

                                    <tr>
                                        <th scope="row">Parking Availabile</th>
                                        <td>{this.state.propertyDetails.parkingAvailability}</td>
                                    </tr>

                                    {parkingFee}

                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div className="property-display-pricing-content col-4 border">
                            <div className="display-price">
                                {weekdayRentPrice}
                                {weekendRentPrice}
                                {parkingPrice}
                            </div>
                            <div>
                                {reservationOrEditDiv}
                                <hr/>
                                <div className="center-content">
                                    <label htmlFor="ownername">Property Owner: </label>
                                    <br />
                                    <span id="ownername"><strong> {ownerName}</strong></span>
                                </div>
                            </div>
                            <div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
            </div>
        )
    }
}

export default PropertyDisplay;
