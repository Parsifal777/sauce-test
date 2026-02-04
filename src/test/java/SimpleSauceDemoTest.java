import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import pages.LoginPage;

import static org.junit.jupiter.api.Assertions.*;

class SauceDemoLoginTest {
    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        loginPage = new LoginPage(driver);
    }

    @Test
    @DisplayName("Успешный логин с standard_user / secret_sauce")
    void testSuccessfulLoginWithStandardUser() {

        loginPage.open();

        loginPage.login("standard_user", "secret_sauce");

        assertTrue(loginPage.isLoginSuccessful(),
                "Логин должен быть успешным");

        assertTrue(loginPage.getCurrentUrl().contains("inventory.html"),
                "URL должен содержать 'inventory.html'");

        System.out.println("✅ Тест пройден: Успешный логин с standard_user / secret_sauce");
    }

    @Test
    @DisplayName("Логин с неверным паролем должен показать ошибку")
    void testLoginWithInvalidPassword() {
        String validUsername = "standard_user";
        String invalidPassword = "wrong_password";

        loginPage.open();
        loginPage.login(validUsername, invalidPassword);

        assertTrue(loginPage.isErrorMessageDisplayed(),
                "Должно отображаться сообщение об ошибке");

        String expectedErrorMessage = "Epic sadface: Username and password do not match any user in this service";
        String actualErrorMessage = loginPage.getErrorMessageText();

        assertEquals(expectedErrorMessage, actualErrorMessage,
                "Текст сообщения об ошибке должен соответствовать ожидаемому");

        assertTrue(loginPage.getCurrentUrl().contains("saucedemo.com") &&
                        !loginPage.getCurrentUrl().contains("inventory.html"),
                "Должны остаться на странице логина");

        assertTrue(loginPage.isLoginButtonEnabled(),
                "Кнопка логина должна отображаться");
        System.out.println("✅ Тест пройден: Логин с неверным паролем показал корректную ошибку");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
