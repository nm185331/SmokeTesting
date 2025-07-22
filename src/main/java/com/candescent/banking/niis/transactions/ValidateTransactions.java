package com.candescent.banking.niis.transactions;

import com.candescent.banking.niis.utils.AttachScreenshot;
import com.candescent.banking.niis.utils.ConfigLoader;
import com.candescent.banking.niis.utils.fileLoader;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class ValidateTransactions {
    String env;
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    Properties configs;
    Properties selectors;
    Map<String, Map<String, String>> accountsMap;
    public ValidateTransactions(String env) {
        this.env=env;
    }
    public static boolean isValidDateFormat(String format, String value) {
        if (value == null || value.trim().isEmpty()) return false;

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            sdf.parse(value.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    public void validateTransactions() throws IOException {
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
        Locator accounts=page.locator(selectors.getProperty("accountTitle"));
        for (int j = 0; j < count; j++) {
            Locator account = accountsLocator.nth(j);
            String accountTitle = account.locator(selectors.getProperty("accountTitle")).innerText().trim();
            System.out.println(accountTitle);
            accounts.nth(j).locator("a").click();
//            page.locator("a[href*='accountId=L"+j+"']").click();
//            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(accountTitle)).click();
            boolean isTxnTableLoaded = false;
            boolean isNoTxnMessageVisible = false;

            for (int attempt = 0; attempt < 30; attempt++) {
                if (page.locator("tbody tr[id^='transaction-']").count() > 0) {
                    isTxnTableLoaded = true;
                    break;
                }
                if (page.locator("div.Table_zero-data-container__2CDx8").isVisible()) {
                    isNoTxnMessageVisible = true;
                    break;
                }
                page.waitForTimeout(1000); // wait 1 second before rechecking
            }
            AttachScreenshot.attachScreenshotToAllure(page,accountTitle);
            if (isNoTxnMessageVisible) {
                String messageText = page.locator("div.Table_zero-data-container__2CDx8").textContent().trim();
                if (messageText.contains("There are no transactions within this date range.")) {
                    System.out.println("✅ No transactions found — valid case.");
                    page.navigate(homeUrl); // navigate back before continuing
                    continue; // move to next account
                } else {
                    throw new RuntimeException("❌ Unexpected message while loading transactions: " + messageText);
                }
            }

            if (!isTxnTableLoaded) {
                throw new RuntimeException("❌ Timeout waiting for transactions table or no-txn message.");
            }

            page.waitForSelector("tbody tr[id^='transaction-']");
            Locator rows = page.locator("tbody tr[id^='transaction-']");
            int rowCount = rows.count();

            List<TransactionRow> transactions = new ArrayList<>();

            for (int i = 0; i < rowCount; i++) {
                Locator row = rows.nth(i);

                String date = row.locator("td:nth-child(1)").textContent().trim();
                String description = row.locator("td:nth-child(2) span").textContent().trim();
                String amount = row.locator("td:nth-child(3) span[id^='formattedMoney-amount-']").textContent().trim();
                String balance = row.locator("td:nth-child(4) span[id^='formattedMoney-balance-']").textContent().trim();
                boolean isValidDate = date.equalsIgnoreCase("Pending") || isValidDateFormat("MM/dd/yyyy", date);
                if (!isValidDate) {
                    throw new AssertionError("❌ Invalid date format or value: " + date);
                }

// Validate other fields
                if (description.isEmpty()) {
                    throw new AssertionError("❌ Description is empty for transaction with date: " + date);
                }
                if (amount.isEmpty()) {
                    throw new AssertionError("❌ Amount is empty for transaction with date: " + date);
                }
                if (balance.isEmpty()) {
                    throw new AssertionError("❌ Balance is empty for transaction with date: " + date);
                }

                transactions.add(new TransactionRow(date, description, amount, balance));
            }

            // ✅ Use the structured list however you want
            for (TransactionRow txn : transactions) {
                System.out.println("Date: " + txn.date);
                System.out.println("Description: " + txn.description);
                System.out.println("Amount: " + txn.amount);
                System.out.println("Balance: " + txn.balance);
                System.out.println("-----");
            }
            page.navigate(homeUrl);
        }

        }
}
