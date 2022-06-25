package QKART_SANITY_LOGIN.Module1;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Checkout {
    RemoteWebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/checkout";
    String urlThanks = "https://crio-qkart-frontend-qa.vercel.app/thanks";
    public Checkout(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCheckout() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    // public void navigateToThanksPage() {
    //     if (!this.driver.getCurrentUrl().equals(this.urlThanks)) {
    //         this.driver.get(this.urlThanks);
    //     }
    // }
    /*
     * Return Boolean denoting the status of adding a new address
     */
    public Boolean addNewAddress(String addresString) {
        try {
           
            WebDriverWait wait = new WebDriverWait(driver, 30);
            /* 
             * Click on the "Add new address" button, enter the addressString in the address
             * text box and click on the "ADD" button to save the address
             */
            WebElement addNewAddress =
                    driver.findElement(By.xpath("//button[text()='Add new address']"));
            addNewAddress.click();
            WebElement addressText = driver.findElement(
                    By.xpath("//textarea[@placeholder ='Enter your complete address']"));
            addressText.sendKeys(addresString);
           
            WebElement addAddress = driver.findElement(By.xpath("//button[text() ='Add']"));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Add']")));
            addAddress.click();
            
            return true;
        } catch (Exception e) {
            System.out.println("Exception occurred while entering address: " + e.getMessage());
            return false;

        }
    }

    /*
     * Return Boolean denoting the status of selecting an available address
     */
    public Boolean selectAddress(String addressToSelect) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            /*
             * Iterate through all the address boxes to find the address box with matching
             * text, addressToSelect and click on it
             */
            
            List<WebElement> searchAddress = driver.findElements(
                    By.xpath("//div[contains(@class,'address-item')]//div"));
            for (WebElement selectAddress : searchAddress) {
                
                System.out.println(selectAddress.getText()+ "=" +addressToSelect);
                if (selectAddress.getText().equals(addressToSelect)) {
                    wait.until(
                            ExpectedConditions.elementToBeClickable(By.xpath("//p[@class='MuiTypography-root MuiTypography-body1 css-yg30e6']")));
                    selectAddress.click();
                    
                    return true;
                }
            }
            System.out.println("Unable to find the given address");
            return false;
        } catch (Exception e) {
            System.out.println("Exception Occurred while selecting the given address: " + e.getMessage());
            return false;
        }

    }

    /*
     * Return Boolean denoting the status of place order action
     */
    public Boolean placeOrder() {
        try {
            WebElement placeOrderClick =
                    driver.findElement(By.xpath("//button[text()='PLACE ORDER']"));
            placeOrderClick.click();
            return true;

        } catch (Exception e) {
            System.out.println("Exception while clicking on PLACE ORDER: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the insufficient balance message is displayed
     */
    public Boolean verifyInsufficientBalanceMessage() {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 08: MILESTONE 7
            WebElement inSufficientBalanceMessage = driver.findElement(By.id("notistack-snackbar"));
            inSufficientBalanceMessage.isDisplayed();
            System.out.println(inSufficientBalanceMessage.getText());
            inSufficientBalanceMessage.getText()
                   .equalsIgnoreCase(
                           "You do not have enough balance in your wallet for this purchase");
           return true;
        } catch (Exception e) {
            System.out.println(
                    "Exception while verifying insufficient balance message: " + e.getMessage());
            return false;
        }
    } 

    public List<WebElement> getNumberOfFrame() {
        List<WebElement> numberOfFrames = new ArrayList<WebElement>();
        try {
            numberOfFrames = driver.findElements(By.xpath("//iframe"));
            return numberOfFrames;
        } catch (Exception ex) {
            System.out.println("There were no frames found: " + ex.getMessage());
            return numberOfFrames;
        }

    }
    
    // public boolean insideFrameButtonClickable(List<WebElement> advFrame,String adevertisementName) {
    //     Boolean status = false;
    //     try {
            
    //         //  WebElement firstFrame = driver.findElement(By.xpath(
    //         //         "//div[@id='root']//div[contains(@class,'MuiGrid-spacing-xs-4')]/iframe[1]"));
    //         // driver.switchTo().frame(firstFrame);
    //         List<WebElement> firstadvertisement =
    //                 driver.findElements(By.xpath("//*[@class='MuiCardContent-root css-1qw96cp']"));
    //         for (WebElement advertisement : firstadvertisement) {
    //             if (advertisement.findElement(By.xpath("//p[@class='para']")).getText()
    //                     .equalsIgnoreCase(adevertisementName)) {
    //                 WebElement viewCart =
    //                         advertisement.findElement(By.xpath("//button[text()='View Cart']"));
    //                 WebElement buyNow =
    //                         advertisement.findElement(By.xpath("//button[text()='Buy Now']"));
    //                 if (viewCart.isEnabled()) {
    //                     viewCart.click();
    //                     return status;
    //                 }
    //             }
    //         }
    //         return status;
    //     }
    //     catch(Exception e){
    //         System.out.println("unable to find advertisement"+e.getMessage());
    //         return status;
    //     }
    // }
}
