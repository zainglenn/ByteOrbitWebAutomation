package com.automation.byteorbit.utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public interface SeleniumImpl {

    WebDriver setUpDriver(SeleniumRunner.BrowserType browserType);

    void closeDriver();

    boolean navigateToURL(String url);

    WebElement hasWebElement(By locator);

    WebElement waitForElement(By locator, int timeout);

    Object waitForElementWithExpectedCondition(ExpectedCondition<?> condition, WebElement element, int timeout) throws InterruptedException;

    boolean waitForPresenceOfElement(By locator, int timeout);

    boolean waitForElementNotPresent(By locator, int timeout);

    boolean waitForElementNotVisible(By locator, int timeout);

    boolean waitForElementVisible(By locator, int timeout);

    Point getLocationOfElement(By locator, int timeout);

    boolean waitForElementClickable(By locator, int timeout);

    boolean clickElement(By locator, int timeout);

    boolean contextClickElement(By locator, int timeout);

    boolean enterTextIntoElement(By locator, int timeout, String text, boolean clearText);

    boolean hoverOverElement(By xpath);

    boolean clickAndDragElement(By xpath, By xpathToElement);

    void sleep(long lengthInMs) throws InterruptedException;

    Object getJavaScriptExecutor(String jsScript);

    String takeScreenshot();

    void waitForDocument() throws InterruptedException;

}
