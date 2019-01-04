package com.sofi;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AuthenticateUser  {
    Properties prop =new Properties();

    public void approveUser(String token) throws IOException {
        FileInputStream fis = new FileInputStream("/Users/admin/chhaya/projects/MovieDB/src/main/resources/env.properties");
        prop.load(fis);
        System.setProperty("webdriver.chrome.driver",prop.getProperty("webDriverPath"));
        WebDriver driver = new ChromeDriver();
        //Go to  https://www.themoviedb.org/authenticate/{REQUEST_TOKEN}
        driver.get("https://www.themoviedb.org/authenticate/"+token);
        //Login as a valid user
        driver.findElement(By.cssSelector("div[class='media'] button[class*='login']")).click();
        driver.findElement(By.id("username")).sendKeys(prop.getProperty("userName"));
        driver.findElement(By.id("password")).sendKeys(prop.getProperty("password"));
        driver.findElement(By.cssSelector("div[class='flex'] input[class='k-button k-primary']")).submit();
        //Authentication Request
        driver.findElement(By.cssSelector("button[id='allow_authentication']")).click();
        driver.close();
    }
}
