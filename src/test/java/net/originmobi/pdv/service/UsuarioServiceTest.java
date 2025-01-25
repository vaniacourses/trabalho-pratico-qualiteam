package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import net.originmobi.pdv.model.GrupoUsuario;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.UsuarioRepository;


class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private GrupoUsuarioService grupoUsuarioService;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCadastrarNovoUsuario() {
        Usuario usuario = new Usuario();
        usuario.setUser("novoUsuario");
        usuario.setSenha("senha123");
        usuario.setPessoa(new Pessoa());
        
        when(usuarioRepository.findByUserEquals(anyString())).thenReturn(null);
        when(usuarioRepository.findByPessoaCodigoEquals(anyLong())).thenReturn(null);

        String mensagem = usuarioService.cadastrar(usuario);

        assertEquals("Usuário salvo com sucesso", mensagem);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testCadastrarUsuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setUser("usuarioExistente");
        usuario.setSenha("senha123");
        usuario.setPessoa(new Pessoa());

        when(usuarioRepository.findByUserEquals(anyString())).thenReturn(new Usuario());

        String mensagem = usuarioService.cadastrar(usuario);

        assertEquals("Usuário já existe", mensagem);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }


    @Test
    void testListaUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());

        List<Usuario> usuarios = usuarioService.lista();

        assertEquals(0, usuarios.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    public void testAddGrupo() {
        Usuario usuario = new Usuario();
        usuario.setGrupoUsuario(new ArrayList<>()); // Inicializa a lista de grupos
        GrupoUsuario grupoUsuario = new GrupoUsuario();

        when(usuarioRepository.findByCodigo(anyLong())).thenReturn(usuario);
        when(grupoUsuarioService.buscaGrupo(anyLong())).thenReturn(grupoUsuario);

        String resultado = usuarioService.addGrupo(1L, 1L);

        assertEquals("ok", resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testRemoveGrupo() {
        Usuario usuario = new Usuario();
        GrupoUsuario grupoUsuario = new GrupoUsuario();
        List<GrupoUsuario> grupos = new ArrayList<>();
        grupos.add(grupoUsuario);

        when(usuarioRepository.findByCodigo(anyLong())).thenReturn(usuario);
        when(grupoUsuarioService.buscaGrupo(anyLong())).thenReturn(grupoUsuario);
        when(grupoUsuarioService.buscaGrupos(any(Usuario.class))).thenReturn(grupos);

        String resultado = usuarioService.removeGrupo(1L, 1L);

        assertEquals("ok", resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testBuscaUsuario() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.findByUserEquals(anyString())).thenReturn(usuario);

        Usuario resultado = usuarioService.buscaUsuario("username");

        assertEquals(usuario, resultado);
        verify(usuarioRepository, times(1)).findByUserEquals(anyString());
    }
}
