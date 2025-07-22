package com.candescent.banking.niis.utils;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;

public class AttachScreenshot {
    public static void attachScreenshotToAllure(Page page,String title) {
        byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        Allure.addAttachment(title, new ByteArrayInputStream(screenshotBytes));
    }
}
