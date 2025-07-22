package com.candescent.banking.niis;


import com.candescent.banking.niis.login.Login;
import io.qameta.allure.Allure;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class LoginTest {

    @Test(
            groups = "login",
            description = "Perform login and save state")
    public void testLogin() throws IOException {
        Allure.step("Start login test");
        try{
            String env = System.getProperty("env", "qal");
            Allure.step("Running on environment: " + env);

            Login login = new Login(env);
            login.performLogin();

            Allure.step("Login performed successfully for environment: " + env);

        }catch(AssertionError assertionError){
            Assert.fail("❌ Test failed: " + assertionError.getMessage());

        }




    }
}