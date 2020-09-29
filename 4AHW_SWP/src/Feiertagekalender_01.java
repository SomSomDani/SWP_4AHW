import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Feiertagekalender_01 {
	
	private static int year;
	private static List<LocalDate> feiertage;
	private static LocalDate today;
	public static LocalDate getRecordFromLine(String line, int year) {
        List<LocalDate> values = new ArrayList<LocalDate>();
        int month, day;
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter("#");
            while (rowScanner.hasNext()) {
                month = rowScanner.nextInt();
                day = rowScanner.nextInt();

                values.add(LocalDate.of(year,month,day));
            }
        }
        return values;
    }
	public static void main(String[] args) throws IOException
	{
		BufferedReader stdin =
			      new BufferedReader ( new InputStreamReader ( System.in ) );
				// Berechnung der Ostersonntage
			    System.out.print("Bitte Jahr eingeben: ");
			    String inData = stdin.readLine();
			    int jahr = Integer.parseInt ( inData );
			    
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
			    /*System.out.print("\nIm Jahr " + jahr + " ist der Ostersonntag am " + tagOs);
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
			   
			    do {
		            try (Scanner scanner = new Scanner(new File("C:\\Users\\Platzhalter\\Desktop\\Backup Externe Festplatte\\Daniel SWP\\4AHW_SWP\\Feiertag.csv"));) {
		                while (scanner.hasNextLine()) {
		                    feiertage.add(getRecordFromLine(scanner.nextLine(), year));
		                }
		            } catch (FileNotFoundException e) {
		               e.printStackTrace();
		            }
		            year++;
		        }while(year < today.getYear() + 10);
			  }
	
		//LocalDateTime local = LocalDateTime.parse("2020-01-01T12:39:10");
		//DayOfWeek dayofweek = local.getDayOfWeek();
		//System.out.println("Day of Week: " + dayofweek);
	}