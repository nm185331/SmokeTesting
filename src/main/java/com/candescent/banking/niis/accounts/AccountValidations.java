package com.candescent.banking.niis.accounts;

import com.candescent.banking.niis.utils.AttachScreenshot;
import com.candescent.banking.niis.utils.ConfigLoader;
import com.candescent.banking.niis.utils.fileLoader;
import com.microsoft.playwright.*;
import io.qameta.allure.Step;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AccountValidations {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    Properties configs;
    Properties selectors;
    Map<String, Map<String, String>> accountsMap;
    String env;
    AccountValidations(String env){
        this.env=env;
    }

    @Step("Validate all accounts")
    public  void validateAccounts() throws IOException {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext(new Browser.NewContextOptions()
                .setStorageStatePath(Paths.get("storage/login-state.json"))
        );
        page = context.newPage();
        configs = ConfigLoader.loadConfig(env);
        selectors = new Properties();
        String selectorPath = "src/main/resources/selector-" + env + ".properties";
        Properties selectors = fileLoader.loadProperties(selectorPath);
        String homeUrl = configs.getProperty("homeUrl");
        page.navigate(homeUrl);
        page.waitForTimeout(5000);
        page.waitForSelector(selectors.getProperty("accountsContainer"));
        AttachScreenshot.attachScreenshotToAllure(page, "Accounts Page");
        accountsMap = new HashMap<>();
        Locator accountsLocator = page.locator(selectors.getProperty("accountsContainer"));
        int count = accountsLocator.count();
        System.out.println("Account blocks found: " + count);

        for (int j = 0; j < count; j++) {
            Locator account = accountsLocator.nth(j);
            String name = account.locator(selectors.getProperty("accountTitle")).innerText().trim();

            if (name == null || name.isEmpty()) continue;

            Locator attrContainer = account.locator(selectors.getProperty("accountAttributes"));
            int attrCount = attrContainer.count();

            String available = "0.00";
            String ledger = "0.00";

            for (int k = 0; k < attrCount; k++) {
                Locator attr = attrContainer.nth(k);
                String label = attr.locator(selectors.getProperty("accountAttributeLabel")).innerText().trim().toLowerCase();
                String value = attr.locator(selectors.getProperty("accountAttributeValue")).innerText().trim()
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
        for (Map.Entry<String, Map<String, String>> entry : accountsMap.entrySet()) {
            String accountName = entry.getKey();
            Map<String, String> balances = entry.getValue();

            validateAccountNameFormat(accountName);
            validateBalancesNonNegative(accountName, balances);
            validateBalanceCharacters(accountName, balances);
            // Add more validations as needed
        }
    }


        private void validateAccountNameFormat(String accountName) {
        String pattern = "^.+ \\*.+$";

        assert accountName.matches(pattern) :
                "Account name does not match required format '<name> *<3-digits>': " + accountName;
    }

    @Step("Validate balances non-negative for: {accountName}")
    private static void validateBalancesNonNegative(String accountName, Map<String, String> balances) {
        double available = Double.parseDouble(balances.get("available"));
        double ledger = Double.parseDouble(balances.get("ledger"));

        assert available >= 0 : "Available balance is negative for: " + accountName;
        assert ledger >= 0 : "Ledger balance is negative for: " + accountName;
    }




    @Step("Validate balances contain only allowed characters for: {accountName}")
    private static void validateBalanceCharacters(String accountName, Map<String, String> balances) {
        String available = balances.get("available");
        String ledger = balances.get("ledger");

        String allowedPattern = "^[0-9+\\-\\.:]+$";

        assert available.matches(allowedPattern) :
                "Available balance contains invalid characters for: " + accountName + " (Value: " + available + ")";
        assert ledger.matches(allowedPattern) :
                "Ledger balance contains invalid characters for: " + accountName + " (Value: " + ledger + ")";
    }


}