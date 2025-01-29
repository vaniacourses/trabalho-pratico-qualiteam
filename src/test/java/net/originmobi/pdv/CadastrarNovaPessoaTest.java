package net.originmobi.pdv;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        wait = new WebDriverWait(driver, 20);
        driver.get("http://localhost:8080/login");
        WebElement userField = driver.findElement(By.id("user"));
        userField.sendKeys("gerente");
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("123");
        WebElement loginButton = driver.findElement(By.id("btn-login"));
        loginButton.click();
        assertTrue(driver.findElement(By.className("info-usuario")) != null);
    }

    @ParameterizedTest
    @MethodSource("providePessoaData")
    void CadastrarPessoa(String nome, String apelido, String cpfcnpj, String nascimento,
                                String observacao, String cidade, String rua, String bairro,
                                String numero, String cep, String referencia, String fone,
                                String tipo, boolean esperadoSucesso, String mensagemEsperada,String TipoInvalido,String xpath)  {

        // Navegar para a página de cadastro
        WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[3]/div/div[8]/a")));
        link.click();
        WebElement novaPessoa = driver.findElement(By.xpath("//*[@id=\"btn-padrao\"]/a"));
        novaPessoa.click();

        // Preencher o formulário
        WebElement nomeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nome")));
        nomeField.clear();
        nomeField.sendKeys(nome);

        WebElement apelidoField = driver.findElement(By.id("apelido"));
        apelidoField.clear();
        apelidoField.sendKeys(apelido);

        WebElement cpfcnpjField = driver.findElement(By.id("cpfcnpj"));
        cpfcnpjField.clear();
        cpfcnpjField.sendKeys(cpfcnpj);

        WebElement nascimentoField = driver.findElement(By.id("nascimento"));
        nascimentoField.clear();
        nascimentoField.sendKeys(nascimento);

        // Observação
        WebElement observacaoClick = driver.findElement(By.xpath("//*[@id=\"form_pessoa\"]/ul/li[1]/a"));
        observacaoClick.click();
        WebElement observacaoField = driver.findElement(By.id("observacao"));
        observacaoField.clear();
        observacaoField.sendKeys(observacao);

        // Endereço
        WebElement enderecoClick = driver.findElement(By.xpath("//*[@id=\"form_pessoa\"]/ul/li[2]/a"));
        enderecoClick.click();

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

        // Verificar resultado
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertMessage = alert.getText();
        System.out.println("Texto do alerta: " + alertMessage);

        if (esperadoSucesso) {
            assertTrue(alertMessage.contains(mensagemEsperada), "Mensagem de sucesso não encontrada.");
        } else {
            assertTrue(alertMessage.contains(mensagemEsperada), "Mensagem de erro não encontrada.");
        }

        alert.accept();
    }

    private static Stream<Arguments> providePessoaData() {
        return Stream.of(
            // Classe Equivalente: Dados válidos
            Arguments.of(
                "João Silva", "Silva", "123.456.789-00", "1990/05/20",
                "Nenhuma observação", "Cacoal", "Rua A", "Bairro B",
                "100", "12345-678", "Próximo ao parque", "99999-9999",
                "FIXO", true, "Pessoa salva com sucesso","SUCESSO",""
            ),
            // Classe Equivalente: Nome vazio
            Arguments.of(
                "", "Silva", "123.456.789-00", "1990/05/20",
                "Nenhuma observação", "Cacoal", "Rua A", "Bairro B",
                "100", "12345-678", "Próximo ao parque", "99999-9999",
                "FIXO", false, "Este campo é requerido.","NOME_VAZIO","//*[@id=\"nome-error\"]"
            ),
            // Classe Equivalente: CPF inválido
            Arguments.of(
                "Maria Souza", "Souza", "123.456.789-XX", "1985/12/15",
                "Nenhuma observação", "Cacoal", "Rua B", "Bairro C",
                "200", "87654-321", "Próximo ao mercado", "88888-8888",
                "MÓVEL", false, "Este campo é requerido.","CPF_VAZIO","//*[@id=\"cpfcnpj-error\"]"
            ),
            // Classe Equivalente: Data de nascimento futura
            Arguments.of(
                "Pedro Lima", "Lima", "987.654.321-00", "2050/01/01",
                "Nenhuma observação", "Cacoal", "Rua C", "Bairro D",
                "300", "11223-445", "Próximo à escola", "77777-7777",
                "FIXO", false, "Data de nascimento inválida"
            ),
            // Classe Equivalente: CEP inválido
            Arguments.of(
                "Ana Paula", "Paula", "111.222.333-44", "1995/07/10",
                "Nenhuma observação", "Cacoal", "Rua D", "Bairro E",
                "400", "ABCDE-123", "Próximo ao hospital", "66666-6666",
                "MÓVEL", false, "CEP inválido"
            )
            // Adicione mais casos conforme necessário
        );
    }

    @AfterEach
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
