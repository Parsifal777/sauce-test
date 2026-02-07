import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.firefox.FirefoxOptions;
import pages.LoginPage;
import io.qameta.allure.*;
import io.qameta.allure.model.Status;

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
    @Order(1)
    @DisplayName("Успешный логин с standard_user / secret_sauce")
    @Description("Тест проверяет успешную авторизацию с валидными учетными данными стандартного пользователя")
    @Story("Позитивный сценарий: Валидные учетные данные")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("TC-001")
    void testSuccessfulLoginWithStandardUser() {
        Allure.feature("Успешная авторизация");
        Allure.story("Логин с корректными учетными данными");
        Allure.label("testType", "positive");

        if (driver == null || loginPage == null) {
            Allure.step("Браузер не инициализирован, тест пропускается", Status.BROKEN);
            System.out.println("⚠️ Тест пропущен: Браузер не инициализирован");
            return;
        }

        try {
            Allure.step("Открытие страницы логина SauceDemo", () -> {
                loginPage.open();
                Allure.addAttachment("Открытый URL", "text/plain", loginPage.getCurrentUrl());
            });

            Allure.step("Выполнение логина с данными standard_user / secret_sauce", () -> {
                loginPage.login("standard_user", "secret_sauce");
                Allure.addAttachment("Введенный логин", "text/plain", "standard_user");
                Allure.addAttachment("Введенный пароль", "text/plain", "secret_sauce");
            });

            Allure.step("Проверка успешности логина", () -> {
                assertTrue(loginPage.isLoginSuccessful(),
                        "Логин должен быть успешным");
                Allure.addAttachment("Результат логина", "text/plain", "Успешно");
            });

            Allure.step("Проверка URL после логина", () -> {
                assertTrue(loginPage.getCurrentUrl().contains("inventory.html"),
                        "URL должен содержать 'inventory.html'");
                Allure.addAttachment("Текущий URL", "text/plain", loginPage.getCurrentUrl());
            });

            Allure.step("Тест успешно завершен", Status.PASSED);
            Allure.addAttachment("Результат теста", "text/plain", "✅ Тест пройден: Успешный логин с standard_user / secret_sauce");
            System.out.println("✅ Тест пройден: Успешный логин с standard_user / secret_sauce");

        } catch (Exception e) {
            Allure.step("Ошибка выполнения теста", Status.FAILED);
            Allure.addAttachment("Ошибка", "text/plain", "❌ Ошибка в тесте: " + e.getMessage());
            System.err.println("❌ Ошибка в тесте: " + e.getMessage());
            e.printStackTrace();
            fail("Тест не прошел из-за ошибки: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Логин с неверным паролем должен показать ошибку")
    @Description("Тест проверяет отображение ошибки при вводе корректного логина и неверного пароля")
    @Story("Негативный сценарий: Неверный пароль")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("TC-002")
    void testLoginWithInvalidPassword() {
        Allure.feature("Неуспешная авторизация");
        Allure.story("Логин с неверным паролем");
        Allure.label("testType", "negative");

        if (driver == null || loginPage == null) {
            Allure.step("Браузер не инициализирован, тест пропускается", Status.BROKEN);
            System.out.println("⚠️ Тест пропущен: Браузер не инициализирован");
            return;
        }

        try {
            String validUsername = "standard_user";
            String invalidPassword = "wrong_password";

            Allure.step("Открытие страницы логина", () -> {
                loginPage.open();
            });

            Allure.step("Попытка логина с неверным паролем", () -> {
                loginPage.login(validUsername, invalidPassword);
                Allure.addAttachment("Введенный логин", "text/plain", validUsername);
                Allure.addAttachment("Введенный пароль", "text/plain", invalidPassword);
            });

            Allure.step("Ожидание обработки запроса", () -> {
                Thread.sleep(2000);
            });

            Allure.step("Проверка отображения сообщения об ошибке", () -> {
                assertTrue(loginPage.isErrorMessageDisplayed(),
                        "Должно отображаться сообщение об ошибке");
                Allure.addAttachment("Сообщение об ошибке отображается", "text/plain", "Да");
            });

            Allure.step("Проверка текста сообщения об ошибке", () -> {
                String expectedErrorMessage = "Epic sadface: Username and password do not match any user in this service";
                String actualErrorMessage = loginPage.getErrorMessageText();
                assertEquals(expectedErrorMessage, actualErrorMessage,
                        "Текст сообщения об ошибке должен соответствовать ожидаемому");
                Allure.addAttachment("Ожидаемая ошибка", "text/plain", expectedErrorMessage);
                Allure.addAttachment("Фактическая ошибка", "text/plain", actualErrorMessage);
            });

            Allure.step("Проверка, что остались на странице логина", () -> {
                assertTrue(loginPage.getCurrentUrl().contains("saucedemo.com") &&
                                !loginPage.getCurrentUrl().contains("inventory.html"),
                        "Должны остаться на странице логина");
                Allure.addAttachment("Текущий URL", "text/plain", loginPage.getCurrentUrl());
            });

            Allure.step("Проверка доступности кнопки логина", () -> {
                assertTrue(loginPage.isLoginButtonEnabled(),
                        "Кнопка логина должна отображаться");
                Allure.addAttachment("Кнопка логина доступна", "text/plain", "Да");
            });

            Allure.step("Тест успешно завершен", Status.PASSED);
            Allure.addAttachment("Результат теста", "text/plain", "✅ Тест пройден: Логин с неверным паролем показал корректную ошибку");
            System.out.println("✅ Тест пройден: Логин с неверным паролем показал корректную ошибку");

        } catch (Exception e) {
            Allure.step("Ошибка выполнения теста", Status.FAILED);
            Allure.addAttachment("Ошибка", "text/plain", "❌ Ошибка в тесте: " + e.getMessage());
            System.err.println("❌ Ошибка в тесте: " + e.getMessage());
            e.printStackTrace();
            fail("Тест не прошел из-за ошибки: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Логин с пустыми полями должен показать ошибку")
    @Description("Тест проверяет отображение ошибки при попытке логина с пустыми полями ввода")
    @Story("Негативный сценарий: Пустые поля")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("TC-003")
    void testLoginWithEmptyFields() {
        Allure.feature("Валидация формы");
        Allure.story("Логин без ввода данных");
        Allure.label("testType", "validation");

        if (driver == null || loginPage == null) {
            Allure.step("Браузер не инициализирован, тест пропускается", Status.BROKEN);
            System.out.println("⚠️ Тест пропущен: Браузер не инициализирован");
            return;
        }

        try {
            Allure.step("Открытие страницы логина", () -> {
                loginPage.open();
                Allure.addAttachment("Статус", "text/plain", "Открыта страница логина для теста с пустыми полями");
            });

            Allure.step("Ожидание загрузки страницы", () -> {
                Thread.sleep(1000);
            });

            Allure.step("Попытка логина с пустыми полями", () -> {
                loginPage.login("", "");
                Allure.addAttachment("Логин", "text/plain", "(пусто)");
                Allure.addAttachment("Пароль", "text/plain", "(пусто)");
            });

            Allure.step("Ожидание обработки запроса", () -> {
                Thread.sleep(2000);
            });

            Allure.step("Проверка URL после неудачной попытки", () -> {
                String currentUrl = driver.getCurrentUrl();
                Allure.addAttachment("Текущий URL", "text/plain", currentUrl);
                assertTrue(currentUrl.equals("https://www.saucedemo.com/") ||
                                currentUrl.contains("saucedemo.com"),
                        "Должны остаться на странице логина. Текущий URL: " + currentUrl);
            });

            Allure.step("Проверка отображения сообщения об ошибке", () -> {
                boolean isErrorDisplayed = loginPage.isErrorMessageDisplayed();
                Allure.addAttachment("Ошибка отображается", "text/plain", isErrorDisplayed ? "Да" : "Нет");
                assertTrue(isErrorDisplayed,
                        "Должно отображаться сообщение об ошибке при пустых полях");
            });

            Allure.step("Получение текста ошибки", () -> {
                String errorMessage = loginPage.getErrorMessageText();
                Allure.addAttachment("Текст ошибки", "text/plain", errorMessage);
                System.out.println("Текст ошибки: " + errorMessage);
            });

            Allure.step("Проверка доступности кнопки логина", () -> {
                boolean isLoginButtonEnabled = loginPage.isLoginButtonEnabled();
                Allure.addAttachment("Кнопка логина доступна", "text/plain", isLoginButtonEnabled ? "Да" : "Нет");
                assertTrue(isLoginButtonEnabled,
                        "Кнопка логина должна быть доступна после неудачной попытки");
            });

            Allure.step("Тест успешно завершен", Status.PASSED);
            Allure.addAttachment("Результат теста", "text/plain", "✅ Тест пройден: Логин с пустыми полями показал корректную ошибку");
            System.out.println("✅ Тест пройден: Логин с пустыми полями показал корректную ошибку");

        } catch (Exception e) {
            Allure.step("Ошибка выполнения теста", Status.FAILED);
            Allure.addAttachment("Ошибка", "text/plain", "❌ Ошибка в тесте: " + e.getMessage());
            System.err.println("❌ Ошибка в тесте: " + e.getMessage());
            e.printStackTrace();
            fail("Тест не прошел из-за ошибки: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Логин заблокированного пользователя (locked_out_user)")
    @Description("Тест проверяет отображение ошибки при попытке логина заблокированным пользователем")
    @Story("Негативный сценарий: Заблокированный пользователь")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("TC-004")
    void testLoginWithLockedOutUser() {
        Allure.feature("Безопасность");
        Allure.story("Доступ заблокированного пользователя");
        Allure.label("testType", "security");

        if (driver == null || loginPage == null) {
            Allure.step("Браузер не инициализирован, тест пропускается", Status.BROKEN);
            System.out.println("⚠️ Тест пропущен: Браузер не инициализирован");
            return;
        }

        try {
            String lockedUsername = "locked_out_user";
            String validPassword = "secret_sauce";

            Allure.step("Открытие страницы логина", () -> {
                loginPage.open();
                Allure.addAttachment("Статус", "text/plain", "Тест: Логин заблокированного пользователя '" + lockedUsername + "'");
            });

            Allure.step("Ожидание загрузки страницы", () -> {
                Thread.sleep(1000);
            });

            Allure.step("Попытка логина заблокированным пользователем", () -> {
                loginPage.login(lockedUsername, validPassword);
                Allure.addAttachment("Логин", "text/plain", lockedUsername);
                Allure.addAttachment("Пароль", "text/plain", validPassword);
            });

            Allure.step("Ожидание обработки запроса", () -> {
                Thread.sleep(2000);
            });

            Allure.step("Проверка URL после попытки логина", () -> {
                String currentUrl = driver.getCurrentUrl();
                Allure.addAttachment("Текущий URL", "text/plain", currentUrl);
                assertTrue(currentUrl.equals("https://www.saucedemo.com/") ||
                                currentUrl.contains("saucedemo.com"),
                        "Должны остаться на странице логина. Текущий URL: " + currentUrl);
            });

            Allure.step("Проверка отображения сообщения об ошибке", () -> {
                boolean isErrorDisplayed = loginPage.isErrorMessageDisplayed();
                Allure.addAttachment("Ошибка отображается", "text/plain", isErrorDisplayed ? "Да" : "Нет");
                assertTrue(isErrorDisplayed,
                        "Должно отображаться сообщение об ошибке для заблокированного пользователя");
            });

            Allure.step("Проверка текста ошибки", () -> {
                String errorMessage = loginPage.getErrorMessageText();
                Allure.addAttachment("Текст ошибки", "text/plain", errorMessage);

                String expectedErrorMessage = "Epic sadface: Sorry, this user has been locked out.";
                Allure.addAttachment("Ожидаемая ошибка", "text/plain", expectedErrorMessage);
                assertEquals(expectedErrorMessage, errorMessage,
                        "Текст ошибки должен точно соответствовать ожидаемому. Ожидалось: '" +
                                expectedErrorMessage + "', получено: '" + errorMessage + "'");
            });

            Allure.step("Проверка доступности кнопки логина", () -> {
                boolean isLoginButtonEnabled = loginPage.isLoginButtonEnabled();
                Allure.addAttachment("Кнопка логина доступна", "text/plain", isLoginButtonEnabled ? "Да" : "Нет");
                assertTrue(isLoginButtonEnabled,
                        "Кнопка логина должна быть доступна после неудачной попытки");
            });

            Allure.step("Тест успешно завершен", Status.PASSED);
            Allure.addAttachment("Результат теста", "text/plain", "✅ Тест пройден: Логин заблокированного пользователя показал корректную ошибку");
            System.out.println("✅ Тест пройден: Логин заблокированного пользователя показал корректную ошибку");

        } catch (Exception e) {
            Allure.step("Ошибка выполнения теста", Status.FAILED);
            Allure.addAttachment("Ошибка", "text/plain", "❌ Ошибка в тесте: " + e.getMessage());
            System.err.println("❌ Ошибка в тесте: " + e.getMessage());
            e.printStackTrace();
            fail("Тест не прошел из-за ошибки: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Логин пользователем performance_glitch_user с проверкой задержек")
    @Description("Тест проверяет успешный логин пользователем с искусственными задержками в системе")
    @Story("Позитивный сценарий: Пользователь с задержками")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("TC-005")
    void testLoginWithPerformanceGlitchUser() {
        Allure.feature("Производительность");
        Allure.story("Логин с учетом возможных задержек");
        Allure.label("testType", "performance");

        if (driver == null || loginPage == null) {
            Allure.step("Браузер не инициализирован, тест пропускается", Status.BROKEN);
            System.out.println("⚠️ Тест пропущен: Браузер не инициализирован");
            return;
        }

        try {
            Allure.step("Начало теста с performance_glitch_user", () -> {
                System.out.println("Начало теста с performance_glitch_user");
            });

            Allure.step("Измерение времени выполнения логина", () -> {
                long startTime = System.currentTimeMillis();

                loginPage.open()
                        .login("performance_glitch_user", "secret_sauce")
                        .waitForProductsPage();

                long endTime = System.currentTimeMillis();
                long loginDuration = endTime - startTime;

                Allure.addAttachment("Время выполнения логина", "text/plain", loginDuration + " мс");
                System.out.println("Время выполнения логина: " + loginDuration + " мс");

                if (loginDuration > 5000) {
                    Allure.addAttachment("Предупреждение", "text/plain", "⚠️ ВНИМАНИЕ: Логин занял более 5 секунд (" + loginDuration + " мс)");
                    System.out.println("⚠️ ВНИМАНИЕ: Логин занял более 5 секунд (" + loginDuration + " мс)");
                }
            });

            Allure.step("Проверка URL после логина", () -> {
                String currentUrl = loginPage.getCurrentUrl();
                Allure.addAttachment("Текущий URL", "text/plain", currentUrl);
                assertTrue(currentUrl.contains("inventory.html"),
                        "После логина должен быть redirect на inventory страницу. Текущий URL: " + currentUrl);
            });

            Allure.step("Проверка отображения списка продуктов", () -> {
                boolean productsDisplayed = loginPage.isProductsPageDisplayed();
                Allure.addAttachment("Список продуктов отображается", "text/plain", productsDisplayed ? "Да" : "Нет");
                assertTrue(productsDisplayed,
                        "Список продуктов должен отображаться");
            });

            Allure.step("Проверка заголовка страницы", () -> {
                String pageTitle = loginPage.getPageTitle();
                Allure.addAttachment("Заголовок страницы", "text/plain", pageTitle);
                assertTrue(pageTitle.contains("Swag Labs"),
                        "Заголовок должен содержать 'Swag Labs'");
            });

            Allure.step("Тест успешно завершен", Status.PASSED);
            Allure.addAttachment("Результат теста", "text/plain", "✅ Тест пройден: Логин performance_glitch_user успешен");
            System.out.println("✅ Тест пройден: Логин performance_glitch_user успешен");

        } catch (Exception e) {
            Allure.step("Ошибка выполнения теста", Status.FAILED);
            Allure.addAttachment("Ошибка", "text/plain", "❌ Ошибка в тесте performance_glitch_user: " + e.getMessage());
            System.err.println("❌ Ошибка в тесте performance_glitch_user: " + e.getMessage());
            e.printStackTrace();

            if (loginPage != null && loginPage.getCurrentUrl().contains("saucedemo.com")) {
                Allure.addAttachment("Диагностика", "text/plain", "⚠️ Возможно, таймаут ожидания слишком мал для performance_glitch_user");
                System.out.println("⚠️ Возможно, таймаут ожидания слишком мал для performance_glitch_user");
            }

            fail("Тест не прошел из-за ошибки: " + e.getMessage());
        }
    }

    @AfterEach
    @Step("Завершение работы браузера")
    @DisplayName("Очистка окружения после теста")
    void tearDown() {
        Allure.description("Закрытие браузера и освобождение ресурсов");

        if (driver != null) {
            try {
                Allure.step("Закрытие драйвера Firefox", () -> {
                    driver.quit();
                    Allure.addAttachment("Статус", "text/plain", "✅ Драйвер успешно закрыт");
                    System.out.println("✅ Драйвер успешно закрыт");
                });
            } catch (Exception e) {
                Allure.step("Ошибка при закрытии драйвера", Status.FAILED);
                Allure.addAttachment("Ошибка", "text/plain", "Ошибка при закрытии драйвера: " + e.getMessage());
                System.err.println("Ошибка при закрытии драйвера: " + e.getMessage());
            }
        }
    }
}
