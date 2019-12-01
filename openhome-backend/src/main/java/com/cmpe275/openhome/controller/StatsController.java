package com.cmpe275.openhome.controller;

import com.cmpe275.openhome.model.PayTransaction;
import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.payload.PayMethodResponse;
import com.cmpe275.openhome.payload.ReservationStatsResponse;
import com.cmpe275.openhome.repository.PayTransactionRepository;
import com.cmpe275.openhome.repository.ReservationRepository;
import com.cmpe275.openhome.repository.UserRepository;
import com.cmpe275.openhome.security.CurrentUser;
import com.cmpe275.openhome.security.UserPrincipal;
import com.cmpe275.openhome.util.SystemDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PayTransactionRepository payTransactionRepository;
    @GetMapping("/getreservations")
    @PreAuthorize("hasRole('USER')")
    public ReservationStatsResponse getReservations(@CurrentUser UserPrincipal userPrincipal) {
        //todo if property has a name add it in response.
        final ReservationStatsResponse reservationStatsResponse = new ReservationStatsResponse();
        List<Reservation> reservations = reservationRepository.findByVerifiedGuestId(userPrincipal.getId());
        Date curTime = Date.from( SystemDateTime.getCurSystemTime().atZone( ZoneId.systemDefault()).toInstant());
        for(final Reservation r: reservations) {
            ReservationStatsResponse.ReservationItem ri = ReservationStatsResponse.ReservationItem.newItemFromReservation(r);
            if(curTime.before(r.getStartDate())) {
                // cur time is before startdate, so future reservation
                reservationStatsResponse.setFuture(ri);
            } else if (curTime.after(r.getEndDate())) {
                // curTime is after end date, so reservation is in the past
                reservationStatsResponse.setPast(ri);
            } else {
                reservationStatsResponse.setCurrent(ri);
            }
        }
        reservationStatsResponse.setSuccess(true);
        return reservationStatsResponse;
        /*
          Sample JSON response:
          {
                    "valid_property_id":[], "valid_property_name":[],
                   "past": [{
                       "reservation_id":1,
                       "property_id":1,
                       "property_name: "<TYPE> at <STREET,CITY,STATE>",
                       "start_date": "Jan 21 2019",
                       "end_date":" Jan 24 2019",
                       "price": 1100.00
                   }],
                   "current": [],
                   "future": []
          }
         */
    }

    @GetMapping("/getbillingsummary")
    @PreAuthorize("hasRole('USER')")
    public PayMethodResponse getBillingSummary(@CurrentUser UserPrincipal userPrincipal) {
        final User currentUser = userRepository.findById(userPrincipal.getId()).orElse(null);
        if(currentUser != null && currentUser.getEmailVerified()) {
            if("guest".equals(currentUser.getRole())) {
                List<PayTransaction> transactions = payTransactionRepository.findTransactionsForGuest(currentUser);

            } else {
                List<PayTransaction> transactions = payTransactionRepository.findTransactionsForGuest(currentUser);
            }
        }
        // return all reservations as a guest & as a host.
        // since each user is either guest/host, one of them will be empty.
        /**
         * Sample JSON response:
         * { "valid_months": [], "valid_property_id":[], "valid_property_names":[], line_items:[
         *           {
         *              "transaction_month":January,
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
         * }]
         * }
         */
        return null;
    }

}
