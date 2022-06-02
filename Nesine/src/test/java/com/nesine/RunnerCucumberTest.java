package com.nesine;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(Cucumber.class)
    @CucumberOptions( features = "classpath:features/nesine.feature"
            , glue = {"com.nesine.step"},
    plugin = {"pretty"}, monochrome = true)


public class RunnerCucumberTest {

        WebDriver driver;
        ArrayList<Integer> playUI ;
        ArrayList<Integer> marketUI ;
        ArrayList<Integer> playedCount;
        ArrayList<Integer> marketNo;
        WebDriverWait wait;

       @Given("Chrome ayaga kaldirilir.")
        public void chromeHazirlik() {
            System.setProperty("webdriver.chrome.driver", "libs/chromedriver.exe");
            this.driver = new ChromeDriver();
            driver.manage().window().maximize();

       }

       @Given("Mozilla ayaga kaldirilir.")
        public void mozillaHazirlik() {
           System.setProperty("webdriver.gecko.driver","libs/geckodriver.exe"); // Setting system properties of FirefoxDriver
           this.driver = new FirefoxDriver(); //Creating an object of FirefoxDriver
           driver.manage().window().maximize();
        }

        @Given("Nesine.com anasayfasına gidilir.")
        public void nesineAnasayfa(){
            driver.get("https://www.nesine.com/");
        }

        @Given("Tckn yerine {string} girilir.")  // nesine.feature'da tckn kısmına *********** kısmına tckn girilir.
        public void tcknGir(String tckn) {
            WebElement element = driver.findElement(By.cssSelector("#txtUsername"));
            element.sendKeys(tckn);
        }

        @Given("Parola yerine {string} girilir.")   // nesine.feature'da parola *********** kısmına parola girilir.
        public void sifreGir(String sifre) {
            WebElement parola = driver.findElement(By.id("realpass"));
            parola.sendKeys(sifre);
        }

        @Given("Login butonuna tiklanir.")
        public void loginButton() {
            WebElement button = driver.findElement(By.xpath("//a[@class='btn btn-login lgbtn']")); // //a[contains(text(),'Hesabım ')]
            if (button.isDisplayed() && button.isEnabled()) {
                button.click();
            }
        }

        @Given("Popüler bahis butonuna tiklanir.")
        public void populer()  {
            WebElement populer = driver.findElement(By.xpath("(//a[@href='/iddaa/populer-bahisler'])[2]"));
            if (populer.isDisplayed() && populer.isEnabled()) {
                populer.click();
            }
        }

        @Given("Populer bahisler linki kontrol edilir.")
        public void kontrol() {
            String URL = driver.getCurrentUrl();
            Assert.assertEquals(URL, "https://www.nesine.com/iddaa/populer-bahisler");
            System.out.println("Populer bahisler linki kontrol edildi.");
        }

        @Given("Futbol tabina tiklanir.")
        public void futboltabi() {
            WebElement futboltabi = driver.findElement(By.xpath("//div/nav/button[1]"));
            if (futboltabi.isDisplayed() && futboltabi.isEnabled()) {
                futboltabi.click();
            }
        }
        @When("Oynanma sayisi çekilir.")
        public void oynanmaSayisi() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
            playUI= new  ArrayList<Integer>();
            List<WebElement> listElement = driver.findElements(By.className("playedCount"));
            for(int i =0;i<listElement.size();i++) {
                // System.out.println(elementText);
                String x = listElement.get(i).getText();
                String b= x.replace(".","");
                playUI.add(Integer.parseInt(b));
            }
            System.out.println("Oynanma Sayisi:" + playUI);
        }
        @When("Kod çekilir.")
        public  void Kod(){
            marketUI =new ArrayList<Integer>();
            List<WebElement> listElement1 = driver.findElements(By.xpath("//div[@class='betLine']/div[@class='matchCode']//span"));
            for(int i =0;i<listElement1.size();i++) {
                String x = listElement1.get(i).getText();
                marketUI.add(Integer.parseInt(x));
            }
            System.out.println("Kod Numaraları:" + marketUI);
        }
        @When("Post methodu ile Oynanma sayisi ve Kod çekilir.")
        public void post() {
            String query_url = "https://www.nesine.com/Iddaa/GetPopularBets";
            String json = "{\"eventType\":1,\"date\":null}";
            try {
                URL url = new URL(query_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes("UTF-8"));
                os.close();
                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String result = IOUtils.toString(in, "UTF-8");
                System.out.println(result);
                System.out.println("result after Reading JSON Response");
                JSONObject myResponse = new JSONObject(result);
                JSONArray response = myResponse.getJSONArray("PopularBetList");
                playedCount = new ArrayList<>();
                marketNo = new ArrayList<>();

                for(int i=0; i<response.length(); i++){
                    playedCount.add(response.getJSONObject(i).getInt("PlayedCount"));
                    marketNo.add(response.getJSONObject(i).getInt("MarketNo"));
                }
                System.out.println("Oynanma Sayisi:" + playedCount );
                System.out.println("Kod Numaraları:" + marketNo );
                in.close();
                conn.disconnect();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        @When("Oynanma sayisi ve Kod değerleri kontrol edilir.")
        public void liste1(){
            Assertions.assertEquals(playedCount , playUI);
            System.out.println("playedCount kontrol edildi");
            Assertions.assertEquals(marketNo , marketUI);
            System.out.println("marketNo kontrol edildi");

        }
        @Then("Hesabım'a tiklanir.")
        public void hesabim(){
            WebElement hesabim = driver.findElement(By.xpath("(//a[contains(text(),'Hesabım ')])[2]"));
            if (hesabim.isDisplayed() && hesabim.isEnabled()) {
                hesabim.click();
            }
        }
        @Then("Çıkıs'a tiklanir.")
        public void cikis(){
            WebElement cikis = driver.findElement(By.xpath("//a[contains(text(),'Çıkış')]"));
            if (cikis.isDisplayed() && cikis.isEnabled()) {
                cikis.click();
            }
        }

        @Then("Tarayici kapatılır.")
        public void sonlanma () {
            driver.quit();
        }
    }
