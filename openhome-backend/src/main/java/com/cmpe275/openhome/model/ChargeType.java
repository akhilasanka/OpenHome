package com.cmpe275.openhome.model;

public enum ChargeType {
    HOSTPENALTY,    // penalty when host cancels/changes availability
    GUESTPENALTY,   // penalty when guest cancels/no-show
    GUESTCHECKIN    // normal charge when guest checks-in to the property
}
