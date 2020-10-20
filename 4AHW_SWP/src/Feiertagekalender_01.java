// Daten + Listen + Scanner
import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


// JavaFx + Scenes
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

// Datenbanken
import java.sql.*;

public class Feiertagekalender_01 extends Application 
{
	static int rHmonday=0;
	static int rHtuesday=0;
	static int rHwednesday=0;
	static int rHthursday= 0;
	static int rHfriday=0;
	static int rHsaturday = 0;
	static int rHsunday=0;
	
	static int dynmonday = 0;
	static int dynthursday= 0;
	static int dynsunday =0;
	
	static int monday = 0;
	static int tuesday = 0;
	static int wednesday = 0;
	static int thursday = 0;
	static int friday = 0;
	static int saturday = 0;
	static int sunday =0;
	
	static List<LocalDate> mondays = new ArrayList<>();
	static List<LocalDate> tuesdays = new ArrayList<>();
	static List<LocalDate> wednesdays = new ArrayList<>();
	static List<LocalDate> thursdays = new ArrayList<>();
	static List<LocalDate> fridays = new ArrayList<>();
	static List<LocalDate> saturdays = new ArrayList<>();
	static List<LocalDate> sundays = new ArrayList<>();
	
	static List<LocalDate> rHmondays = new ArrayList<>();
	static List<LocalDate> rHtuesdays = new ArrayList<>();
	static List<LocalDate> rHwednesdays = new ArrayList<>();
	static List<LocalDate> rHthursdays = new ArrayList<>();
	static List<LocalDate> rHfridays = new ArrayList<>();
	static List<LocalDate> rHsaturdays = new ArrayList<>();
	static List<LocalDate> rHsundays = new ArrayList<>();
	
	static Scanner scanner = new Scanner(System.in);
	
    static int startyear;
    static int endyear;
    
	public static void main(String[] args) throws IOException
	{
		System.out.println("Bitte das Startjahr eingeben: ");
		startyear = scanner.nextInt();
		System.out.println("Bitte das Endjahr eingeben: ");
		endyear = scanner.nextInt();
				@SuppressWarnings("resource")
				// Listen Feiertagen
				List<LocalDate> dynamicHolidays = new ArrayList<>();   
			    ArrayList<LocalDate> holidays = new ArrayList<>();
			    
			    // dynamische Feiertage berechnen
			    for (int i = startyear; i <= startyear + endyear - startyear; i++)
			    {
			    	int a = startyear % 19;
			    	int b = startyear % 4;
				    int c = startyear % 7;
				    
				    int m = (8 * (startyear / 100) + 13) / 25 - 2;
				    int s = startyear / 100 - startyear / 400 - 2;
				    m = (15 + s - m) % 30;
				    int n = (6 + s) % 7;
				    
				    int d = (m + 19 * a) % 30;
				    
				    if ( d == 29 )
				      d = 28;
				    else if (d == 28 && a >= 11)
				      d = 27;
				      
				    int e = (2 * b + 4 * c + 6 * d + n) % 7;
				    
				    int tagOs = 21 + d + e + 1;
				    int tagOm = 21 + d + e + 2;
				    int tagCH = 21 + d + e + 41;
				    int tagPfSo = 21 + d + e + 51;
				    int tagPfMo = 21 + d + e + 52;
				    int tagFl = 21 + d + e + 62;
				    
				    int monatOstern = 0;
				    int monatCH = 0;
				    int monatPfingstenSonntag = 0;
				    int monatPfingstenMontag = 0;
				    int monatFronleichnam = 0;
				    
				    // Berechnung der Ostersonntage
				    if (tagOs > 31)
				    {
				      tagOs = tagOs % 31;
				      monatOstern = 5;
				      dynamicHolidays.add(LocalDate.of(i,monatOstern,tagOs));
				    }
				    if (tagOs <= 31)
				    {  monatOstern = 4;
				    	dynamicHolidays.add(LocalDate.of(i,monatOstern,tagOs));
				    }
				    // Berechnung der Ostermontage
				    if (tagOm >31)
				    {
				    	tagOm = tagOm %31;
				    	monatOstern=5;
				    	dynamicHolidays.add(LocalDate.of(i,monatOstern,tagOm));
				    }
				    if (tagOm <= 31)
				    {
				    	monatOstern=4;
				    	dynamicHolidays.add(LocalDate.of(i,monatOstern,tagOm));
				    }
				    // Berechnung der Christi Himmelfahrten
				    if (tagCH >31)
				    {
				    	tagCH = tagCH %31;
				    	monatCH=6;
				    	dynamicHolidays.add(LocalDate.of(i,monatCH,tagCH));
				    }
				    if (tagCH <= 31)
				    {
				    	monatCH=5;
				    	dynamicHolidays.add(LocalDate.of(i,monatCH,tagCH));
				    }
				    // Berechnung von Pfingstsonntag
				    if (tagPfSo > 31)
				    {
				    	tagPfSo = tagPfSo % 31;
				    	if (tagPfSo == 0)
				    	{
				    		tagPfSo += 31;
				    	}
				    	monatPfingstenSonntag=5;
				    	dynamicHolidays.add(LocalDate.of(i,monatPfingstenSonntag,tagPfSo));
				    }
				    if (tagPfSo <= 31)
				    {
				    	monatPfingstenSonntag=5;
				    	dynamicHolidays.add(LocalDate.of(i,monatPfingstenSonntag,tagPfSo));
				    }
				    // Berechnung von Pfingstmontag
				    if (tagPfMo > 31)
				    {
				    	tagPfMo = tagPfMo % 31;
				    	monatPfingstenMontag=7;
				    	dynamicHolidays.add(LocalDate.of(i,monatPfingstenMontag,tagPfMo));
				    }
				    if(tagPfMo <= 31)
				    {
				    	monatPfingstenMontag=6;
				    	dynamicHolidays.add(LocalDate.of(i,monatPfingstenMontag,tagPfMo));
				    }
				    // Berechnung von Fromleichnam
				    if(tagFl > 31)
				    {
				    	tagFl = tagFl % 31;
				    	monatFronleichnam = 7;
				    	dynamicHolidays.add(LocalDate.of(i,monatFronleichnam,tagFl));
				    }
				    if(tagFl <= 31)
				    {
				    	monatFronleichnam = 6;
				    	dynamicHolidays.add(LocalDate.of(i,monatFronleichnam,tagFl));
				    }
			    }
			    
			    // Feiertage generieren
			    holidaysGenerate(holidays, startyear, endyear);
			    
			    // fixen Feiertage
		        for (int i = 0; i < holidays.size(); i++) {

		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.MONDAY)) {
		                mondays.add(holidays.get(i));
		                rHmondays.add(holidays.get(i));
		                rHmonday++;
		            }
		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
		            	tuesdays.add(holidays.get(i));
		            	rHtuesdays.add(holidays.get(i));
		                rHtuesday++;
		            }
		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
		            	wednesdays.add(holidays.get(i));
		            	rHwednesdays.add(holidays.get(i));
		            	rHwednesday++;
		            }
		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
		            	thursdays.add(holidays.get(i));
		            	rHthursdays.add(holidays.get(i));
		                rHthursday++;
		            }
		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
		            	fridays.add(holidays.get(i));
		            	rHfridays.add(holidays.get(i));
		                rHfriday++;
		            }
		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
		            	saturdays.add(holidays.get(i));
		            	rHsaturdays.add(holidays.get(i));
		                rHsaturday++;
		            }
		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
		            	sundays.add(holidays.get(i));
		            	rHsundays.add(holidays.get(i));
		                rHsunday++;
		            }
		        }
			    for (int i = 0; i < dynamicHolidays.size(); i++) {

		            if (dynamicHolidays.get(i).getDayOfWeek().equals(DayOfWeek.MONDAY)) {
		                mondays.add(dynamicHolidays.get(i));
		                dynmonday++;
		            }
		            if (dynamicHolidays.get(i).getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
		            	thursdays.add(dynamicHolidays.get(i));
		                dynthursday++;
		            }
		            if (dynamicHolidays.get(i).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
		            	sundays.add(dynamicHolidays.get(i));
		                dynsunday++;
		            }
		        }
			    monday = rHmonday +dynmonday;
			    tuesday = rHtuesday;
			    wednesday = rHwednesday;
			    thursday = rHthursday + dynthursday;
			    friday= rHfriday;
			    saturday= rHsaturday;
			    sunday = rHsunday + dynsunday;
			    
			    // Feiertage ausgeben
		        holidaysOutput(monday, tuesday, wednesday, thursday, friday, saturday, sunday, mondays, tuesdays,
		        				wednesdays, thursdays, fridays, saturdays, sundays);
		        launch(args);
		        System.out.println("Do you want to save your input [y,n]");
		        if(scanner.next().equals("y"))
		        {
		        	DatabaseInput();
		        }
		        System.out.println("Do you want to emit your database [y,n]");
		        if(scanner.next().equals("n"))
		        {
		        	DatabaseOutput();
		        }
	}
	public static void holidaysGenerate(List<LocalDate> holidays, int startyear, int endyear) {
        for (int i = startyear; i <= startyear + endyear - startyear; i++) {
        	holidays.add(LocalDate.of(i, 1, 1));
        	holidays.add(LocalDate.of(i, 1, 6));
        	holidays.add(LocalDate.of(i, 5, 1));
        	holidays.add(LocalDate.of(i, 8, 15));
        	holidays.add(LocalDate.of(i, 10, 26));
        	holidays.add(LocalDate.of(i, 11, 1));
        	holidays.add(LocalDate.of(i, 12, 8));
        	holidays.add(LocalDate.of(i, 12, 25));
        	holidays.add(LocalDate.of(i, 12, 26));

        	
        }
    }
	
	 public static void holidaysOutput(int mo, int di, int mi, int don, int fr, int sa, int so, List<LocalDate> mondays,
             List<LocalDate> tuesdays, List<LocalDate> wednesdays, List<LocalDate> thursdays,
             List<LocalDate> fridays, List<LocalDate> saturdays, List<LocalDate> sundays) {
				System.out.println("Mondays: " + mo + " " + mondays);
				System.out.println("Tuesdays: " + di + " " + tuesdays);
				System.out.println("Wednesdays: " + mi + " " + wednesdays);
				System.out.println("Thursdays: " + don + " " + thursdays);
				System.out.println("Fridays: " + fr + " " + fridays);
				System.out.println("Saturdays: " + sa + " " + saturdays);
				System.out.println("Sundays: " + so + " " + sundays);
				
				System.out.println("\n");
				
				System.out.println("Mondays: " + rHmonday + " " + rHmondays);
				System.out.println("Tuesdays: " + rHtuesday + " " + rHtuesdays );
				System.out.println("Wednesdays: " + rHwednesday + " " + rHwednesdays);
				System.out.println("Thursdays: " + rHthursday + " " + rHthursdays);
				System.out.println("Fridays: " + rHfriday + " " + rHfridays);
				System.out.println("Saturdays: " + rHsaturday + " " + rHsaturdays);
				System.out.println("Sundays: " + rHsunday + " " + rHsundays);
	 }
	 
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	 public void start(Stage primaryStage) throws Exception
	 {
		 try
		 {
			 	final CategoryAxis xAxis = new CategoryAxis();
			 	final NumberAxis yAxis = new NumberAxis();
			 	final BarChart<String,Number > barChart = new BarChart<String,Number>(xAxis,yAxis);
			 
			 	barChart.setTitle("Holidays in the year "+startyear+" until "+ endyear);
			 	xAxis.setLabel("weekdays");
			 	yAxis.setLabel("number of holidays");
			 
			 	XYChart.Series series1 = new XYChart.Series();
		        series1.setName("with dynamic holidays");
		        series1.getData().add(new XYChart.Data("Mondays", monday));
		        series1.getData().add(new XYChart.Data("Tuesdays", tuesday));
		        series1.getData().add(new XYChart.Data("Wednesdays", wednesday));
		        series1.getData().add(new XYChart.Data("Thursdays", thursday));
		        series1.getData().add(new XYChart.Data("Fridays", friday));
		        series1.getData().add(new XYChart.Data("Saturdays", saturday));
		        series1.getData().add(new XYChart.Data("Sundays", sunday));

		        
		         XYChart.Series series2 = new XYChart.Series();
		         series2.setName("only non-dynamic holidays");
		         series2.getData().add(new XYChart.Data("Mondays",rHmonday));
		         series2.getData().add(new XYChart.Data("Tuesdays",rHtuesday));
		         series2.getData().add(new XYChart.Data("Wednesdays",rHwednesday));
		         series2.getData().add(new XYChart.Data("Thursdays",rHthursday));
		         series2.getData().add(new XYChart.Data("Fridays",rHfriday));
		         series2.getData().add(new XYChart.Data("Saturdays",rHsaturday));
		         series2.getData().add(new XYChart.Data("Sundays",rHsunday));
		         	        
		        barChart.getData().addAll(series1,series2);
		        Scene scene = new Scene(barChart, 640, 480);
				primaryStage.setScene(scene);
				primaryStage.show();
			} 
		 	catch(Exception e) 
		 	{
				e.printStackTrace();
			}
		 }
	 private static void DatabaseInput()
	 {
		 final String hostname ="localhost";
		 @SuppressWarnings("unused")
		final String port = "3306";
		 final String dbname ="Feiertage";
		 final String user = "java";
		 final String password = "MySQL Root-Password";
		 
		 Connection conn;
		 
		 
		 try
		 {
			 System.out.println("* loading Driver");
			 Class.forName("com.mysql.jdbc.Driver");
		 }
		 catch(Exception e)
		 {
			 System.out.println("Unable to load Driver");
			 e.printStackTrace();
		 }
		 
		 try
		 {
			 System.out.println("* building connection");
			 conn = DriverManager.getConnection("jdbc:mysql://"+hostname+"/"+dbname+"?user="+user+"&password="+password+"&serverTimezone=UTC");
			 Statement myStat = conn.createStatement();
			 Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			 String sql = "INSERT INTO Kalender values(" + "'"+timestamp+"',"+monday+","+tuesday+","+wednesday+","+thursday+","+friday+","+saturday+","+sunday+","+startyear+","+endyear+")";
			 myStat.execute(sql);
			 
			 System.out.println("* ending database connection");
			 conn.close();
		 }
		 catch(SQLException sqle)
		 {
			 System.out.println("SQLException: " + sqle.getMessage());
			 System.out.println("SQLState: "+ sqle.getSQLState());
			 System.out.println("VendorError: " + sqle.getErrorCode());
			 sqle.printStackTrace();
		 }
	 }
	 private static void DatabaseOutput()
	 {
		 final String hostname ="localhost";
		 @SuppressWarnings("unused")
		final String port = "3306";
		 final String dbname ="Feiertage";
		 final String user = "java";
		 final String password = "MySQL Root-Password";
		 
		 Connection conn;
		 
		 try 
		 {
			 System.out.println("* loading driver");
			 Class.forName("com.mysql.jdbc.Driver");
		 }
		 catch(Exception e)
		 {
			 System.out.println("Unable to load Driver");
			 e.printStackTrace();
		 }
		 try {
	            System.out.println("* Verbindung aufbauen");
	            conn = DriverManager.getConnection("jdbc:mysql://"+hostname+"/"+dbname+"?user="+user+"&password="+password+"&serverTimezone=UTC");
	            Statement myStat = conn.createStatement();
	            ResultSet reSe=myStat.executeQuery("Select * from kalender");
	            System.out.println("Zeit                                 Montag      Dienstag        Mittwoch        Donnerstag      Freitag     Samstag" +
	                    "       Sonntag         Startjahr       Endjahr");
	            while(reSe.next()){
	                String zeit = reSe.getString("Datum");
	                String Montag = reSe.getString("Montag");
	                String Dienstag = reSe.getString("Dienstag");
	                String Mittwoch = reSe.getString("Mittwoch");
	                String Donnerstag = reSe.getString("Donnerstag");
	                String Freitag = reSe.getString("Freitag");
	                String Samstag = reSe.getString("Samstag");
	                String Sonntag = reSe.getString("Sonntag");
	                String startjahr =reSe.getString("Startjahr");
	                String endjahr =reSe.getString("Endjahr");


	                System.out.printf("%1s",zeit);
	                System.out.printf("%20s", Montag);
	                System.out.printf("%11s", Dienstag);
	                System.out.printf("%16s", Mittwoch);
	                System.out.printf("%17s", Donnerstag);
	                System.out.printf("%15s", Freitag);
	                System.out.printf("%12s", Samstag);
	                System.out.printf("%14s", Sonntag);
	                System.out.printf("%19s", startjahr);
	                System.out.printf("%16s", endjahr);
	                System.out.println();
	            }

	            System.out.println("* Datenbank-Verbindung beenden");
	            conn.close();
	        }
	        catch (SQLException sqle) {
	            System.out.println("SQLException: " + sqle.getMessage());
	            System.out.println("SQLState: " + sqle.getSQLState());
	            System.out.println("VendorError: " + sqle.getErrorCode());
	            sqle.printStackTrace();
	        }

	    
	 }
	 
}