package net.originmobi.pdv.controller;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.service.GrupoUsuarioService;
import net.originmobi.pdv.service.PessoaService;
import net.originmobi.pdv.service.UsuarioService;

public class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private PessoaService pessoaService;

    @Mock
    private GrupoUsuarioService grupoUsuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
    }

    @Test
    void testForm() throws Exception {
        mockMvc.perform(get("/usuario/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuario/form"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void testLista() throws Exception {
        when(usuarioService.lista()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/usuario"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuario/list"))
                .andExpect(model().attributeExists("usuarios"));
    }

    @Test
    void testCadastrarComErros() throws Exception {
        mockMvc.perform(post("/usuario")
                .flashAttr("usuario", new Usuario()))
                .andExpect(status().isOk())
                .andExpect(view().name("usuario/form"));
    }


    @Test
    void testAddGrupo() throws Exception {
        when(usuarioService.addGrupo(anyLong(), anyLong())).thenReturn("Grupo adicionado com sucesso");

        mockMvc.perform(post("/usuario/addgrupo")
                .param("codigoUsu", "1")
                .param("codigoGru", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Grupo adicionado com sucesso"));
    }

    @Test
    void testRemoveGrupo() throws Exception {
        when(usuarioService.removeGrupo(anyLong(), anyLong())).thenReturn("Grupo removido com sucesso");

        mockMvc.perform(put("/usuario/removegrupo")
                .param("codigoUsu", "1")
                .param("codigoGru", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Grupo removido com sucesso"));
    }

    @Test
    void testTeste() throws Exception {
        mockMvc.perform(get("/usuario/teste"))
                .andExpect(status().isOk())
                .andExpect(content().string("tudo ok"));
    }
}
