package com.candescent.banking.niis.customers;

import com.candescent.banking.niis.customers.emailUpdate.EmailUpdate;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

import java.io.IOException;

public class AddressUpdateTest {
    @Test(
            description = "Update primary email and validate if change reflects correctly.",
            dependsOnGroups = "read",
            groups = "write"
    )
    @Description("Updates the user's primary email address and verifies the update is reflected.")
    public void addressUpdate() throws IOException {
        String env = System.getProperty("env", "qal");

        AddressUpdate updater = new AddressUpdate(env);
        updater.updateAddress();
    }
}
