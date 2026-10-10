package com.kalitron.studio.service.dto.measurement;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

/**
 * Wall with the three lengths (floor, 900 mm, ceiling), ceiling height at both
 * ends (sloped ceilings), elements and photos.
 */
public record MeasurementWallDTO(
    String wallCode,
    MeasuredValueDTO lengthFloorMm,
    MeasuredValueDTO length900Mm,
    MeasuredValueDTO lengthCeilingMm,
    MeasuredValueDTO outOfPlumbMm,
    MeasuredValueDTO closingMm,
    MeasuredValueDTO ceilingHeightLeftMm,
    MeasuredValueDTO ceilingHeightRightMm,
    List<MeasurementElementDTO> elements,
    List<String> photoUuids
) implements Serializable {
    public boolean hasAllLengths() {
        return (
            MeasuredValueDTO.present(lengthFloorMm) && MeasuredValueDTO.present(length900Mm) && MeasuredValueDTO.present(lengthCeilingMm)
        );
    }

    /** Design length = minimum of the three lengths; null when incomplete. */
    public Integer designLengthMm() {
        if (!hasAllLengths()) {
            return null;
        }
        return Stream.of(lengthFloorMm, length900Mm, lengthCeilingMm).map(MeasuredValueDTO::value).min(Integer::compare).orElseThrow();
    }
}
