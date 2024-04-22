package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import com.example.adamapbackend.domain.enums.TipoEspacio;
import com.example.adamapbackend.domain.enums.TipoUso;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
public class Reserva{
    @Id
    UUID idReserva;
    @OneToMany
    List<Espacio> espacios;
    Integer numAsistentes;
    String descripcion;
    @Enumerated(EnumType.STRING)
    TipoUso tipoUso;
    String horaInicio;
    Integer duracion;
    Date fecha;
    @ManyToOne
    Persona persona;

    public Reserva(List<Espacio> espacios, Integer numAsistentes, String descripcion, Date fecha, Integer duracion, String horaInicio, TipoUso tipoUso, Persona persona) throws IllegalAccessException {

        if (espacios.isEmpty())
            throw new IllegalArgumentException("Una reserva debe tener uno o más espacios");

        espacios.forEach(espacio -> espacio.checkHorario(horaInicio, duracion, fecha));

        if(espacios.stream().mapToInt(Espacio::getMaxPersonasParaReserva).sum() < numAsistentes)
            throw new IllegalArgumentException("No se puede reservar para más personas de las que permiten los espacios");


        if (persona.getRoles().size() == 1) {
            switch (persona.getRoles().get(0)) {
                case ESTUDIANTE -> checkEstudiante();
                case CONSERJE -> checkConserje();
                case TECNICO_LABORATORIO -> checkTecnico();
                case DOCENTE_INVESTIGADOR -> checkDocente();
                case INVESTIGADOR_CONTRATADO -> checkInvestigador();
            }
        }
    }


    void checkEstudiante() {
        if(espacios.stream().anyMatch(espacio -> !espacio.getTipoEspacioParaReserva().equals(TipoEspacio.SALA_COMUN)))
            throw new IllegalArgumentException("Un estudiante solo puede reservar salas comunes");
    }

    void checkConserje() {
        if(espacios.stream().anyMatch(espacio -> espacio.getTipoEspacioParaReserva().equals(TipoEspacio.DESPACHO)))
            throw new IllegalArgumentException("Un conserje no puede reservar despachos");
    }

    void checkTecnico() {
        if(espacios.stream().anyMatch(espacio -> espacio.getTipoEspacioParaReserva().equals(TipoEspacio.DESPACHO)))
            throw new IllegalArgumentException("Los técnicos de laboratorio no pueden reservas los despachos");
        checkLaboratorio();
    }

    void checkDocente() {
        checkLaboratorio();
        checkDespacho();
    }

    void checkInvestigador() {
        checkLaboratorio();
        checkDespacho();
    }

    void checkLaboratorio() {
        Departamento departamentoTecnico = this.persona.getDepartamento();
        this.espacios.stream()
                .filter(espacio -> espacio.getTipoEspacioParaReserva().equals(TipoEspacio.LABORATORIO))
                .forEach(laboratorio -> {
                    if (laboratorio.getPropietarioEspacio().isEINA)
                        throw new IllegalArgumentException("No se puede reservar un laboratorio que pertenece a la EINA");
                    if (laboratorio.getPropietarioEspacio().isPersonas)
                        throw new IllegalArgumentException("No se puede reservar un laboratorio que pertenece a personas");
                    if (laboratorio.getPropietarioEspacio().isDepartamento && !departamentoTecnico.equals(Departamento.of(laboratorio.getPropietarioEspacio().propietario.get(0))))
                        throw new IllegalArgumentException("No se puede reservar un laboratorio que no pertenezca a tu departamento");
                });
    }

    void checkDespacho() {
        Departamento departamentoTecnico = this.persona.getDepartamento();
        this.espacios.stream()
                .filter(espacio -> espacio.getTipoEspacioParaReserva().equals(TipoEspacio.DESPACHO))
                .forEach(despacho -> {
                    if (despacho.getPropietarioEspacio().isEINA)
                        throw new IllegalArgumentException("No se puede reservar un despacho que pertenece a la EINA");
                    if (despacho.getPropietarioEspacio().isPersonas)
                        throw new IllegalArgumentException("No se puede reservar un despacho que pertenece a personas");
                    if (despacho.getPropietarioEspacio().isDepartamento && !departamentoTecnico.equals(Departamento.of(despacho.getPropietarioEspacio().propietario.get(0))))
                        throw new IllegalArgumentException("No se puede reservar un despacho que no pertenezca a tu departamento");
                });
    }

}
