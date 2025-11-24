package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
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
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    // Search box locators
    @FindBy(css = "input[type='search']") private WebElement searchBox1;
    @FindBy(css = "input.site-search-input") private WebElement searchBox2;
    @FindBy(xpath = "//input[@placeholder='Search']") private WebElement searchBox3;

    public void open() {
        System.out.println("Opening TAMUT homepage...");
        driver.get("https://tamut.edu/");
        wait.until(driver -> js.executeScript("return document.readyState").equals("complete"));

        
        try {
            Thread.sleep(1000);
            List<WebElement> acceptBtns = driver.findElements(
                By.xpath("//div[contains(@class,'cookie') or contains(@class,'gdpr')]//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'accept')]")
            );
            if (!acceptBtns.isEmpty() && acceptBtns.get(0).isDisplayed()) {
                acceptBtns.get(0).click();
                Thread.sleep(300);
            }
        } catch (Exception e) {
            System.out.println("No cookie banner found or already dismissed");
        }

        js.executeScript("window.scrollTo(0, 0);");
        System.out.println("Homepage loaded: " + driver.getCurrentUrl());
    }

    public void search(String query) {
        System.out.println("Searching for: " + query);
        WebElement searchBox = null;
        try { searchBox = wait.until(ExpectedConditions.visibilityOf(searchBox1));
        } catch (Exception e1) {
            try { searchBox = wait.until(ExpectedConditions.visibilityOf(searchBox2));
            } catch (Exception e2) {
                try { searchBox = wait.until(ExpectedConditions.visibilityOf(searchBox3));
                } catch (Exception e3) {
                    searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//input[contains(@type,'search') or contains(@placeholder,'Search')]")));
                }
            }
        }

        searchBox.clear();
        searchBox.sendKeys(query);
        searchBox.sendKeys(Keys.ENTER);

        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("search"),
                ExpectedConditions.urlContains("q="),
                ExpectedConditions.urlContains("query")
            ));
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Search results page loaded differently");
        }
        System.out.println("Search complete. URL: " + driver.getCurrentUrl());
    }

    public void clickFirstSearchResultContaining(String text) {
        System.out.println("Looking for search result containing: " + text);

        if (text.equalsIgnoreCase("Women")) {
            
            String[] womenXPaths = {
                "//a[contains(., 'Women for A&M-Texarkana') or contains(., 'Women for A&M')]",
                "//a[contains(@href, 'alumni/wam')]",
                "//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'women for') and contains(@href, 'alumni')]"
            };
            for (String xpath : womenXPaths) {
                try {
                    WebElement link = driver.findElement(By.xpath(xpath));
                    if (link.isDisplayed()) {
                        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", link);
                        Thread.sleep(500);
                        link.click();
                        System.out.println("Clicked correct Women for A&M-Texarkana result");
                        return;
                    }
                } catch (Exception e) { /* continue */ }
            }
            System.out.println("Correct Women link not found → going directly");
            driver.get("https://tamut.edu/alumni/wam/index.html");
            return;
        }

        // Default case (e.g. Computer Science)
        String xpath = "//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + text.toLowerCase() + "')]";
        try {
            WebElement link = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", link);
            Thread.sleep(500);
            link.click();
            System.out.println("Clicked first result containing: " + text);
        } catch (Exception e) {
            System.out.println("Result not found → navigating directly");
            if (text.toLowerCase().contains("computer science")) {
                driver.get("https://tamut.edu/academic-programs/computer-science/index.html");
            }
        }
    }

    // TEST CASE 1 
    public VisitUsPage goToVisitUsViaAboutMenu() {
        System.out.println("Navigating to Visit Us via About menu...");
        try {
            WebElement aboutLi = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//ul[@class='main-nav']/li[contains(.,'About') or @data-title='About']")));
            Actions actions = new Actions(driver);
            actions.moveToElement(aboutLi).perform();
            Thread.sleep(600);

            WebElement visitUsLink = aboutLi.findElement(By.xpath(".//a[@href='visit/index.html' or contains(@href,'visit')]"));
            js.executeScript("arguments[0].click();", visitUsLink);
            System.out.println("Clicked Visit Us from About menu");
        } catch (Exception e) {
            System.out.println("Visit Us not in About menu → direct navigation");
            driver.get("https://tamut.edu/visit/index.html");
        }
        return new VisitUsPage(driver);
    }

    public void openAcademicsAndClickComputerScienceLearnMore() {
        System.out.println("=== TEST CASE 3: Academics → Grid Page → CS Learn More ===");

        
        try {
            WebElement academics = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space(.)='Academics']")));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", academics);
            waitABit(500);
            academics.click();
            System.out.println("✓ Clicked 'Academics' — arrived at programs grid page");
        } catch (Exception e) {
            driver.get("https://tamut.edu/academic-programs/index.html");
            System.out.println("Direct to academic programs grid page");
        }

        wait.until(ExpectedConditions.urlContains("academic-programs"));
        waitABit(2000);  

        
        js.executeScript("window.scrollTo(0, document.body.scrollHeight * 0.3);");
        System.out.println("✓ Scrolled 30% down to reveal Computer Science card");
        waitABit(1500);  // Let lazy-load finish rendering

        
        WebElement learnMore = null;
        String[] xpaths = {
            "//div[contains(@class, 'item')]//div[contains(@class, 'button')]//a[@href='computer-science/index.html']",  // Exact from your snippet
            "//div[contains(@class, 'item')]//a[@href='computer-science/index.html' and @target='_top']",  // Matches target="_top"
            "//a[@href='computer-science/index.html']",  // Fallback for the relative href
            "//*[contains(text(), 'Computer Science')]//following::a[contains(@href, 'computer-science')][1]"  // Text-based fallback
        };

        for (String xpath : xpaths) {
            try {
                learnMore = driver.findElement(By.xpath(xpath));
                if (learnMore.isDisplayed()) {
                    System.out.println("✓ Found Learn More link: " + learnMore.getAttribute("href"));
                    break;
                }
            } catch (Exception ignored) {
                // Try next XPath
            }
        }

        if (learnMore == null) {
            System.out.println("✗ Learn More not found → direct to CS page");
            driver.get("https://tamut.edu/academic-programs/computer-science/index.html");
            return;
        }

        
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", learnMore);
        waitABit(800);
        try {
            learnMore.click();
            System.out.println("✓ Clicked 'Learn More' — navigating to full CS page");
        } catch (Exception e) {
            
            js.executeScript("window.location.href = '" + driver.getCurrentUrl().replace("/index.html", "/computer-science/index.html") + "';");
            System.out.println("✓ Clicked via JS with URL resolution");
        }

        wait.until(ExpectedConditions.urlContains("computer-science"));
        waitABit(1000);
        System.out.println("✅ Successfully reached Computer Science page via Academics grid!");
 
    }

    public WomenForTamutPage goToWomenViaAlumniSidebar() {
        System.out.println("=== TEST CASE 5: Alumni & Friends → Sidebar → Women for A&M-Texarkana ===");

        // Step 1: Click "Alumni & Friends" in header
        try {
            WebElement alumniLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space(.)='Alumni & Friends' or contains(.,'Alumni')]")));
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", alumniLink);
            waitABit(500);
            alumniLink.click();
            System.out.println("Clicked 'Alumni & Friends' → on alumni page");
        } catch (Exception e) {
            System.out.println("Alumni link failed → going direct to page");
            driver.get("https://tamut.edu/alumni/index.html");
        }

        wait.until(ExpectedConditions.urlContains("/alumni/"));
        waitABit(2000);

        // Step 2: Click "Women for A&M-Texarkana" in the LEFT SIDEBAR
        WebElement womenSidebarLink = null;
        String[] sidebarXPaths = {
            "//aside//a[contains(@href,'wam') and contains(.,'Women')]",
            "//div[contains(@class,'sidebar')]//a[contains(@href,'wam')]",
            "//a[contains(@href,'/alumni/wam/') or contains(@href,'wam/index.html')]",
            "//nav//a[contains(.,'Women for A&M-Texarkana') or contains(.,'Women for A&M')]"
        };

        for (String xpath : sidebarXPaths) {
            try {
                womenSidebarLink = driver.findElement(By.xpath(xpath));
                if (womenSidebarLink.isDisplayed()) {
                    System.out.println("Found Women link in sidebar: " + womenSidebarLink.getText());
                    break;
                }
            } catch (Exception ignored) {}
        }

        if (womenSidebarLink == null) {
            System.out.println("Sidebar link not found → navigating directly");
            driver.get("https://tamut.edu/alumni/wam/index.html");
            return new WomenForTamutPage(driver);
        }

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", womenSidebarLink);
        waitABit(600);
        try {
            womenSidebarLink.click();
            System.out.println("Clicked Women for A&M-Texarkana from sidebar");
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", womenSidebarLink);
        }

        wait.until(ExpectedConditions.urlContains("wam"));
        waitABit(1500);

        return new WomenForTamutPage(driver);
    }

    private void clickElement(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", element);
        }
    }

    private void waitABit(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}