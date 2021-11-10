package edu.uclm.esi.tys2122.selenium;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
	private WebDriver driver;
	private WebDriver driverAnonimo;
	
	@Autowired
	private UserRepository userDao;
	
	@BeforeEach
	public void setUp() throws Exception {
		String userHome = System.getProperty("user.home");
		userHome = userHome.replace('\\', '/');
		if (!userHome.endsWith("/"))
			userHome = userHome + "/";
		
		System.setProperty("webdriver.chrome.driver", userHome + "chromedriver.exe");
		
		this.driver = crearDriver(0, 0);
		this.driverAnonimo = crearDriver(1000,0);
	}
	
	private WebDriver crearDriver(int x, int y) {
		WebDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.manage().window().setSize(new Dimension(1000, 1000));
		driver.manage().window().setPosition(new Point(x, y));
		driver.get("http://localhost");
		return driver;
	}
}
