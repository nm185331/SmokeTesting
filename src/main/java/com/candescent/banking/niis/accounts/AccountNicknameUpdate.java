package com.candescent.banking.niis.accounts;

import com.candescent.banking.niis.utils.AttachScreenshot;
import com.candescent.banking.niis.utils.ConfigLoader;
import com.candescent.banking.niis.utils.fileLoader;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AccountNicknameUpdate {
    String env;
    public Playwright playwright;
    public Browser browser;
    public Page page;
    public AccountNicknameUpdate(String env) {
        this.env=env;
    }

    public void updatePrimaryEmail() throws IOException {
        ConfigLoader configLoader = new ConfigLoader();
        Properties configs = configLoader.loadConfig(env);
        String selectorPath="src/main/resources/selector-"+env+".properties";
        Properties selectors = fileLoader.loadProperties(selectorPath);
        String testDataFile="src/main/java/com/candescent/banking/niis/customers/emailUpdate/TestData.properties";

        Properties testData = fileLoader.loadProperties(testDataFile);

        String homeUrl = configs.getProperty("homeUrl");
        Page page = null;
        try {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions().setStorageStatePath(Paths.get("storage/login-state.json"))
            );
            page = context.newPage();

            // Navigate to URL
            page.navigate(homeUrl);
            page.waitForTimeout(3000);
            Allure.step("Navigated to Home URL");

            page.click(selectors.getProperty("accountSettingsButton"));
//            page.click("fluid-text-input");
//            page.locator(".dbk-accts-account-header__settings-btn").click();

// Step 2: Locate the input field using class selector and store original value
            Locator accountNameInput = page.locator(selectors.getProperty("inputForAccountNickname")).nth(0);
            String originalName = accountNameInput.inputValue();
            AttachScreenshot.attachScreenshotToAllure(page,"Original Nickname");

// Step 3: Update the account name
            String updatedName = originalName+"1";
            accountNameInput.fill(updatedName);
            page.waitForTimeout(5000);
// Optional: Click outside to trigger save
            page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Account Preferences").setExact(true)).click();

// Step 4: Verify the update
            String currentValue = accountNameInput.inputValue();
            if (currentValue.equals(updatedName)) {
                System.out.println("Account name updated successfully.");
                Allure.step("Account Nickname got updated successfully");
                AttachScreenshot.attachScreenshotToAllure(page,"Updated Nickname");
                // Step 5: Revert to original name
                accountNameInput.fill(originalName);
                page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Account Preferences").setExact(true)).click();

                System.out.println("Account name reverted to original.");
                Allure.step("Account name reverted");
                page.waitForTimeout(5000);
                AttachScreenshot.attachScreenshotToAllure(page,"Nickname reverted");
            } else {
                System.out.println("Failed to update account name.");
                throw new Exception();
            }

        } catch (Exception e) {
            Allure.step("❌ Account Nickname  update test failed: " + e.getMessage());
            if (page != null) {
                AttachScreenshot.attachScreenshotToAllure(page,"Error");

            }
            throw new RuntimeException("Failed to update primary email.", e);
        } finally {
            if (page != null) {
                page.context().browser().close();
            }
        }
    }
}
