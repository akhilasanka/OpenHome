import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import swal from 'sweetalert';
import 'react-dates/initialize';
import { DateRangePicker } from 'react-dates';
import 'react-dates/lib/css/_datepicker.css';
import { getReservation } from "../util/APIUtils";
import ReservationCheckInButton from "../Reservation/ReservationCheckInButton";
import ReservationCheckOutButton from "../Reservation/ReservationCheckOutButton";
import ReservationCancelButton from "../Reservation/ReservationCancelButton";
import HostNavigation from "../Navigation/HostNavigation";
import GuestNavigation from "../Navigation/GuestNavigation";
import '../Styles/HostProperty.css'

class ViewReservation extends Component {
    constructor(props) {
        super(props);
        this.state = {
          reservationId: this.props.match.params.id,
          propertyDetails: {},
          photos: [],
          errorRedirect: false,
          reservationDetails: {}
        }
    }

    componentDidMount() {
      var reservationId = this.state.reservationId;
      getReservation(reservationId)
      .then(response => {
          console.log(response);
          this.setState({
              propertyDetails: response.property,
              photos: JSON.parse(response.property.photosArrayJson),
              reservationDetails: response
          });

      }).catch(error => {
          this.setState({errorRedirect:true})
      });
    }

    render() {
        const isGuest = localStorage.getItem("role") === "guest";
        let navigation = isGuest? <GuestNavigation/> : <HostNavigation/>;
        if (this.state.errorRedirect) {
            return <Redirect to="/stats/reservations" />;
        }

        // PROPERTY DETAILS
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

        let ownerName = "";
        if(this.state.propertyDetails.owner) {
            ownerName = this.state.propertyDetails.owner.name
        }

        // RESERVATION DETAILS
        let totalPrice = "";
        if (this.state.reservationDetails.totalPrice) {
          totalPrice = '$' + this.state.reservationDetails.totalPrice.toFixed(2);
        }

        let startDate = "";
        if (this.state.reservationDetails.startDate) {
          startDate = new Date(this.state.reservationDetails.startDate).toDateString()
        }

        let endDate = "";
        if (this.state.reservationDetails.endDate) {
          endDate = new Date(this.state.reservationDetails.endDate).toDateString()
        }

        let reservationStatus = "";
        let statusEnum = this.state.reservationDetails.status;
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

        let cancelButton = "";
        if (statusEnum !== 'automaticallyCanceled' &&
            statusEnum !== 'guestCanceledBeforeCheckIn' &&
            statusEnum !== 'guestCanceledAfterCheckIn' &&
            statusEnum !== 'hostCanceledBeforeCheckIn' &&
            statusEnum !== 'hostCanceledAfterCheckIn' &&
            statusEnum !== 'checkedOut')
        {
            cancelButton = <ReservationCancelButton reservationId={this.state.reservationId}/>;
        }

        let checkOutButton = "";
        if (statusEnum == 'checkedIn' && isGuest) {
          checkOutButton = <ReservationCheckOutButton reservationId={this.state.reservationId}/>;
        }

        let checkInButton = "";
        if (statusEnum == 'pendingCheckIn' && isGuest) {
          checkInButton = <ReservationCheckInButton reservationId={this.state.reservationId}/>;
        }

        return (
            <div>
                {navigation}
                <div className="container property-display-content border">
                    <div className="content">
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
                                <div>
                                  <h3>Reservation Details</h3>
                                  <hr/>
                                </div>

                                <div className="col-12 col-form-label">
                                    Dates:
                                </div>
                                <div className="col-12">
                                    <div id="dates">{startDate} - {endDate}</div>
                                </div>

                                <div className="col-12 col-form-label">
                                    Status:
                                </div>
                                <div className="col-12">
                                    <div id="status">{reservationStatus}</div>
                                </div>

                                <div className="col-12 col-form-label">
                                    Total Price:
                                </div>
                                <div className="col-12">
                                    <div id="price">{totalPrice}</div>
                                </div>

                                <div>
                                    <hr/>
                                    <div className="row">
                                      {cancelButton}
                                      {checkInButton}
                                      {checkOutButton}
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default ViewReservation
