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

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
