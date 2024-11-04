package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import net.originmobi.pdv.model.Endereco;
import net.originmobi.pdv.repository.EnderecoRepository;

class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private EnderecoService enderecoService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLista() {
        List<Endereco> enderecos = new ArrayList<>();
        when(enderecoRepository.findAll()).thenReturn(enderecos);

        List<Endereco> found = enderecoService.lista();

        assertNotNull(found);
        assertEquals(enderecos, found);
        verify(enderecoRepository, times(1)).findAll();
    }

    @Test
    void testCadastrar() {
        Endereco endereco = new Endereco();
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

        Endereco saved = enderecoService.cadastrar(endereco);

        assertNotNull(saved);
        assertEquals(endereco, saved);
        assertEquals(Date.valueOf(LocalDate.now()), saved.getData_cadastro());
        verify(enderecoRepository, times(1)).save(any(Endereco.class));
    }

    @Test
    void testEnderecoCodigo() {
        Endereco endereco = new Endereco();
        when(enderecoRepository.findByCodigo(anyLong())).thenReturn(endereco);

        Endereco found = enderecoService.enderecoCodigo(1L);

        assertNotNull(found);
        assertEquals(endereco, found);
        verify(enderecoRepository, times(1)).findByCodigo(anyLong());
    }

    @Test
    void testUpdate() {
        doNothing().when(enderecoRepository).update(anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString(), anyString());

        enderecoService.update(1L, 1L, "Rua", "Bairro", "Numero", "CEP", "Referencia");

        verify(enderecoRepository, times(1)).update(anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString(), anyString());
    }
}
