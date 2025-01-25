package net.originmobi.pdv.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.originmobi.pdv.enumerado.Ativo;
import net.originmobi.pdv.enumerado.produto.ProdutoBalanca;
import net.originmobi.pdv.enumerado.produto.ProdutoControleEstoque;
import net.originmobi.pdv.enumerado.produto.ProdutoSubstTributaria;
import net.originmobi.pdv.enumerado.produto.ProdutoVendavel;
import net.originmobi.pdv.filter.ProdutoFilter;
import net.originmobi.pdv.model.Categoria;
import net.originmobi.pdv.model.Fornecedor;
import net.originmobi.pdv.model.Grupo;
import net.originmobi.pdv.model.ModBcIcms;
import net.originmobi.pdv.model.Produto;
import net.originmobi.pdv.model.Tributacao;
import net.originmobi.pdv.service.CategoriaService;
import net.originmobi.pdv.service.FornecedorService;
import net.originmobi.pdv.service.GrupoService;
import net.originmobi.pdv.service.ImagemProdutoService;
import net.originmobi.pdv.service.ProdutoService;
import net.originmobi.pdv.service.TributacaoService;
import net.originmobi.pdv.service.notafiscal.ModBcIcmsService;

@Controller
@RequestMapping("/produto")
@SessionAttributes("filterProduto")
public class ProdutoController {

	private static final String PRODUTO_LIST = "produto/list";

	private static final String PRODUTO_FORM = "produto/form";

	@Autowired
	private ProdutoService produtos;

	@Autowired
	private FornecedorService fornecedores;

	@Autowired
	private GrupoService grupos;

	@Autowired
	private CategoriaService categorias;

	@Autowired
	private ImagemProdutoService imagens;

	@Autowired
	private TributacaoService tributacoes;

	@Autowired
	private ModBcIcmsService modbcs;

	@GetMapping("/form")
	public ModelAndView form(Produto produto) {
		ModelAndView mv = new ModelAndView(PRODUTO_FORM);
		mv.addObject(new Produto());
		return mv;
	}

	@ModelAttribute("filterProduto")
	public ProdutoFilter inicializerProdutoFilter() {
		return new ProdutoFilter();
	}

	@GetMapping
	public ModelAndView lista(@ModelAttribute("filterProduto") ProdutoFilter filter, Pageable pageable, Model model) {
		ModelAndView mv = new ModelAndView(PRODUTO_LIST);
		Page<Produto> listProdutos = produtos.filter(filter, pageable);
		mv.addObject("produtos", listProdutos);

		model.addAttribute("qtdpaginas", listProdutos.getTotalPages());
		model.addAttribute("pagAtual", listProdutos.getPageable().getPageNumber());
		model.addAttribute("proxPagina", listProdutos.getPageable().next().getPageNumber());
		model.addAttribute("pagAnterior", listProdutos.getPageable().previousOrFirst().getPageNumber());
		model.addAttribute("hasNext", listProdutos.hasNext());
		model.addAttribute("hasPrevious", listProdutos.hasPrevious());

		return mv;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String cadastrar(@RequestParam Map<String, String> request, RedirectAttributes attributes) {

		String prod = request.get("codigo");
		String descricao = request.get("descricao");
		Long codforne = Long.decode(request.get("fornecedor"));
		Long categoria = Long.decode(request.get("categoria"));
		Long grupo = Long.decode(request.get("grupo"));
		String balanca = request.get("balanca");
		String vlcusto = request.get("valor_custo");
		String vlvenda = request.get("valor_venda");
		String validade = request.get("data_validade");
		String controleEstoque = request.get("controla_estoque");
		String ativo = request.get("ativo");
		String unitario = request.get("unidade");
		String subtribu = request.get("subtributaria");
		String ncm = request.get("ncm");
		String cest = request.get("cest");
		String vlTributacao = request.get("tributacao");
		String codModbc = request.get("modBcIcms");
		String vendavel = request.get("vendavel");

		Long codigoprod = prod.isEmpty() ? 0 : Long.decode(prod);
		Double valorCusto = vlcusto.isEmpty() ? 0.0 : Double.valueOf(vlcusto.replace(",", "."));
		Double valorVenda = vlvenda.isEmpty() ? 0.0 : Double.valueOf(vlvenda.replace(",", "."));
		Long tributacao = vlTributacao.isEmpty() ? null : Long.decode(vlTributacao);
		Long modbc = codModbc.isEmpty() ? null : Long.decode(codModbc);

		Date dataValidade = null;
		if (!validade.isEmpty()) {
			SimpleDateFormat formatoUser = new SimpleDateFormat("dd/MM/yyyy");
			try {
				dataValidade = formatoUser.parse(validade);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		int usaBalanca = balanca.equals("SIM") ? 1 : 0;
		Ativo situacao = ativo.equals("ATIVO") ? Ativo.ATIVO : Ativo.INATIVO;
		ProdutoSubstTributaria substituicao = subtribu.equals("SIM") ? ProdutoSubstTributaria.SIM
				: ProdutoSubstTributaria.NAO;

		String mensagem = "";
		System.out.println(controleEstoque);
		mensagem = produtos.merger(codigoprod, codforne, categoria, grupo, usaBalanca, descricao, valorCusto,
				valorVenda, dataValidade, controleEstoque, situacao.toString(), unitario, substituicao, ncm, cest, tributacao,
				modbc, vendavel);

		attributes.addFlashAttribute("mensagem", mensagem);
		
		if(codigoprod != 0)
			return "redirect:/produto/" + codigoprod.toString();
		
		return "redirect:/produto";

	}

	@GetMapping("{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Produto produto) {
		ModelAndView mv = new ModelAndView(PRODUTO_FORM);
		mv.addObject("produto", produto);
		mv.addObject("imagem", imagens.busca(produto.getCodigo()));
		return mv;
	}

	@ModelAttribute("ativo")
	public List<Ativo> ativo() {
		return Arrays.asList(Ativo.values());
	}

	@ModelAttribute("fornecedores")
	public List<Fornecedor> fornecedores() {
		return fornecedores.lista();
	}

	@ModelAttribute("grupos")
	public List<Grupo> grupos() {
		return grupos.lista();
	}

	@ModelAttribute("categorias")
	public List<Categoria> categorias() {
		return categorias.lista();
	}

	@ModelAttribute("balanca")
	public List<ProdutoBalanca> balanca() {
		return Arrays.asList(ProdutoBalanca.values());
	}

	@ModelAttribute("subtributaria")
	public List<ProdutoSubstTributaria> substTributaria() {
		return Arrays.asList(ProdutoSubstTributaria.values());
	}

	@ModelAttribute("tributacoes")
	public List<Tributacao> tributacoes() {
		return tributacoes.lista();
	}

	@ModelAttribute("modbcs")
	private List<ModBcIcms> modbc() {
		return modbcs.lista();
	}

	@ModelAttribute("controlaestoque")
	private List<ProdutoControleEstoque> controlaestoque() {
		return Arrays.asList(ProdutoControleEstoque.values());
	}
	
	@ModelAttribute("produtoVendavel")
	public List<ProdutoVendavel> produtoVendavel() {
		return Arrays.asList(ProdutoVendavel.values());
	}
}
