package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.TipoEspacio;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Espacio {
    @Id
    String idEspacio;

    @Enumerated(EnumType.STRING)
    TipoEspacio tipoEspacio;
    @Enumerated(EnumType.STRING)
    TipoEspacio tipoEspacioDefecto;

    Integer numMaxPersonas;
    Boolean reservable;
    Double tamano;
    Integer porcentajeUsoDefecto;
    Integer porcentajeUso;

    @Embedded
    Horario horarioDefecto;

    @Embedded
    Horario horario;

    @Embedded
    PropietarioEspacio propietarioEspacio;


    public Espacio(TipoEspacio tipoEspacio, Integer numMaxPersonas, Boolean reservable, Double tamano, Horario horario, PropietarioEspacio propietarioEspacio, Integer porcentajeUso) {
        if (tipoEspacio == null)
            throw new IllegalArgumentException("Un espacio debe tener un tipo");

        if (tamano <= 0)
            throw new IllegalArgumentException("Un espacio debe tener un tamano mayor que cero");

        if (porcentajeUso <= 0)
            throw new IllegalArgumentException("El porcentaje de uso del espacio debe ser mayor que cero");

        if (tipoEspacio.equals(TipoEspacio.DESPACHO) && propietarioEspacio.isPersonas() && reservable)
            throw new IllegalArgumentException("Un despacho asignado a personas no puede ser reservable");

        updatePropietario(propietarioEspacio);

        this.tipoEspacioDefecto = tipoEspacio;
        this.numMaxPersonas = numMaxPersonas;
        this.reservable = reservable;
        this.tamano = tamano;
        this.horarioDefecto = horario;
        this.porcentajeUsoDefecto = porcentajeUso;
    }

    // SOLO GERENTE
    public void cambiarReservabilidad() {
        this.reservable = !this.reservable;
    }

    public void cambiarTipoEspacio(TipoEspacio tipoEspacio) {
        this.tipoEspacio = tipoEspacio;
    }

    public void cambiarHorario(Horario horario) {
        this.horario = horario;
    }

    public void updatePropietario(PropietarioEspacio propietarioEspacio) {
        if ((this.tipoEspacioDefecto.equals(TipoEspacio.AULA) || this.tipoEspacioDefecto.equals(TipoEspacio.SALA_COMUN)) && !propietarioEspacio.isEINA())
            throw new IllegalArgumentException("Una aula o sala común debe estar asignada a la EINA");

        if ((this.tipoEspacioDefecto.equals(TipoEspacio.SEMINARIO) || this.tipoEspacioDefecto.equals(TipoEspacio.LABORATORIO)) && propietarioEspacio.isPersonas())
            throw new IllegalArgumentException("Un seminario o laboratorio debe estar asignado a la EINA");

        if (this.tipoEspacioDefecto.equals(TipoEspacio.DESPACHO) && propietarioEspacio.isEINA())
            throw new IllegalArgumentException("Un despacho debe estar asignado a un departamento o varias personas personas");

        this.propietarioEspacio = propietarioEspacio;
    }

    public TipoEspacio getTipoEspacioParaReserva() {
        return tipoEspacio == null ? tipoEspacioDefecto : tipoEspacio;
    }

    public void cambiarPorcentajeUso(Integer porcentajeUso) {
        this.porcentajeUso = porcentajeUso;
    }

    public Integer getMaxPersonasParaReserva() {
        if (porcentajeUso != null)
            return this.numMaxPersonas * 100 / this.porcentajeUso;
        return this.numMaxPersonas * 100 / this.porcentajeUsoDefecto;
    }

    public Horario getHorarioParaReserva() {
        return horario != null ? horario : horarioDefecto;
    }

    public void checkHorario(String horaInicio, Integer duracion, Date fecha) {
        String horarioToCheck = horario == null ? horarioDefecto.getByDay(fecha.getDay()) : horario.getByDay(fecha.getDay());
        String horaInicioEspacio = horarioToCheck.split("-")[0];
        String horaFinEspacio = horarioToCheck.split("-")[1];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime inicioEspacio = LocalTime.parse(horaInicioEspacio, formatter);
        LocalTime finEspacio = LocalTime.parse(horaFinEspacio, formatter);
        LocalTime inicioReserva = LocalTime.parse(horaInicio, formatter);
        LocalTime finReserva = inicioReserva.plusMinutes(duracion);

        if (inicioReserva.isAfter(finEspacio))
            throw new IllegalArgumentException("No se puede reservar después de la hora de cierre del espacio");

        if (inicioReserva.isBefore(inicioEspacio))
            throw new IllegalArgumentException("No se puede reservar antes de la hora de apertura del espacio");

        if(finReserva.isAfter(finEspacio))
            throw new IllegalArgumentException("No se puede reservar más tiempo del horario del espacio");
    }

    public boolean isHorarioDisponible(String horaInicio, Integer duracion, Date fecha) {
        String horarioToCheck = horario == null ? horarioDefecto.getByDay(fecha.getDay()) : horario.getByDay(fecha.getDay());
        String horaInicioEspacio = horarioToCheck.split("-")[0];
        String horaFinEspacio = horarioToCheck.split("-")[1];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime inicioEspacio = LocalTime.parse(horaInicioEspacio, formatter);
        LocalTime finEspacio = LocalTime.parse(horaFinEspacio, formatter);
        LocalTime inicioReserva = LocalTime.parse(horaInicio, formatter);
        LocalTime finReserva = inicioReserva.plusMinutes(duracion);

        if (inicioReserva.isAfter(finEspacio) || inicioReserva.isBefore(inicioEspacio) || finReserva.isAfter(finEspacio))
            return false;

        return true;
    }

    public Integer getPlanta(){
        return null;
    }

    public boolean esReservablePorElUsuario(Persona persona) {
        if (persona.getRoles().size() == 1) {
            switch (persona.getRoles().get(0)) {
                case ESTUDIANTE -> {
                    return getTipoEspacioParaReserva().equals(TipoEspacio.SALA_COMUN);
                }
                case CONSERJE, TECNICO_LABORATORIO -> {
                    return !getTipoEspacioParaReserva().equals(TipoEspacio.DESPACHO);
                }
                case DOCENTE_INVESTIGADOR, INVESTIGADOR_CONTRATADO -> {
                    if (getTipoEspacioParaReserva().equals(TipoEspacio.LABORATORIO)) {
                        if (!getPropietarioEspacio().isDepartamento())
                            return false;

                        return persona.getDepartamento().equals(Departamento.of(getPropietarioEspacio().propietario.get(0)));
                    }

                    if (getTipoEspacioParaReserva().equals(TipoEspacio.DESPACHO)) {
                        if (!getPropietarioEspacio().isDepartamento())
                            return false;

                        return persona.getDepartamento().equals(Departamento.of(getPropietarioEspacio().propietario.get(0)));
                    }

                    return true;
                }
            }
        }
        return true;
    }
}
