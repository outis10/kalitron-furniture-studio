package com.kalitron.studio.service.dto.measurement;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SquareCheckDTOTest {

    private static SquareCheckDTO check(int legA, int legB, Integer diagonal) {
        return new SquareCheckDTO(
            SquareCheckStatus.VERIFIED,
            legA,
            legB,
            diagonal == null ? null : new MeasuredValueDTO(diagonal, ValueSource.LASER)
        );
    }

    @Test
    void diagonalOf1414WithMetreLegsIsSquare() {
        assertThat(check(1000, 1000, 1414).computedAngleDeg()).isEqualTo(90);
    }

    @Test
    void aboutTwelveMillimetresOfDiagonalIsOneDegree() {
        assertThat(check(1000, 1000, 1426).computedAngleDeg()).isEqualTo(91);
        assertThat(check(1000, 1000, 1402).computedAngleDeg()).isEqualTo(89);
    }

    @Test
    void returnsNullWhenNotComputable() {
        assertThat(check(1000, 1000, null).computedAngleDeg()).isNull();
        assertThat(check(1000, 1000, 2500).computedAngleDeg()).isNull();
        assertThat(check(0, 1000, 1000).computedAngleDeg()).isNull();
    }
}
