package com.candescent.banking.niis.customers.emailUpdate;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.candescent.banking.niis.utils.AttachScreenshot;
import com.candescent.banking.niis.utils.ConfigLoader;
import com.candescent.banking.niis.utils.fileLoader;
import io.qameta.allure.Allure;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class EmailUpdate {
    String env;
    public Playwright playwright;
    public Browser browser;
    public Page page;
    public EmailUpdate(String env){
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
        String newEmail = testData.getProperty("newPrimaryEmail");
        String currentPassword = configs.getProperty("password");
        String settingsPrimaryEmailAddressSelector=selectors.getProperty("settingsPrimaryEmailAddressSelector");

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

            // Click My Settings and Email Edit
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(selectors.getProperty("mySettingsLink"))).click();

            page.waitForTimeout(10000);

            //Screenshot before edit
            AttachScreenshot.attachScreenshotToAllure(page,"Before EmailEdit");
            String originalPrimaryEmail=page.locator(settingsPrimaryEmailAddressSelector).textContent();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(selectors.getProperty("primaryEmailEditLink"))).click();
            Allure.step("Clicked My Settings → Edit Email");

            // Update email
            page.locator(selectors.getProperty("primaryEmailTextbox")).press("ArrowRight");
            page.locator(selectors.getProperty("primaryEmailTextbox")).fill(newEmail);
            page.locator(selectors.getProperty("currentPasswordTextbox")).fill(currentPassword);
            page.locator(selectors.getProperty("saveEmailButton")).click();
            Allure.step("Submitted updated email and password");
            page.waitForTimeout(5000);
            // Assertion
            assertThat(page.locator(selectors.getProperty("updatedEmailLocator"))).isVisible();
            Allure.step("✅ Email updated and verified on page");

            // Screenshot
            AttachScreenshot.attachScreenshotToAllure(page,"After Updating Email");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(selectors.getProperty("primaryEmailEditLink"))).click();
            page.locator(selectors.getProperty("primaryEmailTextbox")).fill(originalPrimaryEmail);
            page.locator(selectors.getProperty("currentPasswordTextbox")).fill(currentPassword);
            page.locator(selectors.getProperty("saveEmailButton")).click();
            page.waitForTimeout(5000);
            Allure.step("🔄 Email reverted to original after test.");


        } catch (Exception e) {
            Allure.step("❌ Email update test failed: " + e.getMessage());
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