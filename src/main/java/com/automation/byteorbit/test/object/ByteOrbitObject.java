package com.automation.byteorbit.test.object;

import org.openqa.selenium.By;

public class ByteOrbitObject {
    public static By sideMenuButton() {
        return By.id("toggle");
    }

    public static By selectSideMenuItem(String name) {
        return By.xpath("//li/*[text() = '" + name + "']");
    }

    public static By contactSectionHeading(){
        return By.xpath("//h2[contains(text(),'Make')]/span[text() = 'contact']");
    }

    public static By contactNameField(){
        return By.id("id_sender");
    }

    public static By contactEmailField(){
        return By.id("id_email");
    }

    public static By contactMessageField(){
        return By.id("id_message");
    }

    public static By contactLaunchButton(){
        return By.xpath("//button[text() = 'Launch']");
    }

    public static By messageNotSentLabel(){
        return By.xpath("//*[@class = 'alert alert-error alert-dismissible' or contains(text(),'Your mesage has not been sent')]");
    }

    //required field selectors
    public  static By nameRequiredLabel(){
        return By.xpath("//div[input[@name = 'sender']]//li[text() = 'This field is required.']");
    }

    public  static By emailRequiredLabel(){
        return By.xpath("//div[input[@name = 'email']]//li[text() = 'This field is required.']");
    }

    public  static By messageRequiredLabel(){
        return By.xpath("//div[textarea[@name = 'message']]//li[text() = 'This field is required.']");
    }

    public  static By emailRegexErrorLabel(){
        return By.xpath("//div[input[@name = 'email']]//li[text() = 'Enter a valid email address.']");
    }

    public static By recapturedRequiredLabel(){
        return By.xpath("//div[div[@class= 'g-recaptcha']]//li[text() = 'This field is required.']");
    }

}
