package tests;

import org.testng.annotations.Test;
import base.BaseTest;
import pages.*;

public class TAMUTTests extends BaseTest {

    @Test(priority = 1)
    public void testCase1_VisitUsViaAboutMenu() {
        System.out.println("\n========== TEST CASE 1: Visit Us via About Menu ==========");
        HomePage home = new HomePage(driver);
        home.open();
        VisitUsPage visit = home.goToVisitUsViaAboutMenu();
        visit.verifyVisitUsElements();
        System.out.println("========== TEST CASE 1 COMPLETED ==========\n");
    }

    @Test(priority = 2)
    public void testCase2_SearchComputerScience() {
        System.out.println("\n========== TEST CASE 2: Search Computer Science ==========");
        HomePage home = new HomePage(driver);
        home.open();
        home.search("Computer Science");
        home.clickFirstSearchResultContaining("Computer Science");
        ComputerSciencePage cs = new ComputerSciencePage(driver);
        cs.verifyCSPageElements();
        cs.clickVisitUsAndVerify();
        System.out.println("========== TEST CASE 2 COMPLETED ==========\n");
    }

    @Test(priority = 3)
    public void testCase3_CSFromAcademics() {
        System.out.println("\n========== TEST CASE 3: CS from Academics Menu ==========");
        HomePage home = new HomePage(driver);
        home.open();
        home.openAcademicsAndClickComputerScienceLearnMore();
        ComputerSciencePage cs = new ComputerSciencePage(driver);
        cs.verifyCSPageElements();
        cs.clickVisitUsAndVerify();
        System.out.println("========== TEST CASE 3 COMPLETED ==========\n");
    }

    @Test(priority = 4)
    public void testCase4_SearchWomen() {
        System.out.println("\n========== TEST CASE 4: Search Women ==========");
        HomePage home = new HomePage(driver);
        home.open();
        home.search("Women");
        home.clickFirstSearchResultContaining("Women");
        WomenForTamutPage women = new WomenForTamutPage(driver);
        women.verifyWomenPage();
        System.out.println("========== TEST CASE 4 COMPLETED ==========\n");
    }

    @Test(priority = 5)
    public void testCase5_WomenViaAlumniMenu() {
        System.out.println("\n========== TEST CASE 5: Women via Alumni Menu ==========");
        HomePage home = new HomePage(driver);
        home.open();
        WomenForTamutPage women = home.goToWomenViaAlumniMenu();
        women.verifyWomenPage();
        System.out.println("========== TEST CASE 5 COMPLETED ==========\n");
    }
}