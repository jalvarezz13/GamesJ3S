package edu.uclm.esi.tys2122.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.uclm.esi.tys2122.dao.UserRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestCuentas {
	@Autowired
	private static UserRepository userRepo;

	private static WebDriver driverPepe;
	private static WebDriver driverAnonimo;

	@BeforeAll
	public static void setUp() throws Exception {
		String userHome = System.getProperty("user.home");
		userHome = userHome.replace('\\', '/');
		if (!userHome.endsWith("/"))
			userHome = userHome + "/";

		System.setProperty("webdriver.chrome.driver", userHome + "chromedriver.exe");

		driverPepe = crearDriver(0, 0);
		driverAnonimo = crearDriver(1001, 0);
	}

	private static WebDriver crearDriver(int x, int y) {
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.manage().window().setSize(new Dimension(1000, 1000));
		driver.manage().window().setPosition(new Point(x, y));
		driver.get("http://localhost");
		return driver;
	}

	@AfterAll
	public static void tearDown() {
		driverPepe.quit();
		driverAnonimo.quit();
		
		userRepo.deleteAll();
	}

	@Test
	@Order(1)
	public void testRegistrar() {
		// Forma larga de hacerlo
		WebElement linkCrearCuenta = driverPepe.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[3]/div/a"));
		linkCrearCuenta.click();

		// Otra forma
		driverPepe.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/button")).click();
		driverPepe.findElement(By.xpath("/html/body/div/div[2]/div/oj-navigation-list/div/div/ul/li[1]/a")).click();
		driverPepe.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[1]/div[3]/button")).click();

		pausa(500);

		WebElement etiqueta = driverPepe.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/h1"));
		assertTrue(etiqueta.getText().contains("Juegos disponibles"));
	}

	@Test
	@Order(2)
	public void testUnirseAPartida() {
		driverPepe.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[1]/button")).click(); // Inicia partida TicTacToe
		
		driverAnonimo.findElement(By.xpath("/html/body/div/div[2]/div/oj-navigation-list/div/div/ul/li[5]/a")).click(); // Va a juegos directo
		driverAnonimo.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div/div[1]/button")).click(); // Inicia partida TicTacToe
	}

	private void pausa(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
