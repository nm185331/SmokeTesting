package com.ncr.banking.niis.customers;

import com.microsoft.playwright.options.LoadState;
import com.ncr.banking.niis.utils.AttachScreenshot;
import com.ncr.banking.niis.utils.ConfigLoader;
import com.ncr.banking.niis.utils.RegexValidator;
import com.ncr.banking.niis.utils.selectorsLoader;
import io.qameta.allure.Allure;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.annotations.Test;
import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class ContactDetailsTest {

    public Playwright playwright;
    public Browser browser;
    public Page page;
    public SettingsPage settingsPage;
    Properties selectors;
    Properties configs;
    String settingsPhone1Selector,settingsPrimaryEmailAddressSelector;

    @BeforeClass
    public void setup() throws IOException {
        String env = System.getProperty("env", "qal");
        System.out.println("Env from system property: " + env);

        String selectorFile = "src/main/resources/selector-" + env + ".properties";
        selectors = selectorsLoader.loadProperties(selectorFile);
        configs= ConfigLoader.loadConfig(env);

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        BrowserContext context = browser.newContext(
                new Browser.NewContextOptions().setStorageStatePath(Paths.get("storage/login-state.json"))
        );
        page = context.newPage();

        String homeUrl = configs.getProperty("homeUrl");
        String settingsPageSelector = selectors.getProperty("settingsPageSelector");
        settingsPhone1Selector=selectors.getProperty("settingsPhone1Selector");
        settingsPrimaryEmailAddressSelector=selectors.getProperty("settingsPrimaryEmailAddressSelector");
        page.navigate(homeUrl);
        page.click(settingsPageSelector);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        settingsPage = new SettingsPage(page);
    }

    @Test(
            dependsOnGroups = "login",
            description = "Validate email and phone number presence and format"
    )
    public void validateEmailAndPhone() {
        Allure.step("Fetching email and phone values from Settings page");

        String email = settingsPage.getEmail(settingsPrimaryEmailAddressSelector);
        String phone = settingsPage.getPhone(settingsPhone1Selector);

        // Attach raw values to Allure
        Allure.addAttachment("Fetched Email", email);
        Allure.addAttachment("Fetched Phone", phone);

        // Email checks
        Allure.step("Checking that email is not null");
        Assert.assertNotNull(email, "Email should not be null");

        Allure.step("Checking that email is not empty");
        Assert.assertFalse(email.isEmpty(), "Email should not be empty");

        Allure.step("Validating email format");
        Assert.assertTrue(RegexValidator.isValidEmail(email), "Email format is invalid: " + email);

        // Phone checks
        Allure.step("Checking that phone is not null");
        Assert.assertNotNull(phone, "Phone should not be null");

        Allure.step("Checking that phone is not empty");
        Assert.assertFalse(phone.isEmpty(), "Phone should not be empty");

        Allure.step("Validating phone format");
        Assert.assertTrue(RegexValidator.isValidPhone(phone), "Phone format is invalid: " + phone);

        // Take and attach screenshot to Allure (if you want)
        AttachScreenshot.attachScreenshotToAllure(page);
        browser.close();
        playwright.close();
    }


}