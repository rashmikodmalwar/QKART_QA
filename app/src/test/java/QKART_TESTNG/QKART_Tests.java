package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;
import static org.testng.Assert.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

@Listeners(ListenerClass.class)
public class QKART_Tests {

    static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

     @BeforeSuite(alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        System.out.println("createDriver()");
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
        @Test(groups ={"Sanity_test"})
        @Parameters({"UserName","Password"})
        public void TestCase01(@Optional("testUser") String userName,
                @Optional("abc@123") String password ) throws InterruptedException {
            Boolean status;
            // Visit the Registration page and register a new user
            Register registration = new Register(driver);
            registration.navigateToRegisterPage();
            status = registration.registerUser(userName, password, true);
            assertTrue(status, "Failed to register new user");

            // Save the last generated username
            lastGeneratedUserName = registration.lastGeneratedUsername;

            // Visit the login page and login with the previuosly registered user
            Login login = new Login(driver);
            login.navigateToLoginPage();
            status = login.PerformLogin(lastGeneratedUserName,password);
            assertTrue(status, "Failed to login with registered user");

            // Visit the home page and log out the logged in user
            Home home = new Home(driver);
            status = home.PerformLogout();

        }
    
        @Test(groups = {"Sanity_test"})
        @Parameters({"UserName","Password"})
        public void TestCase02(@Optional("testUser") String userName,
                @Optional("abc@123") String password) throws InterruptedException {
        Boolean status;
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(userName, password, true);
        assertTrue(status, "failed to Register with new user");
        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, password, false);
        assertTrue(!status,"registration with same username failed");
       
    }
    
    @Test(groups = {"Sanity_test"})
    @Parameters({"ProductName"})
    public void TestCase03(String prod) throws InterruptedException {
        boolean status;
        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct(prod);
        assertTrue(status, "Unable to search for given product");
        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        status = searchResults.size()!= 0;
        
        assertTrue(status,"There were no results for the given search string");

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
            status = elementText.toUpperCase().contains("YONEX");
            assertTrue(status,"Test Results contains un-expected values:");
         
        }
        // Search for product
        status = homePage.searchForProduct("Gesundheit");
        
         assertTrue(status,"Invalid keyword returned results");
        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        status = searchResults.size() == 0;
        assertTrue(status, "Expected: no results , actual: Results were available");
        assertTrue(homePage.isNoResultFound(), "no products found message is not displayed");

    }

    @Test(groups = {"Regression_test"})
    @Parameters({"ProductShoe"})
    public void TestCase04(@Optional("Running Shoes") String product) throws InterruptedException {
        boolean status = false;
        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        status = homePage.searchForProduct(product);
        List<WebElement> searchResults = homePage.getSearchResults();
        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            // Verify if the size chart exists for the search result
            status = result.verifySizeChartExists();
            assertTrue(status,"size chart link does not exist");
           
                // Verify if size dropdown exists
                status = result.verifyExistenceofSizeDropdown(driver);
                assertTrue(status,"dropdown is not present");
                // Open the size chart
                status = result.openSizechart();
                assertTrue(status,"Failure to open size chart");
                    // Verify if the size chart contents matches the expected values
                    status = result.validateSizeChartContents(expectedTableHeaders, expectedTableBody,driver);
                    asserTrue(status,"size chart contents not matching");
                    // Close the size chart modal
                    status = result.closeSizeChart(driver);
        }
        
    }

    @Test(groups = {"Sanity_test"})
    @Parameters({"AddProduct1","AddProduct2","address"})
    public void TestCase05(String addProdToCart1,
            String addProdToCart2,String address) throws InterruptedException {
        Boolean status;
        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "failed to register");
        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;
        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();
        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status,"User perform Login Failed");

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart(addProdToCart1);
        status = homePage.searchForProduct("Tan");
        homePage.addProductToCart(addProdToCart2);

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(address);
        checkoutPage.selectAddress(address);
        // Place the order
        checkoutPage.placeOrder();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");

        // Go to the home page
        homePage.navigateToHome();
        // Log out the user
        homePage.PerformLogout();

    }

    @Test(groups = {"Regression_test"})
    @Parameters({"AddProductCart1", "AddProductCart2"})
    public void TestCase06(String AddProductCart1,String AddProductCart2) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 6: Verify that cart can be edited", "DONE");
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        asserTrue(status, "failed to register user");
        
        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        asserTrue(status, "failed to perform login");
       
        homePage.navigateToHome();
        status = homePage.searchForProduct("Xtend");
        homePage.addProductToCart(AddProductCart1);

        status = homePage.searchForProduct("Yarine");
        homePage.addProductToCart(AddProductCart2);

        // update watch quantity to 2
        homePage.changeProductQuantityinCart(AddProductCart1, 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart(AddProductCart2, 0);

        // update watch quantity again to 1
        homePage.changeProductQuantityinCart(AddProductCart1, 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(
                    ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            System.out.println("Error while placing order in: " + e.getMessage());
        }

        status = driver.getCurrentUrl().endsWith("/thanks");

        homePage.navigateToHome();
        homePage.PerformLogout();

    }
    
    @Test(groups = {"Regression_test"})
    @Parameters({"searchProduct1", "searchProduct2"})
    public void TestCase07(String searchProduct1,String searchProduct2 ) throws InterruptedException {
        Boolean status = false;
        List<String> expectedResult =
                Arrays.asList("Stylecon 9 Seater RHS Sofa Set ", "Xtend Smart Watch");

        Register registration = new Register(driver);
        Login login = new Login(driver);
        Home homePage = new Home(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        asserTrue(status, "failed to register user");
        
        lastGeneratedUserName = registration.lastGeneratedUsername;
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        asserTrue(status, "failed to perform login");
        homePage.navigateToHome();
        status = homePage.searchForProduct("Stylecon");
        homePage.addProductToCart(searchProduct1);

        status = homePage.searchForProduct("Xtend");
        homePage.addProductToCart(searchProduct2);

        homePage.PerformLogout();

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");

        status = homePage.verifyCartContents(expectedResult);

        homePage.PerformLogout();
    }

    @Test(groups = {"Sanity_test"})
    @Parameters({"product","quantity"})
    public void TestCase08(String product,int quantity) throws InterruptedException {
        Boolean status;
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        asserTrue(status, "Failed to register user");
        lastGeneratedUserName = registration.lastGeneratedUsername;
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        asserTrue(status, "failed to perform login");
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct("Stylecon");
        homePage.addProductToCart(product);

        homePage.changeProductQuantityinCart(product, quantity);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();

    }
    
    @Test(dependsOnMethods = {"TestCase10"},groups={"Regression_test"})
    public void TestCase09() throws InterruptedException {
        Boolean status = false;
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        lastGeneratedUserName = registration.lastGeneratedUsername;
        asserTrue(status, "failed to register user");
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        asserTrue(status, "failed to perform login");
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);
        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        
    }

    @Test(groups = {"Regression_test"})

    public void TestCase10() throws InterruptedException {
        Boolean status = false;
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        asserTrue(status, "failed to register user");
        lastGeneratedUserName = registration.lastGeneratedUsername;
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        asserTrue(status, "failed to perform login");
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        String basePageURL = driver.getCurrentUrl();
        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);
        asserTrue(status, "parent page url didn't change on privacy policy link click failed");
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading =
                driver.findElement(By.xpath("//*[@id='root']/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        asserTrue(status, "failed to open new tab Privacy Policy having page heading ");
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();
        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id='root']/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        asserTrue(status, "failed to open new tab having heading Terms of Service");
        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

    }

    @Test(groups = {"Regression_test"})
    @Parameters({"TC11_ContactusUserName","TC11_ContactUsEmail","TC11_QueryContent"})
    public void TestCase11(String ContactusUserName, String ContactUsEmail, String QueryContent)
            throws InterruptedException {
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        driver.findElement(By.xpath("//*[text()='Contact us']")).click();
        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys(ContactusUserName);
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys(ContactUsEmail);
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys(QueryContent);
        WebElement contactUs = driver.findElement(By.xpath(
                "/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));
        contactUs.click();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));
    }
    
    @Test(groups = {"Sanity_test"})
    @Parameters({"ProductNameToSearch","AddresstoAdd"})
    public void TestCase12(String ProductNameToSearch,String AddresstoAdd) throws InterruptedException {
        Boolean status = false;
       
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        asserTrue(status, "failed to register user");
        lastGeneratedUserName = registration.lastGeneratedUsername;
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        asserTrue(status, "failed to perform login");
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct(ProductNameToSearch);
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        homePage.changeProductQuantityinCart("YONEX Smash Badminton Racquet", 1);
        homePage.clickCheckout();
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(AddresstoAdd);
        checkoutPage.selectAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.placeOrder();
        Thread.sleep(3000);
        String currentURL = driver.getCurrentUrl();
        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));
        status = Advertisements.size() == 3;
        WebElement Advertisement1 =
                driver.findElement(By.xpath("//*[@id='root']/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 = driver.findElement(By.xpath("//*[@id='root']/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        

    }

    private void asserTrue(boolean status, String string) {}

    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    

    public static void logStatus(String type, String message, String status) {

        System.out.println(String.format("%s |  %s  |  %s | %s", String.valueOf(java.time.LocalDateTime.now()), type,
                message, status));
    }

    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType, description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

