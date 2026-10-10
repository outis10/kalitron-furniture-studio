package com.kalitron.studio.service;

/**
 * Why a measurer assignment was rejected (E12 #116). The resource maps
 * {@link Reason#SESSION_NOT_FOUND} and {@link Reason#USER_NOT_FOUND} to 404 and
 * the rest to 400 with the reason as error key.
 */
public class MeasurerAssignmentException extends RuntimeException {

    public enum Reason {
        SESSION_NOT_FOUND,
        USER_NOT_FOUND,
        USER_NOT_MEASURER,
        USER_NOT_ACTIVATED,
    }

    private final Reason reason;

    public MeasurerAssignmentException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
