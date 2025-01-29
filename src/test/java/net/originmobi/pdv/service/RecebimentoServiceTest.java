package net.originmobi.pdv.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.AdditionalMatchers.or;
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
import org.mockito.Mockito;
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
    private RecebimentoRepository recebimentos;

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
    private RecebimentoParcelaService receParcelas;
    
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
    Recebimento recebimentoSpy = Mockito.spy(new Recebimento());
    
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
    when(recebimentos.save(any(Recebimento.class))).thenAnswer(invocation -> {
        Recebimento r = invocation.getArgument(0);
        r.setCodigo(1L);
        return r;
    });

    // Execução do método
    String codigoRecebimento = recebimentoService.abrirRecebimento(codpes, arrayParcelas);

    // Verificações
    assertNotNull(codigoRecebimento);
    assertEquals("1", codigoRecebimento);

    assertNotNull(recebimentoSpy);
    verify(parcelas, times(2)).busca(anyLong());
    verify(recebimentos, times(1)).save(any(Recebimento.class));
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
        verify(parcela,times(1)).getQuitado();
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
        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimento));

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
        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimento));
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
        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimento));
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
        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimento));
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
        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimento));
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
        verify(cartaoLancamentos, never()).lancamento(anyDouble(),any());
    }
    
    
    
    
    @Test
    public void testReceber_SucessoPassandoIfTipo5parcelas() {
        // Dados de entrada
    	Parcela parcelaMock1 = mock(Parcela.class);
    	Parcela parcelaMock2 = mock(Parcela.class);
    	Parcela parcelaMock3 = mock(Parcela.class);
    	Parcela parcelaMock4 = mock(Parcela.class);
    	Parcela parcelaMock5 = mock(Parcela.class);
    	
    	
    	Double valorRestante = 2.00;
        Long codreceber = 1L;
        Double vlrecebido = 5.0;
        Double vlacrescimo = 0.0;
        Double vldesconto = 0.0;
        Long codtitulo = 1L;
        List<Parcela> listParcelaMock =  new ArrayList<>();
        listParcelaMock.add(parcelaMock1);
        listParcelaMock.add(parcelaMock2);
        listParcelaMock.add(parcelaMock3);
        listParcelaMock.add(parcelaMock4);
        listParcelaMock.add(parcelaMock5);

        // Mocking do recebimento
        Recebimento recebimento = mock(Recebimento.class);
        when(titulos.busca(codtitulo)).thenReturn(Optional.of(tituloMock));
        when(recebimento.getData_processamento()).thenReturn(null);
        when(recebimento.getValor_total()).thenReturn(10.00);
        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimento));
        doNothing().when(recebimento).setTitulo(tituloMock);
        when(receParcelas.parcelasDoReceber(codreceber)).thenReturn(listParcelaMock);
        when(parcelaMock1.getValor_restante()).thenReturn(valorRestante);
        when(parcelaMock2.getValor_restante()).thenReturn(valorRestante);
        when(parcelaMock3.getValor_restante()).thenReturn(valorRestante);
        when(parcelaMock4.getValor_restante()).thenReturn(valorRestante);
        when(parcelaMock5.getValor_restante()).thenReturn(valorRestante);
        when(parcelaMock1.getCodigo()).thenReturn(1L);
        when(parcelaMock2.getCodigo()).thenReturn(2L);
        when(parcelaMock3.getCodigo()).thenReturn(3L);
        when(parcelaMock4.getCodigo()).thenReturn(4L);
        when(parcelaMock5.getCodigo()).thenReturn(5L);
        when(parcelas.receber(eq(1L), anyDouble(), anyDouble(), anyDouble())).thenReturn("ok");
        when(parcelas.receber(eq(2L), anyDouble(), anyDouble(), anyDouble())).thenReturn("ok");
        when(parcelas.receber(eq(3L), anyDouble(), anyDouble(), anyDouble())).thenReturn("ok");
        when(parcelas.receber(eq(4L), anyDouble(), anyDouble(), anyDouble())).thenReturn("ok");
        when(parcelas.receber(eq(5L), anyDouble(), anyDouble(), anyDouble())).thenReturn("ok");
        when(tituloMock.getTipo()).thenReturn(tituloTipoMock);
        when(tituloTipoMock.getSigla()).thenReturn("CARTDEB");
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixaMock));
        when(lancamentos.lancamento(any(CaixaLancamento.class))).thenReturn("ok");
        // Execução do método
        //RuntimeException exception = assertThrows(RuntimeException.class, () -> {
        String resultado = recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);
       // });
        	
        assertEquals("Recebimento realizado com sucesso", resultado);
        verify(cartaoLancamentos, times(1)).lancamento(anyDouble(),any());
        verify(parcelas,times(3)).receber(anyLong(),or(eq(2.0),eq(1.0)),eq(0.00),eq(0.00));
        verify(parcelaMock1,times(1)).getValor_restante();
        verify(parcelaMock2,times(1)).getValor_restante();
        verify(parcelaMock3,times(1)).getValor_restante();
        //verify(parcelaMock4,times(1)).getValor_restante();
        //verify(parcelaMock5,times(1)).getValor_restante();
        verify(parcelaMock1,times(1)).getCodigo();
        verify(parcelaMock2,times(1)).getCodigo();
        verify(parcelaMock3,times(1)).getCodigo();
        //verify(parcelaMock4,times(1)).getCodigo();
        //verify(parcelaMock5,times(1)).getCodigo();

    
    }
    
    
    
    
    
    
    
    
    
    
    
    
    @Test
    public void testReceber_SucessoPassandoIfTipo() {
        // Dados de entrada
    	Double valorRestante1 = 3.00;
    	Double valorRestante2 = 3.00;
        Long codreceber = 1L;
        Double vlrecebido = 5.0;
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
        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimento));
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
        verify(cartaoLancamentos, times(1)).lancamento(anyDouble(),any());
        verify(parcelas,times(2)).receber(anyLong(),anyDouble(),anyDouble(),anyDouble());
    }
    
    @Test
    public void testReceber_SucessoPassandoIfTipoUmaParceladeDuas() {
        // Dados de entrada
    	Double valorRestante1 = 3.00;
    	Double valorRestante2 = 3.00;
        Long codreceber = 1L;
        Double vlrecebido = 5.0;
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
        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimento));
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
        verify(cartaoLancamentos, times(1)).lancamento(anyDouble(),any());
        verify(parcelas,times(2)).receber(anyLong(),anyDouble(),anyDouble(),anyDouble());
        verify(parcela2Mock,times(1)).getValor_restante();
        verify(parcelaMock,times(1)).getValor_restante();
        verify(recebimento,times(1)).setValor_recebido(vlrecebido);
        verify(recebimento,times(1)).setValor_acrescimo(vlacrescimo);
        verify(recebimento,times(1)).setValor_desconto(vldesconto);
        verify(recebimento,times(1)).setData_processamento(any(Timestamp.class));
        verify(recebimentos,times(1)).save(recebimento);
    }
    @Test
    public void testReceber_VlsobraPositiva() {
        // Dados de entrada
        Long codreceber = 1L;
        Double vlrecebido = 200.0;
        Double vlacrescimo = 0.0;
        Double vldesconto = 0.0;
        Long codtitulo = 1L;

        // Mocking do recebimento
        Recebimento recebimento = mock(Recebimento.class);
        when(titulos.busca(codtitulo)).thenReturn(Optional.of(tituloMock));
        when(recebimentos.findById(codreceber)).thenReturn(Optional.of(recebimento));
        when(recebimento.getData_processamento()).thenReturn(null);
        when(recebimento.getValor_total()).thenReturn(200.0);
        doNothing().when(recebimento).setTitulo(tituloMock);

        // Mocking das parcelas
        Parcela parcela = mock(Parcela.class);
        when(parcela.getValor_restante()).thenReturn(150.0); // valor_restante = 150.0
        when(parcela.getCodigo()).thenReturn(1L);

        List<Parcela> listParcelaMock = new ArrayList<>();
        listParcelaMock.add(parcela);

        when(receParcelas.parcelasDoReceber(codreceber)).thenReturn(listParcelaMock);

        // Simular a chamada de receber na parcela com vlquitado = 150.0
        when(parcelas.receber(eq(1L), eq(150.0), eq(0.0), eq(0.0))).thenReturn("ok");

        when(tituloMock.getTipo()).thenReturn(tituloTipoMock);
        when(tituloTipoMock.getSigla()).thenReturn("DIN"); // Não é CARTDEB nem CARTCRED
        when(caixas.caixaAberto()).thenReturn(Optional.of(caixaMock));

        // Simular o lançamento no caixa
        when(lancamentos.lancamento(any(CaixaLancamento.class))).thenReturn("ok");

        // Execução do método
        String resultado = recebimentoService.receber(codreceber, vlrecebido, vlacrescimo, vldesconto, codtitulo);

        // Verificações
        assertEquals("Recebimento realizado com sucesso", resultado);
        verify(parcelas, times(1)).receber(eq(1L), eq(150.0), eq(0.0), eq(0.0));
        verify(recebimento, times(1)).setTitulo(tituloMock);
        verify(recebimentos, times(1)).save(recebimento);
        verify(lancamentos, times(1)).lancamento(any(CaixaLancamento.class));
    }
    
    @Test
    public void testRemover_Sucesso() {
        // Dados de entrada
        Long codigoRecebimento = 1L;

        // Mocking do recebimento
        Recebimento recebimento = mock(Recebimento.class);
        when(recebimento.getData_processamento()).thenReturn(null);
        when(recebimentos.findById(codigoRecebimento)).thenReturn(Optional.of(recebimento));

        // Execução do método
        String resultado = recebimentoService.remover(codigoRecebimento);

        // Verificação
        assertEquals("removido com sucesso", resultado);
        verify(recebimentos, times(1)).deleteById(codigoRecebimento);
    }

    @Test
    public void testRemover_RecebimentoProcessado() {
        // Dados de entrada
        Long codigoRecebimento = 1L;

        // Mocking de recebimento processado
        Recebimento recebimento = mock(Recebimento.class);
        when(recebimento.getData_processamento()).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(recebimentos.findById(codigoRecebimento)).thenReturn(Optional.of(recebimento));

        // Execução do método e verificação da exceção
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.remover(codigoRecebimento);
        });

        // Verificação da mensagem
        assertEquals("Esse recebimento não pode ser removido, pois ele já esta processado", exception.getMessage());
        verify(recebimentos, never()).deleteById(codigoRecebimento);
    }

 

    @Test
    public void testRemover_ErroAoRemover() {
        // Dados de entrada
        Long codigoRecebimento = 1L;

        // Mocking do recebimento
        Recebimento recebimento = mock(Recebimento.class);
        when(recebimento.getData_processamento()).thenReturn(null);
        when(recebimentos.findById(codigoRecebimento)).thenReturn(Optional.of(recebimento));

        // Simulação de erro ao remover
        doThrow(new RuntimeException("Erro de banco de dados")).when(recebimentos).deleteById(codigoRecebimento);

        // Execução do método e verificação da exceção
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.remover(codigoRecebimento);
        });

        // Verificação da mensagem
        assertEquals("Erro ao remover orçamento, chame o suporte", exception.getMessage());
        verify(recebimentos, times(1)).deleteById(codigoRecebimento);
    }


}
