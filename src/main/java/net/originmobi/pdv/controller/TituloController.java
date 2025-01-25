package net.originmobi.pdv.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.TituloTipo;
import net.originmobi.pdv.model.cartao.MaquinaCartao;
import net.originmobi.pdv.service.TituloTipoService;
import net.originmobi.pdv.service.cartao.MaquinaCartaoService;

@Controller
@RequestMapping("/titulos")
public class TituloController {

	private static final String TITULO_LIST = "titulo/list";

	private static final String TITULO_FORM = "titulo/form";

	@Autowired
	private TituloService titulos;

	@Autowired
	private TituloTipoService tipos;
	
	@Autowired
	private MaquinaCartaoService maquinas;

	@GetMapping("/form")
	public ModelAndView form() {
		ModelAndView mv = new ModelAndView(TITULO_FORM);
		mv.addObject("titulo", new Titulo());
		return mv;
	}

	@GetMapping
	public ModelAndView lista() {
		ModelAndView mv = new ModelAndView(TITULO_LIST);
		mv.addObject("titulos", titulos.lista());
		return mv;
	}

	@PostMapping
	public String cadastrar(@Validated Titulo titulo, Errors errors, RedirectAttributes attributes) {
		if (errors.hasErrors())
			return TITULO_FORM;

		titulos.cadastro(titulo);

		return "redirect:/titulos";
	}

	@GetMapping("{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Titulo titulo) {
		ModelAndView mv = new ModelAndView(TITULO_FORM);
		mv.addObject("titulo", titulo);
		return mv;
	}

	@RequestMapping(value = "/excluir/{codigo}", method = RequestMethod.DELETE)
	public @ResponseBody String excluir(@PathVariable("codigo") Long codigo) {
		return titulos.remover(codigo);
	}

	@ModelAttribute("tipos")
	public List<TituloTipo> tipos() {
		return tipos.lista();
	}
	
	@ModelAttribute("maquinas")
	public List<MaquinaCartao> maquinas() {
		return maquinas.lista();
	}
}
