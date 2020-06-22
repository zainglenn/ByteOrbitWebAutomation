package com.automation.byteorbit;


import com.automation.byteorbit.test.page.ByteOrbitContactUsPage;
import com.automation.byteorbit.utilities.Reporter;
import com.automation.byteorbit.utilities.SeleniumRunner;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.junit.*;

import java.io.IOException;

public class TestBytOrbitContactUs {
    private SeleniumRunner seleniumRunner;
    private static ExtentReports extent;
    private ByteOrbitContactUsPage byteOrbitPage;
    private final String url = "https://byteorbit.com";

    @BeforeClass
    public static void setupClass() {
        extent = new ExtentReports();
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("ByteOrbitReports/ContactUsTests.html");
        extent.attachReporter(sparkReporter);
    }

    @AfterClass
    public static void afterClass() {
        extent.flush();
    }

    @Before
    public void setUp() {
        seleniumRunner = new SeleniumRunner(SeleniumRunner.BrowserType.Chrome);

    }

    @After
    public void tearDown() {
        seleniumRunner.closeDriver();
    }

    @Test
    public void testNavigationWithSideMenuAndRecaptureFailMessage() throws IOException {
        String name = "Zain Glenn";
        String email = "zainglenn1995@gmail.com";
        String message = "This is a make contact automated form";

        //when timeout is set to zero uses dynamic weight of 30 seconds;
        ExtentTest test1 = extent.createTest("Test Contact Us side menu navigation with filled form without recaptcha failed message");
        byteOrbitPage = new ByteOrbitContactUsPage(seleniumRunner, url, test1);

        String result = byteOrbitPage.navigate();
        if (!Reporter.checkFailedResults(result)) {
            byteOrbitPage.messageFailedWithScreenshot(result);
            Assert.fail(result);
        }

        result = byteOrbitPage.navigateToContactViaSideMenu();
        if (!Reporter.checkFailedResults(result)) {
            byteOrbitPage.messageFailedWithScreenshot(result);
            Assert.fail(result);
        }

        result = byteOrbitPage.completeContactUsForm(name, email, message);
        if (!Reporter.checkFailedResults(result)) {
            byteOrbitPage.messageFailedWithScreenshot(result);
            Assert.fail(result);
        }

        result = byteOrbitPage.validateErrorMessageOnSend();
        if (!Reporter.checkFailedResults(result)) {
            byteOrbitPage.messageFailedWithScreenshot(result);
            Assert.fail(result);
        }

    }

    @Test
    public void testRequiredFields() throws IOException {
        ExtentTest test1 = extent.createTest("Test Required Fields are honoured");
        byteOrbitPage = new ByteOrbitContactUsPage(seleniumRunner, url, test1);

        String result = byteOrbitPage.navigate();
        if (!Reporter.checkFailedResults(result)) {
            byteOrbitPage.messageFailedWithScreenshot(result);
            Assert.fail(result);
        }

        result = byteOrbitPage.navigateToContactViaSideMenu();
        if (!Reporter.checkFailedResults(result)) {
            byteOrbitPage.messageFailedWithScreenshot(result);
            Assert.fail(result);
        }

        result = byteOrbitPage.testRequiredFields();
        if (!Reporter.checkFailedResults(result)) {
            byteOrbitPage.messageFailedWithScreenshot(result);
            Assert.fail(result);
        }

        result = byteOrbitPage.validateErrorMessageOnSend();
        if (!Reporter.checkFailedResults(result)) {
            byteOrbitPage.messageFailedWithScreenshot(result);
            Assert.fail(result);
        }
    }

    @Test
    public void testEmailRegex() throws IOException {
        String email = "Zain Glenn";
        String name = "zainglenn1995@gmail.com";
        String message = "This is a make contact automated form";

        //when timeout is set to zero uses dynamic weight of 30 seconds;
        ExtentTest test1 = extent.createTest("Test Email Regex honoured");
        byteOrbitPage = new ByteOrbitContactUsPage(seleniumRunner, url, test1);

        String result = byteOrbitPage.navigate();
        if (!Reporter.checkFailedResults(result)) {
            byteOrbitPage.messageFailedWithScreenshot(result);
            Assert.fail(result);
        }

        result = byteOrbitPage.navigateToContactViaSideMenu();
        if (!Reporter.checkFailedResults(result)) {
            byteOrbitPage.messageFailedWithScreenshot(result);
            Assert.fail(result);
        }

        result = byteOrbitPage.completeContactUsForm(name, email, message);
        if (!Reporter.checkFailedResults(result)) {
            byteOrbitPage.messageFailedWithScreenshot(result);
            Assert.fail(result);
        }

        result = byteOrbitPage.validateEmailRegexHonoured();
        if (!Reporter.checkFailedResults(result)) {
            byteOrbitPage.messageFailedWithScreenshot(result);
            Assert.fail(result);
        }

    }

}