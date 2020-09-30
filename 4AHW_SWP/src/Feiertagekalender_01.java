import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
public class Feiertagekalender_01 {
	
	
	public static void main(String[] args) throws IOException
	{
		BufferedReader stdin =
			      new BufferedReader ( new InputStreamReader ( System.in ) );
				// Berechnung der Ostersonntage
			    System.out.print("Bitte das Startjahr eingeben: ");
			    //System.out.print("Bitte Anzahl der Jahre eingeben, wofür Feiertage berechnet werden sollten: ");
			    String inData = stdin.readLine();
			    int jahr = Integer.parseInt ( inData );
			    
			    int montag=0;
				int dienstag=0;
				int mittwoch=0;
				int donnerstag= 0;
				int freitag=0;
				int samstag = 0;
				int sonntag=0;
				
			    int a = jahr % 19;
			    int b = jahr % 4;
			    int c = jahr % 7;
			    int monatOstern = 0;
			    int monatCH = 0;
			    int monatPfingstenSonntag = 0;
			    int monatPfingstenMontag = 0;
			    int monatFronleichnam = 0;
			    
			    int m = (8 * (jahr / 100) + 13) / 25 - 2;
			    int s = jahr / 100 - jahr / 400 - 2;
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
			    
			    if (tagOs > 31)
			    {
			      tagOs = tagOs % 31;
			      monatOstern = 5;
			    }
			    if (tagOs <= 31)
			    {  monatOstern = 4; }
			    // Berechnung der Ostermontage
			    if (tagOm >31)
			    {
			    	tagOm = tagOm %31;
			    	monatOstern=5;
			    }
			    if (tagOm <= 31)
			    {
			    	monatOstern=4;
			    }
			    // Berechnung der Christi Himmelfahrten
			    if (tagCH >31)
			    {
			    	tagCH = tagCH %31;
			    	monatCH=6;
			    }
			    if (tagCH <= 31)
			    {
			    	monatCH=5;
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
			    }
			    if (tagPfSo <= 31)
			    {
			    	monatPfingstenSonntag=5;
			    }
			    // Berechnung von Pfingstmontag
			    if (tagPfMo > 31)
			    {
			    	tagPfMo = tagPfMo % 31;
			    	monatPfingstenMontag=7;
			    }
			    if(tagPfMo <= 31)
			    {
			    	monatPfingstenMontag=6;
			    }
			    // Berechnung von Fromleichnam
			    if(tagFl > 31)
			    {
			    	tagFl = tagFl % 31;
			    	monatFronleichnam = 7;
			    }
			    if(tagFl <= 31)
			    {
			    	monatFronleichnam = 6;
			    }
			    /*	System.out.print("\nIm Jahr " + jahr + " ist der Ostersonntag am " + tagOs);
			    	System.out.println("." + monatOstern + ".");
			    	System.out.print("\nIm Jahr "+ jahr + " ist der Ostermontag am " + tagOm);
			    	System.out.println("." + monatOstern + ".");
			    	System.out.print("\nIm Jahr "+ jahr + " ist Christi Himmelfahrt am " + tagCH);
			    	System.out.println("." + monatCH + ".");
			    	System.out.print("\nIm Jahr "+ jahr + " ist Pfingstsonntag am " + tagPfSo);
			    	System.out.println("." + monatPfingstenSonntag + ".");
			    	System.out.print("\nIm Jahr "+ jahr + " ist Pfingstmontag am " + tagPfMo);
			    	System.out.println("." + monatPfingstenMontag + ".");
			    	System.out.print("\nIm Jahr "+ jahr + " ist Fromleichnam am " + tagFl);
			    	System.out.println("." + monatFronleichnam + ".");*/
			   
			    ArrayList<LocalDate> freitage = new ArrayList<>();
			    do
			    {
			    	try(Scanner scanner = new Scanner(new File("C:\\Users\\Platzhalter\\Desktop\\Backup Externe Festplatte\\Daniel SWP\\4AHW_Algorithm\\src\\FreiTage.csv")))
			    	{
			    		while(scanner.hasNextLine())
			    		{
			    			freitage.add(getRecordFromLine(scanner.nextLine(),jahr));
			    		}
			    	}
			    	catch(FileNotFoundException e1)
			    	{
			    		e1.printStackTrace();
			    	}
			    	jahr ++;
			    }
			    while(jahr < jahr + 10);
			    
			    // Switch für die Tage hochzählen
			    for(int i = 0; i<freitage.size();i++)
				{
			    	LocalDate date = freitage.get(i);
			    	DayOfWeek dayS = date.getDayOfWeek();
					switch(dayS)
					{
					case MONDAY: montag= (montag + 1) + (jahr * 2);
					case TUESDAY: dienstag++;
					case WEDNESDAY: mittwoch++;
					case THURSDAY: donnerstag= (donnerstag + 1) + (jahr * 2);
					case FRIDAY: freitag++;
					case SATURDAY: samstag++;
					case SUNDAY: sonntag= (sonntag +1) + (jahr * 2);
					}
				}
			    System.out.println(montag);
			    System.out.println(dienstag);
			    System.out.println(mittwoch);
			    System.out.println(donnerstag);
			    System.out.println(freitag);
			  }
	
	
		//LocalDateTime local = LocalDateTime.parse("2020-01-01T12:39:10");
		//DayOfWeek dayofweek = local.getDayOfWeek();
		//System.out.println("Day of Week: " + dayofweek);
	public static LocalDate getRecordFromLine(String line, int year)
	{
		LocalDate values = LocalDate.MIN;
		int month, day;
		try(Scanner rowScanner = new Scanner(line))
		{
			rowScanner.useDelimiter("#");
			while(rowScanner.hasNextInt())
			{
				month=rowScanner.nextInt();
				day = rowScanner.nextInt();
				values = LocalDate.of(year,month,day);
			}
		}
		return values;
	}
}