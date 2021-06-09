package API;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
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
    static LocalDate dateTrade;
    static double startKapital;
    static double startKapitalperStock;
    static LocalDate current = LocalDate.now();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException, SQLException {
        BackTesting_Database backtest = new BackTesting_Database();
        System.out.println("Geben Sie das Startdatum ein: [Jahr-Monat-Tag]");
        dateTrade = LocalDate.parse(scanner.next());
        System.out.println("Geben Sie das Startkapital ein: [z.B. 100000 €/$]");
        startKapital = scanner.nextInt();
        API.readFile(stocks);
        for(int i = 0; i<stocks.size();i++) {
            stock = stocks.get(i);
            //System.out.println(stock);
            startKapitalperStock = (startKapital/stocks.size());
            backtest.createNewTableTrade(stock);
            backtest.fillDateTradeList(stock,dateTrade,current,dateTradeList,closeTradeList,averageTradeList,startKapitalperStock);
            backtest.selecttrade(stock,depotStocktrade,startKapitalperStock);
            backtest.selectbh(stock,depotStockbh,startKapitalperStock);
            backtest.selecttrade3(stock,depotStocktrade3,startKapitalperStock);
        }
        backtest.calculateTrades(stock,depotStocktrade3,depotStocktrade,depotStockbh,startKapital);
    }
}