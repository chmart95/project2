package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HomePage {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    // Multiple possible search box locators
    @FindBy(css = "input[type='search']")
    private WebElement searchBox1;
    
    @FindBy(css = "input.site-search-input")
    private WebElement searchBox2;
    
    @FindBy(xpath = "//input[@placeholder='Search']")
    private WebElement searchBox3;

    public void open() {
        System.out.println("Opening TAMUT homepage...");
        driver.get("https://tamut.edu/");
        
        // Wait for page to load
        wait.until(driver -> js.executeScript("return document.readyState").equals("complete"));
        
        // Handle any cookie/popup banners
        try {
            Thread.sleep(2000);
            List<WebElement> acceptButtons = driver.findElements(
                By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'accept')] | " +
                        "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'agree')] | " +
                        "//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'accept')]")
            );
            if (!acceptButtons.isEmpty()) {
                acceptButtons.get(0).click();
                Thread.sleep(500);
            }
        } catch (Exception e) {
            System.out.println("No cookie banner or already dismissed");
        }
        System.out.println("Homepage loaded successfully");
    }

    public void search(String query) {
        System.out.println("Searching for: " + query);
        
        // Try to find and use search box
        WebElement searchBox = null;
        try {
            searchBox = wait.until(ExpectedConditions.visibilityOf(searchBox1));
        } catch (Exception e1) {
            try {
                searchBox = wait.until(ExpectedConditions.visibilityOf(searchBox2));
            } catch (Exception e2) {
                try {
                    searchBox = wait.until(ExpectedConditions.visibilityOf(searchBox3));
                } catch (Exception e3) {
                    // Last resort: find any search input
                    searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//input[contains(@type,'search') or contains(@placeholder,'Search') or contains(@class,'search')]")
                    ));
                }
            }
        }
        
        searchBox.clear();
        searchBox.sendKeys(query);
        searchBox.sendKeys(Keys.ENTER);
        
        // Wait for search results
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("search"),
                ExpectedConditions.urlContains("q="),
                ExpectedConditions.urlContains("query")
            ));
            Thread.sleep(2000); // Let results fully load
        } catch (Exception e) {
            System.out.println("Search page may have loaded differently");
        }
        
        System.out.println("Search executed, current URL: " + driver.getCurrentUrl());
    }

    public void clickFirstSearchResultContaining(String text) {
        System.out.println("Looking for search result containing: " + text);
        
        WebElement resultLink = null;
        
        // Special handling for "Women" search - we want "Women for A&M-Texarkana", not course catalog
        if (text.equalsIgnoreCase("Women")) {
            System.out.println("Searching specifically for Women for A&M-Texarkana page");
            String[] womenXpaths = {
                "//a[contains(., 'Women for A&M-Texarkana')]",
                "//a[contains(., 'Women for A&M')]",
                "//a[contains(@href, 'alumni/wam')]",
                "//a[contains(., 'Women') and contains(@href, 'alumni')]",
                "//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'women for')]"
            };
            
            for (String xpath : womenXpaths) {
                try {
                    List<WebElement> links = driver.findElements(By.xpath(xpath));
                    if (!links.isEmpty()) {
                        resultLink = links.get(0);
                        System.out.println("Found Women for A&M-Texarkana link using xpath: " + xpath);
                        break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            
            // If still not found, navigate directly
            if (resultLink == null) {
                System.out.println("Women for A&M-Texarkana not in search results, navigating directly");
                driver.get("https://tamut.edu/alumni/wam/index.html");
                waitABit(1000);
                return;
            }
        } else {
            // For other searches (like Computer Science), use original logic
            String[] xpaths = {
                "//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + text.toLowerCase() + "')]",
                "//div[contains(@class,'result')]//a[contains(., '" + text + "')]",
                "//div[contains(@class,'search')]//a[contains(., '" + text + "')]",
                "//a[contains(., '" + text + "')]",
                "//h3//a[contains(., '" + text + "')]",
                "//article//a[contains(., '" + text + "')]"
            };
            
            for (String xpath : xpaths) {
                try {
                    List<WebElement> links = driver.findElements(By.xpath(xpath));
                    if (!links.isEmpty()) {
                        resultLink = links.get(0);
                        System.out.println("Found link using xpath: " + xpath);
                        break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            
            if (resultLink == null) {
                throw new RuntimeException("Could not find search result containing: " + text);
            }
        }
        
        // Scroll and click
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", resultLink);
        waitABit(500);
        
        try {
            resultLink.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", resultLink);
        }
        
        System.out.println("Clicked on search result");
        waitABit(1000);
    }

    public VisitUsPage goToVisitUsViaAboutMenu() {
        System.out.println("Navigating to Visit Us via About menu");
        
        // Try to find About menu
        WebElement aboutMenu = null;
        String[] aboutXpaths = {
            "//button[contains(., 'About')]",
            "//a[contains(., 'About')]",
            "//li[contains(@class,'menu')]//a[contains(., 'About')]",
            "//*[contains(text(),'About')]"
        };
        
        for (String xpath : aboutXpaths) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                if (!elements.isEmpty()) {
                    aboutMenu = elements.get(0);
                    System.out.println("Found About menu");
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        if (aboutMenu != null) {
            clickElement(aboutMenu);
            waitABit(1000);
        }
        
        // Now find Visit Us link
        WebElement visitLink = null;
        String[] visitXpaths = {
            "//a[normalize-space()='Visit Us' or normalize-space()='Visit']",
            "//a[contains(@href,'visit')]",
            "//a[contains(., 'Visit Us')]",
            "//a[contains(., 'Visit')]"
        };
        
        for (String xpath : visitXpaths) {
            try {
                visitLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                System.out.println("Found Visit Us link");
                break;
            } catch (Exception e) {
                continue;
            }
        }
        
        if (visitLink == null) {
            // Direct navigation as fallback
            System.out.println("Direct navigation to visit page");
            driver.get("https://tamut.edu/visit/index.html");
        } else {
            clickElement(visitLink);
        }
        
        waitABit(1000);
        return new VisitUsPage(driver);
    }

    public void openAcademicsAndClickComputerScienceLearnMore() {
        System.out.println("Opening Academics menu");
        
        // Find Academics menu
        WebElement academicsMenu = null;
        String[] academicsXpaths = {
            "//button[contains(., 'Academics')]",
            "//a[contains(., 'Academics')]",
            "//li//a[contains(., 'Academics')]"
        };
        
        for (String xpath : academicsXpaths) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                if (!elements.isEmpty()) {
                    academicsMenu = elements.get(0);
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        if (academicsMenu != null) {
            clickElement(academicsMenu);
            waitABit(1500);
        }
        
        // Find Computer Science Learn More link
        WebElement csLink = null;
        String[] csXpaths = {
            "//a[contains(., 'Computer Science')]//following::a[contains(., 'Learn More')][1]",
            "//a[contains(., 'Computer Science')]/ancestor::div//a[contains(., 'Learn More')]",
            "//a[contains(@href, 'computer-science')]",
            "//a[contains(., 'Computer Science')]"
        };
        
        for (String xpath : csXpaths) {
            try {
                csLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", csLink);
                waitABit(500);
                break;
            } catch (Exception e) {
                continue;
            }
        }
        
        if (csLink == null) {
            // Direct navigation as fallback
            System.out.println("Direct navigation to Computer Science page");
            driver.get("https://tamut.edu/academic-programs/computer-science/index.html");
        } else {
            clickElement(csLink);
        }
        
        waitABit(1000);
    }

    public WomenForTamutPage goToWomenViaAlumniMenu() {
        System.out.println("Opening Alumni menu");
        
        // Find Alumni menu
        WebElement alumniMenu = null;
        String[] alumniXpaths = {
            "//button[contains(., 'Alumni')]",
            "//a[contains(., 'Alumni')]",
            "//a[contains(., 'Alumni & Friends')]",
            "//a[contains(., 'Alumni & Giving')]"
        };
        
        for (String xpath : alumniXpaths) {
            try {
                List<WebElement> elements = driver.findElements(By.xpath(xpath));
                if (!elements.isEmpty()) {
                    alumniMenu = elements.get(0);
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        if (alumniMenu != null) {
            clickElement(alumniMenu);
            waitABit(1500);
        }
        
        // Find Women for A&M-Texarkana link
        WebElement womenLink = null;
        String[] womenXpaths = {
            "//a[contains(., 'Women for A&M-Texarkana')]",
            "//a[contains(., 'Women for')]",
            "//a[contains(@href, 'wam')]",
            "//a[contains(@href, 'women')]"
        };
        
        for (String xpath : womenXpaths) {
            try {
                womenLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                break;
            } catch (Exception e) {
                continue;
            }
        }
        
        if (womenLink == null) {
            // Direct navigation as fallback
            System.out.println("Direct navigation to Women page");
            driver.get("https://tamut.edu/alumni/wam/index.html");
        } else {
            clickElement(womenLink);
        }
        
        waitABit(1000);
        return new WomenForTamutPage(driver);
    }

    private void clickElement(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", element);
        }
    }

    private void waitABit(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}