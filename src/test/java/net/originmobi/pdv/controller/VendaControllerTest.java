package net.originmobi.pdv.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.originmobi.pdv.enumerado.VendaSituacao;
import net.originmobi.pdv.filter.VendaFilter;
import net.originmobi.pdv.model.PagamentoTipo;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.Venda;
import net.originmobi.pdv.service.PagamentoTipoService;
import net.originmobi.pdv.service.PessoaService;
import net.originmobi.pdv.service.ProdutoService;
import net.originmobi.pdv.service.TituloService;
import net.originmobi.pdv.service.VendaProdutoService;
import net.originmobi.pdv.service.VendaService;

@ExtendWith(MockitoExtension.class)
public class VendaControllerTest {

    @InjectMocks
    private VendaController vendaController;

    @Mock
    private VendaService vendas;

    @Mock
    private PessoaService pessoas;

    @Mock
    private ProdutoService produtos;

    @Mock
    private VendaProdutoService vendaProdutos;

    @Mock
    private PagamentoTipoService pagamentoTipos;

    @Mock
    private TituloService titulos;

    @Mock
    private Errors errors;

    @Mock
    private RedirectAttributes attributes;

    @Test
    void testForm() {
        ModelAndView mv = vendaController.form();
        assertNotNull(mv);
        assertEquals("venda/form", mv.getViewName());
        assertTrue(mv.getModel().containsKey("venda"));
    }

    @Test
    void testListaPedidos() {
        // Configuração
        VendaFilter filter = new VendaFilter();
        String status = "ABERTA";
        Pageable pageable = mock(Pageable.class);
        Model model = new BindingAwareModelMap();
        Page<Venda> vendasPaginadas = mock(Page.class);

        when(vendas.busca(filter, status, pageable)).thenReturn(vendasPaginadas);
        when(vendasPaginadas.getTotalPages()).thenReturn(1);
        when(vendasPaginadas.getPageable()).thenReturn(pageable);
        when(pageable.getPageNumber()).thenReturn(0);
        when(pageable.next()).thenReturn(pageable);
        when(pageable.previousOrFirst()).thenReturn(pageable);
        when(vendasPaginadas.hasNext()).thenReturn(false);
        when(vendasPaginadas.hasPrevious()).thenReturn(false);
        when(vendasPaginadas.getContent()).thenReturn(Arrays.asList(new Venda()));

        // Execução
        ModelAndView mv = vendaController.listaPedidos(filter, status, pageable, model);

        // Verificação
        assertNotNull(mv);
        assertEquals("venda/list", mv.getViewName());
        assertTrue(mv.getModel().containsKey("vendas"));
    }

    @Test
    void testAbrirVenda_Sucesso() {
        // Configuração
        Venda venda = new Venda();
        when(errors.hasErrors()).thenReturn(false);
        Long codigo = 1L;
        when(vendas.abreVenda(venda)).thenReturn(codigo);

        // Execução
        String result = vendaController.abrirVenda(venda, errors, attributes);

        // Verificação
        assertEquals("redirect:/venda/1", result);
        verify(attributes).addFlashAttribute("mensagem", "Pedido Salvo");
    }

    @Test
    void testAbrirVenda_ComErros() {
        // Configuração
        Venda venda = new Venda();
        when(errors.hasErrors()).thenReturn(true);

        // Execução
        String result = vendaController.abrirVenda(venda, errors, attributes);

        // Verificação
        assertEquals("venda/form", result);
    }

    @Test
    void testBuscaVenda() {
        // Configuração
        Venda venda = new Venda();
        when(vendaProdutos.listaProdutosVenda(venda)).thenReturn(Arrays.asList());

        // Execução
        ModelAndView mv = vendaController.buscaVenda(venda);

        // Verificação
        assertNotNull(mv);
        assertEquals("venda/form", mv.getViewName());
        assertTrue(mv.getModel().containsKey("venda"));
        assertTrue(mv.getModel().containsKey("produtosVenda"));
    }

    @Test
    void testAddProdutoVenda() {
        // Configuração
        Map<String, String> request = Map.of(
            "codigoVen", "1",
            "codigoPro", "2",
            "valorBalanca", "10.5"
        );
        String mensagem = "Produto adicionado";
        when(vendas.addProduto(1L, 2L, 10.5)).thenReturn(mensagem);

        // Execução
        String result = vendaController.addProdutoVenda(request);

        // Verificação
        assertEquals(mensagem, result);
    }

    @Test
    void testRemoveProdutoVenda() {
        // Configuração
        Map<String, String> request = Map.of(
            "posicaoPro", "1",
            "codigoVen", "2"
        );
        String mensagem = "Produto removido";
        when(vendas.removeProduto(1L, 2L)).thenReturn(mensagem);

        // Execução
        String result = vendaController.removeProdutoVenda(request);

        // Verificação
        assertEquals(mensagem, result);
    }

    @Test
    void testFechar() {
        // Configuração
        Map<String, String> request = Map.of(
            "venda", "1",
            "pagamentotipo", "2",
            "valor_produtos", "100.00",
            "valor_desconto", "5.00",
            "valor_acrescimo", "2.00",
            "valores", "50.00,50.00",
            "titulos", "titulo1,titulo2"
        );
        String mensagem = "Venda fechada";
        when(vendas.fechaVenda(
            1L, 2L, 100.00, 5.00, 2.00,
            new String[]{"50.00", "50.00"},
            new String[]{"titulo1", "titulo2"}
        )).thenReturn(mensagem);

        // Execução
        String result = vendaController.fechar(request);

        // Verificação
        assertEquals(mensagem, result);
    }

    @Test
    void testTitulos() {
        // Configuração
        List<Titulo> tituloList = Arrays.asList(new Titulo());
        when(titulos.lista()).thenReturn(tituloList);

        // Execução
        List<Titulo> result = vendaController.titulos();

        // Verificação
        assertEquals(tituloList, result);
    }

    @Test
    void testClientes() {
        // Configuração
        List<Pessoa> pessoaList = Arrays.asList(new Pessoa());
        when(pessoas.lista()).thenReturn(pessoaList);

        // Execução
        List<Pessoa> result = vendaController.clientes();

        // Verificação
        assertEquals(pessoaList, result);
    }

    @Test
    void testVendaSituacao() {
        // Execução
        List<VendaSituacao> result = vendaController.vendaSituacao();

        // Verificação
        assertEquals(Arrays.asList(VendaSituacao.values()), result);
    }

    @Test
    void testProdutos() {
        // Configuração
        List<Produto> produtoList = Arrays.asList(new Produto());
        when(produtos.listar()).thenReturn(produtoList);

        // Execução
        List<Produto> result = vendaController.produtos();

        // Verificação
        assertEquals(produtoList, result);
    }

    @Test
    void testProdutosVendaveis() {
        // Configuração
        List<Produto> produtoList = Arrays.asList(new Produto());
        when(produtos.listaProdutosVendaveis()).thenReturn(produtoList);

        // Execução
        List<Produto> result = vendaController.produtosVendaveis();

        // Verificação
        assertEquals(produtoList, result);
    }

    @Test
    void testPagamentoTipo() {
        // Configuração
        List<PagamentoTipo> pagamentoTipoList = Arrays.asList(new PagamentoTipo());
        when(pagamentoTipos.listar()).thenReturn(pagamentoTipoList);

        // Execução
        List<PagamentoTipo> result = vendaController.pagamentoTipo();

        // Verificação
        assertEquals(pagamentoTipoList, result);
    }
}
