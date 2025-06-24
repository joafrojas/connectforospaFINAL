package connectforospa_gestiondecategorias.webclient;

import lombok.Data;
import java.util.List;

@Data
public class UsuarioDTO {
    private Long idUsuario;
    private String nombreUsuario;
    private String estado;
    private List<RolDTO> roles;
}