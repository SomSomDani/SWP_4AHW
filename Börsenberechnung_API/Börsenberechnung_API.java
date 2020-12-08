import java.beans.Statement;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.*;
import org.json.JSONException;
import org.json.JSONObject;

// Key: ZF7R0A6T754HDZGA
public class Börsenberechnung_API {
	public static String path = System.getProperty("user.dir") + "\\db\\";
	public static String filename= "MainDB.db";
	static Scanner scanner = new Scanner(System.in);
	
	static StringBuilder urlJson = new StringBuilder();
	static String symbol;
	static String urlBasis = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol= ";
	static String key = "ZF7R0A6T754HDZGA";
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String newDate = "";
		JSONObject jsonObject;
		
		url();
		jsonObject=getAPIJSON();
		try
		{
			newDate = jsonObject.getJSONObject("Meta Data").get("3. Last Refreshed").toString();
		}
		catch(JSONException e)
		{
			System.err.println("Unknown company symbol \n");
			sleep(30);
		}
		jsonObject = jsonObject.getJSONObject("Time Series (Daily)");
		LocalDate date = LocalDate.parse(newDate);
		
		// Database
		Connection conn = connect();
		createNewTable(conn,symbol);
		double[] values = {4.0, 3.0, 2.0, 1.0};
		String[] keys={"1111-01-01","2222-02-02","3333-03-03","4444-04-04"};
		
		for(int i = 0; i<4; i++)
		{
			insertOrReplace(conn,keys[i], values[i], symbol);
		}
		
		try
		{
			double close = Double.parseDouble(jsonObject.getJSONObject(date.toString()).get("4. close").toString());
			System.out.printf("%s : %s%n", date.toString(), close);
			createNewTable(conn,symbol);
			insertOrReplace(conn, date.toString(), close, symbol);
		}
		catch(JSONException e)
		{
			System.err.printf("Error %d on %s%n", date.toString());
		}
	}
	
	private static void url()
	{
		System.out.println("Which stock should be analyzed?");
		System.out.println("Input: ");
		
		symbol = scanner.next().toUpperCase();
		urlJson.append(urlBasis);
		urlJson.append(symbol);
		urlJson.append("&apikey=");
		urlJson.append(key);
		
	}
	private static JSONObject getAPIJSON() throws JSONException
	{
		JSONObject jsonObject = new JSONObject();
		try 
		{
			 jsonObject = new JSONObject(IOUtils.toString(new URL(urlJson.toString()), Charset.forName("UTF-8")));
		}
		catch(IOException e)
		{
			System.out.println("IOException");
		}
		return jsonObject;
	}
	
	private static void sleep(long time)
	{
		try
		{
			TimeUnit.MILLISECONDS.sleep(time);
		}
		catch(InterruptedException interruptedException)
		{
			interruptedException.printStackTrace();
		}
	}
	//Database
	public static Connection connect()
	{
		 final String hostname ="localhost";
		 @SuppressWarnings("unused")
		 final String port = "3306";
		 final String dbname ="Feiertage";
		 final String user = "java";
		 final String password = "MySQL Root-Password";
		 
		Connection conn = null;
		try
		{
			String url="jdbc:mysql://"+hostname+"/"+dbname+"?user="+user+"&password="+password+"&serverTimezone=UTC" + path + filename;
			conn = DriverManager.getConnection(url);
			System.out.println("Connection to MySQL has been established");
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
		finally
		{
			try {
				if(conn != null)
				{
					conn.close();
				}
			}
			catch(SQLException ex)
			{
				System.out.println(ex.getMessage());
			}
		}
		return conn;
	}
	
	public static void close(Connection conn)
	{
		try
		{
			if(conn != null)
			{
				conn.close();
			}
		}
		catch(SQLException ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	
	public static void createNewTable(Connection conn, String symbol) throws Exception
	{
		String statement = "CREATE TABLE IF NOT EXISTS " +symbol + "(date char(10) PRIMARY KEY,value REAL)";
			
		try
		{
			Statement statements = (Statement) conn.createStatement();
			statements.execute(statement);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	public static void insertOrReplace(Connection conn,String key, double value, String symbol) throws Exception
	{
		String statement = "INSERT OR REPLACE INTO " + symbol + "VALUES ('" + key + "', " + value + ")";
		try
		{
			Statement statements = (Statement) conn.createStatement();
			statements.execute(statement);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
}

