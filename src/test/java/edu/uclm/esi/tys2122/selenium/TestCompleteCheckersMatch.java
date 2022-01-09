package edu.uclm.esi.tys2122.selenium;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
		WebElement msg;
		goGames();

		// start CheckersMatch
		driverWhite.findElement(By.xpath("/html/body/div/oj-module/div[1]/div/div/div[3]/div/button")).click();
		pause(500);
		// scroll to bottom page
		driverWhite.findElement(By.tagName("html")).sendKeys(Keys.END);
			
		
		driverBlack.findElement(By.xpath("/html/body/div/oj-module/div[1]/div/div/div[3]/div/button")).click();
		pause(500);
		// scroll to bottom page
		driverBlack.findElement(By.tagName("html")).sendKeys(Keys.END);


		
		// You can't move
		selectItem(driverWhite, "8 BLANCO");
		pause(500);
		msg = driverWhite.findElement(By.id("gameError"));
		pause(500);
		assertEquals(msg.getText(), "No se puede mover esta ficha");
		pause(3000);

		// Not your turn
		movePiece(driverBlack, "3 NEGRO", "rightUp");
		pause(500);
		msg = driverBlack.findElement(By.id("gameError"));
		pause(500);
		assertEquals(msg.getText(), "No es tu turno");
		pause(3000);

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

		movePiece(driverWhite, "7 BLANCO", "leftUp");

		movePiece(driverBlack, "3 NEGRO", "rightUp");

		movePiece(driverWhite, "1 BLANCO", "rightDown");

		movePiece(driverBlack, "5 NEGRO", "rightUp");

		movePiece(driverWhite, "1 BLANCO", "rightDown");

		movePiece(driverBlack, "8 NEGRO", "leftUp");

		movePiece(driverWhite, "6 BLANCO", "rightUp");

		movePiece(driverBlack, "11 NEGRO", "leftUp");

		movePiece(driverWhite, "8 BLANCO", "leftUp");

		movePiece(driverBlack, "4 NEGRO", "leftUp");

		movePiece(driverWhite, "12 BLANCO", "leftUp");

		movePiece(driverBlack, "4 NEGRO", "leftUp");

		movePiece(driverWhite, "12 BLANCO", "leftUp");

		movePiece(driverBlack, "1 NEGRO", "rightUp");

		movePiece(driverWhite, "1 BLANCO", "leftDown");

		movePiece(driverBlack, "1 NEGRO", "leftUp"); // doQueen

		movePiece(driverWhite, "1 BLANCO", "leftDown");

		movePiece(driverBlack, "3 NEGRO", "leftUp");

		movePiece(driverWhite, "9 BLANCO", "rightUp");

		movePiece(driverBlack, "11 NEGRO", "rightUp");

		movePiece(driverWhite, "8 BLANCO", "leftUp");

		movePiece(driverBlack, "11 NEGRO", "rightUp");

		movePiece(driverWhite, "8 BLANCO", "leftUp");

		movePiece(driverBlack, "9 NEGRO", "leftUp");

		movePiece(driverWhite, "6 BLANCO", "leftUp");

		movePiece(driverBlack, "9 NEGRO", "rightUp");

		movePiece(driverWhite, "6 BLANCO", "leftUp");

		movePiece(driverBlack, "9 NEGRO", "leftUp");

		movePiece(driverWhite, "6 BLANCO", "leftUp"); // doQueen

		movePiece(driverBlack, "10 NEGRO", "leftUp");

		movePiece(driverWhite, "6 BLANCO", "rightDown");

		movePiece(driverBlack, "10 NEGRO", "leftUp");

		movePiece(driverWhite, "6 BLANCO", "rightDown");

		movePiece(driverBlack, "12 NEGRO", "leftUp");

		movePiece(driverWhite, "8 BLANCO", "leftUp"); // doQueen

		movePiece(driverBlack, "11 NEGRO", "leftUp");

		movePiece(driverWhite, "5 BLANCO", "rightUp");

		movePiece(driverBlack, "9 NEGRO", "rightUp");

		movePiece(driverWhite, "12 BLANCO", "rightUp");

		movePiece(driverBlack, "10 NEGRO", "rightUp");

		movePiece(driverWhite, "6 BLANCO", "rightDown");

		movePiece(driverBlack, "1 NEGRO", "rightDown");

		movePiece(driverWhite, "11 BLANCO", "rightUp");
		
		// winner assert
		msg = driverWhite.findElement(By.id("winnerMsg"));
		pause(500);
		assertEquals(msg.getText(), "¡Has Ganado!");
		pause(500);
		driverWhite.findElement(By.id("WinnerEndGame")).click();
		
		// looser assert
		msg = driverBlack.findElement(By.id("looserMsg"));
		pause(500);
		assertEquals(msg.getText(), "¡Has Perdido!");
		pause(500);
		driverBlack.findElement(By.id("WinnerEndGame")).click();
	}

	private void selectItem(WebDriver driver, String pieceName) {
		Select driverList = new Select(driver.findElement(By.id("dropDown")));
		driverList.selectByVisibleText(pieceName);
	}

	private void movePiece(WebDriver driver, String pieceName, String movement) {
		selectItem(driver, pieceName);
		pause(500);
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].click()", driver.findElement(By.id(movement)));
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
