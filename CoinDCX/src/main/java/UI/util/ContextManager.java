package UI.util;

import com.aventstack.extentreports.ExtentTest;
import io.appium.java_client.MobileDriver;


public class ContextManager {

    ThreadLocal<MobileDriver> driverPool = new ThreadLocal<>();
    ThreadLocal<ExtentTest> extentReportPool = new ThreadLocal<>();

    public  void setDriver(MobileDriver driver)
    {
        driverPool.set(driver);
    }

    public MobileDriver getDriver()
    {
        return driverPool.get();
    }
    public  void setExtentReportPool(ExtentTest extentReports)
    {
        extentReportPool.set(extentReports);
    }

    public ExtentTest getExtentReport()
    {
        return extentReportPool.get();
    }


}
