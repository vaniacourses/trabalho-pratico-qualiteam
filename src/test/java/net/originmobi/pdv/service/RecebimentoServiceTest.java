package net.originmobi.pdv.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.originmobi.pdv.model.Parcela;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Receber;
import net.originmobi.pdv.model.Recebimento;
import net.originmobi.pdv.repository.RecebimentoRepository;
import net.originmobi.pdv.service.ParcelaService;
import net.originmobi.pdv.service.PessoaService;
import net.originmobi.pdv.service.RecebimentoService;
import net.originmobi.pdv.utilitarios.DataAtual;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RecebimentoServiceTest {

    @InjectMocks
    private RecebimentoService recebimentoService;

    @Mock
    private RecebimentoRepository recebimentoRepository;

    @Mock
    private PessoaService pessoaService;

    @Mock
    private ParcelaService parcelaService;
    
    @Mock
    private Receber receberMock;

    @Mock
    private Pessoa pessoaMock;
    
    @Mock 
    private Parcela parcelaMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
public void testAbrirRecebimento_Success() {
    // Dados de entrada
    Long codpes = 1L;
    String[] arrayParcelas = {"1", "2"};

    // Mockando parcelas
    Parcela parcela1 = mock(Parcela.class);
    Parcela parcela2 = mock(Parcela.class);
    Pessoa pessoaParcela = mock(Pessoa.class);

    when(parcela1.getQuitado()).thenReturn(0);
    when(parcela1.getCodigo()).thenReturn(1L);
    when(parcela1.getValor_restante()).thenReturn(100.0);
    when(parcela1.getReceber()).thenReturn(mock(Receber.class));
    when(parcela1.getReceber().getPessoa()).thenReturn(pessoaParcela);

    when(parcela2.getQuitado()).thenReturn(0);
    when(parcela2.getCodigo()).thenReturn(2L);
    when(parcela2.getValor_restante()).thenReturn(200.0);
    when(parcela2.getReceber()).thenReturn(mock(Receber.class));
    when(parcela2.getReceber().getPessoa()).thenReturn(pessoaParcela);

    when(pessoaParcela.getCodigo()).thenReturn(codpes);

    when(parcelaService.busca(1L)).thenReturn(parcela1);
    when(parcelaService.busca(2L)).thenReturn(parcela2);

    // Mockando pessoa
    Pessoa pessoa = mock(Pessoa.class);
    when(pessoaService.buscaPessoa(codpes)).thenReturn(Optional.of(pessoa));

    // Mockando repositório
    Recebimento recebimentoMock = mock(Recebimento.class);
    when(recebimentoMock.getCodigo()).thenReturn(1L);
    when(recebimentoRepository.save(any(Recebimento.class))).thenAnswer(invocation -> {
        Recebimento r = invocation.getArgument(0);
        r.setCodigo(1L);
        return r;
    });

    // Execução do método
    String codigoRecebimento = recebimentoService.abrirRecebimento(codpes, arrayParcelas);

    // Verificações
    assertNotNull(codigoRecebimento);
    assertEquals("1", codigoRecebimento);
    verify(parcelaService, times(2)).busca(anyLong());
    verify(recebimentoRepository, times(1)).save(any(Recebimento.class));
}

    @Test(expected = RuntimeException.class)
    public void testAbrirRecebimento_ParcelaQuitada() {
        // Dados de entrada
        Long codpes = 1L;
        String[] arrayParcelas = {"1"};

        // Mocking de parcela quitada
        Parcela parcela = mock(Parcela.class);
        when(parcela.getQuitado()).thenReturn(1);
        when(parcelaService.busca(1L)).thenReturn(parcela);

        // Execução do método
        recebimentoService.abrirRecebimento(codpes, arrayParcelas);
    }

    @Test(expected = RuntimeException.class)
    public void testAbrirRecebimento_ParcelaDeOutroCliente() {
        // Dados de entrada
        Long codpes = 1L;
        String[] arrayParcelas = {"1"};

        // Mocking de parcela de outro cliente
        Parcela parcela = mock(Parcela.class);
        Pessoa outraPessoa = mock(Pessoa.class);
        when(outraPessoa.getCodigo()).thenReturn(2L);
        when(parcela.getQuitado()).thenReturn(0);
        when(parcela.getReceber()).thenReturn(receberMock);
        when(parcelaService.busca(1L)).thenReturn(parcela);

        // Execução do método
        recebimentoService.abrirRecebimento(codpes, arrayParcelas);
    }
}
