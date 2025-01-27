package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import net.originmobi.pdv.enumerado.TituloTipoEnum;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.Parcela;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Receber;
import net.originmobi.pdv.model.Recebimento;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.TituloTipo;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.RecebimentoRepository;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;
import net.originmobi.pdv.singleton.Aplicacao;
import net.originmobi.pdv.utilitarios.DataAtual;

@ExtendWith(MockitoExtension.class)
class RecebimentoServiceTest {

    @Mock
    private RecebimentoRepository recebimentos;

    @Mock
    private PessoaService pessoas;

    @Mock
    private RecebimentoParcelaService receParcelas;

    @Mock
    private ParcelaService parcelas;

    @Mock
    private CaixaService caixas;

    @Mock
    private UsuarioService usuarios;

    @Mock
    private CaixaLancamentoService lancamentos;

    @Mock
    private TituloService titulos;

    @Mock
    private CartaoLancamentoService cartaoLancamentos;

    @InjectMocks
    private RecebimentoService recebimentoService;
    private Aplicacao aplicacaoMock;
    private Usuario usuarioMock;
    private Pessoa pessoaMock;
    private Parcela parcelaMock;
    private Parcela parcela2Mock;
    private Recebimento recebimentoMock;
    private Titulo tituloMock;
    private TituloTipo tituloTipoloMock;
    private Receber receberMock;
    private Caixa caixaMock;
    private SecurityContextHolder securityContextHolderMock;
    private SecurityContext securityContextMock;
    private Authentication autenticationMock;

    @BeforeEach
    void setUp() {
    	securityContextHolderMock = mock(SecurityContextHolder.class);
    	aplicacaoMock = mock(Aplicacao.class);
    	receberMock = mock(Receber.class);
        usuarioMock = mock(Usuario.class);
        pessoaMock = mock(Pessoa.class);
        parcelaMock = mock(Parcela.class);
        recebimentoMock = mock(Recebimento.class);
        tituloTipoloMock = mock(TituloTipo.class);
        tituloMock = mock(Titulo.class);
        caixaMock = mock(Caixa.class);
        parcela2Mock = mock(Parcela.class);
        

    }

    // ### Testes para abrirRecebimento ###

    @Test
    @DisplayName("abrirRecebimento - Sucesso")
    void abrirRecebimento_Sucesso() {
        Long codpes = 1L;
        String[] arrayParcelas = {"100"};
        when(parcelas.busca(100L)).thenReturn(parcelaMock);
        when(parcelaMock.getQuitado()).thenReturn(2);
        when(parcelaMock.getReceber()).thenReturn(receberMock);
        when(receberMock.getPessoa()).thenReturn(pessoaMock);
        when(pessoaMock.getCodigo()).thenReturn(codpes);
        when(pessoas.buscaPessoa(codpes)).thenReturn(Optional.of(pessoaMock));
        when(recebimentos.save(any(Recebimento.class))).thenAnswer(invocation -> {
            Recebimento r = invocation.getArgument(0);
            r.setCodigo(1L);
            return r;
        });

        String resultado = recebimentoService.abrirRecebimento(codpes, arrayParcelas);

        assertNotNull(resultado);
        assertEquals("1", resultado);
        verify(parcelas, times(1)).busca(100L);
        verify(pessoas, times(1)).buscaPessoa(codpes);
        verify(recebimentos, times(1)).save(any(Recebimento.class));
    }

    @Test
    @DisplayName("abrirRecebimento - Parcela Já Quitada")
    void abrirRecebimento_ParcelaJaQuitada() {

        parcelaMock.setQuitado(1);
        Long codpes = 1L;
        String[] arrayParcelas = {"100"};
        when(parcelas.busca(100L)).thenReturn(parcelaMock);
        when(parcelaMock.getQuitado()).thenReturn(1);
        when(parcelaMock.getCodigo()).thenReturn(100L);
        

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.abrirRecebimento(codpes, arrayParcelas);
        });

        assertEquals("Parcela 100 já esta quitada, verifique.", exception.getMessage());
        verify(parcelas, times(1)).busca(100L);
        verify(pessoas, never()).buscaPessoa(anyLong());
        verify(recebimentos, never()).save(any(Recebimento.class));
    }

    @Test
    @DisplayName("abrirRecebimento - Parcela Não Pertence ao Cliente")
    void abrirRecebimento_ParcelaNaoPertenceAoCliente() {
    	parcelaMock.setQuitado(1);
        Long codpes = 1L;
        Long codpes2 = 2L;
        String[] arrayParcelas = {"100"};
        when(parcelas.busca(100L)).thenReturn(parcelaMock);
        when(parcelaMock.getQuitado()).thenReturn(2);
        when(parcelaMock.getCodigo()).thenReturn(100L);
        when(parcelaMock.getReceber()).thenReturn(receberMock);
        when(receberMock.getPessoa()).thenReturn(pessoaMock);
        when(pessoaMock.getCodigo()).thenReturn(codpes2);
        

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.abrirRecebimento(codpes, arrayParcelas);
        });

        assertEquals("A parcela 100 não pertence ao cliente selecionado", exception.getMessage());
        verify(parcelas, times(1)).busca(100L);
        verify(pessoas, never()).buscaPessoa(anyLong());
        verify(recebimentos, never()).save(any(Recebimento.class));
    }

    @Test
    @DisplayName("abrirRecebimento - Cliente Não Encontrado")
    void abrirRecebimento_ClienteNaoEncontrado() {
    	parcelaMock.setQuitado(1);
        Long codpes = 1L;
        Long codpes2 = 1L;
        String[] arrayParcelas = {"100"};
        when(parcelas.busca(100L)).thenReturn(parcelaMock);
        when(parcelaMock.getQuitado()).thenReturn(2);
        when(parcelaMock.getReceber()).thenReturn(receberMock);
        when(receberMock.getPessoa()).thenReturn(pessoaMock);
        when(pessoaMock.getCodigo()).thenReturn(codpes2);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.abrirRecebimento(codpes, arrayParcelas);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(parcelas, times(1)).busca(100L);
        verify(pessoas, times(1)).buscaPessoa(codpes);
        verify(recebimentos, never()).save(any(Recebimento.class));
    }



    // ### Testes para receber ###

    @Test
    @DisplayName("receber - Sucesso com Título Não Cartão")
    void receber_Sucesso_NaoCartao() {
    	List<Parcela> listaParcela = new ArrayList<>();
    	listaParcela.add(parcela2Mock);
    	listaParcela.add(parcelaMock);
        Long codreceber = 1L;
        Double vlrecebido = 100.0;
        Double vlacrescimo = 10.0;
        Double vldesconto = 5.0;
        Long codtitulo = 1L;

        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimentoMock));
        when(titulos.busca(codtitulo)).thenReturn(Optional.of(tituloMock));
        when(recebimentoMock.getData_processamento()).thenReturn(null);
        when(recebimentoMock.getValor_total()).thenReturn(100.0);
        when(receParcelas.parcelasDoReceber(codreceber)).thenReturn(listaParcela);
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixaMock));
        when(usuarios.buscaUsuario(anyString())).thenReturn(usuarioMock);
        when(parcelaMock.getValor_restante()).thenReturn(30.0);
        when(parcela2Mock.getValor_restante()).thenReturn(15.0);
        when(parcelaMock.getCodigo()).thenReturn(50L);
        when(parcela2Mock.getCodigo()).thenReturn(30L);
        
        

        // Mocking Aplicacao singleton
        try (MockedStatic<Aplicacao> mockedAplicacao = mockStatic(Aplicacao.class)) {
            // Criando um mock para a instância de Aplicacao
            mockedAplicacao.when(Aplicacao::getInstancia).thenReturn(aplicacaoMock);

            // Configurando o comportamento do método getUsuarioAtual()
            //when(aplicacaoMock.getUsuarioAtual()).thenReturn("usuarioAtual");
            when(tituloMock.getTipo()).thenReturn(tituloTipoloMock);
            when(tituloTipoloMock.getSigla()).thenReturn("DIN");

            // Configurando o comportamento do serviço de usuários
            Usuario usuarioAtual = mock(Usuario.class);
            //when(usuarios.buscaUsuario("usuarioAtual")).thenReturn(usuarioAtual);
            String resultado = recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);

            assertEquals("Recebimento realizado com sucesso", resultado);
            verify(recebimentos, times(1)).findById(codreceber);
            verify(titulos, times(1)).busca(codtitulo);
            verify(receParcelas, times(1)).parcelasDoReceber(codreceber);
            verify(parcelas, times(2)).receber(anyLong(), anyDouble(), anyDouble(), anyDouble());
            verify(caixas, times(1)).caixaAberto();
            verify(lancamentos, times(1)).lancamento(any());
            verify(recebimentos, times(1)).save(recebimentoMock);
        }
    }

    @Test
    @DisplayName("receber - Sucesso com Título Cartão")
    void receber_Sucesso_Cartao() {
    	List<Parcela> listaParcela = new ArrayList<>();
    	listaParcela.add(parcela2Mock);
    	listaParcela.add(parcelaMock);
        Long codreceber = 1L;
        Double vlrecebido = 100.0;
        Double vlacrescimo = 10.0;
        Double vldesconto = 5.0;
        Long codtitulo = 1L;

        //tituloMock.setSigla(TituloTipoEnum.CARTDEB.toString());

        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimentoMock));
        when(titulos.busca(codtitulo)).thenReturn(Optional.of(tituloMock));
        when(recebimentoMock.getData_processamento()).thenReturn(null);
        when(recebimentoMock.getValor_total()).thenReturn(100.0);
        when(receParcelas.parcelasDoReceber(codreceber)).thenReturn(listaParcela);
        when(tituloMock.getTipo()).thenReturn(tituloTipoloMock);
        when(tituloTipoloMock.getSigla()).thenReturn("CARTDEB");
        

        // Mocking Aplicacao singleton
        try (MockedStatic<Aplicacao> mockedAplicacao = mockStatic(Aplicacao.class)) {
            // Criando um mock para a instância de Aplicacao
            mockedAplicacao.when(Aplicacao::getInstancia).thenReturn(aplicacaoMock);
            
            String resultado = recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);

            assertEquals("Recebimento realizado com sucesso", resultado);
            verify(recebimentos, times(1)).findById(codreceber);
            verify(titulos, times(1)).busca(codtitulo);
            verify(receParcelas, times(1)).parcelasDoReceber(codreceber);
            verify(parcelas, times(2)).receber(anyLong(), anyDouble(), anyDouble(), anyDouble());
            verify(cartaoLancamentos, times(1)).lancamento(100.0, Optional.of(tituloMock));
            verify(recebimentos, times(1)).save(recebimentoMock);
            verifyNoInteractions(caixas, lancamentos);
        }
    }

    

    
    @Test
    @DisplayName("receber - Valor Recebido Superior ao Permitido")
    void receber_ValorRecebidoSuperior() {
        Long codreceber = 1L;
        Double vlrecebido = 150.0; // Superior ao valor_total de 100.0
        Double vlacrescimo = 10.0;
        Double vldesconto = 5.0;
        Long codtitulo = 1L;

        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimentoMock));
        when(titulos.busca(codtitulo)).thenReturn(Optional.of(tituloMock));
        when(recebimentoMock.getData_processamento()).thenReturn(null);
        when(recebimentoMock.getValor_total()).thenReturn(100.0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);
        });

        assertEquals("Valor de recebimento é superior aos títulos", exception.getMessage());
        verify(recebimentos, times(1)).findById(codreceber);
        verify(titulos, times(1)).busca(codtitulo);
    }

    @Test
    @DisplayName("receber - Recebimento Sem Parcelas")
    void receber_RecebimentoSemParcelas() {
        Long codreceber = 1L;
        Double vlrecebido = 100.0;
        Double vlacrescimo = 10.0;
        Double vldesconto = 5.0;
        Long codtitulo = 1L;

        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimentoMock));
        when(titulos.busca(codtitulo)).thenReturn(Optional.of(tituloMock));
        when(recebimentoMock.getData_processamento()).thenReturn(null);
        when(recebimentoMock.getValor_total()).thenReturn(100.0);
        when(receParcelas.parcelasDoReceber(codreceber)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);
        });

        assertEquals("Recebimento não possue parcelas", exception.getMessage());
        verify(receParcelas, times(1)).parcelasDoReceber(codreceber);
    }

    
    
    
    // ### Testes para remover ###

    @Test
    @DisplayName("remover - Sucesso")
    void remover_Sucesso() {
        Long codigo = 1L;
        when(recebimentos.findById(codigo)).thenReturn(Optional.of(recebimentoMock));
        when(recebimentoMock.getData_processamento()).thenReturn(null);

        String resultado = recebimentoService.remover(codigo);

        assertEquals("removido com sucesso", resultado);
        verify(recebimentos, times(1)).findById(codigo);
        verify(recebimentos, times(1)).deleteById(codigo);
    }

    @Test
    @DisplayName("remover - Recebimento Já Processado")
    void remover_RecebimentoJaProcessado() {
        Long codigo = 1L;
        Timestamp dataProcessamento = new Timestamp(System.currentTimeMillis());
        when(recebimentos.findById(codigo)).thenReturn(Optional.of(recebimentoMock));
        when(recebimentoMock.getData_processamento()).thenReturn(dataProcessamento);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.remover(codigo);
        });

        assertEquals("Esse recebimento não pode ser removido, pois ele já esta processado", exception.getMessage());
        verify(recebimentos, times(1)).findById(codigo);
        verify(recebimentos, never()).deleteById(codigo);
    }

    @Test
    @DisplayName("remover - Erro ao Remover Recebimento")
    void remover_ErroAoRemoverRecebimento() {
        Long codigo = 1L;
        when(recebimentos.findById(codigo)).thenReturn(Optional.of(recebimentoMock));
        when(recebimentoMock.getData_processamento()).thenReturn(null);
        doThrow(new RuntimeException("DB Error")).when(recebimentos).deleteById(codigo);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.remover(codigo);
        });

        assertEquals("Erro ao remover orçamento, chame o suporte", exception.getMessage());
        verify(recebimentos, times(1)).findById(codigo);
        verify(recebimentos, times(1)).deleteById(codigo);
    }
}
