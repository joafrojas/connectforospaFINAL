package com.example.AnuncioConnectForoSpa.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Rol asignado a un usuario")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    @Schema(description = "ID único del rol")
    private Long idRol;

    @Column(name = "nombre_rol")
    @Schema(description = "Nombre del rol")
    private String nombreRol;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @Schema(description = "Usuario asignado al rol")
    private Usuario usuario;
}
