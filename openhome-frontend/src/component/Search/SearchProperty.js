import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import swal from 'sweetalert';
import '../Styles/Search.css';

class SearchProperty extends Component {
    constructor(props) {
        super(props);
        this.state = {
        }
    }

    componentWillMount() {
    }

    handleSearch = async (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);
        let validInput = true;
        var token = localStorage.getItem("accessToken");
        if (formData.get("city") == '' && formData.get("zipcode") == '') {
            swal("Oops!", "City or Zipcode is required", "error");
            validInput = false;
        }
        else {
            var to = formData.get("to");
            var from = formData.get("from");
            if (to < from) {
                swal("Oops!", "From Date must be before To.", "error");
                validInput = false;
            }
            else {
                var priceMin = formData.get("priceFrom");
                var priceMax = formData.get("priceTo");
                if (priceMax != '' && priceMin != '' && priceMin > priceMax) {
                    swal("Oops!", "Please make sure Max price is greater Min price.", "error");
                    validInput = false;
                }
            }
        }


    }
    render() {

        return (
            <div>
                <div className='rowC backgroundImg' style={{ display: "flex", flexDirection: "row" }}>
                    <div className="card card-css">
                        <div className="container">
                            <div className="row justify-content-center align-items-center" >


                                <div className="col-12">
                                    <div className="border-bottom row" style={{ marginBottom: "3%", marginTop: "2%" }}>
                                        <h3>Search For Properties</h3>
                                    </div>
                                    <form onSubmit={this.handleSearch} method="post">
                                        <div className="row">
                                            <div className="form-group row">
                                                <label htmlFor="location" className="col-sm-3 col-form-label" style={{ marginLeft: "-1em" }}>Location:*</label>
                                                <div className="col-sm-6">
                                                    <input type="text" className="form-control" name="city" placeholder="Enter City Name" />
                                                    <label htmlFor="or" className="col-sm-5 col-form-label">OR</label>
                                                    <input type="text" className="form-control" name="zipcode" placeholder="Enter Zipcode" pattern="^\d{5}$" />
                                                </div>
                                            </div>
                                            <div className="form-group row">
                                                <label htmlFor="from" className="col-sm-4 col-form-label">From:*</label>
                                                <div className="col-sm-8">
                                                    <input type="date" className="form-control" name="from" required />
                                                </div>
                                            </div>
                                            <div className="form-group row">
                                                <label htmlFor="to" className="col-sm-3 col-form-label">&nbsp;&nbsp;To:*</label>
                                                <div className="col-sm-9">
                                                    <input type="date" className="form-control" name="to" required />
                                                </div>
                                            </div>
                                            <div className="form-group row">
                                                <label htmlFor="sharingType" className="col-form-label" style={{ marginLeft: "1.5em" }}>Sharing Type:</label>
                                                <label> <input type="radio" name="sharingType" value="entirePlace" style={{ marginLeft: "2em", marginTop: "0.8em" }} />&nbsp;Entire Place</label>&nbsp;&nbsp;
                                                    &nbsp;&nbsp;<label> <input type="radio" name="sharingType" value="room" style={{ marginTop: "0.8em" }} />&nbsp;A Room</label>

                                            </div>
                                            <div className="form-group row">
                                                <label htmlFor="propertyType" className="col-form-label" style={{ marginLeft: "1.5em" }} >Property Type:</label>
                                                <select className="form-control col-sm-6" name="propertyType" style={{ marginLeft: "1.7em" }}>
                                                    <option value="any">Any</option>
                                                    <option value="house">House</option>
                                                    <option value="condoApt">Condo/Apartment</option>

                                                    <option value="townHouse">Town House</option>
                                                </select>
                                            </div>
                                            <div className="form-group row">
                                                <label htmlFor="internet" className="col-form-label" style={{ marginLeft: "1.5em" }}>Internet Avialibilty:</label>
                                                <label> <input type="radio" name="sharingType" value="yes" style={{ marginLeft: "2em", marginTop: "0.8em" }} />&nbsp;Yes</label>&nbsp;&nbsp;
                                                    &nbsp;&nbsp;<label> <input type="radio" name="sharingType" value="no" style={{ marginTop: "0.8em" }} />&nbsp;No</label>

                                            </div>
                                            <div className="form-group row">
                                                <label htmlFor="rangeFrom" className="col-sm-3 col-form-label">Price Range:</label>
                                                <label htmlFor="rangeFrom" className="col-form-label">Min($)</label>
                                                <div className="col-sm-2">
                                                    <input type="number" className="form-control" name="priceFrom" />
                                                </div>
                                                <label htmlFor="rangeTo" className=" col-form-label">Max($)</label>

                                                <div className="col-sm-2">
                                                    <input type="number" className="form-control" name="priceTo" />
                                                </div>
                                            </div>
                                            <div className="form-group row">
                                                <label htmlFor="desc" className="col-sm-5 col-form-label">Keywords:</label>
                                                <div className="col-sm-7">
                                                    <input type="text" className="form-control" name="desc" />
                                                </div>
                                            </div>
                                            <div className="form-group row">
                                                <div className="col-12" style={{ marginTop: "-2em", marginLeft: "19em" }}>

                                                    <button type="submit" className="btn btn-primary align-center">Search</button>
                                                </div>
                                            </div>
                                        </div>
                                    </form>
                                    <br></br>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default SearchProperty;