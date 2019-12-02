import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router';
import { getReservationList } from '../util/APIUtils';
import swal from 'sweetalert';
import ReactPaginate from 'react-paginate';

import '../Styles/SearchResult.css';

class ReservationListGuest extends Component {
    constructor(props) {
        super(props);
        this.state = {
          data: [],
          elementsPerPage: 5,
          currentPage: 0,
          pageCount: 0
        };

        this.loadReservationsFromServer = this.loadReservationsFromServer.bind(this);
        this.handlePageClick = this.handlePageClick.bind(this);
    }

    loadReservationsFromServer() {
      var payload = {
        currentPage: this.state.currentPage,
        elementsPerPage: this.state.elementsPerPage,
      }
      console.log(payload);
      getReservationList(payload)
      .then(data => {
        console.log(data);
        this.setState({
          data: data.reservations,
          pageCount: data.pageCount
        });
      }).catch(error => {
          swal("Oops!", (error && error.message) || 'Oops! Something went wrong while fetching reservations. Please try again!', "error");
      });
    }

    handlePageClick(data) {
      let selected = data.selected;

      this.setState({ currentPage: selected }, () => {
        this.loadReservationsFromServer();
      });      
    }

    componentDidMount() {
        //this.loadReservationsFromServer();
    }

    render() {
        let resultsDiv = this.state.data.map(function(reservation, index) {
            var link = "/reservation/"+reservation.id;
            return(
                <div key={index} className="card bg-light text-dark">
                  <div className="row">
                    <h3>{reservation.startDate}</h3>
                  </div>
                </div>
            )
        });

        return (
          <div>
              {resultsDiv}
              <ReactPaginate
              previousLabel={'Previous'}
              nextLabel={'Next'}
              breakLabel={'...'}
              breakClassName={'break-me'}
              pageCount={this.state.pageCount}
              initialPage={ 0 }
              marginPagesDisplayed={2}
              pageRangeDisplayed={5}
              onPageChange={this.handlePageClick}
              containerClassName={'pagination'}
              subContainerClassName={'pages pagination'}
              activeClassName={'active'}
              forcePage={this.state.currentPage}
              />
          </div>
        );
    }
}

export default ReservationListGuest
