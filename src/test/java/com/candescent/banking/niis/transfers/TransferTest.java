package com.candescent.banking.niis.transfers;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

import java.io.IOException;


public class TransferTest {
    @Test(
            description = "To test whether transfer is working",
            dependsOnGroups = "read",
            groups = "write"
    )
    @Description("Making a transfer")
    public  void makeTransferTest() throws IOException {
        String env = System.getProperty("env", "qal");
        Transfer transfer=new Transfer(env);
        transfer.performTransfer();



    }


}