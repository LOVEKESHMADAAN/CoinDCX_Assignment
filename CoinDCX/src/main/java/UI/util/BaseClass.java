package UI.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class BaseClass {

    Properties properties=null;
    ExtentReports extent= null;
    MobileDriver<MobileElement> driver=null;
    ContextManager contextManager = new ContextManager();
    ExtentTest logger = null;
    @BeforeSuite
    public void init(Method method, ITestContext context) throws IOException {
        loadConfigFile();
        System.out.println("app Name is "+properties.getProperty("appName"));
        loadExtentReport();
       // startAppiumServer();

    }

    @BeforeClass
    public void launchApplication() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities= new DesiredCapabilities();
        desiredCapabilities.setCapability("platformName",properties.getProperty("platformName"));
        desiredCapabilities.setCapability("deviceName",properties.getProperty("deviceName"));
        desiredCapabilities.setCapability("appPackage",properties.getProperty("appPackage"));
        desiredCapabilities.setCapability("appActivity",properties.getProperty("appActivity"));
        desiredCapabilities.setCapability("automationName",properties.getProperty("automationName"));
        desiredCapabilities.setCapability("noReset",properties.getProperty("noReset"));

        URL url = new URL(properties.getProperty("127.0.0.1:4723/wd/hub"));

        if(properties.getProperty("platformName").equalsIgnoreCase("Android"))
        driver = new AndroidDriver(url, desiredCapabilities);
        else
            driver= new IOSDriver(url,desiredCapabilities);

        contextManager.setDriver(driver);

    }

    @BeforeMethod
    public void beforeMethod(Method method)
    {
      configExtentTest(method);
      startRecording();
    }

    @AfterMethod
    public void afterMethod(ITestResult result , Method method)
    {
        if(result.getStatus()==ITestResult.SUCCESS)
        {
            logger.log(Status.PASS,"test case has been Passed "+ method.getName());
        }
        else if (result.getStatus()==ITestResult.FAILURE)
        {
            logger.log(Status.FAIL,"test case has been Failed "+ method.getName());
        }
        else if(result.getStatus()==ITestResult.SKIP)
        {
            logger.log(Status.SKIP,"test case has been Skipped "+ method.getName());
        }

        extent.flush();

    }

    @AfterClass
    public void closeApplication()
    {
        try
        {
          contextManager.getDriver().quit();
        }
        catch (Exception e)
        {
            logger.info("Getting error in Quitting the Driver "+e.getStackTrace());
        }

    }

    @Parameters({"appName"})
    @AfterSuite
    public void AfterSuite(@Optional String appName)
    {
        extent.flush();
    }

    private void startRecording() {
        try {
        //    contextManager.getDriver().startRecording();
        }catch (Exception e )
        {
            System.out.println("error in starting the recording "+ e.getMessage());
        }
    }

    private void configExtentTest(Method method) {
        ExtentTest test = extent.createTest(method.getName());
        contextManager.setExtentReportPool(test);
        logger= contextManager.getExtentReport();
        logger.info("Test has been started");
    }


    private void loadExtentReport() {
        File file = new File("ExtentReport.html");
        extent=new ExtentReports();
        extent.attachReporter(getHtmlSparkReporter(file));


    }

    private ExtentSparkReporter getHtmlSparkReporter(File file) {
        ExtentSparkReporter spark = new ExtentSparkReporter(file);

        return spark;
    }

    private void loadConfigFile() throws IOException {
        FileInputStream file = new FileInputStream("../config/config.properties");
        properties.load(file);

    }


}
