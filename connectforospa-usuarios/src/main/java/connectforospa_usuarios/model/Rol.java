package connectforospa_usuarios.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Rol asignado a un usuario")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    @Schema(description = "ID interno del rol", example = "1")
    private Long idRol;

    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String nombreRol;

    @Schema(description = "Nombre de usuario asociado al rol", example = "johndoe")
    private String nombreUsuario;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @JsonBackReference
    @Schema(description = "Usuario al que pertenece el rol")
    private Usuarios usuario;
}
