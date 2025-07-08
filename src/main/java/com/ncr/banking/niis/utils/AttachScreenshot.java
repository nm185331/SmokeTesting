package com.ncr.banking.niis.utils;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;

public class AttachScreenshot {
    public static void attachScreenshotToAllure(Page page) {
        byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        Allure.addAttachment("Validation Screenshot", new ByteArrayInputStream(screenshotBytes));
    }
}
