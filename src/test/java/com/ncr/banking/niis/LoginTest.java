package com.ncr.banking.niis;


import com.ncr.banking.niis.login.Login;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

public class LoginTest {

    @Test(
            groups = "login",
            description = "Perform login and save state")
    public void testLogin() throws IOException {
        Allure.step("Start login test");

        String env = System.getProperty("env", "qal");
        Allure.step("Running on environment: " + env);

        Login login = new Login(env);
        login.performLogin();

        Allure.step("Login performed successfully for environment: " + env);
        Assert.assertTrue(true, "Dummy assertion to appear in Allure");


        System.out.println("Login test finished for environment: " + env);
    }
}