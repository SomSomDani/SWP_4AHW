package sample;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.application.Application;

public class API_Stock extends Application{
    static Scanner reader = new Scanner(System.in);
    static ArrayList<Double> closeValue = new ArrayList<>();
    static ArrayList<Double> movingAverage = new ArrayList<>();
    static ArrayList<LocalDate> data = new ArrayList<>();

    static ArrayList<Double> avgDB = new ArrayList<>();
    static ArrayList<Double> closeDB = new ArrayList<>();
    static ArrayList<String> dateDB = new ArrayList<>();

    static String url, Stock;
    static int chosenDays;
    static double min, max;

    public static void main (String args[]) throws IOException, JSONException {
        API_Stock stock = new API_Stock();
        stock.inputUser();
        stock.readURL();
        stock.getValue(url);
        stock.connect();
        stock.createNewTable();
        stock.insert();
        stock.Average();
        stock.insertAVG();
        stock.MinAndMax();
        stock.selectAll();
        Application.launch(args);
    }
    static void inputUser() {
        System.out.println("Stock (in the US)(TSLA,IBM,AMZN,AAPL,...): ");
        Stock = reader.next();
        System.out.println("How many days should the graphic use to draw the chart:");
        chosenDays = reader.nextInt();
    }
    static void readURL() {
        url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="+ Stock + "&outputsize=full&apikey=ZF7R0A6T754HDZGA"; //alphavantage-schüssel einfügen
    }
    static void getValue(String URL) throws JSONException, IOException {
        JSONObject json = new JSONObject(IOUtils.toString(new URL(url), Charset.forName("UTF-8")));
        json = json.getJSONObject("Time Series (Daily)");
        for(int i = 0; i < chosenDays/*json.names().length()*/; i++) {
            data.add(LocalDate.parse((CharSequence) json.names().get(i)));
            closeValue.add(json.getJSONObject(LocalDate.parse((CharSequence) json.names().get(i)).toString()).getDouble("4. close"));
        }

    }

    public static void connect() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/api?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
            conn = DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            System.out.println("Connection to MySQL has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    private Connection connection() {
        String url = "jdbc:mysql://localhost:3306/api?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC"; //Pfad einfügen
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url,"root", "Destiny@hi!.com");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    public static void createNewTable() {
        String url = "jdbc:mysql://localhost:3306/api?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC"; //Pfad einfügen
        String drop = "Drop Table if exists " + Stock + ";";
        String sql = "CREATE table if not exists "+ Stock +" (\n"
                + "datum Date primary key unique," + "close double);";
        String dropavg = "drop table if exists " + Stock +"avg ;";
        String avgsql = "CREATE TABLE IF NOT EXISTS " + Stock + "avg (\n"
                + "datum Date primary key unique," + "gleitenderDurchschnitt double)";
        try{
            Connection conn = DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            Statement stmt = conn.createStatement();
            stmt.execute(drop);
            stmt.execute(sql);
            stmt.execute(dropavg);
            stmt.execute(avgsql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void insert()
    {
        String sql = "INSERT INTO " + Stock + "(datum, close) VALUES('?', ?);";
        try {
            Connection conn = this.connection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for (int i = 1; i < closeValue.size(); i++) {
                sql = "INSERT INTO " + Stock + "(datum, close) VALUES(\""+data.get(i).toString()+"\","+ closeValue.get(i)+");";
                pstmt.execute(sql);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void Average() {
        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;
        try {
            String url = "jdbc:mysql://localhost:3306/api?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
            conn = DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            stmt = conn.createStatement();
            String sql;
            for(LocalDate avg : data) {
                sql = "Select avg(close) from " + Stock + " where (datum < \'" + avg.toString() + "\') and (datum >= \'" + avg.minusDays(200).toString() + "\') order by datum desc;";
                rs = stmt.executeQuery(sql);
                while (rs.next())
                {
                    for(int i = 0; i<rs.getMetaData().getColumnCount();i++)
                    {
                        movingAverage.add(rs.getDouble(i+1));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void insertAVG() {
        String sqlAVG = "INSERT INTO "+ Stock +"avg (datum, gleitenderDurchschnitt) VALUES(?, ?)";
        try{
            Connection conn = this.connection();
            PreparedStatement pstmt = conn.prepareStatement(sqlAVG);
            for (int i = 0; i < movingAverage.size(); i++) {
                sqlAVG = "INSERT INTO "+ Stock +"avg (datum, gleitenderDurchschnitt) VALUES(\""+data.get(i).toString()+"\","+ movingAverage.get(i)+");";
                pstmt.execute(sqlAVG);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void MinAndMax()
    {
        String sqlmax = "select max(close) from "+ Stock + ";";
        String sqlmin = "select min(close) from "+ Stock + ";";
        try
        {
            Connection conn = this.connection();
            Statement stmt = conn.createStatement();
            ResultSet rsmax = stmt.executeQuery(sqlmax);
            while(rsmax.next())
            {
                max = rsmax.getDouble(1);
            }
            ResultSet rsmin = stmt.executeQuery(sqlmin);
            while (rsmin.next())
            {
                min = rsmin.getDouble(1);
            }

        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    public void selectAll() {
        String sql = "SELECT * FROM "+ Stock +" order by datum;";
        String sqlAVG = "SELECT * FROM "+ Stock +"AVG order by datum;";
        try {
            Connection conn = this.connection();
            Statement stmt = conn.createStatement();
            Statement stmtAVG  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSet rsAVG = stmtAVG.executeQuery(sqlAVG);

            System.out.println("Datum               Close Werte             Durchschnitt");
            while (rs.next() && rsAVG.next()) {
                System.out.println(
                        rs.getString("datum")  + "\t \t \t \t" +
                                rs.getDouble("close") + "\t \t \t \t" +
                                rsAVG.getDouble("gleitenderDurchschnitt")
                );
                Double avgTemp = rsAVG.getDouble("gleitenderDurchschnitt");
                    dateDB.add(rsAVG.getString("datum"));
                    closeDB.add(rs.getDouble("close"));
                    avgDB.add(avgTemp == 0 ? null : avgTemp);
            }
            dateDB.sort(null);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void start(Stage primaryStage) {
        try {
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            yAxis.setAutoRanging(false);

            yAxis.setLowerBound(min - (min * 0.1));         // Minimum Value
            yAxis.setUpperBound(max + (max * 0.1));         // Maximum Value

            xAxis.setLabel("date");
            yAxis.setLabel("close-value");
            final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
            lineChart.setTitle("stock-price");
            XYChart.Series<String, Number> tatsaechlich = new XYChart.Series();
            tatsaechlich.setName("close-value");
            for (int i = (closeValue.size() == 10) ? closeValue.size()-10 : closeValue.size() - 11; i < closeValue.size() -1; i++) {
                tatsaechlich.getData().add(new XYChart.Data(dateDB.get(i), closeDB.get(i)));
            }
            /*for (int i = 0; i< closeWerte.size() - 1; i++)
            {
                tatsaechlich.getData().add(new XYChart.Data(dateDB.get(i), closeDB.get(i)));
            }*/
            XYChart.Series<String, Number> durchschnitt = new XYChart.Series();
            durchschnitt.setName("moving average");
            for (int i = (movingAverage.size() == 10) ? movingAverage.size()-10 : movingAverage.size() - 11; i < movingAverage.size()-1; i++) {
                durchschnitt.getData().add(new XYChart.Data(dateDB.get(i), avgDB.get(i)));
            }
            /*for (int i = 1; i< gleitenderDurchschnitt.size() - 1; i++)
            {
                durchschnitt.getData().add(new XYChart.Data(dateDB.get(i), avgDB.get(i)));
            }*/
            if(closeValue.get(closeValue.size()-1) > movingAverage.get(movingAverage.size()-1))
            {
                lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color:transparent;");
                lineChart.setStyle("-fx-background-color:#0ef898;");
            }
            else
            {
                lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color:transparent;");
                lineChart.setStyle("-fx-background-color:#fa6f50;");
            }
            Scene scene = new Scene(lineChart, 1000, 600);
            lineChart.getData().add(tatsaechlich);
            lineChart.getData().add(durchschnitt);

            lineChart.setCreateSymbols(false);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
