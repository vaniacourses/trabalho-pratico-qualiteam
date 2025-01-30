package net.originmobi.pdv;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

public class CadastrarNovaPessoaTest {

    protected WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    public static void configuraDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void createDriver() throws InterruptedException {
        driver = WebDriverManager.chromedriver().create();
        wait = new WebDriverWait(driver, 3);
        driver.get("http://localhost:8080/login");
        WebElement userField = driver.findElement(By.id("user"));
        userField.sendKeys("gerente");
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("123");
        WebElement loginButton = driver.findElement(By.id("btn-login"));
        loginButton.click();
        assertNotNull(driver.findElement(By.className("info-usuario")));
    }

    @ParameterizedTest
    @MethodSource("providePessoaData")
    void CadastrarPessoa(String nome, String apelido, String cpfcnpj, String nascimento,
                                String observacao, String cidade, String rua, String bairro,
                                String numero, String cep, String referencia, String fone,
                                String tipo, boolean esperadoSucesso)  {

        System.out.println("passei aq1");
        WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[3]/div/div[8]/a")));
        link.click();
        System.out.println("passei aq1");
        WebElement novaPessoa = driver.findElement(By.xpath("//*[@id=\"btn-padrao\"]/a"));
        novaPessoa.click();
        System.out.println("passei aq1");
        // Preencher o formulário
        WebElement nomeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nome")));
        nomeField.clear();
        System.out.println("passei aq1");
        nomeField.sendKeys(nome);
        System.out.println("passei aq1");
        
        WebElement apelidoField = driver.findElement(By.id("apelido"));
        apelidoField.clear();
        apelidoField.sendKeys(apelido);
        System.out.println("passei aq1");
        WebElement cpfcnpjField = driver.findElement(By.id("cpfcnpj"));
        cpfcnpjField.clear();
        cpfcnpjField.sendKeys(cpfcnpj);
        System.out.println("passei aq1");
        WebElement nascimentoField = driver.findElement(By.id("nascimento"));
        nascimentoField.clear();
        nascimentoField.sendKeys(nascimento);
        System.out.println("passei aq3");
        // Observação
        WebElement observacaoClick = driver.findElement(By.xpath("//*[@id=\"form_pessoa\"]/ul/li[1]/a"));
        observacaoClick.click();
        WebElement observacaoField = driver.findElement(By.id("observacao"));
        System.out.println("passei aq4");
        observacaoField.clear();
        observacaoField.sendKeys(observacao);

        // Endereço
        WebElement enderecoClick = driver.findElement(By.xpath("//*[@id=\"form_pessoa\"]/ul/li[2]/a"));
        enderecoClick.click();
        System.out.println("passei a213213");
        WebElement cidadeField = wait.until(ExpectedConditions.elementToBeClickable(By.id("cidade")));
        Select selectCidade = new Select(cidadeField);
        selectCidade.selectByVisibleText(cidade);

        WebElement ruaField = driver.findElement(By.id("rua"));
        ruaField.clear();
        ruaField.sendKeys(rua);

        WebElement bairroField = driver.findElement(By.id("bairro"));
        bairroField.clear();
        bairroField.sendKeys(bairro);

        WebElement numeroField = driver.findElement(By.id("numero"));
        numeroField.clear();
        numeroField.sendKeys(numero);

        WebElement cepField = driver.findElement(By.id("cep"));
        cepField.clear();
        cepField.sendKeys(cep);

        WebElement referenciaField = driver.findElement(By.id("referencia"));
        referenciaField.clear();
        referenciaField.sendKeys(referencia);

        // Contato
        WebElement contatoClick = driver.findElement(By.xpath("//*[@id=\"form_pessoa\"]/ul/li[3]/a"));
        contatoClick.click();

        WebElement foneField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fone")));
        foneField.clear();
        foneField.sendKeys(fone);

        WebElement tipoField = driver.findElement(By.id("tipo"));
        Select selectTipo = new Select(tipoField);
        selectTipo.selectByVisibleText(tipo);

        // Submeter o formulário
        WebElement form = driver.findElement(By.id("form_pessoa"));
        form.submit();
        WebElement elementoErroEsperado;
        Alert alert = null;
        try {
         alert = wait.until(ExpectedConditions.alertIsPresent());

    } catch (Exception e) {
    	System.out.println("Timeout: Alerta não apareceu dentro do tempo esperado.");
        e.printStackTrace();
        }
        if (esperadoSucesso) {
        	// Verificar resultado
            String alertMessage = alert.getText();
            System.out.println("Texto do alerta: " + alertMessage);
            assertTrue("Pessoa salva com sucesso".contains(alertMessage), "Mensagem de sucesso não encontrada.");
            
        } 
        else {
           
        	String alertMessage = alert == null? null:alert.getText();
        	
        	assertFalse(alertMessage != null && "Pessoa salva com sucesso".contains(alertMessage), "Falha no teste: Sucesso ao criar");
                
            
        }

    
    }
    private static Stream<Arguments> providePessoaData() {
    	return Stream.of(
    		    // 1) Caso Válido (Happy Path)
    		    Arguments.of(
    		        "João Silva", "Silva", "123.456.789-00", "1990/05/20",
    		        "Nenhuma observação", "Cacoal", "Rua A", "Bairro B",
    		        "100", "12345-678", "Próximo ao parque", "99999-9999",
    		        "FIXO", true
    		    ),

    		    // 2) Nome vazio
    		    Arguments.of(
    		        "", "Silva", "124.456.789-00", "1990/05/20",
    		        "Nenhuma observação", "Cacoal", "Rua A", "Bairro B",
    		        "100", "12345-678", "Próximo ao parque", "99999-9999",
    		        "FIXO", false
    		    ),

    		    // 3) CPF inválido (caracteres fora do padrão)
    		    Arguments.of(
    		        "Maria Souza", "Souza", "152.456.789-XX", "1985/12/15",
    		        "Nenhuma observação", "Cacoal", "Rua B", "Bairro C",
    		        "200", "87654-321", "Próximo ao mercado", "88888-8888",
    		        "CELULAR", false
    		    ),

    		    // 4) Data de nascimento futura
    		    Arguments.of(
    		        "Pedro Lima", "Lima", "985.654.321-00", "2050/01/01",
    		        "Nenhuma observação", "Cacoal", "Rua C", "Bairrow3",
    		        "300", "11223-445", "Próximo à escola", "77777-7777",
    		        "FIXO", false
    		    ),

    		    // 5) CEP inválido (contém letras)
    		    Arguments.of(
    		        "Ana Paula", "Paula", "111.222.333-44", "1995/07/10",
    		        "Nenhuma observação", "Cacoal", "Rua D", "Bairrobasd",
    		        "400", "ABCDE-123", "Próximo ao hospital", "66666-6666",
    		        "CELULAR", false
    		    ),

    		    // 6) Apelido com caracteres proibidos
    		    Arguments.of(
    		        "Carlos Alberto", "Al!b3rt@", "222.333.444-55", "1980/01/01",
    		        "Observação qualquer", "Cacoal", "Rua E", "Bairro Z",
    		        "500", "99999-888", "Referência X", "9999-9999",
    		        "FIXO", false
    		    ),

    		    // 7) Observação excedendo limite (se houver, por ex. 200 chars) - falha
    		    Arguments.of(
    		        "Mariana Torres", "MariT", "333.444.555-66", "1992/03/15",
    		        "Observação muito grande que ultrapassa o limite esperado de caracteres. " +
    		        "Exemplo de texto que continua crescendo para forçar erro no sistema.",
    		        "Cacoal", "Rua F", "Bairro Y",
    		        "600", "11111-111", "Próximo à padaria", "98888-0000",
    		        "CELULAR", false
    		    ),

    		    // 8) Telefone sem formatação suficiente (faltando dígitos)
    		    Arguments.of(
    		        "Bruno Santos", "BSantos", "444.555.666-77", "1993/08/25",
    		        "Observação dentro do limite", "Cacoal", "Rua G", "Bairro W",
    		        "700", "22222-222", "Em frente ao posto", "1234-567", // faltando dígitos
    		        "FIXO", false
    		    ),

    		    // 10) Número do endereço vazio (campo obrigatório)
    		    Arguments.of(
    		        "Fernanda Costa", "FeCost", "666.777.888-99", "1989/09/09",
    		        "Observação normal", "Cacoal", "Rua I", "Bairro U",
    		        "", // Número vazio
    		        "44444-444", "Ao lado da farmácia", "94555-2222",
    		        "CELULAR", false
    		    ),

    		    // 11) Nome com caracteres especiais (validar se realmente é inválido ou se é aceito)
    		    // Se o sistema não aceitar caracteres como '&', deve dar false:
    		    Arguments.of(
    		        "Mar&cia", "Marcia", "777.888.999-00", "1985/10/10",
    		        "Observação normal", "Cacoal", "Rua J", "Bairro T",
    		        "900", "55555-555", "Referência N", "99876-5432",
    		        "FIXO", false
    		    ),

    		    // 12) CNPJ válido (caso o sistema aceite CPF/CNPJ no mesmo campo)
    		    // Para exemplificar, assumindo "12.345.678/0001-99" seja válido
    		    Arguments.of(
    		        "Empresa ABC", "ABC", "12.345.678/0001-99", "2000/01/01",
    		        "Observação com CNPJ", "Cacoal", "Av. Central", "Bairro Comercial",
    		        "1000", "99999-000", "Próximo ao centro", "4002-8922",
    		        "FIXO", true
    		    ),

    		    // 13) Formato de data incorreto (ex.: YYYY-MM-DD ao invés de YYYY/MM/DD, se o sistema não aceita)
    		    Arguments.of(
    		        "Felipe Alves", "Fa", "888.999.000-11", "2020-05-05", 
    		        "Observação normal", "Cacoal", "Rua K", "Bairro S",
    		        "120", "88888-888", "Sem referência", "97777-9999",
    		        "CELULAR", false
    		    ),

    		    // 14) Bairro vazio (se for obrigatório, deve falhar)
    		    Arguments.of(
    		        "Carla Nunes", "C_nun", "999.000.111-22", "1990/02/25",
    		        "Observação normal", "Cacoal", "Rua L", "",
    		        "121", "77777-777", "Referência teste", "96666-9999",
    		        "FIXO", false
    		    ),

    		    // 15) Happy Path 2 (dados distintos mas todos válidos, inclusive telefone celular)
    		    Arguments.of(
    		        "Roberto Fonseca", "RF", "111.888.999-77", "1982/12/10",
    		        "Cliente VIP, tratar bem", "seringueiras", "Av. Sete", "Centro",
    		        "101", "54321-999", "Ponto de referência qualquer", "99999-0000",
    		        "CELULAR", true
    		    )

   
    		);
    }

    @AfterEach
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}

