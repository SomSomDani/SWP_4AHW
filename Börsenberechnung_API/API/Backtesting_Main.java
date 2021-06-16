package API;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Backtesting_Main {
    static ArrayList<String> stocks = new ArrayList<>();
    static String stock;
    static ArrayList<LocalDate>dateTradeList = new ArrayList<LocalDate>();
    static ArrayList<Double> closeTradeList = new ArrayList<Double>();
    static ArrayList<Double> averageTradeList = new ArrayList<Double>();
    static ArrayList<Integer> depotStocktrade = new ArrayList<>();
    static ArrayList<Integer> depotStockbh = new ArrayList<>();
    static ArrayList<Integer> depotStocktrade3 = new ArrayList<>();
    static ArrayList<Date> dateChartTrade = new ArrayList<>();
    static LocalDate dateTrade;
    static double startKapital;
    static double startKapitalperStock;
    static double startKapitalperStockProMethod;
    static LocalDate current = LocalDate.now();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException, SQLException {
        BackTesting_Database backtest = new BackTesting_Database();
        Date();
        API.readFile(stocks);
        for(int i = 0; i<stocks.size();i++) {
            stock = stocks.get(i);
            startKapitalperStock = (startKapital/stocks.size());
            startKapitalperStockProMethod = (startKapitalperStock/3);
            backtest.createNewTableTrade(stock);
            backtest.fillDateTradeList(stock,dateTrade,current,dateTradeList,closeTradeList,
                                averageTradeList,startKapitalperStockProMethod/*,dateChartTrade*/);
            dateChartTrade = backtest.getDateChartTrade();
            backtest.selecttrade(stock,depotStocktrade,startKapitalperStockProMethod);
            backtest.selectbh(stock,depotStockbh,startKapitalperStockProMethod);
            backtest.selecttrade3(stock,depotStocktrade3,startKapitalperStockProMethod);
        }
        backtest.calculateTrades(depotStocktrade3,depotStocktrade,depotStockbh,startKapitalperStockProMethod);
        //createFile(createChart(dateChartTrade, depotStocktrade, depotStocktrade3));
        //new SwingWrapper<XYChart>(createChart(dateChartTrade, depotStocktrade, depotStocktrade3)).displayChart();
    }
    /*public static XYChart createChart(List<Date> d, List<Integer>... multipleYAxis) {
        XYChart chart = new XYChartBuilder().title(stock).width(1000).height(600).build();
        chart.setYAxisTitle("Depot");
        chart.setXAxisTitle("Dates");
        List<String> seriesName = new ArrayList<String>();
        seriesName.add("Depot-Trading200");
        seriesName.add("Depot-Trading200+3%");
        for (int i = 0; i < seriesName.size(); i++) {
            XYSeries seriesStock = chart.addSeries(seriesName.get(i), dateChartTrade, multipleYAxis[i]);
            seriesStock.setMarker(SeriesMarkers.NONE);
        }
        return chart;
    }
    public static boolean createFile(Object object) throws IOException {
        if (object.getClass() != XYChart.class) {
            return false;
        }
        BitmapEncoder.saveBitmap((XYChart) object, "C:\\Users\\danis\\OneDrive\\Desktop\\Aktien_Images\\Backtesting_"
                + stock + "_Calculating_" + LocalDate.now(), BitmapEncoder.BitmapFormat.JPG);
        return true;
    }*/
    public static void Date()
    {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd,MM,yyyy");
        System.out.println("Geben Sie das Startdatum ein: [Tag,Monat,Jahr]");
        String dateString = scanner.next();
        dateTrade = LocalDate.parse(dateString, dateTimeFormatter);
        System.out.println("Geben Sie das Startkapital ein: [z.B. 100000 €/$]");
        startKapital = scanner.nextInt();
    }
}
