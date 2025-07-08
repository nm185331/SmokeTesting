package com.ncr.banking.niis.login;
import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;
import com.ncr.banking.niis.utils.AttachScreenshot;
import com.ncr.banking.niis.utils.ConfigLoader;
import com.ncr.banking.niis.utils.selectorsLoader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class Login {

    private final Properties config;
    String env;

    public Login(String env) throws IOException {
        this.config= ConfigLoader.loadConfig(env);
        this.env=env;
    }

    public Page performLogin() throws IOException {

        String selectorFile="src/main/resources/selector-"+env+".properties";
        Properties selectors= selectorsLoader.loadProperties(selectorFile);

        Playwright playwright=Playwright.create();
        Browser browser;
        browser=playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        Page page=browser.newPage();

        String username = config.getProperty("username");
        String password = config.getProperty("password");
        String loginUrl = config.getProperty("baseUrl");
        String inputCode=config.getProperty("inputCode");
        String usernameSelector = selectors.getProperty("usernameSelector");
        String passwordSelector = selectors.getProperty("passwordSelector");
        String loginButtonSelector = selectors.getProperty("loginButtonSelector");
        String secureLoginSelector = selectors.getProperty("secureLoginSelector");
        String codeInputField = selectors.getProperty("codeInputField");
        String expectedErrorMessage = selectors.getProperty("errorMessage");

        page.navigate(loginUrl, new Page.NavigateOptions().setTimeout(60000).setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        page.fill(usernameSelector, username);
        page.waitForTimeout(500);
        page.fill(passwordSelector, password);
        page.click(loginButtonSelector);
        System.out.println("Clicking Login");
        page.waitForTimeout(7000);
        try {
            String errorMessageSelector = selectors.getProperty("errorMessageSelector");
            Locator errorLocator = page.locator(errorMessageSelector);
            errorLocator.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            PlaywrightAssertions.assertThat(errorLocator).containsText(expectedErrorMessage);

            // ❌ Login Failed
            System.out.println("Login Failed");
            throw new AssertionError("Login failed: Incorrect credentials or unexpected error message.");

        } catch (PlaywrightException e) {
            // ✅ Login Successful
            System.out.println("Login Success");

            page.click(secureLoginSelector);
            page.fill(codeInputField, inputCode);
            page.press(codeInputField, "Enter");
            page.waitForTimeout(20000);
            page.waitForLoadState(LoadState.LOAD);
            AttachScreenshot.attachScreenshotToAllure(page);
            page.context().storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get("storage/login-state.json")));
        }

//        browser.close();
//        playwright.close();
        return page;

    }
}