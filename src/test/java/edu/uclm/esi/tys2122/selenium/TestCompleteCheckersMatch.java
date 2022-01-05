package edu.uclm.esi.tys2122.selenium;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("unused") // remember to delete this
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestCompleteCheckersMatch {
	private WebDriver driverWhite;
	private WebDriver driverBlack;

	@BeforeEach
	public void setUp() throws Exception {
		String userHome = System.getProperty("user.home");
		userHome = userHome.replace('\\', '/');
		if (!userHome.endsWith("/"))
			userHome = userHome + "/";

		System.setProperty("webdriver.chrome.driver", userHome + "chromedriver.exe");

		this.driverWhite = crearDriver(0, 0);
		this.driverBlack = crearDriver(960, 0);
	}

	private WebDriver crearDriver(int x, int y) {
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.manage().window().setSize(new Dimension(960, 1080));
		driver.manage().window().setPosition(new Point(x, y));
		driver.get("http://localhost");
		return driver;
	}

	@Test
	public void playMatch() {
		goGames();

		// start CheckersMatch
		driverWhite.findElement(By.xpath("/html/body/div/oj-module/div[1]/div/div/form/div[3]/div/button")).click();
		pause(500);
		driverBlack.findElement(By.xpath("/html/body/div/oj-module/div[1]/div/div/form/div[3]/div/button")).click();
		pause(500);

		// start Movements
		movePiece(driverWhite, "1 BLANCO", "rightUp");

		movePiece(driverBlack, "1 NEGRO", "rightUp");

		movePiece(driverWhite, "4 BLANCO", "leftUp");

		movePiece(driverBlack, "1 NEGRO", "leftUp");

		movePiece(driverWhite, "2 BLANCO", "rightUp");

		movePiece(driverBlack, "3 NEGRO", "rightUp");

		movePiece(driverWhite, "2 BLANCO", "leftUp");

		movePiece(driverBlack, "2 NEGRO", "leftUp");

		movePiece(driverWhite, "6 BLANCO", "rightUp");

		movePiece(driverBlack, "5 NEGRO", "rightUp");

		movePiece(driverWhite, "2 BLANCO", "rightUp");

		movePiece(driverBlack, "6 NEGRO", "rightUp");
		
		movePiece(driverWhite, "1 BLANCO", "rightUp");

		movePiece(driverBlack, "8 NEGRO", "leftUp");

		movePiece(driverWhite, "3 BLANCO", "rightUp");

		movePiece(driverBlack, "5 NEGRO", "leftUp");

		movePiece(driverWhite, "7 BLANCO", "rightUp");

		movePiece(driverBlack, "11 NEGRO", "rightUp");

		movePiece(driverWhite, "1 BLANCO", "leftUp"); // doQueen

		movePiece(driverBlack, "2 NEGRO", "rightUp");



	}

	private void movePiece(WebDriver driver, String pieceName, String movement) {
		Select driverList = new Select(driver.findElement(By.id("dropDown")));
		driverList.selectByVisibleText(pieceName);
		pause(500);
		driver.findElement(By.id(movement)).click();
		pause(1000);
	}

	private void goGames() {
		driverWhite.findElement(By.xpath("/html/body/div/div[2]/div/oj-navigation-list/div/div/ul/li[5]/a/span")).click();
		driverBlack.findElement(By.xpath("/html/body/div/div[2]/div/oj-navigation-list/div/div/ul/li[5]/a/span")).click();
		pause(500);
	}

	private void pause(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
