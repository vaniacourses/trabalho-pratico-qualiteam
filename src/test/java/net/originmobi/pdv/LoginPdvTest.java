package net.originmobi.pdv;

import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class LoginPdvTest {

    protected WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    public static void configuraDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void createDriver() {
        driver = WebDriverManager.chromedriver().create();
        wait = new WebDriverWait(driver, 20);
        driver.get("http://localhost:8080/login");
    }

    @Test
    public void testaLogin() throws InterruptedException {
        // Localiza o campo de usuário e insere o valor
        WebElement userField = driver.findElement(By.id("user"));
        userField.sendKeys("gerente"); // Substitua "gerente" pelo usuário desejado

        // Localiza o campo de senha e insere o valor
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("123"); // Substitua "123" pela senha desejada

        // Localiza o botão de login e clica
        WebElement loginButton = driver.findElement(By.id("btn-login"));
        loginButton.click();

        // Aguarda para verificar se o login foi bem-sucedido // Não recomendado em produção, substitua por WebDriverWait

        // Verifica se o título ou URL indica que o login foi bem-sucedido
        WebElement infoUsuario =wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("info-usuario")));
        assertTrue(infoUsuario != null); // Correção de sintaxe
    }

    @AfterEach
    public void quitDriver() {
        driver.quit();
    }
}
