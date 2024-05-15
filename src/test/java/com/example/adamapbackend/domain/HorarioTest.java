package com.example.adamapbackend.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HorarioTest {

    private static final Horario horario = new Horario(
            "01:00-02:00",
            "02:00-03:00",
            "03:00-04:00",
            "04:00-05:00",
            "05:00-06:00",
            "06:00-07:00",
            "07:00-08:00");

    @ParameterizedTest
    @MethodSource("getByDay")
    public void shouldUpdateDepartamento(Integer dia, String result) {


        assertEquals(horario.getByDay(dia), result);
    }

    public static Stream<Arguments> getByDay() {
        return Stream.of(
                Arguments.of(0, horario.getHorarioDomingo()),
                Arguments.of(1, horario.getHorarioLunes()),
                Arguments.of(2, horario.getHorarioMartes()),
                Arguments.of(3, horario.getHorarioMiercoles()),
                Arguments.of(4, horario.getHorarioJueves()),
                Arguments.of(5, horario.getHorarioViernes()),
                Arguments.of(6, horario.getHorarioSabado())
        );
    }
}