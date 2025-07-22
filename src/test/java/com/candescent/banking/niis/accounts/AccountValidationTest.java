package com.candescent.banking.niis.accounts;
import com.beust.ah.A;
import com.candescent.banking.niis.utils.fileLoader;
import com.microsoft.playwright.*;
import com.candescent.banking.niis.utils.AttachScreenshot;
import com.candescent.banking.niis.utils.ConfigLoader;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class AccountValidationTest {

    @Test(
            dependsOnGroups = "login",
            groups = "read",
            description = "Collect account data and perform validations"
    )
    @Description("Collect account blocks from UI, build map, and perform multiple validations")

    public void accountDataCollectionTest() throws IOException {
        String env=System.getProperty("env","qal");
        AccountValidations validate=new AccountValidations(env);
        validate.validateAccounts();
    }
}