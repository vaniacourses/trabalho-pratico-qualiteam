package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import net.originmobi.pdv.model.Cidade;
import net.originmobi.pdv.repository.CidadeRepository;

class CidadeServiceTest {

    @Mock
    private CidadeRepository cidadeRepository;

    @InjectMocks
    private CidadeService cidadeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLista() {
        List<Cidade> cidades = new ArrayList<>();
        when(cidadeRepository.findAll()).thenReturn(cidades);

        List<Cidade> found = cidadeService.lista();

        assertNotNull(found);
        assertEquals(cidades, found);
        verify(cidadeRepository, times(1)).findAll();
    }

    @Test
    void testBusca() {
        Cidade cidade = new Cidade();
        when(cidadeRepository.findById(anyLong())).thenReturn(Optional.of(cidade));

        Optional<Cidade> found = cidadeService.busca(1L);

        assertTrue(found.isPresent());
        assertEquals(cidade, found.get());
        verify(cidadeRepository, times(1)).findById(anyLong());
    }

    @Test
    void testBuscaNaoEncontrada() {
        when(cidadeRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Cidade> found = cidadeService.busca(1L);

        assertTrue(found.isEmpty());
        verify(cidadeRepository, times(1)).findById(anyLong());
    }
}
