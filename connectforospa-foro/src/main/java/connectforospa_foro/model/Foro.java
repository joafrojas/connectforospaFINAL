package connectforospa_foro.model;

import jakarta.persistence.*;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "foro")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Modelo de Foro para la gestión de foros en ConnectForo")
public class Foro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del foro", example = "1")
    private Long idForo;

    @Schema(description = "Nombre del foro", example = "Foro de Tecnología")
    private String nombreForo;

    @Column(name = "id_usuario", nullable = false)
    @Schema(description = "ID del usuario que creó el foro", example = "1")
    private Long idUsuario;
}