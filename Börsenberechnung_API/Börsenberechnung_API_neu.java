import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

//Primarykey muss date sein
public class Börsenberechnung_API_neu extends Application{

	//Key: ZF7R0A6T754HDZGA
	static final String hostname ="localhost";
	static final String port = "3306";
	static final String dbname ="Feiertage";
	static final String user = "java";
	static final String password = "MySQL Root-Passwort";

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override public void start(Stage stage) {
        stage.setTitle("Aktienkurs");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Tag");
        final LineChart<String,Number> lineChart =
                new LineChart<String, Number>(xAxis,yAxis);
        lineChart.setTitle("Aktienkurs "+ symbol.toUpperCase());
        XYChart.Series series = new XYChart.Series();
        series.setName("Close Werte");

        for (LocalDate i : PriceTreeMap.keySet()) {
            series.getData().addAll(new XYChart.Data(i.toString(), PriceTreeMap.get(i)));
        }

        Scene scene  = new Scene(lineChart,1300,800);
        lineChart.getData().add(series);

        stage.setScene(scene);
        stage.show();
    }
    //Treemap verwenden, da es bei der insertion automatisch die Dates sortiert einfügt
    static Map<LocalDate, Double> PriceTreeMap = new TreeMap<LocalDate, Double>();

    static int basis;
    static String symbol;
    static List<String> dates = new ArrayList<>();

    static JSONObject json;
    public static void main(String[] args) throws IOException, JSONException {
        @SuppressWarnings("resource")
		Scanner reader = new Scanner(System.in);
        System.out.println("From which company do you want to know the share price?[TSLA, AAPL, AMZN, ...]");
        symbol = reader.next();
        String URL = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="+symbol+"&outputsize=compact&apikey=ZF7R0A6T754HDZGA";
        JSONObject json = new JSONObject(IOUtils.toString(new URL(URL), Charset.forName("UTF-8")));
        json = json.getJSONObject("Time Series (Daily)");
        System.out.println("How many shares should the system use? [20,50,200]");
        basis = reader.nextInt();
	        for(int i = 0;i<basis;i++){
	            dates.add(json.names().get(i).toString());
	        }
        
        Collections.sort(dates);
        for(int i=0;i<dates.size();i++) {
            String temp = dates.get(i);
            PriceTreeMap.put(LocalDate.parse(temp), getWert(temp));
            System.out.println(temp);
        }
        System.out.println("Test");
        System.out.println(PriceTreeMap);
        Application.launch(args);
        CreateTable();
        DataBaseInput();


        System.out.println("Table Output?");
        if(reader.next().equals("yes")){
        	DataBaseOutput();
        }
        else {
            System.exit(0);
        }
    }
    private static double getWert (String key) throws JSONException {

        JSONObject jsonO = (JSONObject) json.get(key);
        String Wert = jsonO.getString("4. close");
        return Double.parseDouble(Wert);
    }

    private static void CreateTable(){
        Connection conn = null;

        try {
            System.out.println("* Treiber laden");
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception e) {
            System.err.println("Unable to load driver.");
            e.printStackTrace();
        }
        try {
            System.out.println("* Build up Connection ");
            conn = DriverManager.getConnection("jdbc:mysql://"+hostname+"/"+dbname+"?user="+user+"&password="+password+"&serverTimezone=UTC");
            Statement myStat = conn.createStatement();
            System.out.println("* create table aktie, if not exists");
            String sql = "CREATE TABLE if not exists "+symbol +
                    "(Date datetime, Value double)";
            myStat.executeUpdate(sql);

        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("VendorError: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
    }
    static Connection conn = null;
    private static void DataBaseInput(){
        try {
            conn = DriverManager.getConnection("jdbc:mysql://"+hostname+"/"+dbname+"?user="+user+"&password="+password+"&serverTimezone=UTC");
            Statement myStat = conn.createStatement();

            for (LocalDate i : PriceTreeMap.keySet()) {
                String sql = "INSERT INTO " + symbol +" values('"+i+"',"+PriceTreeMap.get(i)+")";
                myStat.execute(sql);
            }


        }
        catch (SQLException sqle) {
            System.out.println("SQLException: " + sqle.getMessage());
            System.out.println("SQLState: " + sqle.getSQLState());
            System.out.println("VendorError: " + sqle.getErrorCode());
            sqle.printStackTrace();
        }
    }
    private static void DataBaseOutput(){
    	final String hostname ="localhost";
    	@SuppressWarnings("unused")
		final String port = "3306";
    	final String dbname ="Feiertage";
    	final String user = "java";
    	final String password = "MySQL Root-Passwort";
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://"+hostname+"/"+dbname+"?user="+user+"&password="+password+"&serverTimezone=UTC");
            Statement myStat = conn.createStatement();
            ResultSet reSe=myStat.executeQuery("Select * from "+symbol);
            System.out.println("Datum                                Value");
            while(reSe.next()){
                String zeit = reSe.getString("Date");
                String Wert = reSe.getString("Value");



                System.out.printf("%1s",zeit);
                System.out.printf("%20s", Wert);

                System.out.println();
            }

            System.out.println("* Database Connection close:");
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