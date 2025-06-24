package connectforospa_publicacion.webclient;

import lombok.Data;
import java.util.List;

@Data
public class UsuarioDTO {
    private Long idUsuario;
    private String nombreUsuario;
    private String email;
    private String estado;
    private List<RolDTO> roles;
}