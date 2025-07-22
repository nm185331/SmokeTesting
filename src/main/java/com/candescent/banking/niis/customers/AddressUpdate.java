package com.candescent.banking.niis.customers;

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

public class AddressUpdate {
    String env;
    public Playwright playwright;
    public Browser browser;
    public Page page;
    public AddressUpdate(String env) {
        this.env=env;
    }


    public void updateAddress() throws IOException {
        ConfigLoader configLoader = new ConfigLoader();
        Properties configs = configLoader.loadConfig(env);
        String selectorPath="src/main/resources/selector-"+env+".properties";
        Properties selectors = fileLoader.loadProperties(selectorPath);


        String homeUrl = configs.getProperty("homeUrl");
        String currentPassword = configs.getProperty("password");

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
            page.waitForTimeout(3000);
            AttachScreenshot.attachScreenshotToAllure(page,"Before Address Edit");
//            <span id="settingsAddress1PostalCode">07470</span>
            String originalPostalCode=page.locator(selectors.getProperty("settingsAddress1PostalCode")).textContent();
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(selectors.getProperty("contactEditLink"))).click();
            Allure.step("Clicked My Settings → Edit Contact Information");


            int numericCode = Integer.parseInt(originalPostalCode); // Converts to 0
            numericCode += 1; // Adds 1

            System.out.println("Updated postal code: " + numericCode); // Output: 1
            String newPostalCode = String.format("%05d", numericCode);
            page.locator(selectors.getProperty("settingsContactPostalCode0Input")).fill(String.valueOf(newPostalCode));
            page.waitForTimeout(5000);
            page.click(selectors.getProperty("saveContactButton"));
            page.waitForTimeout(5000);
            String updatedPostalCode=page.locator(selectors.getProperty("settingsAddress1PostalCode")).textContent();

            if (!updatedPostalCode.equals(newPostalCode)) {
                throw new AssertionError("❌ Postal code didnot update: expected 00000 but found " + updatedPostalCode);
            } else {
                System.out.println("✅ Postal code correctly saved as 99999");
            }
            AttachScreenshot.attachScreenshotToAllure(page,"After updateAddress");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(selectors.getProperty("contactEditLink"))).click();

            page.locator(selectors.getProperty("settingsContactPostalCode0Input")).fill( originalPostalCode);
            page.waitForTimeout(5000);
            page.click(selectors.getProperty("saveContactButton"));
            page.waitForTimeout(5000);
            Allure.step("Reverted to original address");
            AttachScreenshot.attachScreenshotToAllure(page,"reverted to original address");






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
