package TestVegrant.TestVegrant;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;

import java.io.FileNotFoundException;
import io.restassured.path.json.JsonPath;

public class Weather {
	WebDriver driver;
	int UItempinC;
	int APItempinC;
	
	@BeforeTest
	public void Setup() throws InterruptedException {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.get("https://www.ndtv.com/");
		driver.manage().window().maximize();
		Thread.sleep(5000);
	}
	
	@Test(priority=1)
	public void TestVeg() {
	
		driver.findElement(By.xpath("//a[@class='notnow']")).click();
		driver.findElement(By.xpath("//a[@id='h_sub_menu']")).click();
		driver.findElement(By.linkText("WEATHER")).click();
		driver.findElement(By.id("searchBox")).sendKeys("Mumbai");
		driver.findElement(By.xpath("//*[@class='outerContainer' and @title='Mumbai']")).click();
		String temp = driver.findElement(By.xpath("//*[@class='outerContainer' and @title='Mumbai']//div/span[1]")).getText();
		
		int tpmlen = temp.length();
		String UItemp = temp.substring(0, tpmlen-1);
		UItempinC = Integer.parseInt(UItemp);
		System.out.println("UItemp "+UItempinC);
		driver.close();
	}
	
	@Test(priority=2)
	public void getTempApi() {
		RestAssured.baseURI = "http://api.openweathermap.org";
		String st = given().log().all().headers("Content-Type","application/json").queryParam("q", "Mumbai")
		.queryParam("appid", "0c0ccd65dc3d65659551ff3357ea23ca")
		.queryParam("units", "metric").when().get("/data/2.5/weather").then().log().all().statusCode(200).extract().asString();
		
		JsonPath js = new JsonPath(st);
		APItempinC = js.get("main.temp");
		System.out.println("APItemp "+APItempinC);
	}
	
	@Test(priority=3)
	public void Comparator() throws FileNotFoundException  {
		System.out.println("********************************************");
		
		int diff = APItempinC - UItempinC;
		System.out.println("temp diff "+diff);
		if((-2<=diff && diff<=0)||(0<=diff && diff<=2) ) {
			System.out.println("Testcase passed in variance of 2 degree");
		}else {
			System.out.println("Testcase failed, Variance does not fall in range of 2 degree");
			throw new RuntimeException("Testcase failed, Variance does not fall in range of 2 degree");
		}
		
	}

}
