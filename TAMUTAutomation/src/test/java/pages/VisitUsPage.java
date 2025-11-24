package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

public class VisitUsPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    public VisitUsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    public void verifyVisitUsElements() {
        System.out.println("Verifying Visit Us page elements");
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Page Title: " + driver.getTitle());
        
        // Verify page title contains "Visit"
        String title = driver.getTitle().toLowerCase();
        Assert.assertTrue(title.contains("visit"), 
            "Title does not contain 'visit'. Actual title: " + driver.getTitle());
        System.out.println("✓ Title contains 'visit'");
        
        // Look for "Upcoming Events" link
        boolean upcomingEventsFound = findElement(new String[]{
            "//a[contains(., 'Upcoming Events')]",
            "//a[contains(., 'Events')]",
            "//a[contains(@href, 'events')]"
        }, "Upcoming Events");
        Assert.assertTrue(upcomingEventsFound, "Upcoming Events link not found");
        
        // Look for "Schedule a Campus Tour" link
        boolean scheduleTourFound = findElement(new String[]{
            "//a[contains(., 'Schedule a Campus Tour')]",
            "//a[contains(., 'Schedule') and contains(., 'Tour')]",
            "//a[contains(., 'Campus Tour')]"
        }, "Schedule a Campus Tour");
        Assert.assertTrue(scheduleTourFound, "Schedule a Campus Tour link not found");
        
        // Look for "Virtual Tour" link
        boolean virtualTourFound = findElement(new String[]{
            "//a[contains(., 'Virtual Tour')]",
            "//a[contains(., 'Virtual')]"
        }, "Virtual Tour");
        Assert.assertTrue(virtualTourFound, "Virtual Tour link not found");
        
        System.out.println("All Visit Us page elements verified successfully!");
        
        
        scrollPage();
    }
    
    private void scrollPage() {
        System.out.println("\nScrolling through Visit Us page...");
        try {
            
            Object heightObj = js.executeScript("return document.body.scrollHeight");
            Object viewportObj = js.executeScript("return window.innerHeight");

            double pageHeight = ((Number) heightObj).doubleValue();
            double viewportHeight = ((Number) viewportObj).doubleValue();

            System.out.println("Page height: " + pageHeight + "px, Viewport: " + viewportHeight + "px");

            // Smooth scroll to bottom
            js.executeScript("window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});");
            Thread.sleep(1000);

            // Verify scroll
            Object finalScrollObj = js.executeScript("return window.pageYOffset");
            double finalScroll = ((Number) finalScrollObj).doubleValue();
            System.out.println("Final scroll position: " + finalScroll + "px");
            System.out.println("Scrolled through entire page");

            // Scroll back to top
            js.executeScript("window.scrollTo({top: 0, behavior: 'smooth'});");
            Thread.sleep(500);

        } catch (Exception e) {
            System.out.println("⚠ Scroll had issues: " + e.getMessage());
           
        }
    }

    private boolean findElement(String[] xpaths, String elementName) {
        for (String xpath : xpaths) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                    System.out.println("✓ Found: " + elementName);
                    return true;
                }
            } catch (Exception e) {
                continue;
            }
        }
        System.out.println("✗ Not found: " + elementName);
        return false;
    }
}