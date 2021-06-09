package API;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import java.awt.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class API_Main {
    static ArrayList<String> stocks = new ArrayList<>();
    static String stock, url;
    static ArrayList<LocalDate>date = new ArrayList<>();
    static ArrayList<Double> closeValue = new ArrayList<Double>();
    static ArrayList<Double> splitValue = new ArrayList<Double>();
    static ArrayList<Double> splitCorrected = new ArrayList<>();
    static ArrayList<Double> movingAverage = new ArrayList<>();
    static ArrayList<Double> avgDB = new ArrayList<>();
    static ArrayList<Double> closeDB = new ArrayList<>();
    static ArrayList<String> dateDB = new ArrayList<>();
    static List<Date> dateChart = new ArrayList<>();

    public static void main(String[] args) throws IOException, SQLException {
        API_Database database = new API_Database();
        API api = new API();
        api.readFile(stocks);
        for(int i = 0; i<stocks.size();i++) {
            stock = stocks.get(i);
            System.out.println(stock);
            if (!API.check(stock)) {
                api.readURL(stock);
                api.getValue(url, date, closeValue, splitValue);
                database.connect();
                database.createNewTable(stock);
                database.insert(stock, date, closeValue);
                database.splitInsert(stock, date, closeValue, splitValue);
                database.split(stock,date,closeValue,splitValue,splitCorrected);
                database.Average(stock, date, movingAverage);
                database.insertAVG(stock, date, movingAverage);
                database.selectAll(stock, dateDB, closeDB, avgDB);
                for (String dates : dateDB) {
                    dateChart.add(Date.valueOf(dates.toString()));
                }
                createFile(createChart(dateChart, closeDB, avgDB));
                new SwingWrapper<XYChart>(createChart(dateChart, closeDB, avgDB)).displayChart();
                System.exit(0);
            }
        }
    }
    public static XYChart createChart(List<Date> d, List<Double>... multipleYAxis) {
        XYChart chart = new XYChartBuilder().title(stock).width(1000).height(600).build();
        chart.setYAxisTitle("Close_Values");
        chart.setXAxisTitle("Dates");
        if (closeValue.get(closeValue.size() - 1) > movingAverage.get(movingAverage.size() - 1)) {
            chart.getStyler().setPlotBackgroundColor(Color.green);
        } else {
            chart.getStyler().setPlotBackgroundColor(Color.red);
        }
        List<String> seriesName = new ArrayList<String>();
        seriesName.add("Close_Value");
        seriesName.add("Average_Value");
        //chart.getStyler().setZoomEnabled(true);
        for (int i = 0; i < seriesName.size(); i++) {
            XYSeries seriesStock = chart.addSeries(seriesName.get(i), dateChart, multipleYAxis[i]);
            seriesStock.setMarker(SeriesMarkers.NONE);
        }
        return chart;
    }
    public static boolean createFile(Object object) throws IOException {
        if (object.getClass() != XYChart.class) {
            return false;
        }
        BitmapEncoder.saveBitmap((XYChart) object, "C:\\Users\\danis\\OneDrive\\Desktop\\Aktien_Images\\Chart_"
                + stock + "_" + LocalDate.now(), BitmapEncoder.BitmapFormat.JPG);
        return true;
    }
}