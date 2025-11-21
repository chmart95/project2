package pages;

import org.openqa.selenium.By;
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

    public VisitUsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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
        
        // Look for "Upcoming Events" link - try multiple variations
        boolean upcomingEventsFound = findElement(new String[]{
            "//a[contains(., 'Upcoming Events')]",
            "//a[contains(., 'Events')]",
            "//a[contains(@href, 'events')]",
            "//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'upcoming')]"
        }, "Upcoming Events");
        Assert.assertTrue(upcomingEventsFound, "Upcoming Events link not found");
        
        // Look for "Schedule a Campus Tour" link
        boolean scheduleTourFound = findElement(new String[]{
            "//a[contains(., 'Schedule a Campus Tour')]",
            "//a[contains(., 'Schedule') and contains(., 'Tour')]",
            "//a[contains(., 'Campus Tour')]",
            "//a[contains(@href, 'tour')]"
        }, "Schedule a Campus Tour");
        Assert.assertTrue(scheduleTourFound, "Schedule a Campus Tour link not found");
        
        // Look for "Virtual Tour" link
        boolean virtualTourFound = findElement(new String[]{
            "//a[contains(., 'Virtual Tour')]",
            "//a[contains(., 'Virtual')]",
            "//a[contains(@href, 'virtual')]"
        }, "Virtual Tour");
        Assert.assertTrue(virtualTourFound, "Virtual Tour link not found");
        
        System.out.println("All Visit Us page elements verified successfully!");
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