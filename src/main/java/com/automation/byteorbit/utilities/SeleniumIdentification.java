/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automation.byteorbit.utilities;


import org.openqa.selenium.By;

/**
 *
 * @author Zain
 */
public class SeleniumIdentification {
    public By findElementByXpath(String xpath){
        return By.xpath(xpath);
    }
    
    public By findElementByClassName(String className){
        return By.className(className);
    }
    
    public By findElementByID(String id){
        return By.id(id);
    }
    
    public By findElementByCssSelector(String cssSelector){
        return By.cssSelector(cssSelector);
    }
    
    public By findElementByName(String name){
        return By.name(name);
    }
}
