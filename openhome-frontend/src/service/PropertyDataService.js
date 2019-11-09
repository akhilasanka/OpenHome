import axios from 'axios'

const OPENHOME_API_URL = 'http://localhost:8080'
const PROPERTY_API_URL = `${OPENHOME_API_URL}/hosts/meep`

class PropertyDataService {
    retrieveAllProperties() {
        console.log('executed service')
        return axios.get(`${PROPERTY_API_URL}/properties`);
    }
}

export default new PropertyDataService()
