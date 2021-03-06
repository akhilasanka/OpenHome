import React, { Component } from 'react'
import '../Styles/HostProperty.css'
import { Redirect } from 'react-router';
import {ACCESS_TOKEN, API_BASE_URL} from "../constants";
import axios from "axios";
import Alert from "react-s-alert";
import HostNavigation from "../Navigation/HostNavigation";

class HostProperty extends Component {

    constructor(props) {
        super(props);

        this.state = {
            locationActive: true,
            detailsActive: false,
            photosActive: false,
            pricingActive: false,
            propertyContact: "",
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

        if(name.startsWith('photo')) {
            var index = parseInt(name.substr(name.length - 1))-1;
            var photoArr = this.state.photos;
            if(value === "")
                photoArr[index] = undefined;
            else
                photoArr[index] = value;

            this.setState( {
                photos : photoArr
            }, console.log(this.state.photos));
        }

        if(name === 'weeklyAvailability') {
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

            axios(
                {
                    method:'post',
                    url:API_BASE_URL + '/api/hosts/'+ localStorage.id + '/property',
                    data:data,
                    headers: {"Authorization" : "Bearer "+localStorage.getItem(ACCESS_TOKEN)}
                }
            ).then((response)=>{
                console.log(response)
                Alert.success("Hosted property!");
                this.props.history.push("/home");
            })
                .catch(Alert.error("Failed to add property"))

        }
    };

    render() {

        if(localStorage.verified && localStorage.verified === "false") {
            return(
                <div>
                    <HostNavigation />
                    <div className="container-fluid">
                        <br/>
                        <br/>
                        Please verify your email id. If you have already verified. Logout and login back again to list your property!
                    </div>
                </div>
            )
        }

        let redirectVar = null;
        if (!localStorage.getItem(ACCESS_TOKEN)) {
            redirectVar = <Redirect to="/login" />
        }

        if (this.state.errorRedirect === true) {
            redirectVar = <Redirect to="/error" />
        }

        if(this.state.propertyInsertComplete){
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
            if(thumbnail !== undefined && thumbnail !== '')
            return (
                <img src={thumbnail} className="img-thumbnail" alt="thumbnail" width="304" height="236" key={index}></img>
            )
        });
        console.log('PhotoThumbnail inside return: ', this.state.photos);

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
        if(this.state.privateBathroomAvailable === 'No') {
            privateBathShower = <div className="form-group">
                <label htmlFor="privateBathShowerAvailable" className="col-sm-3">Private Bath/Shower avaiable?</label>
                <select className="form-control" name="privateBathShowerAvailable" id="privateBathShowerAvailable"
                        onChange={this.handleInputChange}>
                    <option value="" selected disabled hidden>-- Please Select --</option>
                    <option>Yes</option>
                    <option>No</option>
                </select>
            </div>;
        }

        let parkingAvailable =
            <div className="form-group">
                <label htmlFor="parkingAvailable"  className="col-sm-3">Parking Available?</label>
                <select className="form-control"  name="parkingAvailable" id="parkingAvailable" onChange={this.handleInputChange}>
                    <option value="" selected disabled hidden>-- Please Select --</option>
                    <option>Yes</option>
                    <option>No</option>
                </select>
            </div>;

        let parkingFree = "";
        if(this.state.parkingAvailable === 'Yes') {
            parkingFree = <div className="form-group">
                <label htmlFor="parkingFree"  className="col-sm-3">Free Parking?</label>
                <select className="form-control"  name="parkingFree" id="parkingFree" onChange={this.handleInputChange}>
                    <option value="" selected disabled hidden>-- Please Select --</option>
                    <option>Yes</option>
                    <option>No</option>
                </select>
            </div>
        }

        let parkingCost = "";
        if(this.state.parkingFree === 'No') {
            parkingCost = <div className="form-group">
                <label htmlFor="parkingCost"  className="col-sm-3">Daily Parking Fee</label>
                <input type="number" name="parkingCost" id="parkingCost" className="form-control form-control-lg" placeholder="in USD" onChange={this.handleInputChange} />
            </div>
        }

        let weeklyAvailability = "";
        if(this.state.alwaysAvailable === 'No') {
            weeklyAvailability = <div className="form-group">
                <label htmlFor="weeklyAvailability">Select the days of the week that your property is available</label>
                <select multiple className="form-control"  name="weeklyAvailability" id="weeklyAvailability" size="7" onChange={this.handleInputChange}>
                    <option>Monday</option>
                    <option>Tuesday</option>
                    <option>Wednesday</option>
                    <option>Thursday</option>
                    <option>Friday</option>
                    <option>Saturday</option>
                    <option>Sunday</option>
                </select>
            </div>
        }

        let weekdayRentPrice = "";
        if(this.state.alwaysAvailable === 'Yes'
        || this.state.weeklyAvailability.some(item => this.state.weekdays.includes(item)) ) {
            weekdayRentPrice = <div className="form-group">
                <label htmlFor="alwaysAvailable" className="col-sm-3">Weekday Rent Price</label>
                <input type="number" name="weekdayRentPrice" id="weekdayRentPrice" className="form-control form-control-lg" onChange={this.handleInputChange} />
            </div>
        }

        let weekendRentPrice = "";
        if(this.state.alwaysAvailable === 'Yes'
        || this.state.weeklyAvailability.some(item => this.state.weekends.includes(item)) ) {
            weekendRentPrice = <div className="form-group">
                <label htmlFor="alwaysAvailable" className="col-sm-3">Weekend Rent Price</label>
                <input type="number" name="weekendRentPrice" id="weekendRentPrice" className="form-control form-control-lg" onChange={this.handleInputChange} />
            </div>
        }

        return (
            <div>
                <div className="add-property-content">
                    {redirectVar}
                    <HostNavigation />
                    <div className="container">
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
                            <div className="menu-bar-hor col-8">
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
                                            <input type="text" name="streetAddress" id="streetAddress" className="form-control form-control-lg" placeholder="Street Address" onChange={this.handleInputChange} />
                                        </div>
                                        <div className="form-group">
                                            <input type="text" name="city" id="city" className="form-control form-control-lg" placeholder="City" onChange={this.handleInputChange} />
                                        </div>
                                        <div className="form-group">
                                            <input type="text" name="state" id="state" className="form-control form-control-lg" placeholder="State" onChange={this.handleInputChange} />
                                        </div>
                                        <div className="form-group">
                                            <input type="text" name="zipCode" id="zipCode" className="form-control form-control-lg" placeholder="Zip Code" onChange={this.handleInputChange} />
                                        </div>
                                        <div className="form-group">
                                            <input type="number" name="propertyContact" id="propertyContact" className="form-control form-control-lg" placeholder="Property Contact" onChange={this.handleInputChange} />
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
                                            <input type="text" name="headline" id="headline" className="form-control form-control-lg" placeholder="Please give a descriptive headline" onChange={this.handleInputChange} />
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="description"  className="col-sm-3">Description</label>
                                            <textarea type="text" name="description" id="description" className="form-control form-control-lg" placeholder="Please give a detailed description" onChange={this.handleInputChange} />
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="propertyType"  className="col-sm-3">Property Type</label>
                                            <select className="form-control"  name="propertyType" id="propertyTypeSelect" onChange={this.handleInputChange}>
                                                <option value="" selected disabled hidden>-- Please Select --</option>
                                                <option>House</option>
                                                <option>Townhouse</option>
                                                <option>Condo/Apartment</option>
                                            </select>
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="bedrooms"  className="col-sm-3">Bedrooms</label>
                                            <select className="form-control"  name="bedrooms" id="bedrooms" onChange={this.handleInputChange}>
                                                <option value="" selected disabled hidden>-- Please Select --</option>
                                                <option value={"1"}>1</option>
                                                <option value={"2"}>2</option>
                                                <option value={"3"}>3</option>
                                                <option value={"4"}>4+</option>
                                            </select>
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="sharingType"  className="col-sm-3">Sharing Type</label>
                                            <select className="form-control"  name="sharingType" id="sharingType" onChange={this.handleInputChange}>
                                                <option value="" selected disabled hidden>-- Please Select --</option>
                                                <option>Private Room</option>
                                                <option>Entire Place</option>
                                            </select>
                                        </div>
                                        {bathRooms}
                                        {privateBathShower}
                                        <div className="form-group">
                                            <label htmlFor="sqft"  className="col-sm-3">Square Footage</label>
                                            <input type="number" name="sqft" id="sqft" className="form-control form-control-lg" placeholder="in sq.ft." onChange={this.handleInputChange} />
                                        </div>
                                        <div className="form-group">
                                            <label htmlFor="freeWifi"  className="col-sm-3">Free Wifi?</label>
                                            <select className="form-control"  name="freeWifi" id="freeWifi" onChange={this.handleInputChange}>
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
                                                <input type="text" name="photo1" id="photo1" className="form-control form-control-lg" placeholder="Please paste a img URL here" onChange={this.handleInputChange} />
                                                <input type="text" name="photo2" id="photo2" className="form-control form-control-lg" placeholder="Please paste a img URL here" onChange={this.handleInputChange} />
                                                <input type="text" name="photo3" id="photo3" className="form-control form-control-lg" placeholder="Please paste a img URL here" onChange={this.handleInputChange} />
                                                <input type="text" name="photo4" id="photo4" className="form-control form-control-lg" placeholder="Please paste a img URL here" onChange={this.handleInputChange} />
                                                <input type="text" name="photo5" id="photo5" className="form-control form-control-lg" placeholder="Please paste a img URL here" onChange={this.handleInputChange} />

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
                                            <select className="form-control"  name="alwaysAvailable" id="alwaysAvailable" onChange={this.handleInputChange}>
                                                <option value="" selected disabled hidden>-- Please Select --</option>
                                                <option>Yes</option>
                                                <option>No</option>
                                            </select>
                                        </div>

                                        {weeklyAvailability}
                                        {weekdayRentPrice}
                                        {weekendRentPrice}

                                        <div className="form-group flt-right">
                                            <button className="btn btn-primary btn-lg" onClick={this.submitPropertyDetails}>Submit</button>
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

export default HostProperty
