package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.originmobi.pdv.model.GrupoUsuario;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.GrupoUsuarioRepository;

class GrupoUsuarioServiceTest {

    @Mock
    private GrupoUsuarioRepository grupoUsuarioRepository;

    @InjectMocks
    private GrupoUsuarioService grupoUsuarioService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuscaGrupos() {
        List<GrupoUsuario> grupos = new ArrayList<>();
        Usuario usuario = new Usuario();
        when(grupoUsuarioRepository.findByUsuario(any(Usuario.class))).thenReturn(grupos);

        List<GrupoUsuario> found = grupoUsuarioService.buscaGrupos(usuario);

        assertNotNull(found);
        assertEquals(grupos, found);
        verify(grupoUsuarioRepository, times(1)).findByUsuario(any(Usuario.class));
    }

    @Test
    void testLista() {
        List<GrupoUsuario> grupos = new ArrayList<>();
        when(grupoUsuarioRepository.findAll()).thenReturn(grupos);

        List<GrupoUsuario> found = grupoUsuarioService.lista();

        assertNotNull(found);
        assertEquals(grupos, found);
        verify(grupoUsuarioRepository, times(1)).findAll();
    }

    @Test
    void testBuscaGrupo() {
        GrupoUsuario grupo = new GrupoUsuario();
        when(grupoUsuarioRepository.findByCodigo(anyLong())).thenReturn(grupo);

        GrupoUsuario found = grupoUsuarioService.buscaGrupo(1L);

        assertNotNull(found);
        assertEquals(grupo, found);
        verify(grupoUsuarioRepository, times(1)).findByCodigo(anyLong());
    }

    @Test
    void testMergeNovoGrupo() {
        RedirectAttributes attributes = mock(RedirectAttributes.class);
        GrupoUsuario grupo = new GrupoUsuario();

        grupoUsuarioService.merge(grupo, attributes);

        verify(grupoUsuarioRepository, times(1)).save(any(GrupoUsuario.class));
        verify(attributes, times(1)).addFlashAttribute("mensagem", "Grupo adicionado com sucesso");
    }

    @Test
    void testMergeGrupoExistente() {
        RedirectAttributes attributes = mock(RedirectAttributes.class);
        GrupoUsuario grupo = new GrupoUsuario();
        grupo.setCodigo(1L);
        grupo.setNome("Nome do Grupo");
        grupo.setDescricao("Descrição do Grupo");

        grupoUsuarioService.merge(grupo, attributes);

        verify(grupoUsuarioRepository, times(1)).update(eq("Nome do Grupo"), eq("Descrição do Grupo"), eq(1L));
        verify(attributes, times(1)).addFlashAttribute("mensagem", "Grupo atualizado com sucesso");
    }

    @Test
    void testRemoveGrupoComUsuarios() {
        RedirectAttributes attributes = mock(RedirectAttributes.class);
        when(grupoUsuarioRepository.grupoTemUsuaio(anyLong())).thenReturn(1);

        String result = grupoUsuarioService.remove(1L, attributes);

        assertEquals("redirect:/grupousuario/1", result);
        verify(attributes, times(1)).addFlashAttribute("mensagemErro", "Este grupo possue usuários vinculados a ele, verifique");
        verify(grupoUsuarioRepository, never()).deleteById(anyLong());
    }

    @Test
    void testRemoveGrupoSemUsuarios() {
        RedirectAttributes attributes = mock(RedirectAttributes.class);
        when(grupoUsuarioRepository.grupoTemUsuaio(anyLong())).thenReturn(0);

        String result = grupoUsuarioService.remove(1L, attributes);

        assertEquals("redirect:/grupousuario", result);
        verify(grupoUsuarioRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testAddPermissao() {
        when(grupoUsuarioRepository.grupoTemPermissao(anyLong(), anyLong())).thenReturn(0);

        String result = grupoUsuarioService.addPermissao(1L, 1L);

        assertEquals("Permissao adicionada com sucesso", result);
        verify(grupoUsuarioRepository, times(1)).addPermissao(anyLong(), anyLong());
    }

    @Test
    void testAddPermissaoExistente() {
        when(grupoUsuarioRepository.grupoTemPermissao(anyLong(), anyLong())).thenReturn(1);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            grupoUsuarioService.addPermissao(1L, 1L);
        });

        assertEquals("Esta permissão já esta adicionada a este grupo", exception.getMessage());
        verify(grupoUsuarioRepository, never()).addPermissao(anyLong(), anyLong());
    }

    @Test
    void testRemovePermissao() {
        String result = grupoUsuarioService.removePermissao(1L, 1L);

        assertEquals("Permissão removida com sucesso", result);
        verify(grupoUsuarioRepository, times(1)).removePermissao(anyLong(), anyLong());
    }

    @Test
    void testRemovePermissaoErro() {
        doThrow(new RuntimeException("Erro ao tentar remover permissão, chame o suporte")).when(grupoUsuarioRepository).removePermissao(anyLong(), anyLong());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            grupoUsuarioService.removePermissao(1L, 1L);
        });

        assertEquals("Erro ao tentar remover permissão, chame o suporte", exception.getMessage());
        verify(grupoUsuarioRepository, times(1)).removePermissao(anyLong(), anyLong());
    }
}
