package com.example.adamapbackend.domain;

import com.example.adamapbackend.domain.enums.Departamento;
import com.example.adamapbackend.domain.enums.Rol;
import lombok.Getter;
import lombok.NoArgsConstructor;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Persona {

    @Id
    private String correo;
    private String nombre;
    @Enumerated(EnumType.STRING)
    private Departamento departamento;
    @ElementCollection(targetClass = String.class)
    private List<String> notificaciones;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Rol.class)
    private List<Rol> roles = new ArrayList<>();

    public Persona(String correo, String nombre, Departamento departamento, List<Rol> roles) {
        this.correo = correo;
        this.nombre = nombre;

        if(roles == null || roles.isEmpty())
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
            throw new IllegalArgumentException("El segundo rol del docente investigador solo puede ser gerente");

        if (this.roles.size() == 1 && !this.roles.get(0).equals(Rol.DOCENTE_INVESTIGADOR) && !this.roles.get(0).equals(Rol.GERENTE))
            throw new IllegalArgumentException("Solo el gerente y el docente investigador pueden tener varios roles");

        this.roles.add(rol);
    }

    public void updateRoles(List<Rol> rol) {
        roles.clear();
        rol.forEach(this::addRol);
    }



    public void checkDepartamento(Departamento departamento) {
        if (this.roles.size() == 1) {
            Rol rol = this.roles.get(0);
            if ((rol.equals(Rol.GERENTE) || rol.equals(Rol.CONSERJE) || rol.equals(Rol.ESTUDIANTE)) && departamento != null)
                throw new IllegalArgumentException("Los estudiantes, conserjes y gerentes que no sean docentes no pueden estar adscritos a un departamento");
            if((rol.equals(Rol.INVESTIGADOR_CONTRATADO) || rol.equals(Rol.DOCENTE_INVESTIGADOR) ||rol.equals(Rol.TECNICO_LABORATORIO)) && departamento == null)
                throw new IllegalArgumentException("Los investigadores, técnicos y docentes deben estar adscritos a un departamento");
        }
        else {
            if (departamento == null)
                throw new IllegalArgumentException("Un gerente que sea docente debe estar adscrito a un departamento");
        }
    }

    public void updateDepartamento(Departamento departamento) {
        this.departamento = departamento;
        checkDepartamento(departamento);
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
