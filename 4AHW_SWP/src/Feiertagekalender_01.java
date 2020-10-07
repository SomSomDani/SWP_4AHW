import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage; 

public class Feiertagekalender_01 extends Application 
{
	static int monday=0;
	static int tuesday=0;
	static int wednesday=0;
	static int thursday= 0;
	static int friday=0;
	static int saturday = 0;
	static int sunday=0;
	
	static List<LocalDate> mondays = new ArrayList<>();
	static List<LocalDate> tuesdays = new ArrayList<>();
	static List<LocalDate> wednesdays = new ArrayList<>();
	static List<LocalDate> thursdays = new ArrayList<>();
	static List<LocalDate> fridays = new ArrayList<>();
	static List<LocalDate> saturdays = new ArrayList<>();
	static List<LocalDate> sundays = new ArrayList<>();
	public static void main(String[] args) throws IOException
	{
				Scanner scanner = new Scanner(System.in);
			    System.out.println("Bitte das Startjahr eingeben: ");
			    int startyear;
			    startyear = scanner.nextInt();
			    System.out.println("Bitte das Endjahr eingeben: ");
			    int endyear;
			    endyear = scanner.nextInt();
			 
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
				    	monatPfingstenSonntag=6;
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
		                monday++;
		            }
		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.TUESDAY)) {
		            	tuesdays.add(holidays.get(i));
		                tuesday++;
		            }
		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) {
		            	wednesdays.add(holidays.get(i));
		            	wednesday++;
		            }
		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
		            	thursdays.add(holidays.get(i));
		                thursday++;
		            }
		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
		            	fridays.add(holidays.get(i));
		                friday++;
		            }
		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
		            	saturdays.add(holidays.get(i));
		                saturday++;
		            }
		            if (holidays.get(i).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
		            	sundays.add(holidays.get(i));
		                sunday++;
		            }
		        }
			    for (int i = 0; i < dynamicHolidays.size(); i++) {

		            if (dynamicHolidays.get(i).getDayOfWeek().equals(DayOfWeek.MONDAY)) {
		                mondays.add(dynamicHolidays.get(i));
		                monday++;
		            }
		            if (dynamicHolidays.get(i).getDayOfWeek().equals(DayOfWeek.THURSDAY)) {
		            	thursdays.add(dynamicHolidays.get(i));
		                thursday++;
		            }
		            if (dynamicHolidays.get(i).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
		            	sundays.add(dynamicHolidays.get(i));
		                sunday++;
		            }
		        }
			    // Feiertage ausgeben
		        holidaysOutput(monday, tuesday, wednesday, thursday, friday, saturday, sunday, mondays, tuesdays,
		        				wednesdays, thursdays, fridays, saturdays, sundays);
		        launch(args);
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
	
	 public static void holidaysOutput(int mo, int di, int mi, int don, int fr, int sa, int so, List<LocalDate> Montage,
             List<LocalDate> Dienstage, List<LocalDate> Mittwoche, List<LocalDate> Donnerstage,
             List<LocalDate> Freitage, List<LocalDate> Samstage, List<LocalDate> Sonntage) {
				System.out.println("Montage: " + mo + " " + Montage);
				System.out.println("Dienstage: " + di + " " + Dienstage);
				System.out.println("Mittwoche: " + mi + " " + Mittwoche);
				System.out.println("Donnerstage: " + don + " " + Donnerstage);
				System.out.println("Freitage: " + fr + " " + Freitage);
				System.out.println("Samstage: " + sa + " " + Samstage);
				System.out.println("Sonntage: " + so + " " + Sonntage);
	 }
	 
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	 public void start(Stage primaryStage) throws Exception
	 {
		 try
		 {
			 final CategoryAxis xAxis = new CategoryAxis();
			 final NumberAxis yAxis = new NumberAxis();
			 final BarChart<String,Number > barChart = new BarChart<String,Number>(xAxis,yAxis);
			 
			 barChart.setTitle("Feiertage in den Jahren 2021 bis 2031 ");
			 xAxis.setLabel("Wochentage");
			 yAxis.setLabel("Anzahl der Feiertage");
			 
			 	XYChart.Series series1 = new XYChart.Series();
		        series1.setName("2021-2031");
		        series1.getData().add(new XYChart.Data("Montage", monday));
		        series1.getData().add(new XYChart.Data("Tuesdays", tuesday));
		        series1.getData().add(new XYChart.Data("Wednesdays", wednesday));
		        series1.getData().add(new XYChart.Data("Thursdays", thursday));
		        series1.getData().add(new XYChart.Data("Fridays", friday));
		        series1.getData().add(new XYChart.Data("Saturdays", saturday));
		        series1.getData().add(new XYChart.Data("Sundays", sunday));

		        barChart.getData().addAll(series1);
		        Scene scene = new Scene(barChart, 640, 480);
				primaryStage.setScene(scene);
				primaryStage.show();
			} catch(Exception e) {
				e.printStackTrace();
			}
		 }
}