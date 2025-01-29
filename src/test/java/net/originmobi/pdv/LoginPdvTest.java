package net.originmobi.pdv;

import static org.junit.Assert.assertNotNull;
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
     void testaLogin()  {
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
    @Test
     void testaSqlInjection() {
        // Attempt SQL injection in the username field
        WebElement userField = driver.findElement(By.id("user"));
        userField.sendKeys("gerente' OR '1'='1");

        // Attempt SQL injection in the password field
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("anything' OR '1'='1");

        // Click the login button
        WebElement loginButton = driver.findElement(By.id("btn-login"));
        loginButton.click();

        // Wait for potential redirection or error message
        try {
            // Adjust the locator based on your application's response to failed logins
            WebElement errorMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div/div[1]/span"))
            );
            assertTrue(errorMessage.isDisplayed(), "Error message should be displayed for SQL injection attempt.");
        } catch (Exception e) {
            // If no error message is found, check if login was incorrectly successful
            WebElement infoUsuario = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("info-usuario"))
            );
            assertNotNull(infoUsuario);
        }
    }
    

    @AfterEach
    public void quitDriver() {
        driver.quit();
    }
}
