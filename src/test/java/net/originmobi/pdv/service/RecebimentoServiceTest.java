package net.originmobi.pdv.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.originmobi.pdv.controller.TituloService;
import net.originmobi.pdv.enumerado.TituloTipoEnum;
import net.originmobi.pdv.enumerado.caixa.EstiloLancamento;
import net.originmobi.pdv.enumerado.caixa.TipoLancamento;
import net.originmobi.pdv.model.*;
import net.originmobi.pdv.repository.RecebimentoRepository;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;
import net.originmobi.pdv.singleton.Aplicacao;
import net.originmobi.pdv.utilitarios.DataAtual;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.errorprone.annotations.DoNotCall;

public class RecebimentoServiceTest {

    @InjectMocks
    private RecebimentoService recebimentoService;

    @Mock
    private RecebimentoParcelaService receParcelas;
    @Mock
    private RecebimentoRepository recebimentoRepository;

    @Mock
    private PessoaService pessoas;

    @Mock
    private ParcelaService parcelas;

    @Mock
    private CaixaService caixas;

    @Mock
    private UsuarioService usuarios;

    @Mock
    private CartaoLancamentoService cartaoLancamentos;

    @Mock
    private TituloService titulos;
    
    @Mock
    private CaixaLancamentoService lancamentos;

    @Mock
    private RecebimentoParcelaService recebimentos;
    
    @Mock
    private Receber receberMock;
    
    @Mock
    private TituloTipo tituloTipoMock;
    @Mock
    private Aplicacao aplicacaoMock;
    @Mock
    private Usuario usuarioMock;
    @Mock
    private Pessoa pessoaMock;
    @Mock
    private Parcela parcelaMock;
    @Mock
    private Parcela parcela2Mock;
    @Mock
    private Recebimento recebimentoMock;
    @Mock
    private Titulo tituloMock;
    @Mock
    private Caixa caixaMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("UsuarioAutenticado");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Aplicacao aplicacaoReal = Aplicacao.getInstancia();
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

    when(parcelas.busca(1L)).thenReturn(parcela1);
    when(parcelas.busca(2L)).thenReturn(parcela2);

    // Mockando pessoa
    Pessoa pessoa = mock(Pessoa.class);
    when(pessoas.buscaPessoa(codpes)).thenReturn(Optional.of(pessoa));

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
    verify(parcelas, times(2)).busca(anyLong());
    verify(recebimentoRepository, times(1)).save(any(Recebimento.class));
}

    @Test
    public void testAbrirRecebimento_ParcelaQuitada() {
        // Dados de entrada
        Long codpes = 1L;
        String[] arrayParcelas = {"1"};

        // Mocking de parcela quitada
        Parcela parcela = mock(Parcela.class);
        when(parcela.getQuitado()).thenReturn(1);
        when(parcela.getCodigo()).thenReturn(1L);

        // Mocking do repositório ou serviço de parcelas
        when(parcelas.busca(1L)).thenReturn(parcela);

        // Dependência do recebimentoService

        // Execução do método e verificação da exceção
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.abrirRecebimento(codpes, arrayParcelas);
        });

        // Verificação da mensagem
        assertEquals("Parcela " + parcela.getCodigo() + " já esta quitada, verifique.", exception.getMessage());
    }

    @Test
    public void testAbrirRecebimento_ParcelaDeOutroCliente() {
        // Dados de entrada
        Long codpes = 1L;
        String[] arrayParcelas = {"1"};

        // Mocking de parcela de outro cliente
        Parcela parcela = mock(Parcela.class);	
        Pessoa outraPessoa = mock(Pessoa.class);
        when(outraPessoa.getCodigo()).thenReturn(5L);
        when(parcela.getQuitado()).thenReturn(0);
        when(parcela.getReceber()).thenReturn(receberMock);
        when(receberMock.getPessoa()).thenReturn(pessoaMock);
        when(pessoaMock.getCodigo()).thenReturn(20L);
        when(parcelas.busca(1L)).thenReturn(parcela);

        // Execução do método
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.abrirRecebimento(codpes, arrayParcelas);
        });

        // Verificação da mensagem
        assertEquals("A parcela " + parcela.getCodigo() + " não pertence ao cliente selecionado", exception.getMessage());
    }
    @Test
    public void testAbrirRecebimento_ClienteNaoEncontrado() {
        // Dados de entrada
        Long codpes = 1L;
        String[] arrayParcelas = {"1"};

        // Mocking de parcela de outro cliente
        Parcela parcela = mock(Parcela.class);	
        Pessoa outraPessoa = mock(Pessoa.class);
        when(outraPessoa.getCodigo()).thenReturn(5L);
        when(parcela.getQuitado()).thenReturn(0);
        when(parcela.getReceber()).thenReturn(receberMock);
        when(receberMock.getPessoa()).thenReturn(pessoaMock);
        when(pessoaMock.getCodigo()).thenReturn(codpes);
        when(parcelas.busca(1L)).thenReturn(parcela);
        when(pessoas.buscaPessoa(codpes)).thenReturn(Optional.empty());
        // Execução do método
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.abrirRecebimento(codpes, arrayParcelas);
        });

        // Verificação da mensagem
        assertEquals("Cliente não encontrado", exception.getMessage());
    }
    
    
    @Test
    public void testReceber_Success() {
        // Dados de entrada
        Long codreceber = 1L;
        Double vlrecebido = 100.0;
        Double vlacrescimo = 0.0;
        Double vldesconto = 0.0;
        Long codtitulo = 1L;

        // Mocking do recebimento
        Recebimento recebimento = mock(Recebimento.class);
        when(recebimento.getData_processamento()).thenReturn(null);
        when(recebimento.getValor_total()).thenReturn(100.0);
        when(recebimentoRepository.findById(codreceber)).thenReturn(Optional.of(recebimento));

        // Mocking do título
        Titulo titulo = mock(Titulo.class);
        when(titulo.getTipo()).thenReturn(tituloTipoMock);
        when(titulos.busca(codtitulo)).thenReturn(Optional.of(titulo));

        // Mocking das parcelas
        List<Parcela> listParcelas = new ArrayList<>();
        Parcela parcela = mock(Parcela.class);
        when(parcela.getValor_restante()).thenReturn(100.0);
        listParcelas.add(parcela);
        when(recebimentos.parcelasDoReceber(codreceber)).thenReturn(listParcelas);

        // Mocking do caixa
        Caixa caixa = mock(Caixa.class);
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixa));

        // Mocking do usuário
        Usuario usuario = mock(Usuario.class);
        when(usuarios.buscaUsuario(anyString())).thenReturn(usuario);
        
        when(tituloMock.getTipo()).thenReturn(tituloTipoMock);
        when(tituloTipoMock.getSigla()).thenReturn("CARTDEB");
        doNothing().when(cartaoLancamentos).lancamento(vlrecebido, Optional.of(titulo));


        // Execução do método

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        	recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);
        });
        // Verificações
        assertEquals("Recebimento não possue parcelas", exception.getMessage());
    }
    
    

    @Test
    public void testReceber_TituloInvalido() {
        // Dados de entrada
        Long codreceber = 1L;
        Double vlrecebido = 100.0;
        Double vlacrescimo = 0.0;
        Double vldesconto = 0.0;
        Long codtitulo = 0L;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        	recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);
        });
        assertEquals("Selecione um título para realizar o recebimento", exception.getMessage());

        
    }

    @Test
    public void testReceber_RecebimentoFechado() {
        // Dados de entrada
        Long codreceber = 1L;
        Double vlrecebido = 100.0;
        Double vlacrescimo = 0.0;
        Double vldesconto = 0.0;
        Long codtitulo = 1L;
        Timestamp dataProcessamento = new Timestamp(System.currentTimeMillis());
        // Mocking do recebimento já fechado
        Recebimento recebimento = mock(Recebimento.class);
        when(recebimento.getData_processamento()).thenReturn(dataProcessamento);
        when(recebimentoRepository.findById(codreceber)).thenReturn(Optional.of(recebimento));

        // Execução do método
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        	recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);
        });
        assertEquals("Recebimento já esta fechado", exception.getMessage());
    }
    

    @Test
    public void testReceber_ValorSuperior() {
        // Dados de entrada
        Long codreceber = 1L;
        Double vlrecebido = 200.0;
        Double vlacrescimo = 0.0;
        Double vldesconto = 0.0;
        Long codtitulo = 1L;

        // Mocking do recebimento
        Recebimento recebimento = mock(Recebimento.class);
        when(titulos.busca(codtitulo)).thenReturn(Optional.of(tituloMock));
        when(recebimento.getData_processamento()).thenReturn(null);
        when(recebimento.getValor_total()).thenReturn(100.0);
        when(recebimentoRepository.findById(codreceber)).thenReturn(Optional.of(recebimento));
        doNothing().when(recebimento).setTitulo(tituloMock);

        // Execução do método
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        	recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);
        });
        assertEquals("Valor de recebimento é superior aos títulos", exception.getMessage());
        verify(recebimento,times(1)).getValor_total();
    }
    @Test
    public void testReceber_SemParcelas() {
        // Dados de entrada
        Long codreceber = 1L;
        Double vlrecebido = 100.0;
        Double vlacrescimo = 0.0;
        Double vldesconto = 0.0;
        Long codtitulo = 1L;
        List<Parcela> listParcelaMock =  new ArrayList<>();
        //listParcelaMock.add(parcela2Mock);
        //listParcelaMock.add(parcelaMock);
        // Mocking do recebimento
        Recebimento recebimento = mock(Recebimento.class);
        when(titulos.busca(codtitulo)).thenReturn(Optional.of(tituloMock));
        when(recebimento.getData_processamento()).thenReturn(null);
        when(recebimento.getValor_total()).thenReturn(100.0);
        when(recebimentoRepository.findById(codreceber)).thenReturn(Optional.of(recebimento));
        doNothing().when(recebimento).setTitulo(tituloMock);
        when(receParcelas.parcelasDoReceber(codreceber)).thenReturn(listParcelaMock);
        
        

        // Execução do método
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        	recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);
        });
        assertEquals("Recebimento não possue parcelas", exception.getMessage());
    }


    @Test
    public void testReceber_ValorRecebimentoInvalido() {
        // Dados de entrada
        Long codreceber = 1L;
        Double vlrecebido = 0.0;
        Double vlacrescimo = 0.0;
        Double vldesconto = 0.0;
        Long codtitulo = 1L;
        List<Parcela> listParcelaMock =  new ArrayList<>();
        listParcelaMock.add(parcela2Mock);
        listParcelaMock.add(parcelaMock);
        // Mocking do recebimento
        Recebimento recebimento = mock(Recebimento.class);
        when(titulos.busca(codtitulo)).thenReturn(Optional.of(tituloMock));
        when(recebimento.getData_processamento()).thenReturn(null);
        when(recebimento.getValor_total()).thenReturn(100.0);
        when(recebimentoRepository.findById(codreceber)).thenReturn(Optional.of(recebimento));
        doNothing().when(recebimento).setTitulo(tituloMock);
        when(receParcelas.parcelasDoReceber(codreceber)).thenReturn(listParcelaMock);
        
        

        // Execução do método
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        	recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);
        });
        assertEquals("Valor de recebimento inválido", exception.getMessage());
    }
    
    @Test
    public void testReceber_Sucesso() {
        // Dados de entrada
    	Double valorRestante1 = 150.00;
    	Double valorRestante2 = 51.00;
        Long codreceber = 1L;
        Double vlrecebido = 200.0;
        Double vlacrescimo = 0.0;
        Double vldesconto = 0.0;
        Long codtitulo = 1L;
        List<Parcela> listParcelaMock =  new ArrayList<>();
        listParcelaMock.add(parcela2Mock);
        listParcelaMock.add(parcelaMock);
        // Mocking do recebimento
        Recebimento recebimento = mock(Recebimento.class);
        when(titulos.busca(codtitulo)).thenReturn(Optional.of(tituloMock));
        when(recebimento.getData_processamento()).thenReturn(null);
        when(recebimento.getValor_total()).thenReturn(1000.0);
        when(recebimentoRepository.findById(codreceber)).thenReturn(Optional.of(recebimento));
        doNothing().when(recebimento).setTitulo(tituloMock);
        when(receParcelas.parcelasDoReceber(codreceber)).thenReturn(listParcelaMock);
        when(parcela2Mock.getValor_restante()).thenReturn(valorRestante1);
        when(parcelaMock.getValor_restante()).thenReturn(valorRestante2);
        when(parcela2Mock.getCodigo()).thenReturn(5L);
        when(parcelaMock.getCodigo()).thenReturn(10L);
        when(parcelas.receber(anyLong(), anyDouble(), anyDouble(), anyDouble())).thenReturn("ok");
        when(tituloMock.getTipo()).thenReturn(tituloTipoMock);
        when(tituloTipoMock.getSigla()).thenReturn("DIN");
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixaMock));
        when(lancamentos.lancamento(any(CaixaLancamento.class))).thenReturn("ok");
        // Execução do método
        //RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        String resultado = recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);
       // });
        	
        assertEquals("Recebimento realizado com sucesso", resultado);
    }
    
    @Test
    public void testReceber_SucessoPassandoIfTipo() {
        // Dados de entrada
    	Double valorRestante1 = 150.00;
    	Double valorRestante2 = 51.00;
        Long codreceber = 1L;
        Double vlrecebido = 200.0;
        Double vlacrescimo = 0.0;
        Double vldesconto = 0.0;
        Long codtitulo = 1L;
        List<Parcela> listParcelaMock =  new ArrayList<>();
        listParcelaMock.add(parcela2Mock);
        listParcelaMock.add(parcelaMock);
        // Mocking do recebimento
        Recebimento recebimento = mock(Recebimento.class);
        when(titulos.busca(codtitulo)).thenReturn(Optional.of(tituloMock));
        when(recebimento.getData_processamento()).thenReturn(null);
        when(recebimento.getValor_total()).thenReturn(1000.0);
        when(recebimentoRepository.findById(codreceber)).thenReturn(Optional.of(recebimento));
        doNothing().when(recebimento).setTitulo(tituloMock);
        when(receParcelas.parcelasDoReceber(codreceber)).thenReturn(listParcelaMock);
        when(parcela2Mock.getValor_restante()).thenReturn(valorRestante1);
        when(parcelaMock.getValor_restante()).thenReturn(valorRestante2);
        when(parcela2Mock.getCodigo()).thenReturn(5L);
        when(parcelaMock.getCodigo()).thenReturn(10L);
        when(parcelas.receber(anyLong(), anyDouble(), anyDouble(), anyDouble())).thenReturn("ok");
        when(tituloMock.getTipo()).thenReturn(tituloTipoMock);
        when(tituloTipoMock.getSigla()).thenReturn("CARTDEB");
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixaMock));
        when(lancamentos.lancamento(any(CaixaLancamento.class))).thenReturn("ok");
        // Execução do método
        //RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        String resultado = recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);
       // });
        	
        assertEquals("Recebimento realizado com sucesso", resultado);
    }


}
