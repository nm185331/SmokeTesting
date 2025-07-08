package com.ncr.banking.niis.accounts;
import com.microsoft.playwright.*;
import com.ncr.banking.niis.accounts.AccountValidations;
import com.ncr.banking.niis.utils.AttachScreenshot;
import com.ncr.banking.niis.utils.ConfigLoader;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class AccountValidationTest {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    Properties configs;

    @Test(
            dependsOnGroups = "login",
            description = "Collect account data and perform validations"
    )
    @Description("Collect account blocks from UI, build map, and perform multiple validations")

    public void accountDataCollectionTest() throws IOException {
        setup();

        Map<String, Map<String, String>> accountsMap = collectAccountsData(page);

        // Call your main folder validations
        AccountValidations.validateAccounts(accountsMap);
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Step("Setup Playwright and login context")
    private void setup() throws IOException {
        String env= System.getProperty("env", "qal");
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext(new Browser.NewContextOptions()
                .setStorageStatePath(Paths.get("storage/login-state.json"))
        );
        page = context.newPage();
        configs= ConfigLoader.loadConfig(env);
        String homeUrl = configs.getProperty("homeUrl");
        page.navigate(homeUrl);
        page.waitForTimeout(5000);
        page.waitForSelector(".dbk-accts-account");
        AttachScreenshot.attachScreenshotToAllure(page);
    }

    @Step("Collect account data from UI")
    private Map<String, Map<String, String>> collectAccountsData(Page page) {
        Map<String, Map<String, String>> accountsMap = new HashMap<>();

        Locator accountsLocator = page.locator(".dbk-accts-account");
        int count = accountsLocator.count();
        System.out.println("Account blocks found: " + count);

        for (int j = 0; j < count; j++) {
            Locator account = accountsLocator.nth(j);
            String name = account.locator(".dbk-accts-account__title").innerText().trim();
            if (name == null || name.isEmpty()) continue;

            Locator attrContainer = account.locator(".dbk-accts-account-attr");
            int attrCount = attrContainer.count();

            String available = "0.00";
            String ledger = "0.00";

            for (int k = 0; k < attrCount; k++) {
                Locator attr = attrContainer.nth(k);
                String label = attr.locator(".dbk-accts-account-attr__label").innerText().trim().toLowerCase();
                String value = attr.locator(".dbk-accts-account-attr__value").innerText().trim()
                        .replace("$", "").replace(",", "");

                if (label.contains("avail")) {
                    available = value;
                } else if (label.contains("current") || label.contains("balance**")) {
                    ledger = value;
                }
            }

            Map<String, String> data = new HashMap<>();
            data.put("available", available);
            data.put("ledger", ledger);

            accountsMap.put(name, data);
        }

        return accountsMap;
    }


}