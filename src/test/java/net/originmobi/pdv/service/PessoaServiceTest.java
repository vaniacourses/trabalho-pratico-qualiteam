package net.originmobi.pdv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.originmobi.pdv.model.Cidade;
import net.originmobi.pdv.model.Endereco;
import net.originmobi.pdv.model.Pessoa;
import net.originmobi.pdv.model.Telefone;
import net.originmobi.pdv.repository.PessoaRepository;

import net.originmobi.pdv.filter.PessoaFilter;

public class PessoaServiceTest {

    @Mock
    private PessoaRepository pessoaRepository;

    @Mock
    private CidadeService cidadeService;

    @Mock
    private EnderecoService enderecoService;

    @Mock
    private TelefoneService telefoneService;

    @InjectMocks
    private PessoaService pessoaService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLista() {
        when(pessoaRepository.findAll()).thenReturn(new ArrayList<>());

        List<Pessoa> pessoas = pessoaService.lista();

        assertNotNull(pessoas);
        assertEquals(0, pessoas.size());
        verify(pessoaRepository, times(1)).findAll();
    }

    @Test
    public void testBusca() {
        Pessoa pessoa = new Pessoa();
        when(pessoaRepository.findByCodigo(anyLong())).thenReturn(pessoa);

        Pessoa found = pessoaService.busca(1L);

        assertNotNull(found);
        verify(pessoaRepository, times(1)).findByCodigo(anyLong());
    }

    @Test
    public void testBuscaPessoa() {
        Pessoa pessoa = new Pessoa();
        when(pessoaRepository.findById(anyLong())).thenReturn(Optional.of(pessoa));

        Optional<Pessoa> found = pessoaService.buscaPessoa(1L);

        assertNotNull(found);
        assertEquals(pessoa, found.get());
        verify(pessoaRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testFilter() {
        List<Pessoa> pessoas = new ArrayList<>();
        when(pessoaRepository.findByNomeContaining(anyString())).thenReturn(pessoas);

        PessoaFilter filter = new PessoaFilter();
        filter.setNome("nome");

        List<Pessoa> found = pessoaService.filter(filter);

        assertNotNull(found);
        assertEquals(pessoas, found);
        verify(pessoaRepository, times(1)).findByNomeContaining(anyString());
    }

    @Test
    public void testCadastrar() throws ParseException {
        RedirectAttributes attributes = mock(RedirectAttributes.class);

        Cidade cidade = new Cidade();
        when(cidadeService.busca(anyLong())).thenReturn(Optional.of(cidade));

        Endereco endereco = new Endereco();
        when(enderecoService.cadastrar(any(Endereco.class))).thenReturn(endereco);

        Telefone telefone = new Telefone();
        when(telefoneService.cadastrar(any(Telefone.class))).thenReturn(telefone);

        String result = pessoaService.cadastrar(0L, "nome", "apelido", "cpfcnpj", "01/01/2000", "observacao", 0L, 1L, "rua", "bairro", "numero", "cep", "referencia", 0L, "fone", "FIXO", attributes);

        assertEquals("Pessoa salva com sucesso", result);
        verify(pessoaRepository, times(1)).save(any(Pessoa.class));
    }

    @Test
    public void testCadastrarPessoaExistente() {
        RedirectAttributes attributes = mock(RedirectAttributes.class);

        when(pessoaRepository.findByCpfcnpjContaining(anyString())).thenReturn(new Pessoa());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pessoaService.cadastrar(0L, "nome", "apelido", "cpfcnpj", "01/01/2000", "observacao", 0L, 1L, "rua", "bairro", "numero", "cep", "referencia", 0L, "fone", "FIXO", attributes);
        });

        assertEquals("Já existe uma pessoa cadastrada com este CPF/CNPJ, verifique", exception.getMessage());
        verify(pessoaRepository, never()).save(any(Pessoa.class));
    }

    @Test
    public void testCadastrarErro() throws ParseException {
        RedirectAttributes attributes = mock(RedirectAttributes.class);

        Cidade cidade = new Cidade();
        when(cidadeService.busca(anyLong())).thenReturn(Optional.of(cidade));

        Endereco endereco = new Endereco();
        when(enderecoService.cadastrar(any(Endereco.class))).thenReturn(endereco);

        Telefone telefone = new Telefone();
        when(telefoneService.cadastrar(any(Telefone.class))).thenReturn(telefone);

        doThrow(new RuntimeException("Erro ao tentar cadastrar pessoa, chame o suporte")).when(pessoaRepository).save(any(Pessoa.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pessoaService.cadastrar(0L, "nome", "apelido", "cpfcnpj", "01/01/2000", "observacao", 0L, 1L, "rua", "bairro", "numero", "cep", "referencia", 0L, "fone", "FIXO", attributes);
        });

        assertEquals("Erro ao tentar cadastrar pessoa, chame o suporte", exception.getMessage());
        verify(pessoaRepository, times(1)).save(any(Pessoa.class));
    }
}
