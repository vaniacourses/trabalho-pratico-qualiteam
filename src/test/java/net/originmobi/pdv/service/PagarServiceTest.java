package net.originmobi.pdv.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.Fornecedor;
import net.originmobi.pdv.model.Pagar;
import net.originmobi.pdv.model.PagarParcela;
import net.originmobi.pdv.model.PagarTipo;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.repository.PagarRepository;
import net.originmobi.pdv.singleton.Aplicacao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@RunWith(MockitoJUnitRunner.class)
public class PagarServiceTest {

    @InjectMocks
    private PagarService pagarService;

    @Mock
    private PagarRepository pagarRepo;

    @Mock
    private PagarParcelaService pagarParcelaServ;

    @Mock
    private FornecedorService fornecedores;

    @Mock
    private CaixaService caixas;

    @Mock
    private UsuarioService usuarios;

    @Mock
    private CaixaLancamentoService lancamentos;

    @Mock
    private Caixa caixa;

    private Fornecedor fornecedor;
    private PagarTipo pagarTipo;
    private Pagar pagar;
    private PagarParcela parcela;
    private Usuario usuario;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        fornecedor = new Fornecedor();
        fornecedor.setCodigo(1L);
        fornecedor.setNome("Fornecedor Teste");

        pagarTipo = new PagarTipo();
        pagarTipo.setCodigo(1L);
        pagarTipo.setDescricao("Despesa");

        pagar = new Pagar();
        pagar.setCodigo(1L);
        pagar.setObservacao("Observação Teste");
        pagar.setValor_total(100.0);
        pagar.setData_cadastro(LocalDate.now());
        pagar.setFornecedor(fornecedor);
        pagar.setTipo(pagarTipo);

        parcela = new PagarParcela();
        parcela.setCodigo(1L);
        parcela.setValor_total(100.0);
        parcela.setValor_restante(100.0);
        parcela.setValor_pago(0.0);
        parcela.setValor_desconto(0.0);
        parcela.setValor_acrescimo(0.0);
        parcela.setQuitado(0);
        parcela.setData_vencimento(LocalDate.now());
        parcela.setPagar(pagar);

        usuario = new Usuario();
        usuario.setCodigo(1L);
        usuario.setUser("usuarioTeste");

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(usuario.getUser());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Aplicacao aplicacaoReal = Aplicacao.getInstancia();
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCadastrarComSucesso() throws Exception {
        when(fornecedores.busca(fornecedor.getCodigo())).thenReturn(Optional.of(fornecedor));
        when(pagarRepo.save(any(Pagar.class))).thenReturn(pagar);
        doNothing().when(pagarParcelaServ).cadastrar(
                anyDouble(), anyDouble(), anyInt(), any(Timestamp.class), any(LocalDate.class), any(Pagar.class));

        String resultado = pagarService.cadastrar(
                fornecedor.getCodigo(),
                pagar.getValor_total(),
                pagar.getObservacao(),
                parcela.getData_vencimento(),
                pagarTipo
        );

        assertEquals("Despesa lançada com sucesso", resultado);

        verify(pagarRepo, times(1)).save(any(Pagar.class));
        verify(pagarParcelaServ, times(1)).cadastrar(
                eq(pagar.getValor_total()), eq(pagar.getValor_total()), eq(0), any(Timestamp.class), eq(parcela.getData_vencimento()), any(Pagar.class));
    }

    @Test(expected = RuntimeException.class)
    public void testCadastrarSalvarPagarException() throws Exception {
        when(fornecedores.busca(fornecedor.getCodigo())).thenReturn(Optional.of(fornecedor));
        when(pagarRepo.save(any(Pagar.class))).thenThrow(new RuntimeException("Database error"));

        pagarService.cadastrar(
                fornecedor.getCodigo(),
                pagar.getValor_total(),
                pagar.getObservacao(),
                parcela.getData_vencimento(),
                pagarTipo
        );
    }

    @Test(expected = RuntimeException.class)
    public void testCadastrarPagarParcelaException() throws Exception {
        when(fornecedores.busca(fornecedor.getCodigo())).thenReturn(Optional.of(fornecedor));
        when(pagarRepo.save(any(Pagar.class))).thenReturn(pagar);
        doThrow(new RuntimeException("Service error")).when(pagarParcelaServ).cadastrar(
                anyDouble(), anyDouble(), anyInt(), any(Timestamp.class), any(LocalDate.class), any(Pagar.class));

        pagarService.cadastrar(
                fornecedor.getCodigo(),
                pagar.getValor_total(),
                pagar.getObservacao(),
                parcela.getData_vencimento(),
                pagarTipo
        );
    }

    @Test
    public void testQuitarComSucesso() throws Exception {
        when(pagarParcelaServ.busca(parcela.getCodigo())).thenReturn(Optional.of(parcela));
        when(usuarios.buscaUsuario(usuario.getUser())).thenReturn(usuario);
        when(caixas.busca(anyLong())).thenReturn(Optional.of(caixa));
        when(caixa.getValor_total()).thenReturn(200.0);
        when(pagarParcelaServ.merger(any(PagarParcela.class))).thenReturn(parcela);
        when(lancamentos.lancamento(any(CaixaLancamento.class))).thenReturn("Lançamento realizado com sucesso");

        String resultado = pagarService.quitar(
                parcela.getCodigo(), 50.0, 5.0, 2.0, 1L);

        assertEquals("Pagamento realizado com sucesso", resultado);

        verify(pagarParcelaServ, times(1)).merger(any(PagarParcela.class));
        verify(lancamentos, times(1)).lancamento(any(CaixaLancamento.class));
    }

    @Test(expected = RuntimeException.class)
    public void testQuitarMergerException() throws Exception {
        when(pagarParcelaServ.busca(parcela.getCodigo())).thenReturn(Optional.of(parcela));
        doThrow(new RuntimeException("Database error")).when(pagarParcelaServ).merger(any(PagarParcela.class));

        pagarService.quitar(
                parcela.getCodigo(), 50.0, 0.0, 0.0, 1L);
    }

    @Test(expected = RuntimeException.class)
    public void testQuitarLancamentoException() throws Exception {
        when(pagarParcelaServ.busca(parcela.getCodigo())).thenReturn(Optional.of(parcela));
        when(pagarParcelaServ.merger(any(PagarParcela.class))).thenReturn(parcela);
        when(usuarios.buscaUsuario(usuario.getUser())).thenReturn(usuario);
        when(caixas.busca(anyLong())).thenReturn(Optional.of(caixa));
        when(caixa.getValor_total()).thenReturn(200.0);
        doThrow(new RuntimeException("Service error")).when(lancamentos).lancamento(any(CaixaLancamento.class));

        pagarService.quitar(
                parcela.getCodigo(), 50.0, 0.0, 0.0, 1L);
    }

    @Test(expected = RuntimeException.class)
    public void testQuitarSaldoInsuficienteException() throws Exception {
        when(pagarParcelaServ.busca(parcela.getCodigo())).thenReturn(Optional.of(parcela));
        when(pagarParcelaServ.merger(any(PagarParcela.class))).thenReturn(parcela);
        when(usuarios.buscaUsuario(usuario.getUser())).thenReturn(usuario);
        when(caixas.busca(anyLong())).thenReturn(Optional.of(caixa));
        when(caixa.getValor_total()).thenReturn(40.0);

        pagarService.quitar(
                parcela.getCodigo(), 50.0, 0.0, 0.0, 1L);
    }

    @Test(expected = RuntimeException.class)
    public void testQuitarValorPagamentoInvalidoException() throws Exception {
        when(pagarParcelaServ.busca(parcela.getCodigo())).thenReturn(Optional.of(parcela));

        double vlPagoInvalido = parcela.getValor_restante() + 10.0;

        pagarService.quitar(
                parcela.getCodigo(), vlPagoInvalido, 0.0, 0.0, 1L);
    }
}

