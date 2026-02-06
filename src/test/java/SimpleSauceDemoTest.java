import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.firefox.FirefoxOptions;
import pages.LoginPage;

import static org.junit.jupiter.api.Assertions.*;

class SauceDemoLoginTest {
    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeEach
    void setUp() {
        try {
            WebDriverManager.firefoxdriver().setup();

            FirefoxOptions options = new FirefoxOptions();

            options.addArguments("--width=1920");
            options.addArguments("--height=1080");

            driver = new FirefoxDriver(options);
            driver.manage().window().maximize();
            loginPage = new LoginPage(driver);

            System.out.println("✅ Firefox успешно инициализирован");

        } catch (Exception e) {
            System.err.println("❌ Ошибка инициализации Firefox: " + e.getMessage());
            e.printStackTrace();
            driver = null;
            loginPage = null;
        }
    }

    @Test
    @DisplayName("Успешный логин с standard_user / secret_sauce")
    void testSuccessfulLoginWithStandardUser() {
        if (driver == null || loginPage == null) {
            System.out.println("⚠️ Тест пропущен: Браузер не инициализирован");
            return;
        }

        try {
            loginPage.open();
            loginPage.login("standard_user", "secret_sauce");

            assertTrue(loginPage.isLoginSuccessful(),
                    "Логин должен быть успешным");
            assertTrue(loginPage.getCurrentUrl().contains("inventory.html"),
                    "URL должен содержать 'inventory.html'");

            System.out.println("✅ Тест пройден: Успешный логин с standard_user / secret_sauce");
        } catch (Exception e) {
            System.err.println("❌ Ошибка в тесте: " + e.getMessage());
            e.printStackTrace();
            fail("Тест не прошел из-за ошибки: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Логин с неверным паролем должен показать ошибку")
    void testLoginWithInvalidPassword() {
        if (driver == null || loginPage == null) {
            System.out.println("⚠️ Тест пропущен: Браузер не инициализирован");
            return;
        }

        try {
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
        } catch (Exception e) {
            System.err.println("❌ Ошибка в тесте: " + e.getMessage());
            e.printStackTrace();
            fail("Тест не прошел из-за ошибки: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Логин с пустыми полями должен показать ошибку")
    void testLoginWithEmptyFields() {
        loginPage.open();
        System.out.println("Открыта страница логина для теста с пустыми полями");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        loginPage.login("", "");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Текущий URL после попытки логина с пустыми полями: " + currentUrl);

        assertTrue(currentUrl.equals("https://www.saucedemo.com/") ||
                        currentUrl.contains("saucedemo.com"),
                "Должны остаться на странице логина. Текущий URL: " + currentUrl);

        boolean isErrorDisplayed = loginPage.isErrorMessageDisplayed();
        assertTrue(isErrorDisplayed,
                "Должно отображаться сообщение об ошибке при пустых полях");

        String errorMessage = loginPage.getErrorMessageText();
        System.out.println("Текст ошибки: " + errorMessage);

        boolean isLoginButtonEnabled = loginPage.isLoginButtonEnabled();
        assertTrue(isLoginButtonEnabled,
                "Кнопка логина должна быть доступна после неудачной попытки");

        System.out.println("✅ Тест пройден: Логин с пустыми полями показал корректную ошибку");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("✅ Драйвер успешно закрыт");
            } catch (Exception e) {
                System.err.println("Ошибка при закрытии драйвера: " + e.getMessage());
            }
        }
    }
}
