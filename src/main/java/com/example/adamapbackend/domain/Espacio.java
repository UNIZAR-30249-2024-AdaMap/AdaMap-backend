package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.domain.enums.TipoEspacio;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Espacio implements Serializable {
    @Id
    private String idEspacio;

    @Enumerated(EnumType.STRING)
    private TipoEspacio tipoEspacio;
    @Enumerated(EnumType.STRING)
    private TipoEspacio tipoEspacioDefecto;

    private Integer numMaxPersonas;
    private Boolean reservable = false;
    private Double tamano;
    private Integer porcentajeUsoDefecto;
    private Integer porcentajeUso;
    private Integer planta;
    private String nombre;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "horarioLunes", column = @Column(name = "horarioLunesDefecto")),
            @AttributeOverride(name = "horarioMartes", column = @Column(name = "horarioMartesDefecto")),
            @AttributeOverride(name = "horarioMiercoles", column = @Column(name = "horarioMiercolesDefecto")),
            @AttributeOverride(name = "horarioJueves", column = @Column(name = "horarioJuevesDefecto")),
            @AttributeOverride(name = "horarioViernes", column = @Column(name = "horarioViernesDefecto")),
            @AttributeOverride(name = "horarioSabado", column = @Column(name = "horarioSabadoDefecto")),
            @AttributeOverride(name = "horarioDomingo", column = @Column(name = "horarioDomingoDefecto"))
    })
    Horario horarioDefecto;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "horarioLunes", column = @Column(name = "horarioLunes")),
            @AttributeOverride(name = "horarioMartes", column = @Column(name = "horarioMartes")),
            @AttributeOverride(name = "horarioMiercoles", column = @Column(name = "horarioMiercoles")),
            @AttributeOverride(name = "horarioJueves", column = @Column(name = "horarioJueves")),
            @AttributeOverride(name = "horarioViernes", column = @Column(name = "horarioViernes")),
            @AttributeOverride(name = "horarioSabado", column = @Column(name = "horarioSabado")),
            @AttributeOverride(name = "horarioDomingo", column = @Column(name = "horarioDomingo"))
    })
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

        this.tipoEspacioDefecto = tipoEspacio;
        this.numMaxPersonas = numMaxPersonas;
        this.reservable = reservable;
        this.tamano = tamano;
        this.horarioDefecto = horario;
        this.porcentajeUsoDefecto = porcentajeUso;

        updatePropietario(propietarioEspacio);
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
        if ((getTipoEspacioParaReserva().equals(TipoEspacio.AULA) || getTipoEspacioParaReserva().equals(TipoEspacio.SALA_COMUN)) && !propietarioEspacio.isEINA())
            throw new IllegalArgumentException("Una aula o sala común debe estar asignada a la EINA");

        if ((getTipoEspacioParaReserva().equals(TipoEspacio.SEMINARIO) || getTipoEspacioParaReserva().equals(TipoEspacio.LABORATORIO)) && propietarioEspacio.isPersonas())
            throw new IllegalArgumentException("Un seminario o laboratorio debe estar asignado a la EINA");

        if (getTipoEspacioParaReserva().equals(TipoEspacio.DESPACHO) && propietarioEspacio.isEINA())
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
        if (porcentajeUso != 0)
            return this.numMaxPersonas * this.porcentajeUso / 100;
        return this.numMaxPersonas * this.porcentajeUsoDefecto / 100;
    }

    public Horario getHorarioParaReserva() {
        return horario != null ? horario : horarioDefecto;
    }

    public void checkHorario(String horaInicio, Integer duracion, Date fecha) {
        String horarioToCheck = getHorarioParaReserva().getByDay(fecha.getDay());
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
        String horarioToCheck = getHorarioParaReserva().getByDay(fecha.getDay());
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
        return planta;
    }

    public boolean esReservablePorElUsuario(Persona persona) {
        if (!reservable)
            return false;

        if (persona.getRoles().size() == 1) {
            switch (persona.getRoles().get(0)) {
                case ESTUDIANTE -> {
                    return getTipoEspacioParaReserva().equals(TipoEspacio.SALA_COMUN);
                }
                case CONSERJE, TECNICO_LABORATORIO -> {
                    if (persona.getRoles().get(0).equals(Rol.TECNICO_LABORATORIO) && getTipoEspacioParaReserva().equals(TipoEspacio.LABORATORIO)) {
                        if (!propietarioEspacio.isDepartamento())
                            return false;

                        return persona.getDepartamento().equals(Departamento.of(propietarioEspacio.getPropietario().get(0)));
                    }

                    return !getTipoEspacioParaReserva().equals(TipoEspacio.DESPACHO);
                }
                case DOCENTE_INVESTIGADOR, INVESTIGADOR_CONTRATADO -> {
                    if (getTipoEspacioParaReserva().equals(TipoEspacio.LABORATORIO)) {
                        if (!propietarioEspacio.isDepartamento())
                            return false;

                        return persona.getDepartamento().equals(Departamento.of(propietarioEspacio.getPropietario().get(0)));
                    }

                    if (getTipoEspacioParaReserva().equals(TipoEspacio.DESPACHO)) {
                        if (!propietarioEspacio.isDepartamento())
                            return false;

                        return persona.getDepartamento().equals(Departamento.of(propietarioEspacio.getPropietario().get(0)));
                    }

                    return true;
                }
            }
        }
        return true;
    }
}
