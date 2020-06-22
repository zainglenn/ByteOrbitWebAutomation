package com.automation.byteorbit.test.page;

import com.automation.byteorbit.test.object.ByteOrbitObject;
import com.automation.byteorbit.utilities.SeleniumRunner;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import java.io.IOException;

public class ByteOrbitContactUsPage {
    private final SeleniumRunner seleniumRunner;
    private final String url;
    private final ExtentTest extentTest;

    public ByteOrbitContactUsPage(SeleniumRunner seleniumRunner, String url, ExtentTest currentTest) {
        this.extentTest = currentTest;
        this.url = url;
        this.seleniumRunner = seleniumRunner;
    }


    public String navigateToContactViaSideMenu() throws IOException {
        if (!seleniumRunner.clickElement(ByteOrbitObject.sideMenuButton(), 0)) {
            return "Failed to click side menu button";
        }

        if (!seleniumRunner.waitForElementVisible(ByteOrbitObject.selectSideMenuItem("Contact"), 0)) {
            return "Failed to wait for contact button";
        }

        messagePassWithScreenshot("Successfully validated Byte Orbit side menu is open");

        if (!seleniumRunner.clickElement(ByteOrbitObject.selectSideMenuItem("Contact"), 0)) {
            return "Failed to click contact button";
        }

        if (!seleniumRunner.waitForElementVisible(ByteOrbitObject.contactSectionHeading(), 0)) {
            return "Failed to validate contact section was open";
        }

        return null;
    }


    public String navigate() throws IOException {
        if (!seleniumRunner.navigateToURL(url)) {
            return "Failed to navigate to url -> " + url;
        }
        messagePassWithScreenshot("Successfully navigated to url ->" + url);
        return null;
    }

    public String completeContactUsForm(String name, String email, String message) throws IOException {
        if (!seleniumRunner.enterTextIntoElement(ByteOrbitObject.contactNameField(), 0, name, true)) {
            return "Failed to enter name into text field -> " + name;
        }
        if (!seleniumRunner.enterTextIntoElement(ByteOrbitObject.contactEmailField(), 0, email, true)) {
            return "Failed to email into text field -> " + email;
        }
        if (!seleniumRunner.enterTextIntoElement(ByteOrbitObject.contactMessageField(), 0, message, true)) {
            return "Failed to message into text field -> " + message;
        }

        messagePassWithScreenshot("Successfully completed form with name -> " + name + "& email -> " + email);

        if (!seleniumRunner.clickElement(ByteOrbitObject.contactLaunchButton(), 0)) {
            return "Failed to click launch button";
        }

        return null;
    }

    public String validateErrorMessageOnSend() throws IOException {
        if (!seleniumRunner.waitForElementVisible(ByteOrbitObject.messageNotSentLabel(), 0)) {
            return "Failed to wait for fail to send message to appear";
        }
        return messagePassWithScreenshot("Successfully validated message failed notification appeared");
    }

    public String testRequiredFields() throws IOException {
        if(!seleniumRunner.clickElement(ByteOrbitObject.contactLaunchButton(),0)){
            return "Failed to click launch button";
        }
        if(!seleniumRunner.waitForElementVisible(ByteOrbitObject.nameRequiredLabel(),0)){
            return "Failed to validate name has required field present";
        }
        if(!seleniumRunner.waitForElementVisible(ByteOrbitObject.emailRequiredLabel(),0)){
            return "Failed to validate email has required field present";
        }
        if(!seleniumRunner.waitForElementVisible(ByteOrbitObject.messageRequiredLabel(),0)){
            return "Failed to validate message has required field present";
        }
        if(!seleniumRunner.waitForElementVisible(ByteOrbitObject.recapturedRequiredLabel(),0)){
            return "Failed to validate recapture has required field present";
        }
        return messagePassWithScreenshot("Successfully validated name, email , message & recaptured are required fields");
    }

    public String validateEmailRegexHonoured() throws IOException {
        if(!seleniumRunner.waitForElementVisible(ByteOrbitObject.emailRegexErrorLabel(),0)){
            return "Failed to validate recapture has required field present";
        }

       return messagePassWithScreenshot("Successfully validated email regex error is presented");
    }



    public String messagePassWithScreenshot(String message) throws IOException {
        String atest = String.format("image/PASS-%s.png", System.currentTimeMillis());
        seleniumRunner.takeScreenshot(atest);
        extentTest.pass(message, MediaEntityBuilder.createScreenCaptureFromPath(atest).build());
        return null;
    }

    public String messageFailedWithScreenshot(String message) throws IOException {
        String atest = String.format("image/FAIL-%s.png", System.currentTimeMillis());
        seleniumRunner.takeScreenshot(atest);
        extentTest.fail(message, MediaEntityBuilder.createScreenCaptureFromPath(atest).build());
        return message;
    }
}
