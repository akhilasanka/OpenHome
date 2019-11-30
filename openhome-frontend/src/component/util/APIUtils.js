import { API_BASE_URL, ACCESS_TOKEN } from '../constants';

const request = (options) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    })

    if(localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options)
    .then(response =>
        response.json().then(json => {
            if(!response.ok) {
                return Promise.reject(json);
            }
            return json;
        })
    );
};

export function login(loginRequest) {
    return request({
        url: API_BASE_URL + "/auth/login",
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function signup(signupRequest) {
    return request({
        url: API_BASE_URL + "/auth/signup",
        method: 'POST',
        body: JSON.stringify(signupRequest)
    });
}

export function createReservation(createReservationRequest) {
  return request({
      url: API_BASE_URL + "/reservation/create",
      method: 'POST',
      body: JSON.stringify(createReservationRequest)
  });
}

export function getCurrentSystemTime() {
  var options = {
      url: API_BASE_URL + "/system/time",
      method: 'GET'
  }

  const headers = new Headers({
      'Content-Type': 'application/json',
  })
  const defaults = {headers: headers};

  options = Object.assign({}, defaults, options);

  return fetch(options.url, options)
  .then(response =>
      response.json().then(currentDateTime => {
          if(!response.ok) {
              return Promise.reject(currentDateTime);
          }
          return new Date(currentDateTime);
      })
  );
}

// System Time Related Methods
export function addToCurrentSystemTime(addTimeRequest) {
  return request({
      url: API_BASE_URL + "/system/addTime",
      method: 'POST',
      body: JSON.stringify(addTimeRequest)
  });
}
