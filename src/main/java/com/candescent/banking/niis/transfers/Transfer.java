package com.candescent.banking.niis.transfers;

import com.candescent.banking.niis.utils.AttachScreenshot;
import com.candescent.banking.niis.utils.ConfigLoader;
import com.candescent.banking.niis.utils.fileLoader;
import com.microsoft.playwright.*;
import io.qameta.allure.Allure;

import javax.imageio.IIOException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class Transfer {
    String env;
    Transfer(String env){
        this.env=env;
    }
    void performTransfer() throws IOException {
        Browser browser;
        Page page;
        Properties configs;
        configs= ConfigLoader.loadConfig(env);
        String selectorPath="src/main/resources/selector-"+env+".properties";
        Properties selectors = fileLoader.loadProperties(selectorPath);
//            String homeUrl=configs.getProperty("homeUrl");
        Playwright playwright=Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        BrowserContext context = browser.newContext(
                new Browser.NewContextOptions().setStorageStatePath(Paths.get("storage/login-state.json"))
        );
        page = context.newPage();
        String homeUrl = configs.getProperty("homeUrl");
        page.navigate(homeUrl);
        page.click(selectors.getProperty("transferButton"));
//            <button type="button" class="fluid-button dbk-accts-account-header__transfer-btn"><span class="fluid-button__content"><span class="fluid-button__icon fluid-icon-exchange"></span> <span class="fluid-button__text">Transfer</span></span></button>


        // --- Select FROM account ---
        Locator fromDropdown = page.locator(selectors.getProperty("accountDropDownFrom"));
        fromDropdown.click();
//            Locator fromDropdown = page.locator("#accountDropdownFrom");
        Locator fromAccountOptions = page.locator(selectors.getProperty("fromAccountOptions"));
        Locator fromAccountOption = fromAccountOptions.first();

        String fromAccountText = fromAccountOption.innerText();

        fromAccountOption.click();


        // --- Select TO account (second account) ---
        Locator toDropdown = page.locator(selectors.getProperty("accountDropDownTo"));
        toDropdown.click();
        Locator toAccountOptions = page.locator(selectors.getProperty("toAccountOptions"));
        Locator toAccountOption = toAccountOptions.nth(1);

        String toAccountText = toAccountOption.innerText();

        toAccountOption.click();
        // --- Enter amount ---
        double transferAmount = 0.01;
        page.fill(selectors.getProperty("amountInputField"), String.valueOf(transferAmount));

        // --- Make transfer ---
        page.locator(selectors.getProperty("makeTransferButton")).click();
        page.waitForTimeout(5000); // Adjust for actual confirmation

        page.click(selectors.getProperty("transfersConfirmationConfirmButton"));
        page.waitForTimeout(5000);
        AttachScreenshot.attachScreenshotToAllure(page,"Transfer Success");
        Locator successMessage = page.locator(selectors.getProperty("transferSuccessMessage")).getByText("Success!");
        String text = successMessage.innerText();

        boolean isSuccess = text.trim().equals("Success!");

        if (!isSuccess) {
            throw new AssertionError("❌ Transfer failed: Unexpected success message text");
        }
        page.waitForTimeout(5000);
        Allure.step("Transfer success");
//            <button type="button" class="btn btn-secondary xs-width-100" id="makeAnotherTransferButton">Make another transfer</button>
        page.click(selectors.getProperty("makeAnotherTransferButton"));
        fromDropdown.click(new Locator.ClickOptions().setForce(true));
        fromAccountOption=fromAccountOptions.nth(1);
        fromAccountOption.click(new Locator.ClickOptions().setForce(true));

        toDropdown.click(new Locator.ClickOptions().setForce(true));
        toAccountOption=toAccountOptions.nth(0);
        toAccountOption.click(new Locator.ClickOptions().setForce(true));
        page.fill(selectors.getProperty("amountInputField"), String.valueOf(transferAmount));
        page.locator(selectors.getProperty("makeTransferButton")).click();
        page.waitForTimeout(5000); // Adjust for actual confirmation

        page.click(selectors.getProperty("transfersConfirmationConfirmButton"));
        page.waitForTimeout(5000);
        AttachScreenshot.attachScreenshotToAllure(page,"TransactionReverted");
        page.close();
        browser.close();
    }
}
