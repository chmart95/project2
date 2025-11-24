package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

public class WomenForTamutPage {
	private JavascriptExecutor js;
    private WebDriver driver;
    private WebDriverWait wait;

    public WomenForTamutPage(WebDriver driver) {
    	this.js = (JavascriptExecutor) driver;
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
        PageFactory.initElements(driver, this);
    }
    
    public void scrollHalfwayDown() {
        System.out.println("\nScrolling halfway down Women for A&M-Texarkana page...");
        try {
            
            js.executeScript("window.scrollTo({ top: document.body.scrollHeight * 0.20, behavior: 'smooth' });");
            Thread.sleep(1200);

            Thread.sleep(800);
        } catch (Exception e) {
            System.out.println("⚠ Scroll on Women page had minor issue: " + e.getMessage());
        }
    }

    public void verifyWomenPage() {
        System.out.println("Verifying Women for A&M-Texarkana page");
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Page Title: " + driver.getTitle());
        
        // Verify page title contains "Women for A&M-Texarkana"
        String title = driver.getTitle();
        boolean titleCorrect = title.contains("Women for A&M-Texarkana") || 
                              title.contains("Women for A&M") ||
                              title.contains("Women") && title.contains("Texarkana");
        Assert.assertTrue(titleCorrect, 
            "Title does not contain expected text. Actual title: " + title);
        System.out.println("✓ Title contains expected text");
        
        // Look for "Make a Scholarship Donation" link
        boolean donationFound = findElement(new String[]{
            "//a[contains(., 'Make a Scholarship Donation')]",
            "//a[contains(., 'Scholarship Donation')]",
            "//a[contains(., 'Donation')]",
            "//a[contains(., 'Donate')]",
            "//a[contains(@href, 'donation')]",
            "//a[contains(@href, 'donate')]"
        }, "Make a Scholarship Donation");
        Assert.assertTrue(donationFound, "Donation link not found");
        
        // Look for "Join Women for A&M-Texarkana" link
        boolean joinFound = findElement(new String[]{
            "//a[contains(., 'Join Women for A&M-Texarkana')]",
            "//a[contains(., 'Join Women')]",
            "//a[contains(., 'Join')]",
            "//a[contains(@href, 'join')]",
            "//a[contains(@href, 'membership')]"
        }, "Join Women for A&M-Texarkana");
        Assert.assertTrue(joinFound, "Join link not found");
        
        System.out.println("All Women page elements verified successfully!");
    }

    private boolean findElement(String[] xpaths, String elementName) {
        for (String xpath : xpaths) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                if (!elements.isEmpty()) {
                    WebElement element = elements.get(0);
                    try {
                        if (element.isDisplayed()) {
                            System.out.println("✓ Found: " + elementName);
                            return true;
                        }
                    } catch (Exception e) {
                        
                        System.out.println("✓ Found: " + elementName + " (exists in DOM)");
                        return true;
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
        System.out.println("✗ Not found: " + elementName);
        return false;
    }
}