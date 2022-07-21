package QKART_TESTNG;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ListenerClass extends QKART_Tests implements ITestListener {
    public void onTestStart(ITestResult result) {
        System.out.println("New test started " + result.getName());
        takeScreenshot(driver, "TestStart",""+result.getName());
    }

    public void onTestSuccess(ITestResult result) {
        System.out.println("onTestSuccess method " + result.getName());
        takeScreenshot(driver, "TestSuccess",""+result.getName());

    }
    
    public void onTestFailure(ITestResult result) {
        System.out.println("onTestFailure method "+result.getName());
        takeScreenshot(driver, "TestFailure",""+result.getName());
    }
}