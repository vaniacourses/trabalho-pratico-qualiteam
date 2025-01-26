package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import net.originmobi.pdv.enumerado.EntradaSaida;
import net.originmobi.pdv.enumerado.TituloTipoEnum;
import net.originmobi.pdv.enumerado.VendaSituacao;
import net.originmobi.pdv.filter.VendaFilter;
import net.originmobi.pdv.model.Caixa;
import net.originmobi.pdv.model.VendaProduto;
import net.originmobi.pdv.model.CaixaLancamento;
import net.originmobi.pdv.model.PagamentoTipo;
import net.originmobi.pdv.model.Receber;
import net.originmobi.pdv.model.TituloTipo;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.Usuario;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Venda;
import net.originmobi.pdv.repository.VendaRepository;
import net.originmobi.pdv.service.cartao.CartaoLancamentoService;
import net.originmobi.pdv.singleton.Aplicacao;
import net.originmobi.pdv.utilitarios.DataAtual;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendas;

    @Mock
    private UsuarioService usuarios;

    @Mock
    private VendaProdutoService vendaProdutos;

    @Mock
    private PagamentoTipoService formaPagamentos;

    @Mock
    private CaixaService caixas;

    @Mock
    private ReceberService receberServ;

    @Mock
    private ParcelaService parcelas;

    @Mock
    private CaixaLancamentoService lancamentos;

    @Mock
    private TituloService tituloService;

    @Mock
    private CartaoLancamentoService cartaoLancamento;

    @Mock
    private ProdutoService produtos;
    
    @Mock
    private DataAtual dataAtual;
    
    @Mock
    private Receber receber;
    
 

    @InjectMocks
    private VendaService vendaService;

    private Venda vendaAberta;
    private Venda vendaFechada;
    private Usuario usuarioMock;
    private Aplicacao aplicacaoMock;
    private Pessoa pessoaMock;
    private Venda vendaMock;

    @BeforeEach
    void setUp() {
        // Inicializa mocks e objetos necessários
        MockitoAnnotations.openMocks(this);
        vendaMock = mock(Venda.class);
        usuarioMock = new Usuario();
        usuarioMock.setCodigo(1L);
        usuarioMock.setUser("Usuário Teste");

        aplicacaoMock = mock(Aplicacao.class);
        vendaAberta = new Venda();
        vendaAberta.setCodigo(1L);
        vendaAberta.setSituacao(VendaSituacao.ABERTA);
        vendaAberta.setValor_produtos(100.00);
        vendaAberta.setUsuario(usuarioMock);

        vendaFechada = new Venda();
        vendaFechada.setCodigo(2L);
        vendaFechada.setSituacao(VendaSituacao.FECHADA);
        vendaFechada.setValor_produtos(200.00);
        vendaFechada.setUsuario(usuarioMock);
        vendaFechada = new Venda();
 
    }
    @Test
    @DisplayName("1 teste do primeiro metodo")
    void abreVenda1() {
    	pessoaMock = mock(Pessoa.class);
        // Criando um mock para a classe Aplicacao usando MockedStatic
        try (MockedStatic<Aplicacao> mockedAplicacao = mockStatic(Aplicacao.class)) {
            // Criando um mock para a instância de Aplicacao
            Aplicacao aplicacaoMock = mock(Aplicacao.class);
            mockedAplicacao.when(Aplicacao::getInstancia).thenReturn(aplicacaoMock);

            // Configurando o comportamento do método getUsuarioAtual()
            when(aplicacaoMock.getUsuarioAtual()).thenReturn("usuarioAtual");

            // Configurando o comportamento do serviço de usuários
            Usuario usuarioAtual = mock(Usuario.class);
            when(usuarios.buscaUsuario("usuarioAtual")).thenReturn(usuarioAtual);

            // Configurando o comportamento do mock de Venda
            when(vendaMock.getCodigo()).thenReturn(null, 1L, 1L);            // Os métodos set_* não precisam ser mockados, pois eles são void e não retornam nada
            // No entanto, se forem utilizados para lógica interna, considere espiar o objeto

            // Configurando o comportamento do repository para salvar a venda
            when(vendas.save(any(Venda.class))).thenAnswer(invocation -> {
                Venda vendaSalva = invocation.getArgument(0);
                vendaSalva.setCodigo(1L); // Simulando a geração de um ID
                return vendaSalva;
            });

            // Chamando o método que está sendo testado
            Long codigoRetornado = vendaService.abreVenda(vendaMock);

            // Verificações:

            // 1. Verificar se Aplicacao.getInstancia() foi chamado
            mockedAplicacao.verify(Aplicacao::getInstancia, times(1));

            // 2. Verificar se usuarios.buscaUsuario() foi chamado com o argumento correto
            verify(usuarios, times(1)).buscaUsuario("usuarioAtual");

            // 3. Verificar se vendas.save() foi chamado uma vez
            verify(vendas, times(1)).save(vendaMock);

            // 4. Verificar se o código retornado não é nulo
            assertNotNull(codigoRetornado, "Deveria retornar algum ID (mesmo que seja o do objeto salvo)");

            // 5. Verificar se o código retornado é o esperado
            assertEquals(1L, codigoRetornado, "O ID retornado deveria ser 1L");

            // 6. Verificar se os setters foram chamados corretamente (opcional)
            // Como os métodos set_* são void e não retornam valores, você pode usar verify para checar se foram chamados
            verify(vendaMock, times(1)).setData_cadastro(any(Timestamp.class));
            verify(vendaMock, times(1)).setSituacao(VendaSituacao.ABERTA);
            verify(vendaMock, times(1)).setUsuario(usuarioAtual);
            verify(vendaMock, times(1)).setValor_produtos(0.00);
        }
    }
    @Test
    @DisplayName("Teste do método busca com código fornecido")
    void testBusca_comCodigo() {
        // Cenário
        VendaFilter filter = new VendaFilter();
        filter.setCodigo(1L); // Definindo o código para buscar

        String situacao = "ABERTA"; // Pode ser "ABERTA" ou "FECHADA", mas neste caso, o código tem prioridade
        Pageable pageable = PageRequest.of(0, 10); // Página 0, 10 itens por página

        // Criando uma lista de vendas para retornar
        List<Venda> listaVendas = Arrays.asList(vendaAberta);
        Page<Venda> pageVendas = new PageImpl<>(listaVendas, pageable, listaVendas.size());

        // Configurando o comportamento do mock do repositório
        when(vendas.findByCodigo(1L, pageable)).thenReturn(pageVendas);

        // Ação
        Page<Venda> resultado = vendaService.busca(filter, situacao, pageable);

        // Verificações
        // 1. Verificar se findByCodigo foi chamado com os parâmetros corretos
        verify(vendas, times(1)).findByCodigo(1L, pageable);

        // 2. Verificar se findBySituacaoEquals NÃO foi chamado
        verify(vendas, never()).findBySituacaoEquals(any(VendaSituacao.class), any(Pageable.class));

        // 3. Asserção sobre o resultado
        assertNotNull(resultado, "O resultado não deveria ser nulo");
        assertEquals(1, resultado.getTotalElements(), "Deveria retornar uma venda");
        assertEquals(VendaSituacao.ABERTA, resultado.getContent().get(0).getSituacao(), "A situação da venda deveria ser ABERTA");
    }
    @Test
    @DisplayName("Teste addProduto com venda aberta e salvar com sucesso")
    void testAddProduto_vendaAberta_salvarComSucesso() {
        // Cenário
        Long codVen = 1L;
        Long codPro = 100L;
        Double vlBalanca = 50.0;

        // Configurando o comportamento do mock do repositório para verificar a situação da venda
        when(vendas.verificaSituacao(codVen)).thenReturn(VendaSituacao.ABERTA.toString());

        // Configurando o comportamento do mock de vendaProdutos.salvar para retornar sem lançar exceção
        doNothing().when(vendaProdutos).salvar(any(VendaProduto.class));

        // Ação
        String resultado = vendaService.addProduto(codVen, codPro, vlBalanca);

        // Verificações
        // 1. Verificar se vendas.verificaSituacao foi chamado com o código correto
        verify(vendas, times(1)).verificaSituacao(codVen);

        // 2. Verificar se vendaProdutos.salvar foi chamado uma vez com o objeto correto
        ArgumentCaptor<VendaProduto> vendaProdutoCaptor = ArgumentCaptor.forClass(VendaProduto.class);
        verify(vendaProdutos, times(1)).salvar(vendaProdutoCaptor.capture());

        VendaProduto vendaProdutoSalvo = vendaProdutoCaptor.getValue();
        // 3. Asserção sobre o resultado
        assertEquals("ok", resultado, "O método deveria retornar 'ok'");
    }
    @Test
    @DisplayName("Teste removeProduto com venda aberta e remoção com sucesso")
    void testRemoveProduto_vendaAberta_remocaoComSucesso() {
        // Cenário
        Long posicaoProd = 10L;
        Long codVenda = 1L;

        // Configurando o comportamento do mock do repositório para encontrar a venda
        Venda vendaAbertaMock = mock(Venda.class);
        when(vendas.findByCodigoEquals(codVenda)).thenReturn(vendaAbertaMock);
        when(vendaAbertaMock.getSituacao()).thenReturn(VendaSituacao.ABERTA);

        // Configurando o comportamento do mock de vendaProdutos.removeProduto para não lançar exceção
        doNothing().when(vendaProdutos).removeProduto(posicaoProd);

        // Ação
        String resultado = vendaService.removeProduto(posicaoProd, codVenda);

        // Verificações
        // 1. Verificar se vendas.findByCodigoEquals foi chamado com o código correto
        verify(vendas, times(1)).findByCodigoEquals(codVenda);

        // 2. Verificar se venda.getSituacao() foi chamado
        verify(vendaAbertaMock, times(1)).getSituacao();

        // 3. Verificar se vendaProdutos.removeProduto foi chamado com o parâmetro correto
        verify(vendaProdutos, times(1)).removeProduto(posicaoProd);

        // 4. Asserção sobre o resultado
        assertEquals("ok", resultado, "O método deveria retornar 'ok'");
    }
    @Test
    @DisplayName("Deve lançar exceção 'venda fechada'")
    void deveLancarExcecaoVendaFechada() {
        
        Long vendaId = 1L;
        when(vendas.findByCodigoEquals(vendaId)).thenReturn(vendaMock); // Venda não encontrada ou fechada
        when(vendaMock.isAberta()).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vendaService.fechaVenda(vendaId, 1L, 100.0, 10.0, 5.0, new String[]{"50.0"}, new String[]{"1"});
        });
        assertEquals("venda fechada", exception.getMessage());
        verify(vendas, never()).fechaVenda(anyLong(), any(), anyDouble(), anyDouble(), anyDouble(), any(), any());
    }
    
    @Test
    @DisplayName("Teste removeProduto com venda fechada")
    void testRemoveProduto_vendaFechada() {
        // Cenário
        Long posicaoProd = 20L;
        Long codVenda = 2L;

        // Configurando o comportamento do mock do repositório para encontrar a venda
        Venda vendaFechadaMock = mock(Venda.class);
        when(vendas.findByCodigoEquals(codVenda)).thenReturn(vendaFechadaMock);
        when(vendaFechadaMock.getSituacao()).thenReturn(VendaSituacao.FECHADA);

        // Ação
        String resultado = vendaService.removeProduto(posicaoProd, codVenda);

        // Verificações
        // 1. Verificar se vendas.findByCodigoEquals foi chamado com o código correto
        verify(vendas, times(1)).findByCodigoEquals(codVenda);

        // 2. Verificar se venda.getSituacao() foi chamado
        verify(vendaFechadaMock, times(1)).getSituacao();

        // 3. Verificar se vendaProdutos.removeProduto NÃO foi chamado
        verify(vendaProdutos, never()).removeProduto(anyLong());

        // 4. Asserção sobre o resultado
        assertEquals("Venda fechada", resultado, "O método deveria retornar 'Venda fechada'");
    }
    
    @Test
    @DisplayName("Teste fechaVenda - Venda Sem Valor")
    void testFechaVenda_vendaSemValor() {
        vendaAberta = mock(Venda.class);
        Long vendaId = 1L;
        Long pagamentoTipoId = 1L;
        Double vlprodutos = 0.0; // Valor inválido
        Double desconto = 10.0;
        Double acrescimo = 5.0;
        String[] vlParcelas = {"50.0"};
        String[] titulos = {"1"};

        // Configurando o comportamento do mock para vendaIsAberta
        when(vendas.findByCodigoEquals(vendaId)).thenReturn(vendaAberta);
        when(vendaAberta.isAberta()).thenReturn(true);

        // Ação & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vendaService.fechaVenda(vendaId, pagamentoTipoId, vlprodutos, desconto, acrescimo, vlParcelas, titulos);
        });

        assertEquals("Venda sem valor, verifique", exception.getMessage());
    }

    @Test
    @DisplayName("Teste fechaVenda - Venda à Vista com Dinheiro e Caixa Fechado")
    void testFechaVenda_vendaAVistaDinheiro_caixaFechado() {
    	vendaAberta = mock(Venda.class);
        Long vendaId = 1L;
        Long pagamentoTipoId = 1L;
        Double vlprodutos = 100.0;
        Double desconto = 10.0;
        Double acrescimo = 5.0;
        String[] vlParcelas = {"50.0"};
        String[] titulos = {"1"};

     // Configurando o comportamento do mock para vendaIsAberta
        when(vendas.findByCodigoEquals(vendaId)).thenReturn(vendaAberta);
        when(vendaAberta.isAberta()).thenReturn(true);

        // Configurando o comportamento do mock para formaPagamento
        PagamentoTipo formaPagamentoMock = mock(PagamentoTipo.class);
        when(formaPagamentoMock.getFormaPagamento()).thenReturn("00/DIN");
        when(formaPagamentos.busca(pagamentoTipoId)).thenReturn(formaPagamentoMock);

        // Configurando o comportamento do mock para vendas.findByCodigoEquals
        when(vendas.findByCodigoEquals(vendaId)).thenReturn(vendaAberta);

        // Configurando o comportamento do mock para receberServ.cadastrar
        Receber receberMock = mock(Receber.class);
        doNothing().when(receberServ).cadastrar(any(Receber.class));

        // Configurando o comportamento do mock para tituloService.busca
        Titulo tituloMock = mock(Titulo.class);
        TituloTipo tituloTipoMock = mock(TituloTipo.class);

        when(tituloMock.getTipo()).thenReturn(tituloTipoMock);
        when(tituloTipoMock.getSigla()).thenReturn("DIN");
        when(tituloService.busca(anyLong())).thenReturn(Optional.of(tituloMock));
        // Configurando o comportamento do mock para caixas.caixaIsAberto
        when(caixas.caixaIsAberto()).thenReturn(false);

        // Ação & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vendaService.fechaVenda(vendaId, pagamentoTipoId, vlprodutos, desconto, acrescimo, vlParcelas, titulos);
        });

        assertEquals("nenhum caixa aberto", exception.getMessage());

        // Verificações
        verify(formaPagamentos, times(1)).busca(pagamentoTipoId);
        verify(receberServ, times(1)).cadastrar(any(Receber.class));
        verify(tituloService, times(1)).busca(anyLong());
        verify(caixas, times(1)).caixaIsAberto();
        verifyNoMoreInteractions(vendas, formaPagamentos, receberServ, tituloService, caixas, cartaoLancamento, produtos);
    }
    
    @Test
    @DisplayName("Teste fechaVenda - Falha ao Fechar Venda")
    void testFechaVenda_falhaAoFecharVenda() {
        Venda dadosVendaMock = mock(Venda.class);
        DataAtual dataAtualmock = mock (DataAtual.class);
        TituloTipo tituloTipoMock = mock(TituloTipo.class);
        Long vendaId = 4L;
        Long pagamentoTipoId = 4L;
        Double vlprodutos = 400.0;
        Double desconto = 40.0;
        Double acrescimo = 20.0;
        String[] vlParcelas = {"200.0", "200.0"};
        String[] titulos = {"5", "6"};

        // Configurando o comportamento do mock para vendaIsAberta
        when(vendas.findByCodigoEquals(vendaId)).thenReturn(dadosVendaMock);
        when(dadosVendaMock.isAberta()).thenReturn(true);

        // Configurando o comportamento do mock para formaPagamento
        PagamentoTipo formaPagamentoMock = mock(PagamentoTipo.class);
        when(formaPagamentoMock.getFormaPagamento()).thenReturn("01/PRAZO");
        when(formaPagamentos.busca(pagamentoTipoId)).thenReturn(formaPagamentoMock);



        // Configurando o comportamento do mock para receberServ.cadastrar
        Receber receberMock = mock(Receber.class);
        doNothing().when(receberServ).cadastrar(any(Receber.class));

        // Configurando o comportamento do mock para tituloService.busca
        Titulo tituloMock1 = mock(Titulo.class);
        Titulo tituloMock2 = mock(Titulo.class);
        when(tituloService.busca(5L)).thenReturn(Optional.of(tituloMock1));

        // Configurando o comportamento do mock para dadosVenda.getPessoa()
        Pessoa pessoaMock = mock(Pessoa.class);
        when(dadosVendaMock.getPessoa()).thenReturn(pessoaMock);

        // Configurando o comportamento do mock para aprazo
        //when(vendaService.aprazo(anyDouble(), any(String[].class), dataAtualmock, any(String[].class), anyInt(), anyInt(), receberMock, anyInt(), anyDouble(), anyDouble()))
        //    .thenReturn(1);

        // Configurando o comportamento do mock para vendas.fechaVenda para lançar exceção
        doThrow(new RuntimeException("Erro ao fechar a venda")).when(vendas).fechaVenda(anyLong(), any(VendaSituacao.class), anyDouble(), anyDouble(), anyDouble(), any(Timestamp.class), any());

        // Configurando o comportamento do mock para produtos.movimentaEstoque
        

        // Ação & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            vendaService.fechaVenda(vendaId, pagamentoTipoId, vlprodutos, desconto, acrescimo, vlParcelas, titulos);
        });

        assertEquals("Erro ao fechar a venda, chame o suporte", exception.getMessage());

        // Verificações
        verify(formaPagamentos, times(1)).busca(pagamentoTipoId);
        verify(vendas, times(2)).findByCodigoEquals(vendaId);
        verify(receberServ, times(1)).cadastrar(any(Receber.class));
        verify(tituloService, times(1)).busca(anyLong());
        verify(vendas, times(1)).fechaVenda(anyLong(), any(VendaSituacao.class), anyDouble(), anyDouble(), anyDouble(), any(Timestamp.class), any());
    }
}
