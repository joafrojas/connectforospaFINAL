package connectforospa_usuarios.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID interno del usuario", example = "1")

    private Long idUsuario;

    @Column(unique = true, nullable = false)
    @Schema(description = "Nombre de usuario único", example = "johndoe")
    private String nombreUsuario;

    @Column(unique = true, nullable = false)
    @Schema(description = "Email único del usuario", example = "johndoe@example.com")
    private String email;

    @Schema(description = "Nombre completo del usuario", example = "John Doe")
    private String nombre;

    @Schema(description = "Contraseña del usuario", example = "password123")
    private String password;

    @Schema(description = "Fecha de nacimiento del usuario", example = "1990-01-01")
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    @Schema(description = "Estado del usuario", example = "ACTIVO")
    private String estado;

    @Column(nullable = false)
    @Schema(description = "Fecha de creación del usuario", example = "2023-10-01T12:00:00")
    private LocalDateTime fechaCreacion;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Schema(description = "Roles asociados al usuario")
    private List<Rol> roles;
}
