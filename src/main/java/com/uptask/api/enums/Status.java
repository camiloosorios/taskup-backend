package com.uptask.api.enums;

public enum Status {
    PENDING("pending"),
    HOLD_ON("onHold"),
    IN_PROGRESS("inProgress"),
    UNDER_REVIEW("underReview"),
    COMPLETED("completed");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
