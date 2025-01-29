package net.originmobi.pdv;

import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class CriarNovaPessoaTest {

    protected WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    public static void configuraDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void createDriver() {
        
        driver = WebDriverManager.chromedriver().create();
        wait = new WebDriverWait(driver, 20);
        driver.get("http://localhost:8080/login");
        WebElement userField = driver.findElement(By.id("user"));
        userField.sendKeys("gerente");
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("123");
        WebElement loginButton = driver.findElement(By.id("btn-login"));
        loginButton.click();
        assertTrue(driver.findElement(By.className("info-usuario"))!=null);
    }

    @Test
    void CadastrarPessoa()  {
        WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[3]/div/div[8]/a")));
        link.click();
        WebElement novaPessoa = driver.findElement(By.xpath("//*[@id=\"btn-padrao\"]/a"));
        novaPessoa.click();
        WebElement nome = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nome")));
        
        nome.sendKeys("TestePessoa");
        WebElement apelido = driver.findElement(By.id("apelido"));
        
        apelido.sendKeys("TesteApelido");
        WebElement cpfcnpj = driver.findElement(By.id("cpfcnpj"));
        
        cpfcnpj.sendKeys("618.548.111-61");
        WebElement nascimento = driver.findElement(By.id("nascimento"));
        
        nascimento.sendKeys("2000/01/31");
        
        WebElement observacaoClick = driver.findElement(By.xpath("//*[@id=\"form_pessoa\"]/ul/li[1]/a"));
        observacaoClick.click();
        
        WebElement observacao = driver.findElement(By.id("observacao"));
        observacao.sendKeys("Observação no Teste");
        
        WebElement enderecoClick = driver.findElement(By.xpath("//*[@id=\"form_pessoa\"]/ul/li[2]/a"));
        enderecoClick.click();
        
        WebElement cidade = wait.until(ExpectedConditions.elementToBeClickable(By.id("cidade")));
        cidade.click();
        
        Select selectCidade = new Select(cidade);
        selectCidade.selectByVisibleText("Cacoal");
        
        cidade.sendKeys("Observação no Teste");
        WebElement rua = driver.findElement(By.id("rua"));
        
        rua.sendKeys("rua Teste");
        
        WebElement bairro = driver.findElement(By.id("bairro"));
        bairro.sendKeys("bairro no Teste");
        
        WebElement numero = driver.findElement(By.id("numero"));
        numero.sendKeys("2");
        
        WebElement cep = driver.findElement(By.id("cep"));
        cep.sendKeys("cep no Teste");
        
        WebElement referencia = driver.findElement(By.id("referencia"));
        referencia.sendKeys("referencia no Teste");

        WebElement contatooClick = driver.findElement(By.xpath("//*[@id=\"form_pessoa\"]/ul/li[3]/a"));
        
        contatooClick.click();

        WebElement fone = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fone")));
        fone.click();
        fone.sendKeys("fone no Teste");
        WebElement tipo = driver.findElement(By.id("tipo"));
        Select selectTipo = new Select(tipo);
        selectTipo.selectByVisibleText("FIXO");
        WebElement form = driver.findElement(By.id("form_pessoa"));
        observacaoClick.click();
        form.submit();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        // Capturar a mensagem do alerta
        String alertMessage = alert.getText();
        System.out.println("Texto do alerta: " + alertMessage);
        assertTrue(alertMessage.contains("Pessoa salva com sucesso"));

        // Aceitar o alerta
        alert.accept();
        
        


    }

    @AfterEach
    public void quitDriver() {
        driver.quit();
    }
}
