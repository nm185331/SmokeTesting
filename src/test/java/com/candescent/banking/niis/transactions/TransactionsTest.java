package com.candescent.banking.niis.transactions;

import com.candescent.banking.niis.transfers.Transfer;
import org.testng.annotations.Test;

import java.io.IOException;

public class TransactionsTest {
    @Test(
            description = "To test whether transactions are properly loaded properly or not",
            dependsOnGroups = "login",
            groups = "read"
    )
    public  void testTransactions() throws IOException {
        String env = System.getProperty("env", "qal");
        ValidateTransactions transfer=new ValidateTransactions(env);
        transfer.validateTransactions();



    }
}
