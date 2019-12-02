import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import swal from 'sweetalert';
import '../Styles/Search.css';
import { API_BASE_URL } from '../constants';
import { getCurrentSystemTime } from '../util/APIUtils';

class SearchProperty extends Component {
    constructor(props) {
        super(props);
        this.state = {
            results: [],
            curTime: null
        }
    }

    componentDidMount = () => {
        getCurrentSystemTime().then(response => {
            this.setState({
              curTime : response.toLocaleString()
            })
          });
    }

    handleSearch = async (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);
        let validInput = true;
        var token = localStorage.getItem("accessToken");

        var to = formData.get("to");
        var from = formData.get("from");

        var priceMin = formData.get("priceFrom");
        var priceMax = formData.get("priceTo");

        if (formData.get("city") == '' && formData.get("zipcode") == '') {
            swal("Oops!", "City or Zipcode is required", "error");
            validInput = false;
        }
        else {
            if (to < from) {
                swal("Oops!", "From Date must be before To.", "error");
                validInput = false;
            }
            else {
               /* let curTime = this.state.curTime;
                let curDate = curTime.substr(0,curTime.indexOf(','));
                
                console.log("From Date"+from);
                var curFormmatedDate = curDate.split("/").reverse();
                var tmp = curFormmatedDate[2];
                curFormmatedDate[2] = curFormmatedDate[1];
                curFormmatedDate[1] = tmp;
                curFormmatedDate = curFormmatedDate.join("-");
                console.log("Current formatted Date:"+curFormmatedDate);
                if(from < curFormmatedDate || to < curFormmatedDate){
                    swal("Oops!", "Current System time is "+curTime+". Please select a date on or after current time.", "error");
                    validInput = false;
                }
                else{*/
                    if (priceMax != '' && priceMin != '' && priceMin > priceMax) {
                        swal("Oops!", "Please make sure Max price is greater Min price.", "error");
                        validInput = false;
                    }
                //}
            }
        }

        if (validInput == true) {
            var data = {
                "city": formData.get("city"), "zip": formData.get("zipcode"),
                "to": to, "from": from, "sharingType": formData.get("sharingType"),
                "propertyType": formData.get("propertyType"), "internet": formData.get("internet"),
                "minPrice": parseInt(priceMin), "maxPrice": parseInt(priceMax), "desc": formData.get("desc")
            };
            console.log(data);
            /*this.setState({
                results : [{ "id":1,"headline": "House by the ocean", "imageurl":"https://picsum.photos/id/866/200/200", "weekendprice":60, "weekdayprice":50, city:"Santa Clara", street:"El Sandro", 
                "zip":"900000", "state":"CA"
            }, { "id":2, "headline": "House by the ocean2", "imageurl":"https://picsum.photos/id/866/200/200", "weekendprice":60, "weekdayprice":50, city:"Santa Clara", street:"El Sandro", 
            "zip":"900000", "state":"CA"
        }]
            });*/
            await axios({
                method: 'post',
                url: API_BASE_URL + '/property/search',
                data: data,
                config: { headers: { 'Content-Type': 'multipart/form-data' } },
                headers: { "Authorization": `Bearer ${token}` }
            })
                .then((response) => {
                    if (response.status >= 500) {
                        throw new Error("Bad response from server");
                    }
                    return response.data;
                })
                .then((responseData) => {
                    console.log("responseData", responseData);
                        var results = responseData;
                        console.log(results);
                        this.setState({
                            results : results.properties
                        });
                        if(results.properties.length==0){
                            swal("Unable to find properties with given values. Please refine search criteria!");
                        }
                }).catch(function (err) {
                    console.log(err)
                });
        }

    }
    render() {
        console.log(this.state.curTime);
        return (
            <div>
                <div className='rowC backgroundImg' style={{ display: "flex", flexDirection: "row" }}>
                    <div className="card card-css">
                        <div className="container">
                            <div className="row justify-content-center align-items-center" >


                                <div className="col-12">
                                    <div className="border-bottom row" style={{ marginBottom: "3%", marginTop: "2%" }}>
                                        <h3>Search For Property</h3>
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
                                                <label> <input type="radio" name="sharingType" value="Entire Place" style={{ marginLeft: "2em", marginTop: "0.8em" }} />&nbsp;Entire Place</label>&nbsp;&nbsp;
                                                    &nbsp;&nbsp;<label> <input type="radio" name="sharingType" value="Private Room" style={{ marginTop: "0.8em" }} />&nbsp;A Room</label>

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
                                                <label> <input type="radio" name="internet" value="yes" style={{ marginLeft: "2em", marginTop: "0.8em" }} />&nbsp;Yes</label>&nbsp;&nbsp;
                                                    &nbsp;&nbsp;<label> <input type="radio" name="internet" value="no" style={{ marginTop: "0.8em" }} />&nbsp;No</label>

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
                                    {this.state.results.length > 0 &&
                                        <Redirect to={{
                                            pathname: '/property/result',
                                            state: { results: this.state.results }
                                        }} />
                                    }
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