package com.candescent.banking.niis.customers;

import com.candescent.banking.niis.customers.emailUpdate.EmailUpdate;
import io.qameta.allure.Description;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.io.IOException;

public class EmailUpdateTest {

    @Test(
            description = "Update primary email and validate if change reflects correctly.",
            dependsOnGroups = "read",
            groups = "write"
    )
    @Description("Updates the user's primary email address and verifies the update is reflected.")
    public void testEmailUpdate() throws IOException {
        String env = System.getProperty("env", "qal");

        EmailUpdate updater = new EmailUpdate(env);
        updater.updatePrimaryEmail();
    }
}