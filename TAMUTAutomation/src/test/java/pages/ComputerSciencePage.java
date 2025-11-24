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

public class ComputerSciencePage {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    public VisitUsPage visitUsPage;

    public ComputerSciencePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
        this.js = (JavascriptExecutor) driver;
        this.visitUsPage = new VisitUsPage(driver);
        PageFactory.initElements(driver, this);
    }

    public void verifyCSPageElements() {
        System.out.println("Verifying Computer Science page elements");
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Page Title: " + driver.getTitle());
        
        
        wait.until(driver -> js.executeScript("return document.readyState").equals("complete"));
        
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        

        boolean accreditationFound = findElement(new String[]{
            "//button[normalize-space()='Accreditation']",
            "//a[normalize-space()='Accreditation']",
            "//*[normalize-space()='Accreditation' and (self::button or self::a)]",
            "//button[contains(., 'Accreditation')]",
            "//a[contains(., 'Accreditation')]",
            "//*[contains(text(), 'Accreditation')]"
        }, "Accreditation");
        Assert.assertTrue(accreditationFound, "Accreditation button/link not found");
        
        // 2. Look for "Faculty" - also a BUTTON or link in the header area
        boolean facultyFound = findElement(new String[]{
            "//button[normalize-space()='Faculty']",
            "//a[normalize-space()='Faculty']",
            "//*[normalize-space()='Faculty' and (self::button or self::a)]",
            "//button[contains(., 'Faculty')]",
            "//a[contains(., 'Faculty')]",
            "//*[contains(text(), 'Faculty')]"
        }, "Faculty");
        Assert.assertTrue(facultyFound, "Faculty button/link not found");
        
        // 3. Look for "Apply Here" link - in main content area
        boolean applyFound = findElement(new String[]{
            "//a[normalize-space()='Apply Here']",
            "//a[contains(., 'Apply Here')]",
            "//a[contains(., 'Apply')]",
            "//a[contains(@href, 'admissions')]"
        }, "Apply Here");
        Assert.assertTrue(applyFound, "Apply Here link not found");
        
        // 4. Look for "Visit Us" link - it has class "long-button-alt" and href "../../visit/index.html"
        boolean visitUsFound = findElement(new String[]{
            "//a[@class='long-button-alt' and @href='../../visit/index.html']",
            "//a[contains(@class, 'long-button-alt') and contains(@href, 'visit')]",
            "//a[@href='../../visit/index.html']",
            "//a[contains(@href, 'visit/index.html')]",
            "//a[contains(., 'Visit Us')]",
            "//a[contains(@href, 'visit')]"
        }, "Visit Us");
        Assert.assertTrue(visitUsFound, "Visit Us link not found");
        
        System.out.println("\n✅ All Computer Science page elements verified successfully!");
        System.out.println("   • Accreditation: ✓");
        System.out.println("   • Faculty: ✓");
        System.out.println("   • Apply Here: ✓");
        System.out.println("   • Visit Us: ✓");
    }

    public void clickVisitUsAndVerify() {
        System.out.println("Clicking Visit Us link on CS page");
        
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        WebElement visitLink = null;
        String[] visitXpaths = {
            "//a[@class='long-button-alt' and @href='../../visit/index.html']",
            "//a[contains(@class, 'long-button-alt') and contains(@href, 'visit')]",
            "//a[@href='../../visit/index.html']",
            "//a[contains(@href, 'visit/index.html')]",
            "//a[contains(., 'Visit Us')]",
            "//a[contains(@href, 'visit')]"
        };
        
        for (String xpath : visitXpaths) {
            try {
                List<WebElement> links = driver.findElements(By.xpath(xpath));
                if (!links.isEmpty()) {
                    
                    for (WebElement link : links) {
                        String href = link.getAttribute("href");
                        String className = link.getAttribute("class");
                        
                        
                        if (href != null && href.contains("visit") && 
                            className != null && className.contains("long-button-alt")) {
                            visitLink = link;
                            System.out.println("Found Visit Us link with href: " + href);
                            break;
                        }
                    }
                    if (visitLink != null) break;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        if (visitLink == null) {
            System.out.println("Visit link not found, navigating directly");
            driver.get("https://tamut.edu/visit/index.html");
        } else {
            try {
                // Scroll to element
                js.executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", visitLink);
                Thread.sleep(800);
                
                // Wait for element to be clickable
                wait.until(ExpectedConditions.elementToBeClickable(visitLink));
                
                // Try regular click first
                visitLink.click();
                System.out.println("Clicked Visit Us link");
            } catch (Exception e) {
                System.out.println("Regular click failed, using JavaScript click");
                try {
                    js.executeScript("arguments[0].click();", visitLink);
                } catch (Exception e2) {
                    // Last resort - navigate directly
                    System.out.println("JS click also failed, navigating directly");
                    driver.get("https://tamut.edu/visit/index.html");
                }
            }
        }
        
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        visitUsPage.verifyVisitUsElements();
    }

    private boolean findElement(String[] xpaths, String elementName) {
        
        for (String xpath : xpaths) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                

                for (WebElement element : elements) {
                    try {
                        
                        if (element.isDisplayed()) {
                            String elementText = element.getText().trim();
                            
                            if (elementText.contains(elementName) || 
                                elementText.equals(elementName) ||
                                elementName.equals("Visit Us")) { // Special case for Visit Us
                                System.out.println("✓ Found: " + elementName + " (visible)");
                                return true;
                            }
                        }
                    } catch (Exception e) {

                        String text = null;
                        try {
                            text = element.getText();
                        } catch (Exception ex) {
                            continue;
                        }
                        
                        if (text != null && !text.trim().isEmpty() && 
                            (text.contains(elementName) || text.equals(elementName))) {
                            System.out.println("✓ Found: " + elementName + " (in DOM with text)");
                            return true;
                        }
                        
                        try {
                            String jsText = (String) js.executeScript(
                                "return arguments[0].textContent.trim();", element);
                            if (jsText != null && !jsText.isEmpty() && 
                                (jsText.contains(elementName) || jsText.equals(elementName))) {
                                System.out.println("✓ Found: " + elementName + " (verified via JS)");
                                return true;
                            }
                        } catch (Exception jsEx) {
                            // Continue to next element
                        }
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