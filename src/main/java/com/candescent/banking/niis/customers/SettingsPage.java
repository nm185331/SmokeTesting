package com.candescent.banking.niis.customers;


import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class SettingsPage {
    private final Page page;

    public SettingsPage(Page page) {
        this.page = page;
    }

    public String getPhone(String selector) {
        page.waitForSelector(selector,new Page.WaitForSelectorOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
        return page.locator(selector).textContent();
    }

    public String getEmail(String selector) {
        page.waitForSelector(selector,new Page.WaitForSelectorOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
        return page.locator(selector).textContent();
    }
}
