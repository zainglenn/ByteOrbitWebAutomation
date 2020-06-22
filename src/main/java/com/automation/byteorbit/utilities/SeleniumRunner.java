package com.automation.byteorbit.utilities;

/*
    Author : Zain Glenn
*/

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;


public class SeleniumRunner implements SeleniumImpl {
    private final WebDriver webDriver;
    private FluentWait<WebDriver> fluentWait;
    private final Logger logger = LogManager.getLogger(SeleniumRunner.class);

    public SeleniumRunner(BrowserType browserType) {
        this.webDriver = setUpDriver(browserType);
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    private long waitTime(int timeout) {
        Duration duration = Duration.ofSeconds(30);
        if (timeout > 0) {
            return Duration.ofSeconds(timeout).toMillis() + System.currentTimeMillis();
        }
        return System.currentTimeMillis() + duration.toMillis();
    }

    private boolean isWaitComplete(long waitTime) {
        return System.currentTimeMillis() < waitTime;
    }

    public boolean clickUsingId(String id) {
        boolean clickElement = false;
        try {
            boolean didWait = waitForPresenceOfElement(By.id(id), 0);
            if (didWait) {
                JavascriptExecutor executor = (JavascriptExecutor) webDriver;
                String script = "window.document.getElementById('" + id + "').click();";
                executor.executeScript(script);
                clickElement = true;
            }
        } catch (Exception e) {
            logger.info("Clicking Element Using JavaScript - " + id + "'\n" + e.getLocalizedMessage());
            clickElement = false;
        }
        logger.info("Clicking Element Using JavaScript - '{}'", id);
        return clickElement;
    }


    @Override
    public WebDriver setUpDriver(BrowserType browserType) {
        logger.info("Setting up webdriver on localhost");
        switch (browserType) {
            case Chrome:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                // ChromeDriver is just AWFUL because every version or two it breaks unless you pass cryptic arguments
                chromeOptions.addArguments("start-maximized"); // https://stackoverflow.com/a/26283818/1689770
                chromeOptions.addArguments("enable-automation"); // https://stackoverflow.com/a/43840128/1689770
                WebDriver chromeDriver = new ChromeDriver(chromeOptions);
                logger.info("Setting chrome options");
                fluentWait = new FluentWait<>(chromeDriver).withTimeout(Duration.of(1, ChronoUnit.SECONDS))
                        .ignoring(NoSuchElementException.class);
                return chromeDriver;

            case Firefox:
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("start-maximized");
                firefoxOptions.addArguments("enable-automation");
                WebDriver firefoxDriver = new FirefoxDriver();
                logger.info("Setting up firefox options");
                fluentWait = new FluentWait<>(firefoxDriver).withTimeout(Duration.of(1, ChronoUnit.SECONDS))
                        .ignoring(NoSuchElementException.class);
                return firefoxDriver;
        }
        return null;
    }

    @Override
    public void closeDriver() {
        if (webDriver != null) {
            logger.info("quiting current webdriver open");
            webDriver.quit();
        }
    }

    @Override
    public boolean navigateToURL(String url) {
        try {
            webDriver.navigate().to(url);
            logger.info("Navigating to URL : {}", url);
            return true;
        } catch (Exception ex) {
            logger.error("Failed to navigate to url - {}", url);
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public WebElement hasWebElement(By locator) {
        try {
            waitForDocument();
            WebElement webElement = webDriver.findElement(locator);
            logger.info("DOM Structure has element immediately available with locator {}", locator);
            return webElement;
        } catch (Exception ignored) {
            logger.warn("Element not immediately present {}", locator);
            return null;
        }
    }

    @Override
    public WebElement waitForElement(By locator, int timeout) {
        long waitTime = waitTime(timeout);
        WebElement element = hasWebElement(locator);
        if (element != null) {
            return element;
        } else {
            logger.info("Element not found with locator '{}' waiting for {} secs", locator, timeout);
            while (isWaitComplete(waitTime)) {
                try {
                    WebElement webElement = fluentWait.until(d -> d.findElement(locator));
                    logger.info("Successfully waited for element with locator : {} to be present", locator);
                    return webElement;
                } catch (Exception e) {
                    logger.warn("Element with locator {} not found retrying", locator);
                } finally {
                    try {
                        sleep(100);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            logger.info("Element not found with locator '{}'", locator);
            return null;
        }
    }

    @Override
    public Object waitForElementWithExpectedCondition(ExpectedCondition<?> condition, WebElement element, int timeout) {
        long waitTime = waitTime(timeout);
        if (element != null) {
            while (isWaitComplete(waitTime)) {
                try {
                    waitForDocument();
                    return fluentWait.until(condition);
                } catch (Exception e) {
                    logger.warn(" {} - Failed to wait for element with expected condition {} on element {}", e.getMessage(), condition.toString(), element.toString());
                } finally {
                    try {
                        sleep(100);
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
        } else {
            logger.info("Element was still found with selector with expected conditions");
            return null;
        }
        logger.info("Element not found with selector with expected conditions");
        return null;
    }

    @Override
    public boolean waitForPresenceOfElement(By locator, int timeout) {
        return waitForElement(locator, timeout) != null;
    }

    @Override
    public boolean waitForElementNotPresent(By locator, int timeout) {
        WebElement element = hasWebElement(locator);
        if (element == null) {
            logger.info("Successfully validated element not present '{}'", locator);
            return true;
        }
        Object status = waitForElementWithExpectedCondition(ExpectedConditions.invisibilityOf(element), element, timeout);
        if (status == null) {
            logger.error("Timeout exceeded for element to be invisible with locator '{}'", locator);
            return false;
        }
        if ((boolean) status) {
            logger.info("Successfully validated the locator '{}' is not present", locator);
            return true;
        } else {
            logger.error("Failed to validate the locator '{}' was not present within the timeout {}", locator, timeout);
            return false;
        }
    }

    @Override
    public boolean waitForElementNotVisible(By locator, int timeout) {
        WebElement element = hasWebElement(locator);
        if (locator == null) {
            return true;
        }
        if ((boolean
                ) waitForElementWithExpectedCondition(ExpectedConditions.invisibilityOfElementLocated(locator), element, timeout)) {
            logger.info("Successfully validated element with locator {} is not visible", locator);
            return true;
        } else {
            logger.warn("Failed to wait for element with locator {} to be visible", locator);
            return false;
        }
    }

    @Override
    public boolean waitForElementVisible(By locator, int timeout) {
        WebElement element = waitForElement(locator, timeout);
        if (element == null) {
            return false;
        }
        if (waitForElementWithExpectedCondition(ExpectedConditions.visibilityOf(element), element, timeout) != null) {
            logger.info("Successfully waited for element with locator {} to be visible", locator);
            return true;
        } else {
            logger.warn("Failed to wait for element with locator {} to be visible", locator);
            return false;
        }
    }

    @Override
    public Point getLocationOfElement(By locator, int timeout) {
        WebElement element = waitForElement(locator, timeout);
        if (element != null) {
            Point point = element.getLocation();
            logger.info("Element with locator {} found with location data [{}]", locator, point);

        }
        logger.warn("Failed to locate element with locator {}", locator);
        return null;
    }

    @Override
    public boolean waitForElementClickable(By locator, int timeout) {
        WebElement element = waitForElement(locator, timeout);
        if (element == null) {
            return false;
        }

        boolean result = waitForElementWithExpectedCondition(ExpectedConditions.elementToBeClickable(element), element, timeout) != null;
        if (result) {
            logger.info("Successfully waited for element with locator {} to be clickable", locator);
        } else {
            logger.info("Failed to wait for element with locator {} to be clickable", locator);
        }
        return result;
    }

    @Override
    public boolean clickElement(By locator, int timeout) {
        WebElement element = waitForElement(locator, timeout);
        if (element == null) {
            return false;
        }

        element = (WebElement) waitForElementWithExpectedCondition(ExpectedConditions.elementToBeClickable(element), element, timeout);
        if (element == null) {
            return false;
        }

        logger.info("Successfully waited for element with locator {} to be clickable", locator);


        Duration duration = Duration.ofSeconds(30);
        long milliseconds = duration.toMillis();
        long timeForWaitToComplete = System.currentTimeMillis() + milliseconds;
        while (System.currentTimeMillis() < timeForWaitToComplete) {
            try {
                element.click();
                logger.info("Element found and clicked successfully with loacator '{}'", locator);
                return true;
            } catch (Exception exception) {
                logger.error("{} - Trying to click {}", exception.getMessage(), locator);
            }
        }
        return false;
    }

    @Override
    public boolean contextClickElement(By locator, int timeout) {
        boolean loggedFail = false;
        WebElement element = waitForElement(locator, timeout);
        if (element == null) {
            return false;
        }

        element = (WebElement) waitForElementWithExpectedCondition(ExpectedConditions.elementToBeClickable(element), element, timeout);
        if (element == null) {
            return false;
        }
        int retry = 0;
        while (retry < 3) {
            try {
                Actions actions = new Actions(this.webDriver);
                actions.contextClick(element).perform();
                logger.info("Element found and context clicked successfully with locator '{}'", locator);
                return true;
            } catch (Exception exception) {
                logger.error("Element not found with selector with expected conditions");
                if (!loggedFail) {
                    logger.error(exception.getMessage(), exception);
                    loggedFail = true;
                }

                retry++;
            }
            logger.debug("retrying {}/3 context click fot element with locator '{}'", retry, locator);
        }
        return false;
    }

    @Override
    public boolean enterTextIntoElement(By locator, int timeout, String text, boolean clearText) {
        WebElement element = waitForElement(locator, timeout);
        if (element == null) {
            return false;
        }

        logger.info("Starting wait for element to be clickable '{}'", locator);

        element = (WebElement) waitForElementWithExpectedCondition(ExpectedConditions.elementToBeClickable(element), element, timeout);
        if (element == null) {
            return false;
        }


        boolean clicked = clickElement(locator, 0);
        clearText = clearText && clicked;
        if (clearText) {
            logger.info("clearing text on locator '{}'", locator);
            element.clear();
        }
        if (text != null) {
            element.sendKeys(text);
            logger.info("sent text to locator '{}'", locator);
            return true;
        } else {
            logger.error("no input provided for locator '{}' ", locator);
            return false;
        }

    }

    @Override
    public boolean hoverOverElement(By xpath) {
        boolean hoverOverElement = false;
        try {
            if (waitForElementClickable(xpath, 0)) {
                Actions action = new Actions(webDriver);
                action.moveToElement(webDriver.findElement(xpath)).perform();
                hoverOverElement = true;
            }
        } catch (Exception e) {
            logger.error("Failed to hover on element", e);
        }
        logger.info("Hovering over status :'{}' with locator '{}'", hoverOverElement, xpath);
        return hoverOverElement;
    }

    @Override
    public boolean clickAndDragElement(By xpath, By xpathToElement) {
        boolean clickElement = false;
        try {
            boolean draggableElement = waitForElementClickable(xpath, 0);
            boolean elementToDragTo = waitForElementClickable(xpathToElement, 0);
            Actions actions = new Actions(webDriver);
            if (draggableElement) {
                actions.clickAndHold(webDriver.findElement(xpath)).perform();
                if (elementToDragTo) {
                    actions.moveToElement(webDriver.findElement(xpathToElement), 2, 0).perform();
                }
                actions.release(webDriver.findElement(xpath)).perform();
                clickElement = true;
                logger.info("Successfully dragged element from locator '{}' to locator '{}'", xpath, xpathToElement);
            } else {
                logger.info("Failed to drag element from locator '{}' to locator '{}'", xpath, xpathToElement);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return clickElement;
    }

    @Override
    public void sleep(long lengthInMs) throws InterruptedException {
        Thread.sleep(lengthInMs);
    }

    @Override
    public Object getJavaScriptExecutor(String jsScript) {
        JavascriptExecutor executor = (JavascriptExecutor) getWebDriver();
        return executor.executeScript(jsScript);
    }

    @Override
    public String takeScreenshot() {
        return ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BASE64);
    }

    public void takeScreenshot(String name) throws IOException {
        File file = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(file, new File("ByteOrbitReports/"+name));
    }

    @Override
    public void waitForDocument() throws InterruptedException {
        JavascriptExecutor executor = (JavascriptExecutor) this.webDriver;
        String readyState = executor.executeScript("return document.readyState").toString();
        String ready = "complete";

        while (!readyState.equals(ready)) {
            sleep(250);
            readyState = executor.executeScript("return document.readyState").toString();
        }
    }

    public enum BrowserType {
        Chrome, Firefox
    }
}
