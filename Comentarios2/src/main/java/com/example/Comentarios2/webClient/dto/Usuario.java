package com.example.Comentarios2.webClient.dto;

import java.util.List;
import lombok.Data;

@Data
public class Usuario {
    public Long idUsuario;
    public String nombre;
    public String email;
    public String estado;
    public List<RolDTO> roles;
}
