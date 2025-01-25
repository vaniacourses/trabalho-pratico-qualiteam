package net.originmobi.pdv.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.NumberFormat;

/**
 * 
 * @author joaotux
 * 
 *         Responsavél por mapear a tebela venda_produtos
 */

@Entity
@Table(name = "venda_produtos")
public class VendaProduto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	@Column(name = "produto_codigo")
	private Long produto;

	@Column(name = "venda_codigo")
	private Long venda;

	@Column(name = "valor_balanca")
	@NumberFormat(pattern = "#,##0.00")
	private Double valor_balanca;

	public VendaProduto() {
	}

	public VendaProduto(Long produto, Long venda, Double valor_balanca) {
		super();
		this.produto = produto;
		this.valor_balanca = valor_balanca;
		this.venda = venda;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Long getProduto() {
		return produto;
	}

	public void setProduto(Long produto) {
		this.produto = produto;
	}

	public Long getVenda() {
		return venda;
	}

	public void setVenda(Long venda) {
		this.venda = venda;
	}

	public Double getValor_balanca() {
		return valor_balanca;
	}

	public void setValor_balanca(Double valor_balanca) {
		this.valor_balanca = valor_balanca;
	}

}
