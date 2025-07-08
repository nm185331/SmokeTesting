package com.ncr.banking.niis.accounts;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.util.Map;

public class AccountValidations {

    @Step("Validate all accounts")
    public static void validateAccounts(Map<String, Map<String, String>> accountsMap) {
        for (Map.Entry<String, Map<String, String>> entry : accountsMap.entrySet()) {
            String accountName = entry.getKey();
            Map<String, String> balances = entry.getValue();

            validateAccountNameCasing(accountName);
            validateBalancesNonNegative(accountName, balances);
            validateAccountCode(accountName);
            validateAvailableVsLedger(accountName, balances);
            // Add more validations here as needed
        }
    }

    @Step("Validate account name casing: {accountName}")
    private static void validateAccountNameCasing(String accountName) {
        for (String word : accountName.split(" ")) {
            if (!word.isEmpty() && Character.isLetter(word.charAt(0))) {
                Assert.assertTrue(Character.isUpperCase(word.charAt(0)),
                        "Word '" + word + "' is not title cased in account: " + accountName);
            }
        }
    }

    @Step("Validate balances non-negative for: {accountName}")
    private static void validateBalancesNonNegative(String accountName, Map<String, String> balances) {
        double available = Double.parseDouble(balances.get("available"));
        double ledger = Double.parseDouble(balances.get("ledger"));

        Assert.assertTrue(available >= 0, "Available balance is negative for: " + accountName);
        Assert.assertTrue(ledger >= 0, "Ledger balance is negative for: " + accountName);
    }

    @Step("Validate account code format for: {accountName}")
    private static void validateAccountCode(String accountName) {
        Assert.assertTrue(accountName.contains("*"),
                "Account name does not contain '*' as expected: " + accountName);
    }

    @Step("Validate ledger balance >= available balance for: {accountName}")
    private static void validateAvailableVsLedger(String accountName, Map<String, String> balances) {
        double available = Double.parseDouble(balances.get("available"));
        double ledger = Double.parseDouble(balances.get("ledger"));

        Assert.assertTrue(ledger >= available,
                "Ledger balance should be >= available balance for: " + accountName);
    }
}