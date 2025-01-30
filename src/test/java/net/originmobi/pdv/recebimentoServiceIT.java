/**
 * 
 */
package net.originmobi.pdv;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import net.originmobi.pdv.enumerado.TituloTipoEnum;
import net.originmobi.pdv.model.Cidade;
import net.originmobi.pdv.model.Endereco;
import net.originmobi.pdv.model.Estado;
import net.originmobi.pdv.model.Pais;
import net.originmobi.pdv.model.Parcela;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Recebimento;
import net.originmobi.pdv.model.Titulo;
import net.originmobi.pdv.model.Receber;
import net.originmobi.pdv.model.TituloTipo;
import net.originmobi.pdv.repository.RecebimentoRepository;
import net.originmobi.pdv.repository.TituloRepository;
import net.originmobi.pdv.service.EnderecoService;
import net.originmobi.pdv.service.RecebimentoService;
import net.originmobi.pdv.repository.ParcelaRepository;
import net.originmobi.pdv.repository.PessoaRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
 class recebimentoServiceIT {

    @Autowired
    private RecebimentoService recebimentoService;

    @Autowired
    private RecebimentoRepository recebimentoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private TituloRepository tituloRepository;
    
    @Autowired
    private EnderecoService enderecoService;


    private Pessoa pessoa;
    private Parcela parcela;
    private Titulo titulo;
    private TituloTipo tituloTipo;
    private Receber receber;
    private Endereco endereco;
    @BeforeEach
    void setup() {
    	endereco = enderecoService.lista().get(0);
    	
    	
    	Timestamp dataProcessamento = new Timestamp(System.currentTimeMillis());
    	long millis2 = System.currentTimeMillis(); // Obtém o tempo atual em milissegundos
        Date dataSql2 = new Date(millis2);
    	tituloTipo = new TituloTipo();
        tituloTipo.setCodigo(1L);
        tituloTipo.setDescricao("Descrição de teste");
        tituloTipo.setSigla("DIN");
         
    	receber = new Receber();
    	receber.setCodigo(1L);
    	receber.setData_cadastro(dataProcessamento);
    	
        // Criando uma pessoa de teste
        pessoa = new Pessoa();
        pessoa.setNome("Cliente Teste");
        pessoa.setCpfcnpj("111.269.424-21");
        pessoa.setData_cadastro(dataSql2);
        pessoa.setData_nascimento(dataSql2);
        pessoa.setEndereco(endereco);
        pessoa.setObservacao("Observação");
        pessoaRepository.save(pessoa);

        // Criando um título
        titulo = new Titulo();
        titulo.setDescricao("Título de Teste");
        titulo.setTipo(tituloTipo);
        titulo.setCodigo(30L);
        tituloRepository.save(titulo);

        // Criando uma parcela vinculada ao título e pessoa
        parcela = new Parcela();
        parcela.setReceber(receber);
        parcela.setValor_restante(100.0);
        parcela.setData_cadastro(dataProcessamento);
        parcela.setData_vencimento(dataSql2);
        parcela.setValor_total(300.00);
        parcelaRepository.save(parcela);
        
        
       
    }

    @Test
    void testAbrirRecebimento_Sucesso() {
        String[] arrayParcelas = { parcela.getCodigo().toString() };

        String recebimentoId = recebimentoService.abrirRecebimento(pessoa.getCodigo(), arrayParcelas);
        Optional<Recebimento> recebimento = recebimentoRepository.findById(Long.parseLong(recebimentoId));

        assertThat(recebimento).isPresent();
        assertThat(recebimento.get().getValor_total()).isEqualTo(100.0);
    }

    @Test
    void testReceber_Sucesso() {
        String[] arrayParcelas = { parcela.getCodigo().toString() };
        String recebimentoId = recebimentoService.abrirRecebimento(pessoa.getCodigo(), arrayParcelas);

        // Tenta receber o valor total
        String resposta = recebimentoService.receber(Long.parseLong(recebimentoId), 100.0, 0.0, 0.0, titulo.getCodigo());

        assertThat(resposta).isEqualTo("Recebimento realizado com sucesso");

        // Verifica se foi atualizado corretamente
        Optional<Recebimento> recebimento = recebimentoRepository.findById(Long.parseLong(recebimentoId));
        assertThat(recebimento).isPresent();
        assertThat(recebimento.get().getValor_recebido()).isEqualTo(100.0);
    }

    @Test
    void testReceber_ValorMaiorQuePermitido() {
        String[] arrayParcelas = { parcela.getCodigo().toString() };
        String recebimentoId = recebimentoService.abrirRecebimento(pessoa.getCodigo(), arrayParcelas);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recebimentoService.receber(Long.parseLong(recebimentoId), 200.0, 0.0, 0.0, titulo.getCodigo());
        });

        assertThat(exception.getMessage()).isEqualTo("Valor de recebimento é superior aos títulos");
    }

    @Test
    void testRemoverRecebimento_Sucesso() {
        String[] arrayParcelas = { parcela.getCodigo().toString() };
        String recebimentoId = recebimentoService.abrirRecebimento(pessoa.getCodigo(), arrayParcelas);

        String resposta = recebimentoService.remover(Long.parseLong(recebimentoId));
        assertThat(resposta).isEqualTo("removido com sucesso");

        Optional<Recebimento> recebimento = recebimentoRepository.findById(Long.parseLong(recebimentoId));
        assertThat(recebimento).isEmpty();
    }

}