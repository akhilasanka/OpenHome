import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import '../Styles/SearchResult.css';
import ReactPaginate from 'react-paginate';

class SearchResult extends Component {
    //call the constructor method
    constructor(props) {
        //Call the constrictor of Super class i.e The Component
        super(props);
        console.log(this.props);
        //maintain the state required for this component
        this.state = {
            results : this.props.location.state.results,
            
            //for pagination
            paginated_results:[],
            results_per_page: 1,
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
        console.log(this.state.results);
        // for pagination
        const all_results = this.state.results;
        const pages = Math.ceil(all_results.length/this.state.results_per_page)
        this.setState({
            num_pages:pages,
            paginated_results: all_results.slice(0,this.state.results_per_page),
        });
    }

    render() {
        let resultsDiv = this.state.paginated_results.map(record => {
            var link = "/property/"+record.id;
            return(
                <div class="card bg-light text-dark">
                <div class="row">
                    <div class="col-3">
                        <img class="card-img" src={record.imageurl} alt="Card image" />
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
                                <label className="row">Weekday Price($): {record.weekdayprice}</label>
                                <label className="row">Weekend Price($): {record.weekendprice}</label>
                                </div>
                            </div>
                            
                        </div>
                    </div>
                </div>
                </div>
            )
        });

        return (
            <div>
                <div className="container">
                <div className="row justify-content-center align-items-center" style={{ height: '75vh' }}>
                <div className="col-12">
                            <div className="border-bottom row" style={{ marginBottom: "3%" }}>
                                <h3>Search Results</h3>
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
export default SearchResult;
