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
        url: API_BASE_URL + "/api/auth/login",
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function signup(signupRequest) {
    return request({
        url: API_BASE_URL + "/api/auth/signup",
        method: 'POST',
        body: JSON.stringify(signupRequest)
    });
}

// Reservation related methods
export function createReservation(createReservationRequest) {
  return request({
      url: API_BASE_URL + "/api/reservation/create",
      method: 'POST',
      body: JSON.stringify(createReservationRequest)
  });
}

export function cancelReservation(cancelReservationRequest) {
  return request({
      url: API_BASE_URL + "/api/reservation/cancel",
      method: 'POST',
      body: JSON.stringify(cancelReservationRequest)
  });
}

export function checkInReservation(checkInReservationRequest) {
  return request({
      url: API_BASE_URL + "/api/reservation/checkIn",
      method: 'POST',
      body: JSON.stringify(checkInReservationRequest)
  });
}

export function checkOutReservation(checkOutReservationRequest) {
  return request({
      url: API_BASE_URL + "/api/reservation/checkOut",
      method: 'POST',
      body: JSON.stringify(checkOutReservationRequest)
  });
}

export function getReservationPrice(getReservationPriceRequest) {
  return request({
      url: API_BASE_URL + "/api/reservation/priceRequest",
      method: 'POST',
      body: JSON.stringify(getReservationPriceRequest)
  });
}

export function getReservation(reservationId) {
  return request({
      url: API_BASE_URL + "/api/reservation/" + reservationId,
      method: 'GET'
  });
}

// System Time Related Methods
export function getCurrentSystemTime() {
  var options = {
      url: API_BASE_URL + "/api/system/time",
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

export function addToCurrentSystemTime(addTimeRequest) {
  return request({
      url: API_BASE_URL + "/api/system/addTime",
      method: 'POST',
      body: JSON.stringify(addTimeRequest)
  });
}

// Payment Method Utilities (HACKY)
export function hasValidPaymentMethod() {
  return request({
      url: API_BASE_URL + "/api/pay/getvalidpaymentmethod",
      method: 'GET'
  });
}
