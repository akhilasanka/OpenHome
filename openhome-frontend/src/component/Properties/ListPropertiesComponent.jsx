import React, { Component } from 'react'
import PropertyDataService from '../../service/PropertyDataService';

class ListPropertiesComponent extends Component {
  constructor(props) {
      super(props)
      this.state = {
          properties: [],
          message: null
      }
  }

  componentDidMount() {
      this.loadProperties();
  }

  loadProperties() {
      PropertyDataService.retrieveAllProperties()//HARDCODED
          .then(
              response => {
                  console.log(response);
                  this.setState({ properties: response.data })
              }
          )
  }

  render() {
      return (
          <div className="container">
              <h3>All Properties</h3>
              <div className="container">
                  <table className="table">
                      <thead>
                          <tr>
                              <th>Id</th>
                              <th>Owner</th>
                          </tr>
                      </thead>
                      <tbody>
                          {
                              this.state.properties.map(
                                  property =>
                                      <tr key={property.id}>
                                          <td>{property.id}</td>
                                          <td>{property.owner}</td>
                                      </tr>
                              )
                          }
                      </tbody>
                  </table>
              </div>
          </div>
      )
  }
}

export default ListPropertiesComponent
