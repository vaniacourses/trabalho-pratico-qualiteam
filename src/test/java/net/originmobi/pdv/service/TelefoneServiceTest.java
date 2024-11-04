package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import net.originmobi.pdv.model.Telefone;
import net.originmobi.pdv.repository.TelefoneRepository;

class TelefoneServiceTest {

    @Mock
    private TelefoneRepository telefoneRepository;

    @InjectMocks
    private TelefoneService telefoneService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCadastrar() {
        Telefone telefone = new Telefone();
        when(telefoneRepository.save(any(Telefone.class))).thenReturn(telefone);

        Telefone saved = telefoneService.cadastrar(telefone);

        assertNotNull(saved);
        assertEquals(telefone, saved);
        assertEquals(Date.valueOf(LocalDate.now()), saved.getData_cadastro());
        verify(telefoneRepository, times(1)).save(any(Telefone.class));
    }

    @Test
    void testTelefoneCodigo() {
        Telefone telefone = new Telefone();
        when(telefoneRepository.findByCodigo(anyLong())).thenReturn(telefone);

        Telefone found = telefoneService.telefoneCodigo(1L);

        assertNotNull(found);
        assertEquals(telefone, found);
        verify(telefoneRepository, times(1)).findByCodigo(anyLong());
    }
}
