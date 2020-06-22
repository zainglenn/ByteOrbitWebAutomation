package com.automation.byteorbit.utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class Reporter {
    private ExtentReports extentReports;
    public Reporter() {
        this.extentReports = new ExtentReports();
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("reports/spark.html");
        extentReports.attachReporter(sparkReporter);
    }

    public ExtentReports getExtentReports() {
        return extentReports;
    }

    public static boolean checkFailedResults(String message){
        return message == null;
    }
}
