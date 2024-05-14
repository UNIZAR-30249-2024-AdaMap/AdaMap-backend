package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Persona {

    @Id
    String correo;
    String nombre;
    Departamento departamento;
    List<String> notificaciones;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Rol.class)
    List<Rol> roles;

    public Persona(String correo, String nombre, Departamento departamento, List<Rol> roles) {
        this.correo = correo;
        this.nombre = nombre;

        if(roles.isEmpty())
            throw new IllegalArgumentException("Una persona debe tener un rol");

        for (Rol rol : roles) {
            addRol(rol);
        }

        updateDepartamento(departamento);
    }

    public void addRol(Rol rol) {
        if (this.roles.size() > 1)
            throw new IllegalArgumentException("No se pueden tener más de 2 roles");

        if (this.roles.size() == 1 && this.roles.get(0).equals(Rol.GERENTE) && !rol.equals(Rol.DOCENTE_INVESTIGADOR))
            throw new IllegalArgumentException("El segundo rol del gerente solo puede ser docente investigador");

        if (this.roles.size() == 1 && this.roles.get(0).equals(Rol.DOCENTE_INVESTIGADOR) && !rol.equals(Rol.GERENTE))
            throw new IllegalArgumentException("Solo el gerente puede tener varios roles");

        this.roles.add(rol);
    }


    public void updateDepartamento(Departamento departamento) {
        if (this.roles.size() == 1) {
            Rol rol = this.roles.get(0);
            if ((rol.equals(Rol.GERENTE) || rol.equals(Rol.CONSERJE) || rol.equals(Rol.ESTUDIANTE)) && departamento != null)
                throw new IllegalArgumentException("Los estudiantes, conserjes y gerentes que no sean docentes no pueden estar adscritos a un departamento");
            if((rol.equals(Rol.INVESTIGADOR_CONTRATADO) || rol.equals(Rol.DOCENTE_INVESTIGADOR) ||rol.equals(Rol.TECNICO_LABORATORIO)) && departamento == null)
                throw new IllegalArgumentException("Los investigadores, técnicos y docentes deben estar adscritos a un departamento");
        }
        else {
            if (departamento != null)
                throw new IllegalArgumentException("Un gerente que sea docente debe estar adscrito a un departamento");
        }
    }

    public boolean isAdmin() {
        return roles.contains(Rol.GERENTE);
    }

    public void addNotificacion(String notificacion) {
        notificaciones.add(notificacion);
    }

    public void deleteNotificaciones() {
        notificaciones.clear();
    }

}
