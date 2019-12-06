import React, { Component } from 'react'
import '../Styles/HostProperty.css'
import { Redirect } from 'react-router';
import { ACCESS_TOKEN, API_BASE_URL } from "../constants";
import axios from "axios";
import Alert from "react-s-alert";
import swal from 'sweetalert';
import HostNavigation from '../Navigation/HostNavigation';

class EditProperty extends Component {

    constructor(props) {
        super(props);

        this.state = {
            locationActive: true,
            detailsActive: false,
            photosActive: false,
            pricingActive: false,
            propertyContact: null,
            streetAddress: "",
            city: "",
            state: "",
            zipCode: "",
            headline: "",
            description: "",
            sharingType: "",
            propertyType: "",
            bedrooms: "",
            sqft: "",
            privateBathShowerAvailable: "Yes",
            privateBathroomAvailable: "Yes",
            photos: [],
            freeWifi: "",
            parkingAvailable: "",
            parkingFree: "Yes",
            parkingCost: "",
            alwaysAvailable: "",
            weeklyAvailability: [],
            weekdayRentPrice: "",
            weekendRentPrice: "",
            weekdays: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
            weekends: ['Saturday', 'Sunday'],
            monSelected: false,
            tueSelected: false,
            wedSelected: false,
            thuSelected: false,
            friSelected: false,
            satSelected: false,
            sunSelected: false,
            locationError: false,
            detailsError: false,
            photosError: false,
            pricingError: false,
            propertyInsertComplete: false,
            errorRedirect: false
        };

        //bind
        this.handleInputChange = this.handleInputChange.bind(this);
        this.submitPropertyDetails = this.submitPropertyDetails.bind(this);
    }


    async componentDidMount() {
        var result = null;
        var propertyID = this.props.match.params.propertyID;
        console.log(propertyID);
        var token = localStorage.getItem("accessToken");
        await axios({
            method: 'get',
            url: API_BASE_URL + '/property/' + propertyID,
            config: { headers: { 'Content-Type': 'multipart/form-data' } },
            headers: { "Authorization": `Bearer ${token}` }
        })
            .then((response) => {
                if (response.status >= 500) {
                    throw new Error("Bad response from server");
                }
                return response;
            })
            .then((responseData) => {
                result = responseData.data;
                console.log(result);

                //for calculating availability days
                let adString = result.availableDays;
                var count = 0;
                let monSelected = false;
                let tueSelected = false
                let wedSelected = false;
                let thuSelected = false;
                let friSelected = false;
                let satSelected = false;
                let sunSelected = false;
                if (adString && adString !== "") {
                    if (adString.includes("M")) {
                        console.log("Monday");
                        monSelected = true;
                        count = count+1;
                    }
                    console.log(adString);
                    if (adString.includes("TU")) {
                        console.log("Tuesday");
                        tueSelected = true;
                        count = count+1;
                    }
                    if (adString.includes("W")) {
                        console.log("Wednesday");
                        wedSelected = true;
                        count = count+1;
                    }
                    if (adString.includes("TH")) {
                        console.log("Thursday");
                        thuSelected = true;
                        count = count +1;
                    }
                    if (adString.includes("F")) {
                        console.log("Friday");
                        friSelected = true;
                        count = count +1;
                    }
                    if (adString.includes("SA")) {
                        console.log("Saturday");
                        satSelected = true;
                        count = count +1;
                    }
                    if (adString.includes("SU")) {
                        console.log("Sunday");
                        sunSelected = true;
                        count = count + 1;
                    }
                }

                this.setState({
                    propertyID: propertyID,
                    locationActive: true,
                    detailsActive: false,
                    photosActive: false,
                    pricingActive: false,
                    propertyContact: result.phoneNumber,
                    streetAddress: result.addressStreet,
                    city: result.addressCity,
                    state: result.addressState,
                    zipCode: result.addressZipcode,
                    headline: result.headline,
                    description: result.description,
                    sharingType: result.sharingType,
                    propertyType: result.propertyType,
                    bedrooms: result.numBedroom,
                    sqft: result.squareFootage,
                    privateBathShowerAvailable: result.hasPrivateShower,
                    privateBathroomAvailable: result.hasPrivateBathroom,
                    photos: JSON.parse(result.photosArrayJson),
                    freeWifi: result.wifiAvailability,
                    parkingAvailable: result.parkingAvailability,
                    parkingFree: result.dailyParkingFee == 0 ? "Yes" : "No",
                    parkingCost: result.dailyParkingFee,
                    alwaysAvailable: count == 7 ? "Yes" : "No",
                    weeklyAvailability: [],
                    weekdayRentPrice: result.weekdayPrice,
                    weekendRentPrice: result.weekendPrice,
                    weekdays: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
                    weekends: ['Saturday', 'Sunday'],
                    locationError: false,
                    detailsError: false,
                    photosError: false,
                    pricingError: false,
                    propertyInsertComplete: false,
                    errorRedirect: false,
                    monSelected: monSelected,
                    tueSelected: tueSelected,
                    wedSelected: wedSelected,
                    thuSelected: thuSelected,
                    friSelected: friSelected,
                    satSelected: satSelected,
                    sunSelected: sunSelected,
                });
                console.log(count);
            }).catch(function (err) {
                console.log(err)
            });
    }

    handleLocationClick = () => {

        this.setState({
            locationActive: true,
            detailsActive: false,
            photosActive: false,
            pricingActive: false
        });
    };

    handleDetailsClick = () => {

        const validator = this.state.propertyContact === "" || this.state.streetAddress === "" || this.state.city === "" || this.state.state === "" || this.state.zipCode === "";

        console.log(validator);
        if (validator) {

            this.setState({
                locationError: true
            })

        }
        else {
            this.setState({
                locationActive: false,
                detailsActive: true,
                photosActive: false,
                pricingActive: false,
                locationError: false

            });
        }
    };

    handlePhotosClick = () => {

        const validator = this.state.headline === ""
            || this.state.description === ""
            || this.state.sharingType === ""
            || this.state.propertyType === ""
            || this.state.bedrooms === ""
            || this.state.sqft === ""
            || (this.state.sharingType === "Private Room" && this.state.privateBathroomAvailable === "")
            || (this.state.privateBathShowerAvailable === "" && this.state.privateBathroomAvailable === "No")
            || this.state.parkingAvailable === ""
            || this.state.freeWifi === "";


        if (validator) {
            this.setState({
                detailsError: true
            })
        }
        else {
            this.setState({
                locationActive: false,
                detailsActive: false,
                photosActive: true,
                pricingActive: false,
                detailsError: false
            });
        }


    };

    handlePricingClick = () => {
        const validator = this.state.photos.filter(Boolean).length == 0;

        if (validator) {
            this.setState({
                photosError: true
            })

        }
        else {

            this.setState({
                locationActive: false,
                detailsActive: false,
                photosActive: false,
                pricingActive: true,
                photosError: false

            });
        }

    };

    handleInputChange(event) {
        const target = event.target;
        const name = target.name;
        var value = target.value;

        if (name.startsWith('photo')) {
            var index = parseInt(name.substr(name.length - 1)) - 1;
            var photoArr = this.state.photos;
            if (value === "")
                photoArr[index] = undefined;
            else
                photoArr[index] = value;

            this.setState({
                photos: photoArr
            }, console.log(this.state.photos));
        }

        if (name === 'weeklyAvailability') {
            var options = target.options;
            value = [];
            for (var i = 0, l = options.length; i < l; i++) {
                if (options[i].selected) {
                    value.push(options[i].value);
                }
            }
        }

        console.log("Name: " + name + " ; Value: " + value);
        this.setState({
            [name]: value
        });
    }

    submitPropertyDetails = (e) => {

        var validator = this.state.alwaysAvailable === ""
            || (this.state.alwaysAvailable === 'No' && this.state.weeklyAvailability.length == 0)
            || (this.state.alwaysAvailable === 'Yes' && (this.state.weekdayRentPrice === "" || this.state.weekendRentPrice === ""))
            || (this.state.weeklyAvailability.some(item => this.state.weekdays.includes(item)) && this.state.weekdayRentPrice === "")
            || (this.state.weeklyAvailability.some(item => this.state.weekends.includes(item)) && this.state.weekendRentPrice === "")


        if (validator) {
            this.setState({
                pricingError: true
            })
        }
        else {
            this.setState({
                pricingError: false
            })
            e.preventDefault();

            const data = {
                isDeleted: false,
                propertyContact: this.state.propertyContact,
                streetAddress: this.state.streetAddress,
                city: this.state.city,
                state: this.state.state,
                zipCode: this.state.zipCode,
                headline: this.state.headline,
                description: this.state.description,
                sharingType: this.state.sharingType,
                propertyType: this.state.propertyType,
                bedrooms: this.state.bedrooms,
                sqft: this.state.sqft,
                privateBathShowerAvailable: this.state.privateBathShowerAvailable,
                privateBathroomAvailable: this.state.privateBathroomAvailable,
                freeWifi: this.state.freeWifi,
                parkingAvailable: this.state.parkingAvailable,
                parkingFree: this.state.parkingFree,
                parkingCost: this.state.parkingCost,
                photos: this.state.photos,
                alwaysAvailable: this.state.alwaysAvailable,
                weeklyAvailability: this.state.weeklyAvailability,
                weekdayRentPrice: this.state.weekdayRentPrice,
                weekendRentPrice: this.state.weekendRentPrice,
            };

            console.log(data);
            var propertyID = this.state.propertyID;
            axios(
                {
                    method: 'post',
                    url: API_BASE_URL + '/hosts/' + localStorage.id + '/property/' + this.state.propertyID + '/edit',
                    params: { "isPenalityApproved": false },
                    data: data,
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
                                    url: API_BASE_URL + '/hosts/' + localStorage.id + '/property/' + propertyID + '/edit',
                                    params: { "isPenalityApproved": true },
                                    data: data,
                                    headers: { "Authorization": "Bearer " + localStorage.getItem(ACCESS_TOKEN) }
                                }
                            ).then((response) => {
                               if(response.data.status=="EditSuccessful"){
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
                    swal("Sucessfully edited property!");
                }
                // this.props.history.push("/home");
            })
                .catch(Alert.error("Failed to update property. Please try again."))
        }
    };

    render() {
        console.log(this.state);

        let redirectVar = null;
        if (!localStorage.getItem(ACCESS_TOKEN)) {
            redirectVar = <Redirect to="/login" />
        }

        if (this.state.errorRedirect === true) {
            redirectVar = <Redirect to="/error" />
        }

        if (this.state.propertyInsertComplete) {
            redirectVar = <Redirect to="/" />
        }

        let locationErrorPane = null;

        if (this.state.locationError) {
            locationErrorPane = <div>
                <div className="alert alert-danger" role="alert">
                    <strong>Error!</strong> All fields are required!
                </div>
            </div>
        }

        let detailsErrorPane = null;

        if (this.state.detailsError) {
            detailsErrorPane = <div>
                <div className="alert alert-danger" role="alert">
                    <strong>Error!</strong> All fields are required!
                </div>
            </div>
        }

        let photosErrorPane = null;

        if (this.state.photosError) {
            photosErrorPane = <div>
                <div className="alert alert-danger" role="alert">
                    <strong>Error!</strong> Atleast 1 image is required!
                </div>
            </div>
        }

        let pricingErrorPane = null;

        if (this.state.pricingError) {
            pricingErrorPane = <div>
                <div className="alert alert-danger" role="alert">
                    <strong>Error!</strong> All fields are required!
                </div>
            </div>
        }

        let photos = this.state.photos.map(function (thumbnail, index) {
            if (thumbnail !== undefined && thumbnail !== '')
                return (
                    <img src={thumbnail} className="img-thumbnail" alt="thumbnail" width="304" height="236" key={index}></img>
                )
        });
        console.log('PhotoThumbnail inside return: ', this.state.photos);

        let bathRooms = ""
        if (this.state.sharingType === 'Private Room') {
            bathRooms = <div className="form-group">
                <label htmlFor="privateBathroomAvailable" className="col-sm-3">Private Bathroom avaiable?</label>
                <select className="form-control" name="privateBathroomAvailable" id="privateBathroomAvailable" onChange={this.handleInputChange} value={this.state.privateBathroomAvailable}>
                    <option value="" selected disabled hidden>-- Please Select --</option>
                    <option>Yes</option>
                    <option>No</option>
                </select>
            </div>
        }

        let privateBathShower = ""
        if (this.state.privateBathroomAvailable === 'No') {
            privateBathShower = <div className="form-group">
                <label htmlFor="privateBathShowerAvailable" className="col-sm-3">Private Bath/Shower avaiable?</label>
                <select className="form-control" name="privateBathShowerAvailable" id="privateBathShowerAvailable"
                    onChange={this.handleInputChange} value={this.state.privateBathShowerAvailable}>
                    <option value="" disabled hidden>-- Please Select --</option>
                    <option>Yes</option>
                    <option>No</option>
                </select>
            </div>;
        }

        let parkingAvailable =
            <div className="form-group">
                <label htmlFor="parkingAvailable" className="col-sm-3">Parking Available?</label>
                <select className="form-control" name="parkingAvailable" id="parkingAvailable" onChange={this.handleInputChange} value={this.state.parkingAvailable}>
                    <option value="" selected disabled hidden>-- Please Select --</option>
                    <option>Yes</option>
                    <option>No</option>
                </select>
            </div>;

        let parkingFree = "";
        if (this.state.parkingAvailable === 'Yes') {
            parkingFree = <div className="form-group">
                <label htmlFor="parkingFree" className="col-sm-3">Free Parking?</label>
                <select className="form-control" name="parkingFree" id="parkingFree" onChange={this.handleInputChange} value={this.state.parkingFree}>
                    <option value="" selected disabled hidden>-- Please Select --</option>
                    <option>Yes</option>
                    <option>No</option>
                </select>
            </div>
        }

        let parkingCost = "";
        if (this.state.parkingFree === 'No') {
            parkingCost = <div className="form-group">
                <label htmlFor="parkingCost" className="col-sm-3">Daily Parking Fee</label>
                <input type="number" name="parkingCost" id="parkingCost" className="form-control form-control-lg" placeholder="in USD" onChange={this.handleInputChange} value={this.state.parkingCost} />
            </div>
        }

        let weeklyAvailability = "";
        if (this.state.alwaysAvailable === 'No') {
            weeklyAvailability = <div className="form-group">
                <label htmlFor="weeklyAvailability">Select the days of the week that your property is available</label>
                <select multiple className="form-control" name="weeklyAvailability" id="weeklyAvailability" size="7" onChange={this.handleInputChange}>
                    {this.state.monSelected ?
                        <option selected>Monday</option>
                        :
                        <option>Monday</option>
                    }
                    {this.state.tueSelected ?
                        <option selected>Tuesday</option>
                        :
                        <option>Tuesday</option>
                    }
                    {this.state.wedSelected ?
                        <option selected>Wednesday</option>
                        :
                        <option>Wednesday</option>
                    }
                    {this.state.thuSelected ?
                        <option selected>Thursday</option>
                        :
                        <option>Thursday</option>
                    }
                    {this.state.friSelected ?
                        <option selected>Friday</option>
                        :
                        <option>Friday</option>
                    }
                    {this.state.satSelected ?
                        <option selected>Saturday</option>
                        :
                        <option>Saturday</option>
                    }
                    {this.state.sunSelected ?
                        <option selected>Sunday</option>
                        :
                        <option>Sunday</option>
                    }
                </select>
            </div>
        }

        let weekdayRentPrice = "";
        //if (this.state.alwaysAvailable === 'Yes'
           // || this.state.weeklyAvailability.some(item => this.state.weekdays.includes(item))) {
            weekdayRentPrice = <div className="form-group">
                <label htmlFor="alwaysAvailable" className="col-sm-3">Weekday Rent Price</label>
                <input type="number" name="weekdayRentPrice" id="weekdayRentPrice" className="form-control form-control-lg" onChange={this.handleInputChange} defaultValue={this.state.weekdayRentPrice} />
            </div>
        //}

        let weekendRentPrice = "";
        //if (this.state.alwaysAvailable === 'Yes'
          //  || this.state.weeklyAvailability.some(item => this.state.weekends.includes(item))) {
            weekendRentPrice = <div className="form-group">
                <label htmlFor="alwaysAvailable" className="col-sm-3">Weekend Rent Price</label>
                <input type="number" name="weekendRentPrice" id="weekendRentPrice" className="form-control form-control-lg" onChange={this.handleInputChange} defaultValue={this.state.weekendRentPrice} />
            </div>
        //}

        return (
            <div>
             <HostNavigation/>
                <div className="add-property-content">
                    {redirectVar}
                    <div className="container">
                        <hr />
                        <div className="row">
                            <div className="menu-bar-ver col-3">
                                <ul>
                                    <li>Welcome</li>
                                    <li> <a href="#" onClick={this.handleLocationClick}>Location</a></li>
                                    <li><a href="#" onClick={this.handleDetailsClick}>Details</a></li>
                                    <li><a href="#" onClick={this.handlePhotosClick}>Photos</a></li>
                                    <li><a href="#" onClick={this.handlePricingClick}>Pricing</a></li>
                                </ul>
                            </div>
                            <div className="menu-bar-hor border col-8">
                                <div className="add-property-form">
                                    <div className={this.state.locationActive ? "location-form show-form" : "location-form"}>
                                        <div className="location-form-headlinetext">
                                            <h4>Location Details</h4>
                                        </div>
                                        <hr />
                                        <div>
                                            {locationErrorPane}
                                        </div>
                                        <div className="details-form-description pad-bot-10">
                                            <p>Fill in the location details of your property</p>
                                        </div>
                                        <div className="form-group">
                                            <input type="text" name="streetAddress" id="streetAddress" className="form-control form-control-lg" placeholder="Street Address" onChange={this.handleInputChange} defaultValue={this.state.streetAddress} />
                                        </div>
                                        <div className="form-group">
                                            <input type="text" name="city" id="city" className="form-control form-control-lg" placeholder="City" onChange={this.handleInputChange} defaultValue={this.state.city} />
                                        </div>
                                        <div className="form-group">
                                            <input type="text" name="state" id="state" className="form-control form-control-lg" placeholder="State" onChange={this.handleInputChange} defaultValue={this.state.state} />
                                        </div>
                                        <div className="form-group">
                                            <input type="text" name="zipCode" id="zipCode" className="form-control form-control-lg" placeholder="Zip Code" onChange={this.handleInputChange} defaultValue={this.state.zipCode} />
                                        </div>
                                        <div className="form-group">
                                            <input type="number" name="propertyContact" id="propertyContact" className="form-control form-control-lg" placeholder="Property Contact" onChange={this.handleInputChange} defaultValue={this.state.propertyContact} />
                                        </div>
                                        <div className="form-group location-form-btn flt-right">
                                            <button className="btn btn-primary btn-lg" onClick={this.handleDetailsClick}>Next</button>
                                        </div>
                                    </div>

                                    <div className={this.state.detailsActive ? "details-form show-form" : "details-form"}>
                                        <div className="details-form-headlinetext">
                                            <h4>Describe your property</h4>
                                        </div>
                                        <hr />
                                        <div>
                                            {detailsErrorPane}
                                        </div>
                                        <div className="details-form-description pad-bot-10">
                                            <p>Start out with a descriptive headline and a detailed summary of your property</p>
                                        </div>

                                        <div className="form-group">
                                            <label htmlFor="headline" className="col-sm-3">Headline</label>
                                            <input type="text" name="headline" id="headline" className="form-control form-control-lg" placeholder="Please give a descriptive headline" onChange={this.handleInputChange} defaultValue={this.state.headline} />
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="description" className="col-sm-3">Description</label>
                                            <textarea type="text" name="description" id="description" className="form-control form-control-lg" placeholder="Please give a detailed description" onChange={this.handleInputChange} defaultValue={this.state.description} />
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="propertyType" className="col-sm-3">Property Type</label>
                                            <select className="form-control" name="propertyType" id="propertyTypeSelect" onChange={this.handleInputChange} value={this.state.propertyType}>
                                                <option value="" selected disabled hidden>-- Please Select --</option>
                                                <option>House</option>
                                                <option>Townhouse</option>
                                                <option>Condo/Apartment</option>
                                            </select>
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="bedrooms" className="col-sm-3">Bedrooms</label>
                                            <select className="form-control" name="bedrooms" id="bedrooms" onChange={this.handleInputChange} value={this.state.bedrooms}>
                                                <option value="" selected disabled hidden>-- Please Select --</option>
                                                <option value={"1"}>1</option>
                                                <option value={"2"}>2</option>
                                                <option value={"3"}>3</option>
                                                <option value={"4"}>4+</option>
                                            </select>
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="sharingType" className="col-sm-3">Sharing Type</label>
                                            <select className="form-control" name="sharingType" id="sharingType" onChange={this.handleInputChange} value={this.state.sharingType}>
                                                <option value="" selected disabled hidden>-- Please Select --</option>
                                                <option>Private Room</option>
                                                <option>Entire Place</option>
                                            </select>
                                        </div>
                                        {bathRooms}
                                        {privateBathShower}
                                        <div className="form-group">
                                            <label htmlFor="sqft" className="col-sm-3">Square Footage</label>
                                            <input type="number" name="sqft" id="sqft" className="form-control form-control-lg" placeholder="in sq.ft." onChange={this.handleInputChange} defaultValue={this.state.sqft} />
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="freeWifi" className="col-sm-3">Free Wifi?</label>
                                            <select className="form-control" name="freeWifi" id="freeWifi" onChange={this.handleInputChange} value={this.state.freeWifi}>
                                                <option value="" selected disabled hidden>-- Please Select --</option>
                                                <option>Yes</option>
                                                <option>No</option>
                                            </select>
                                        </div>
                                        {parkingAvailable}
                                        {parkingFree}
                                        {parkingCost}
                                        <div className="form-group details-form-btn flt-right">
                                            <button className="btn btn-primary btn-lg" onClick={this.handlePhotosClick}>Next</button>
                                        </div>
                                    </div>

                                    <div className={this.state.photosActive ? "photos-form show-form" : "photos-form"}>
                                        <div>
                                            {photosErrorPane}
                                        </div>
                                        <div className="photos-form-headlinetext">
                                            <h4>Add up to 5 photos of your property</h4>
                                        </div>
                                        <hr />
                                        <div className="photos-form-description pad-bot-10">
                                            <p>Showcase your property’s best features (no pets or people, please). Atleast 1 photo required.</p>
                                        </div>
                                        <div className="container photo-upload-btn-container">
                                            <div className="center-content">
                                                <input type="text" name="photo1" id="photo1" className="form-control form-control-lg" placeholder="Please paste a img URL here" onChange={this.handleInputChange} defaultValue={this.state.photos[0] != null ? this.state.photos[0] : ''} />
                                                <input type="text" name="photo2" id="photo2" className="form-control form-control-lg" placeholder="Please paste a img URL here" onChange={this.handleInputChange} defaultValue={this.state.photos[1] != null ? this.state.photos[1] : ''} />
                                                <input type="text" name="photo3" id="photo3" className="form-control form-control-lg" placeholder="Please paste a img URL here" onChange={this.handleInputChange} defaultValue={this.state.photos[2] != null ? this.state.photos[2] : ''} />
                                                <input type="text" name="photo4" id="photo4" className="form-control form-control-lg" placeholder="Please paste a img URL here" onChange={this.handleInputChange} defaultValue={this.state.photos[3] != null ? this.state.photos[3] : ''} />
                                                <input type="text" name="photo5" id="photo5" className="form-control form-control-lg" placeholder="Please paste a img URL here" onChange={this.handleInputChange} defaultValue={this.state.photos[4] != null ? this.state.photos[4] : ''} />

                                            </div>
                                        </div>
                                        <div className="pad-top-10 pad-bot-10">
                                            {photos}
                                        </div>
                                        <div className="form-group flt-right">
                                            <button className="btn photos-form-btn btn-primary btn-lg" onClick={this.handlePricingClick}>Next</button>
                                        </div>
                                    </div>

                                    <div className={this.state.pricingActive ? "pricing-form show-form" : "pricing-form"}>
                                        <div className="pricing-form-headlinetext">
                                            <h4>How much do you want to charge?</h4>
                                        </div>
                                        <hr />
                                        {pricingErrorPane}
                                        <div className="pricing-form-description pad-bot-10">
                                            <p>We recommend starting with a low price to get a few bookings and earn some initial guest reviews. You can update your rates at any time.</p>
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="alwaysAvailable">Is your property available on all days of the week?</label>
                                            <select className="form-control" name="alwaysAvailable" id="alwaysAvailable" onChange={this.handleInputChange} value={this.state.alwaysAvailable}>
                                                <option value="" selected disabled hidden>-- Please Select --</option>
                                                <option>Yes</option>
                                                <option>No</option>
                                            </select>
                                        </div>

                                        {weeklyAvailability}
                                        {weekdayRentPrice}
                                        {weekendRentPrice}

                                        <div className="form-group flt-right">
                                            <button className="btn btn-primary btn-lg" onClick={this.submitPropertyDetails}>Update</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default EditProperty
