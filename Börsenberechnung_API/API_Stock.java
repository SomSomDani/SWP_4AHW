package sample;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import java.sql.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

import javafx.application.Application;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.imageio.ImageIO;

public class API_Stock /*extends Application*/{

    static ArrayList<Double> closeValue = new ArrayList<>();                // ArrayList for date, movingAverage and closeValue
    static ArrayList<Double> movingAverage = new ArrayList<>();
    static ArrayList<LocalDate> date = new ArrayList<>();
    static ArrayList<Double> splitValue = new ArrayList<>();
    static ArrayList<Double> splitCorrected = new ArrayList<>();
    static List<Date> dateChart = new ArrayList<>();

    static ArrayList<Double> avgDB = new ArrayList<>();                     // ArrayList for the database values
    static ArrayList<Double> closeDB = new ArrayList<>();
    static ArrayList<String> dateDB = new ArrayList<>();

    static ArrayList<String> stocks = new ArrayList<String>();

    static String url, stock;
    static int chosenDays;
    static double min, max;
    static int daysforAverage;

    public static void main (String args[]) throws IOException, JSONException {
        API_Stock stockClass = new API_Stock();
        stockClass.readFile();
        for(int i = 0; i<stocks.size();i++) {
            stock = stocks.get(i);
            System.out.println(stock);
            if(!check(stock)) {
                stockClass.readURL();
                stockClass.getValue(url);
                stockClass.connect();
                stockClass.createNewTable();
                stockClass.insert();
                stockClass.splitInsert();
                stockClass.split();
                stockClass.update();
                stockClass.Average();
                stockClass.insertAVG();
                stockClass.MinAndMax();
                stockClass.ListNull();
                stockClass.selectAll();
                for(String dates : dateDB)
                {
                    dateChart.add(Date.valueOf(dates.toString()));
                }
                createFile(createChart(dateChart,closeDB,avgDB));
                new SwingWrapper<XYChart>(createChart(dateChart,closeDB,avgDB)).displayChart();
                System.exit(0);
                //Application.launch(args);
            }
        }

    }

    static void readFile() throws FileNotFoundException {
        Scanner reader = new Scanner(new File("C:\\Users\\danis\\IdeaProjects\\API_School\\src\\sample\\Stocks.txt"));
        while(reader.hasNextLine())
        {
            stocks.add(reader.nextLine());
        }

    }

    public static boolean check (String stock)
    {
        File file = new File ("C:\\Users\\danis\\OneDrive\\Desktop\\Aktien_Images\\Chart_"+ stock +"_"+LocalDate.now()+".jpg");
        return file.exists();
    }
    static void readURL() {
        url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="+ stock + "&outputsize=full&apikey=ZF7R0A6T754HDZGA"; // API-Key
    }

    static void getValue(String URL) throws JSONException, IOException {
        JSONObject json = new JSONObject(IOUtils.toString(new URL(url), Charset.forName("UTF-8")));
        json = json.getJSONObject("Time Series (Daily)");
        for(int i = 0; i < /*chosenDays*/json.names().length(); i++) {
            date.add(LocalDate.parse((CharSequence) json.names().get(i).toString()));
            closeValue.add(json.getJSONObject(LocalDate.parse((CharSequence) json.names().get(i)).toString()).getDouble("4. close"));
            splitValue.add(json.getJSONObject(LocalDate.parse((CharSequence) json.names().get(i)).toString()).getDouble("8. split coefficient"));
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

    // building connection
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

    // creating table and defining key arguments
    public static void createNewTable() {
        String url = "jdbc:mysql://localhost:3306/api?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC"; //Pfad einfügen
        String drop = "Drop Table if exists " + stock + ";";
        String sql = "CREATE table if not exists "+ stock +" (\n"
                + "datum Date primary key unique," + "close double);";
        String dropavg = "drop table if exists " + stock +"avg ;";
        String avgsql = "CREATE TABLE IF NOT EXISTS " + stock + "avg (\n"
                + "datum Date primary key unique," + "gleitenderDurchschnitt double)";
        String dropsplit = "drop table if exists " + stock + "corrected ;";
        String splitsql = "Create table if not exists " + stock +"corrected (\n"
                + "datum Date primary key unique," + "close double," + " splitCoefficient double)";
        try{
            Connection conn = DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            Statement stmt = conn.createStatement();
            stmt.execute(drop);
            stmt.execute(sql);
            stmt.execute(dropavg);
            stmt.execute(avgsql);
            stmt.execute(dropsplit);
            stmt.execute(splitsql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // insert close values into database
    public void insert()
    {
        String sql = "INSERT INTO " + stock + "(datum, close) VALUES('?', ?);";
        try {
            Connection conn = this.connection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for (int i = 1; i < closeValue.size(); i++) {
                sql = "INSERT INTO " + stock + "(datum, close) VALUES(\""+ date.get(i).toString()+"\","+ closeValue.get(i)+");";
                pstmt.execute(sql);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void splitInsert()
    {
        String sql ="INSERT INTO "+ stock +"corrected (datum, close, splitCoefficient) Value ('?',?,?);";
        try
        {
            Connection conn = this.connection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for(int i= 1; i<splitValue.size();i++)
            {
                sql = "INSERT INTO " + stock + "corrected (datum, close, splitCoefficient) VALUES(\""+date.get(i).toString()+"\","+closeValue.get(i)+","+splitValue.get(i)+");";
                pstmt.execute(sql);
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void split() {
        String sql = "Select * from " + stock + "corrected order by datum desc;";
        try {
            date = new ArrayList<>();
            splitValue = new ArrayList<>();
            closeValue = new ArrayList<>();
            Connection conn = this.connection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                rs.getString("datum");
                rs.getDouble("close");
                rs.getDouble("splitCoefficient");
                date.add(LocalDate.parse(rs.getString("datum")));
                closeValue.add(rs.getDouble("close"));
                splitValue.add(rs.getDouble("splitCoefficient"));
            }
            double divider = 1;
            for (int i = 0; i < splitValue.size(); i++) {
                splitCorrected.add(closeValue.get(i) / divider);
                divider = divider * splitValue.get(i);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void update ()
    {
        String sql = "update " + stock + " set close = ? where datum = '?';";
        try{
            Connection conn = this.connection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for(int i = 0;i<closeValue.size();i++)
            {
                sql = "update "+ stock +" set close = " + splitCorrected.get(i) + " where datum = \""+ date.get(i).toString()+ "\";";
                pstmt.execute(sql);
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    // calculate average
    public void Average() {
        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;
        try {
            String url = "jdbc:mysql://localhost:3306/api?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
            conn = DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            stmt = conn.createStatement();
            String sql;
            for(LocalDate avg : date) {
                sql = "Select avg(close) from " + stock + " where (datum < \'" + avg.toString() + "\') and (datum >= \'" + avg.minusDays(200/*daysforAverage*/).toString() + "\') order by datum desc;";
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

    // insert the average close value into the second table
    public void insertAVG() {
        String sqlAVG = "INSERT INTO "+ stock +"avg (datum, gleitenderDurchschnitt) VALUES(?, ?)";
        try{
            Connection conn = this.connection();
            PreparedStatement pstmt = conn.prepareStatement(sqlAVG);
            for (int i = 0; i < movingAverage.size(); i++) {
                sqlAVG = "INSERT INTO "+ stock +"avg (datum, gleitenderDurchschnitt) VALUES(\""+ date.get(i).toString()+"\","+ movingAverage.get(i)+");";
                pstmt.execute(sqlAVG);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // calculate minimum and maximum of the close value
    public void MinAndMax()
    {
        String sqlmax = "select max(close) from "+ stock + ";";
        String sqlmin = "select min(close) from "+ stock + ";";
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

    // selecting all values from database
    public void selectAll() {
        String sql = "SELECT * FROM "+ stock +" order by datum;";
        String sqlAVG = "SELECT * FROM "+ stock +"AVG order by datum;";
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
    public static void ListNull()
    {
        dateDB = new ArrayList<String>();
        closeDB = new ArrayList<Double>();
        avgDB = new ArrayList<Double>();
        dateChart = new ArrayList<Date>();
    }
    public static XYChart createChart(List<Date> d, List<Double>... multipleYAxis)
    {
        XYChart chart = new XYChartBuilder().title(stock).width(1000).height(600).build();
        chart.setYAxisTitle("Close_Values");
        chart.setXAxisTitle("Dates");
        if(closeValue.get(closeValue.size()-1) > movingAverage.get(movingAverage.size()-1))
        {
            chart.getStyler().setPlotBackgroundColor(Color.green);
        }
        else
        {
            chart.getStyler().setPlotBackgroundColor(Color.red);
        }
        List<String> seriesName = new ArrayList<String>();
        seriesName.add("Close_Value");
        seriesName.add("Average_Value");
        //chart.getStyler().setZoomEnabled(true);
        for(int i = 0; i<seriesName.size();i++)
        {
            XYSeries seriesStock = chart.addSeries(seriesName.get(i), dateChart,multipleYAxis[i]);
            seriesStock.setMarker(null);
            seriesStock.setMarker(SeriesMarkers.NONE);
        }

        return chart;

    }
    public static boolean createFile(Object object) throws  IOException {
        if(object.getClass() != XYChart.class)
        {
            return false;
        }
        BitmapEncoder.saveBitmap((XYChart) object,"C:\\Users\\danis\\OneDrive\\Desktop\\Aktien_Images\\Chart_"+ stock +"_"+ LocalDate.now(),BitmapEncoder.BitmapFormat.JPG);
        return true;
    }
    /*@Override
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
            lineChart.setTitle("stock-price "+ stock);
            XYChart.Series<String, Number> closeStat = new XYChart.Series();
            closeStat.setName("close-value");
            for (int i = (closeValue.size() == 10) ? closeValue.size()-10 : closeValue.size() - 11; i < closeValue.size() -1; i++) {
                closeStat.getData().add(new XYChart.Data(dateDB.get(i), closeDB.get(i)));
            }
            for (int i = 0; i< closeValue.size() - 1; i++)
            {
                closeStat.getData().add(new XYChart.Data(dateDB.get(i), closeDB.get(i)));
            }

            XYChart.Series<String, Number> averageStat = new XYChart.Series();
            averageStat.setName("moving average");
            for (int i = (movingAverage.size() == 10) ? movingAverage.size()-10 : movingAverage.size() - 11; i < movingAverage.size()-1; i++) {
                averageStat.getData().add(new XYChart.Data(dateDB.get(i), avgDB.get(i)));
            }
            for(int i = 1; i< movingAverage.size() - 1; i++)
            {
                averageStat.getData().add(new XYChart.Data(dateDB.get(i), avgDB.get(i)));
            }

            // Background-Color for graph
            if(closeValue.get(closeValue.size()-1) > movingAverage.get(movingAverage.size()-1))
            {
                lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color:transparent;");
                lineChart.setStyle("-fx-background-color:#00e600;");
            }
            else
            {
                lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color:transparent;");
                lineChart.setStyle("-fx-background-color:#ff6666;");
            }

            Scene scene = new Scene(lineChart, 1000, 600);
            lineChart.getData().add(closeStat);
            lineChart.getData().add(averageStat);

            WritableImage image = scene.snapshot(null);
            File file = new File ("C:\\Users\\danis\\OneDrive\\Desktop\\Aktien_Images\\Chart_"+ stock +"_"+LocalDate.now()+".png");
            ImageIO.write(SwingFXUtils.fromFXImage(image,null),"PNG",file);

            lineChart.setCreateSymbols(false);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }*/
}