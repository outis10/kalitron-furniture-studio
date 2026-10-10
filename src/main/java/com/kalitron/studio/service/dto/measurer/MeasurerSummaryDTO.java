package com.kalitron.studio.service.dto.measurer;

import java.io.Serializable;

/**
 * A user with {@code ROLE_MEASURER}, as shown in the assignment select (E12 #116).
 * No email or phone.
 */
public record MeasurerSummaryDTO(String login, String firstName, String lastName) implements Serializable {}
