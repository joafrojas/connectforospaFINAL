package connectforospa_gestiondecategorias.model;

import jakarta.persistence.*;
import lombok.*;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.Schema;
@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Modelo de Categorías para la gestión de foros en ConnectForo")
public class Categorias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID interno de la categoría", example = "1")
    @Column(name = "id_categoria")
    private Long id;

    @Column(name = "nombre_categoria", nullable = false, unique = true)
    @Schema(description = "Nombre de la categoría", example = "Tecnología")
    private String nombre;

    @Column(name = "descripcion_categoria")
    @Schema(description = "Descripción de la categoría", example = "Discusión sobre tecnología")
    private String descripcion;
}