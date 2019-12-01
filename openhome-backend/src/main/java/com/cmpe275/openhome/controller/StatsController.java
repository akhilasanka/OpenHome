package com.cmpe275.openhome.controller;

import com.cmpe275.openhome.payload.PayMethodResponse;
import com.cmpe275.openhome.security.CurrentUser;
import com.cmpe275.openhome.security.UserPrincipal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsController {
    @GetMapping("/getreservations")
    @PreAuthorize("hasRole('USER')")
    public PayMethodResponse getReservations(@CurrentUser UserPrincipal userPrincipal) {
        // return all reservations as a guest & as a host.
        // since each user is either guest/host, one of them will be empty.
        /**
         * Sample JSON response:
         * { "guest" : {
         *          "past": {
         *              "reservation_id":1,
         *              "property_id":1,
         *              "property_name: "<TYPE> at <STREET,CITY,STATE>",
         *              "start_date": "Jan 21 2019",
         *              "end_date":" Jan 24 2019",
         *              "price": 1100.00
         *          },
         *          "current": {},
         *          "future": {}
         *      },
         *   "host" : {
         *       "1": {
         *           "past": {},
         *           "current": {},
         *           "future": {}
         *          },
         *       "2": {
         *           "past": {},
         *           "current": {},
         *           "future": {}
         *          }
         *   }
         *  }
         */
        return null;
    }

    @GetMapping("/getbillingsummary")
    @PreAuthorize("hasRole('USER')")
    public PayMethodResponse getBillingSummary(@CurrentUser UserPrincipal userPrincipal) {
        // return all reservations as a guest & as a host.
        // since each user is either guest/host, one of them will be empty.
        /**
         * Sample JSON response:
         * { "guest" : {
         *          "JAN": {
         *              "summary": {
         *                  "total_reservations":10,
         *                  "total_amount": +/-100.00,
         *              },
         *              "line_items":{
         *              "transaction_id":1,
         *              "reservation_id":1,
         *              "property_id":1,
         *              "property_name: "<TYPE> at <STREET,CITY,STATE>",
         *              "start_date": "Jan 21 2019",
         *              "end_date":" Jan 24 2019",
         *              "amount": +/-1100.00,
         *              "charged_date": "Jan 21 2019",
         *              "type": "PENALTY/CHECKIN",
         *              "card_used": "...4444"
         *              }
         *          },
         *          "FEB": {},
         *          "MAR": {}
         *      },
         *   "host" : {
         *       "1": {
         *           "JAN": {},
         *           "FEB": {},
         *           "MAR": {}
         *          },
         *       "all": {
         *           "JAN": {},
         *           "FEB": {},
         *           "MAR": {}
         *          }
         *   }
         *  }
         */
        return null;
    }

}
