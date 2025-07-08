package com.ncr.banking.niis.accounts;

import io.qameta.allure.Step;

import java.util.Map;

public class AccountValidations {

    @Step("Validate all accounts")
    public static void validateAccounts(Map<String, Map<String, String>> accountsMap) {
        for (Map.Entry<String, Map<String, String>> entry : accountsMap.entrySet()) {
            String accountName = entry.getKey();
            Map<String, String> balances = entry.getValue();

            validateAccountNameFormat(accountName);
            validateBalancesNonNegative(accountName, balances);
            validateAccountCode(accountName);
            validateBalanceCharacters(accountName,balances);
            // Add more validations as needed
        }
    }

    @Step("Validate account name format for: {accountName}")
    private static void validateAccountNameFormat(String accountName) {
        String pattern = "^.+ \\*\\d{3}$";

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

    @Step("Validate account code format for: {accountName}")
    private static void validateAccountCode(String accountName) {
        assert accountName.contains("*") :
                "Account name does not contain '*' as expected: " + accountName;
    }


    @Step("Validate balances contain only allowed characters for: {accountName}")
    private static void validateBalanceCharacters(String accountName, Map<String, String> balances) {
        String available = balances.get("available");
        String ledger = balances.get("ledger");

        String allowedPattern = "^[0-9+\\-\\.]+$";

        assert available.matches(allowedPattern) :
                "Available balance contains invalid characters for: " + accountName + " (Value: " + available + ")";
        assert ledger.matches(allowedPattern) :
                "Ledger balance contains invalid characters for: " + accountName + " (Value: " + ledger + ")";
    }


}