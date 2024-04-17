package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.TipoEspacio;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Espacio {
        @Id
        UUID idEspacio;

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


        public Espacio(TipoEspacio tipoEspacio, Integer numMaxPersonas, Boolean reservable, Double tamano, Horario horario, PropietarioEspacio propietarioEspacio, Integer porcentajeUso) throws IllegalAccessException {
                if (tipoEspacio == null)
                        throw new IllegalAccessException("Un espacio debe tener un tipo");

                if (tamano <= 0)
                        throw new IllegalAccessException("Un espacio debe tener un tamano mayor que cero");

                if (porcentajeUso <= 0)
                        throw new IllegalAccessException("El porcentaje de uso del espacio debe ser mayor que cero");

                if (tipoEspacio.equals(TipoEspacio.DESPACHO) && propietarioEspacio.isPersonas && reservable)
                        throw new IllegalAccessException("Un despacho asignado a personas no puede ser reservable");
                
                updatePropietario(propietarioEspacio);

                this.tipoEspacioDefecto = tipoEspacio;
                this.numMaxPersonas = numMaxPersonas;
                this.reservable = reservable;
                this.tamano = tamano;
                this.horarioDefecto = horario;
                this.porcentajeUsoDefecto = porcentajeUso;
        }

        // SOLO GERENTE
        public void hacerReservable() {
                this.reservable = true;
        }

        public void hacerNoReservable() {
                this.reservable = false;
        }

        public void cambiarTipoEspacio(TipoEspacio tipoEspacio) {
                this.tipoEspacio = tipoEspacio;
        }

        public void cambiarHorario(Horario horario) {
                this.horario = horario;
        }

        public void updatePropietario(PropietarioEspacio propietarioEspacio) throws IllegalAccessException {
                if ((this.tipoEspacioDefecto.equals(TipoEspacio.AULA) || this.tipoEspacioDefecto.equals(TipoEspacio.SALA_COMUN)) && !propietarioEspacio.isEINA)
                        throw new IllegalAccessException("Una aula o sala común debe estar asignada a la EINA");

                if ((this.tipoEspacioDefecto.equals(TipoEspacio.SEMINARIO) || this.tipoEspacioDefecto.equals(TipoEspacio.LABORATORIO)) && propietarioEspacio.isPersonas)
                        throw new IllegalAccessException("Un seminario o laboratorio debe estar asignado a la EINA");

                if (this.tipoEspacioDefecto.equals(TipoEspacio.DESPACHO) && propietarioEspacio.isEINA)
                        throw new IllegalAccessException("Un despacho debe estar asignado a un departamento o varias personas personas");

                this.propietarioEspacio = propietarioEspacio;
        }

        public void cambiarPorcentajeUso(Integer porcentajeUso) {
                this.porcentajeUso = porcentajeUso;
        }

        public Integer getMaxPersonasParaReserva() {
                if (porcentajeUso != null)
                        return this.numMaxPersonas * 100 / this.porcentajeUso;
                return this.numMaxPersonas * 100 / this.porcentajeUsoDefecto;
        }
}
