package QKART_SANITY_LOGIN.Module1;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchResult {
    WebElement parentElement;

    public SearchResult(WebElement SearchResultElement) {
        this.parentElement = SearchResultElement;
    }

    /*
     * Return title of the parentElement denoting the card content section of a
     * search result
     */
    public String getTitleofResult() {
        String titleOfSearchResult="";
        try {
            WebElement title = parentElement.findElement(By.xpath("//p[@class ='MuiTypography-root MuiTypography-body1 css-yg30e6']"));
            System.out.println(title.getText());
            titleOfSearchResult = title.getText();
            //Thread.sleep(2000);

        } catch (Exception ex) {
            System.out.println("element not attach");
        }
        return titleOfSearchResult;
    }
      
    

    /*
     * Return Boolean denoting if the open size chart operation was successful
     */
    public Boolean openSizechart() {
        try {
            // parentElement.isDisplayed();
            Thread.sleep(2000);
            parentElement.findElement(By.xpath("//button[text() ='Size chart']")).click();
            Thread.sleep(3000);
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            // Find the link of size chart in the parentElement and click on it
            return true;
        } catch (Exception e) {
            System.out.println("Exception while opening Size chart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the close size chart operation was successful
     */
    public Boolean closeSizeChart(WebDriver driver) {
        try {
            Thread.sleep(2000);
            Actions action = new Actions(driver);

            action.sendKeys(Keys.ESCAPE);
            action.perform();
            Thread.sleep(2000);
            return true;
        } catch (Exception e) {
            System.out.println("Exception while closing the size chart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean based on if the size chart exists
     */
    public Boolean verifySizeChartExists() {
       
        try {
            /*
             * Check if the size chart element exists. If it exists, check if the text of
             * the element is "SIZE CHART". If the text "SIZE CHART" matches for the
             * element, set status = true , else set to false
             */
            WebElement searchSizeChart = parentElement.findElement(By.xpath("//button[text()='Size chart']"));
            if (searchSizeChart.isDisplayed()
                    && searchSizeChart.getText().toUpperCase().equals("SIZE CHART")) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /*
     * Return Boolean if the table headers and body of the size chart matches the
     * expected values
     */
    public Boolean validateSizeChartContents(List<String> expectedTableHeaders, List<List<String>> expectedTableBody,
            WebDriver driver) {
        Boolean status = true;
        try {
            /*
             * Locate the table element when the size chart modal is open
             * 
             * Validate that the contents of expectedTableHeaders is present as the table
             * header in the same order
             * 
             * Validate that the contents of expectedTableBody are present in the table body
             * in the same order
             */
            List<WebElement> tableHeaders = driver.findElements(
                    By.xpath("//table[@aria-label='simple table']//child::thead//tr//th"));
            List<WebElement> tableBody = driver
                    .findElements(By.xpath("//table[@aria-label='simple table']/child::tbody//tr"));
            for (int i = 0; i < tableHeaders.size(); i++) {
                if (!tableHeaders.get(i).getText().equals(expectedTableHeaders.get(i))) {
                    return status;
                }
            }

            int i = 0;
            for (List<String> tableBodyContents : expectedTableBody) {
                for (String tableBodyInner : tableBodyContents) {
                    if (!tableBody.get(i).getText().equals(tableBodyInner)) {
                        return status;
                    }
                }
            }

            return status;

        } catch (Exception e) {
            System.out.println("Error while validating chart contents");
            return status;
        }
       
    }

    /*
     * Return Boolean based on if the Size drop down exists
     */
    public Boolean verifyExistenceofSizeDropdown(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0,250)", "");
            WebElement sizeDropdown = driver.findElement(By.xpath("//select[@name='age']"));
            sizeDropdown.isDisplayed();
            Thread.sleep(5000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}