package com.candescent.banking.niis.accounts;

import io.qameta.allure.Description;
import org.testng.annotations.Test;

import java.io.IOException;

public class NicknameUpdateTest {
    @Test(
            description = "Update account nickname and validate if change reflects correctly.",
            dependsOnGroups = "read",
            groups = "write"
    )
    @Description("Updates the user's primary email address and verifies the update is reflected.")
    public void updateAccountNickname() throws IOException {
        String env = System.getProperty("env", "qal");

        AccountNicknameUpdate updater = new AccountNicknameUpdate(env);
        updater.updatePrimaryEmail();
    }
}
