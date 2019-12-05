import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import '../Styles/SearchResult.css';
import ReactPaginate from 'react-paginate';
import { API_BASE_URL, ACCESS_TOKEN } from '../constants';
import HostNavigation from "../Navigation/HostNavigation";
import Alert from 'react-s-alert';
import 'react-s-alert/dist/s-alert-default.css';
import 'react-s-alert/dist/s-alert-css-effects/slide.css';

class ListProperties extends Component {
        //call the constructor method
        constructor(props) {
            //Call the constrictor of Super class i.e The Component
            super(props);
            console.log(this.props);
            //maintain the state required for this component
            this.state = {
                //for pagination
                paginated_results:[],
                results_per_page: 3,
                num_pages:0,
            }
            //for pagination
            this.handlePageClick = this.handlePageClick.bind(this);
        }

        //for pagination
        handlePageClick(data){
            console.log(data.selected)
            let page_number = data.selected;
            let offset = Math.ceil(page_number * this.state.results_per_page)
            this.setState({
                paginated_results : this.state.results.slice(offset, offset +this.state.results_per_page)
            })
        }


        componentDidMount() {
        axios(
            {
                method: 'get',
                url: API_BASE_URL + '/host/' + localStorage.id + '/properties',
                headers: {"Authorization": "Bearer " + localStorage.getItem(ACCESS_TOKEN)}
            }
        ).then(response => {
            if (response.status === 200) {
                console.log('Result: ', response.data);
                var propertyDetails = response.data;
                console.log(propertyDetails);
                // for pagination
                const all_results = propertyDetails;
                const pages = Math.ceil(all_results.length/this.state.results_per_page)
                this.setState({
                    num_pages:pages,
                    paginated_results: all_results.slice(0,this.state.results_per_page),
                });
            }
        }).catch((err) => {
            if (err) {
                this.setState({
                    errorRedirect: true
                })
            }
        });
    }

    render() {
            let resultsDiv = ""
            if(this.state.paginated_results.length == 0){
                resultsDiv = <div>
                    <div className="container-fluid">
                        <br/>
                        <br/>
                        No Properties added yet!
                    </div>
                </div>
            }else{
                resultsDiv = this.state.paginated_results.map(record => {
                    var link = "/property/view/"+record.id;
                    return(
                        <div class="card bg-light text-dark">
                            <div class="row">
                                <div class="col-3">
                                    <img class="card-img" src={record.imageUrl} alt="Card image" />
                                </div>
                                <div className="col">
                                    <div class="card-body">
                                        <div className="card-title result-title">
                                            <a href={link}>{record.headline}</a>
                                        </div>
                                        <div className="row">
                                            <div className="col">
                                                <div className="addr">
                                                    <label className="row">Address:</label>
                                                    <label className="row">{record.street}</label>
                                                    <label className="row">{record.city}, {record.state} {record.zip}</label>
                                                </div>
                                            </div>
                                            <div className="col" style={{marginLeft:"10em"}}>
                                                <label className="row">Weekday Price($): {record.weekdayPrice}</label>
                                                <label className="row">Weekend Price($): {record.weekendPrice}</label>
                                            </div>
                                        </div>

                                    </div>
                                </div>
                            </div>
                        </div>
                    )
                });
            }



        return (
            <div style={{background:"white"}}>
                    <HostNavigation />
                <div className="container-fluid">
                    <div className="row m-4" style={{ height: '75vh' }}>
                        <div className="col-12">
                            <div className="border-bottom row mt-3" style={{ marginBottom: "3%" }}>
                                <h3>My Properties</h3>
                            </div>
                            {resultsDiv}
                        </div>
                    </div>
                    <div className="row">
                        <ReactPaginate
                            previousLabel={'Previous'}
                            nextLabel={'Next'}
                            breakLabel={'...'}
                            breakClassName={'break-me'}
                            pageCount={this.state.num_pages}
                            marginPagesDisplayed={2}
                            pageRangeDisplayed={5}
                            onPageChange={this.handlePageClick}
                            containerClassName={'pagination'}
                            subContainerClassName={'pages pagination'}
                            activeClassName={'active'}
                        />
                    </div>
                </div>
            </div>

        )
    }
}

export default ListProperties;