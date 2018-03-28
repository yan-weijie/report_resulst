package com.sun.demo;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class getApiStatus {
	private WebDriver driver;
	private String result_name;
	private String result_path;
	private String success_rate;
	private String result_time;
	
	@BeforeClass
	public void setUp() {
		driver = new HtmlUnitDriver();
		String path = System.getProperty("user.dir");
		File results = new File(path);
    	File newFile = null;
    	File[] fileList = results.listFiles();
    	for(File f: fileList) {
    		if (f.getName().contains("test-results.html")) {
    			// 生成测试结果文件对象
    			File oldFile = new File(path + "\\" + f.getName());
    	    	
    	    	// 生成一个新名字的测试结果文件对象
    			result_time = RandomStr.randomStr();
    	    	result_name = f.getName().replace(".html", "");
    	    	result_path = path  + "\\" + result_name + result_time + ".html";
    	    	newFile = new File(result_path);

    	    	// 修改测试结果的文件名
    	    	oldFile.renameTo(newFile);
    	    	System.out.println("测试报告： " + result_path);
    		}
    	}
		String filePath = "file://" + newFile;
		driver.get(filePath);
		driver.manage().window().maximize();
	}
	
	/**
	 * 分析报告结果成功率并调用微信接口发送失败结果
	 */
	@Test (priority = 1)
	public void parserHtml() {
		
		List<WebElement> username = driver.findElements(By.xpath("//*[@class='details']/tbody/tr/td[2]"));
		System.out.println("接口测试百分比结果：" + username.get(0).getText());
		success_rate = username.get(0).getText();
		
		String message = "报告名称: " + result_name + "\\n"
				 + "测试百分比结果: " +success_rate + "\\n"
				 + "生成时间: "+result_time + "\\n"
				 +"报告链接: "+ getHref();
		
		if (!username.get(0).getText().equals("100.00%")) {
			System.err.println(username.get(0).getText());
			//推送报告信息到微信
			WeChatReminder sendMsg = new WeChatReminder();
			sendMsg.sendWeChatNewsMsg("有接口出错，请检查！", message, result_name + result_time);
			
			Assert.assertEquals(false, true, "有接口出错，请检查后再试！");
		}
	}
	
	/**
	 * 将测试结果数据保存到数据库
	 */
	@Test (priority = 2)
	public void mapAdd() {
		Map<String, String> map = new HashMap<String, String>();
		ConnSql conn = new ConnSql();
		conn.setUp();
		map.put("result_name", result_name);
		map.put("success_rate", success_rate);
		map.put("result_time", result_time);
		map.put("project_name", result_name.split("-")[1]);
		map.put("result_href", getHref());
		map.put("product_name", result_name.split("-")[0]);
		conn.insertData(map);
		conn.tearDown();
	}
	
	public String getHref() {
		return "ftp://127.0.0.1/" + RandomStr.random().split(" ")[0] + "/" + result_name.split("-")[1] + "/" + result_name + result_time + ".html";
	}
	
	@AfterClass
	public void tearDown() {
		driver.quit();
	}
}
