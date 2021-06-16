package API;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BackTesting_Database {
    private static ArrayList<Date> dateChartTrade = new ArrayList<>();
    static String url = "jdbc:mysql://localhost:3306/api?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC"; //Pfad einfügen
    public static void createNewTableTrade(String stock) {
        String use = "use api;";
        String droptrade = "drop table if exists " + stock + "trade ;";
        String trade = "create table if not exists " + stock + "trade (\n"           // select flag from stocktrade order by date desc limit(1);
                + "datum Date primary key unique, " + "ticker varchar(10), " + "flag char(1)," + " number int, " + "depot int)";
        String dropBuyHold = "drop table if exists " + stock +"bh ;";
        String buyHold = "create table if not exists " + stock + "bh (\n"
                + "datum Date primary key unique, " + "ticker varchar(10), " + "flag char(1)," + " number int, " + "depot int)";
        String droptrade200 = "drop table if exists " + stock + "trade3 ;";
        String trade200 = "create table if not exists " + stock + "trade3 (\n"           // Table mit 200er Wert +- 3%
                + "datum Date primary key unique, " + "ticker varchar(10), " + "flag char(1)," + " number int, " + "depot int)";
        try {
            Connection conn = DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            Statement stmt = conn.createStatement();
            stmt.execute(use);
            stmt.execute(droptrade);
            stmt.execute(trade);
            stmt.execute(dropBuyHold);
            stmt.execute(buyHold);
            stmt.execute(droptrade200);
            stmt.execute(trade200);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static boolean disconnect(Connection connection) throws SQLException{
        if(connection == null || connection.isClosed())
        {
            return false;
        }
        else
        {
            connection.close();
            return connection.isClosed();
        }
    }
    // Trading 200er Strategy
    public static void insertStartTrade(String stock, String endung, LocalDate dateTrade, double startKapital) {
        String sql = "insert ignore into " + stock + endung +" (datum, ticker, flag, number, depot) values ('?',?,?,?,?);";
        try {
            Connection conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            PreparedStatement ptsmt = conn.prepareStatement(sql);
            sql = "insert ignore into " + stock + endung + " (datum, ticker, flag, number, depot) values " +
                    "(\'" + dateTrade.minusDays(1) + "\','" + stock + "','s',0," + startKapital + ");";
            ptsmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void fillDateTradeList(String stock, LocalDate dateTrade, LocalDate current, List<LocalDate> dateTradeList,
                                         List<Double> closeTradeList, List<Double> averageTradeList,
                                         double startKapital/*, List<Date> dateChartTrade*/) {
        dateTradeList = new ArrayList<LocalDate>();
        closeTradeList = new ArrayList<Double>();
        averageTradeList = new ArrayList<Double>();
        String sql = "select datum,close from " + stock + " where datum between \'" + dateTrade + "\' AND \'" + current.minusDays(1) + "\' ;";
        String sqlAvg = "select gleitenderDurchschnitt from " + stock + "avg where datum between \'" + dateTrade + "\' " +
                "AND \'" + current.minusDays(1) + "\';";
        try {
            Connection conn = conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            Statement smt = conn.createStatement();
            Statement stmtAvg = conn.createStatement();
            ResultSet rs = smt.executeQuery(sql);
            ResultSet rsA = stmtAvg.executeQuery(sqlAvg);
            while (rs.next() && rsA.next()) {
                rs.getString("datum");
                rs.getDouble("close");
                rsA.getDouble("gleitenderDurchschnitt");
                dateTradeList.add(LocalDate.parse(rs.getString("datum")));
                closeTradeList.add(rs.getDouble("close"));
                averageTradeList.add(rsA.getDouble("gleitenderDurchschnitt"));
            }
            trading200(stock,dateTrade,dateTradeList,closeTradeList,averageTradeList,startKapital);
            buyandHold(stock,dateTrade,dateTradeList,closeTradeList,averageTradeList,startKapital);
            trading200With3(stock,dateTrade,dateTradeList,closeTradeList,averageTradeList,startKapital);
            //addingChartTrade(dateTradeList/*,dateChartTrade*/);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void trading200(String stock, LocalDate dateTrade, List<LocalDate> dateTradeList, List<Double> closeTradeList,
                                  List<Double> averageTradeList, double startKapital) throws SQLException {
        DecimalFormat f = new DecimalFormat("#0.00");
        String flag = null;
        int anzahl = 0;
        int depot=0;
        double prozDepot =0;
        String endung = "trade";
        insertStartTrade(stock, endung, dateTrade, startKapital);
        System.out.println("Trading with _200");
        Connection conn = null;
        conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
        for (int i = 0; i < dateTradeList.size(); i++) {
            int rest = 0;
            String sqlFlag = "select * from " + stock + "trade order by datum desc limit 1";
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlFlag);
                while (rs.next()) {
                    flag = rs.getString("flag");
                    anzahl = rs.getInt("number");
                    depot = rs.getInt("depot");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            if (flag.equals("s")) {
                if (!dateTradeList.get(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)
                        || (!dateTradeList.get(i).getDayOfWeek().equals(DayOfWeek.SUNDAY))) {
                    if (closeTradeList.get(i) > averageTradeList.get(i)) {
                        anzahl = (int) (depot / (closeTradeList.get(i)));
                        rest = (int) (anzahl * closeTradeList.get(i));
                        depot = (depot - rest);
                        flag = "b";

                        insertTradeIntoDB(stock,(LocalDate) dateTradeList.get(i), stock, endung, flag, anzahl, depot,conn);
                        //System.out.println("bought");
                        //System.out.println(anzahl + " number of stocks");
                    }
                }
            } else if (flag.equals("b")) {
                if (!dateTradeList.get(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)
                        || (!dateTradeList.get(i).getDayOfWeek().equals(DayOfWeek.SUNDAY))) {
                    if (closeTradeList.get(i) < averageTradeList.get(i)) {
                        depot = (int) ((anzahl * closeTradeList.get(i)) + depot);
                        flag = "s";
                        anzahl = 0;
                        insertTradeIntoDB(stock,(LocalDate) dateTradeList.get(i),stock, endung, flag, anzahl, depot,conn);
                        //System.out.println("sold");
                        //System.out.println(depot + " money in depot");
                    }
                    if(dateTradeList.get(i) == dateTradeList.get(dateTradeList.size()-1)) {
                        double tempClose = closeTradeList.get(dateTradeList.size() - 1);
                        if(flag.equals("b")) {
                            depot = (int) ((anzahl *tempClose) + depot);
                            flag = "s";
                            anzahl = 0;
                            insertTradeIntoDB(stock,(LocalDate) dateTradeList.get(i), stock, endung, flag, anzahl, depot, conn);
                        }
                    }
                }
            }
            else {
                System.out.println("Datenbankfehler");
            }
        }
        disconnect(conn); //conn.close();
        System.out.println(stock);
        depot = (int) (depot - startKapital);
        System.out.println(depot + " money in depot");
        prozDepot = ((depot/startKapital)*100.00);
        System.out.println(f.format(prozDepot) + " prozentuelle Veränderung");
    }
    public static void insertTradeIntoDB(String stock, LocalDate dateTrading, String ticker, String end, String flag, int anzahl, int depot, Connection conn) throws SQLException
    {
        String insertFlag = "insert ignore into " + stock + end +" (datum, ticker, flag, number, depot) values ('?',?,?,?,?);";
        try {
            PreparedStatement ptsmt = conn.prepareStatement(insertFlag);
            insertFlag = "insert ignore into " + stock + end +" (datum, ticker, flag, number, depot) values " +
                    "(\'" + dateTrading + "\','" + ticker + "','" + flag + "'," + anzahl + "," + depot + ");";
            ptsmt.execute(insertFlag);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Buy and Hold Stragedy
    public static void buyandHold (String stock, LocalDate dateTrade, List<LocalDate> dateTradeList, List<Double> closeTradeList,
                                   List<Double> averageTradeList, double startKapital) throws SQLException {
        DecimalFormat f = new DecimalFormat("#0.00");
        String flag = null;
        int anzahl = 0;
        int depot = (int) startKapital;
        double prozDepot = 0;
        String endung = "bh";
        insertStartTrade(stock, endung, dateTrade, startKapital);
        System.out.println("Buy and Hold");
        Connection conn = null;
        conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
        for (int i = 0; i<dateTradeList.size(); i++) {
            int rest = 0;
            String sqlFlag = "select * from " + stock + "bh order by datum desc limit 1";
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlFlag);
                while (rs.next()){
                    flag = rs.getString("flag");
                    anzahl = rs.getInt("number");
                    depot = rs.getInt("depot");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            if(dateTradeList.get(i) == dateTradeList.get(0)) {
                anzahl = (int) (depot / (closeTradeList.get(i)));
                rest = (int) (anzahl * closeTradeList.get(i));
                depot = (depot - rest);
                flag = "b";
                insertTradeIntoDB(stock,(LocalDate) dateTradeList.get(i), stock, endung, flag, anzahl, depot,conn);
                //System.out.println("bought");
                //System.out.println(anzahl + " number of stocks");
            }
            else if(dateTradeList.get(i) == dateTradeList.get(dateTradeList.size()-1)) {
                depot = (int) ((anzahl * closeTradeList.get(i)) + depot);
                flag = "s";
                anzahl = 0;
                insertTradeIntoDB(stock,(LocalDate) dateTradeList.get(i),stock,endung,flag,anzahl,depot,conn);
                //System.out.println("sold");
                //System.out.println(depot + " money in depot");
            }
        }
        disconnect(conn); //conn.close();
        System.out.println(stock);
        depot = (int) (depot - startKapital);
        System.out.println(depot + " money in depot");
        prozDepot = ((depot/startKapital)*100.00);
        System.out.println(f.format(prozDepot) + " prozentuelle Veränderung");
    }
    public static void trading200With3(String stock, LocalDate dateTrade, List<LocalDate> dateTradeList, List<Double> closeTradeList,
                                       List<Double> averageTradeList, double startKapital) throws SQLException {
        DecimalFormat f = new DecimalFormat("#0.00");
        String flag = null;
        int anzahl = 0;
        int depot = 0;
        double prozDepot = 0;
        String endung = "trade3";
        insertStartTrade(stock, endung, dateTrade, startKapital);
        System.out.println("Trading with _200 plus 3%");
        Connection conn = null;
        conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
        for (int i = 0; i < dateTradeList.size(); i++) {
            int rest = 0;
            String sqlFlag = "select * from " + stock + "trade3 order by datum desc limit 1";
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlFlag);
                while (rs.next()) {
                    flag = rs.getString("flag");
                    anzahl = rs.getInt("number");
                    depot = rs.getInt("depot");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            if (flag.equals("s")) {
                if (!dateTradeList.get(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)
                        || (!dateTradeList.get(i).getDayOfWeek().equals(DayOfWeek.SUNDAY))) {
                    if ((closeTradeList.get(i)*1.03) > averageTradeList.get(i)) {
                        anzahl = (int) (depot / ((closeTradeList.get(i)*1.03)));
                        rest = (int) (anzahl * (closeTradeList.get(i)*1.03));
                        depot = (depot - rest);
                        flag = "b";
                        insertTradeIntoDB(stock,(LocalDate) dateTradeList.get(i), stock, endung, flag, anzahl, depot,conn);
                        //System.out.println("bought");
                        //System.out.println(anzahl + " number of stocks");
                    }
                }
            } else if (flag.equals("b")) {
                if (!dateTradeList.get(i).getDayOfWeek().equals(DayOfWeek.SATURDAY)
                        || (!dateTradeList.get(i).getDayOfWeek().equals(DayOfWeek.SUNDAY))) {
                    if ((closeTradeList.get(i)*1.03) < averageTradeList.get(i)) {
                        depot = (int) ((anzahl * (closeTradeList.get(i)*1.03)) + depot);
                        flag = "s";
                        anzahl = 0;
                        insertTradeIntoDB(stock,(LocalDate) dateTradeList.get(i),stock, endung, flag, anzahl, depot,conn);
                        //System.out.println("sold");
                        //System.out.println(depot + " money in depot");
                    }
                }
                if(dateTradeList.get(i) == dateTradeList.get(dateTradeList.size()-1)) {
                    double tempClose = closeTradeList.get(dateTradeList.size() - 1);
                    if(flag.equals("b")) {
                        depot = (int) ((anzahl *tempClose) + depot);
                        flag = "s";
                        anzahl = 0;
                        insertTradeIntoDB(stock,(LocalDate) dateTradeList.get(i), stock, endung, flag, anzahl, depot, conn);
                    }
                }
            }
            else {
                System.out.println("Datenbankfehler");
            }
        }
        disconnect(conn); //conn.close();
        System.out.println(stock);
        depot = (int) (depot - startKapital);
        System.out.println(f.format(depot) + " money in depot");
        prozDepot = ((depot/startKapital)*100.00);
        System.out.println(f.format(prozDepot) + " prozentuelle Veränderung");
    }
    public static void selecttrade(String stock, List<Integer> depotStockTrade,double startKapital)
    {
        String sql = "select * from " + stock +"trade order by datum desc limit 1";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                rs.getInt("depot");
                depotStockTrade.add(rs.getInt("depot")-(int) startKapital);
            }
            disconnect(conn);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public static void selectbh(String stock, List<Integer> depotStockbh, double startKapital)
    {   Connection conn = null;
        String sql = "select * from " + stock +"bh order by datum desc limit 1";
        try {
            conn = DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                rs.getInt("depot");
                depotStockbh.add(rs.getInt("depot")-(int) startKapital);
            }
            disconnect(conn);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public static void selecttrade3(String stock, List<Integer> depotStockTrade3, double startKapital)
    {
        Connection conn = null;
        String sql = "select * from " + stock +"trade3 order by datum desc limit 1";
        try {
            conn = DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                rs.getInt("depot");
                depotStockTrade3.add(rs.getInt("depot")-(int) startKapital);
            }
            disconnect(conn);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public static void calculateTrades(List<Integer> depotStockTrade3, List<Integer> depotStockTrade, List<Integer> depotStockbh, double startKapital)
    {
        DecimalFormat f = new DecimalFormat("#0.00");
        double alldepoTrade = 0;
        double alldepoBH = 0;
        double alldepoTrade3= 0;
        double alldepoTradePer;
        double alldepoBHPer;
        double alldepoTrade3Per;
        for(int i = 0; i<depotStockTrade.size(); i++)
        {
            alldepoTrade = alldepoTrade + depotStockTrade.get(i);
        }
        System.out.println("\n");
        alldepoTrade = alldepoTrade - startKapital;
        alldepoTradePer = (alldepoTrade / startKapital) * 100;
        System.out.println(f.format(alldepoTrade) + "€ Trading 200");
        System.out.println(f.format(alldepoTradePer) + "% Trading 200 Prozent");
        for(int j = 0; j<depotStockbh.size();j++)
        {
            alldepoBH = alldepoBH + depotStockbh.get(j);
        }
        alldepoBH = alldepoBH- startKapital;
        alldepoBHPer = (alldepoBH/startKapital) * 100;
        System.out.println(f.format(alldepoBH) + "€ Buy and Hold");
        System.out.println(f.format(alldepoBHPer) + "% Buy and Hold Prozent");
        for(int k = 0; k<depotStockTrade3.size();k++)
        {
            alldepoTrade3 = alldepoTrade3 + depotStockTrade3.get(k);
        }
        alldepoTrade3 = alldepoTrade3 - startKapital;
        alldepoTrade3Per = (alldepoTrade3 / startKapital) * 100;
        System.out.println(f.format(alldepoTrade3) + "€ Trading 200 + 3%");
        System.out.println(f.format(alldepoTrade3Per) + "% Trading 200 + 3% Prozent");
    }
    public static void addingChartTrade(List<LocalDate> dateTradeList, List<Date> dateChartTrade)
    {

        BackTesting_Database.dateChartTrade = new ArrayList<>();
        for(LocalDate dates : dateTradeList)
        {
            BackTesting_Database.dateChartTrade.add(Date.valueOf(dates.toString()));
        }
        System.out.println("stop");
    }

    public static ArrayList<Date> getDateChartTrade() {
        return dateChartTrade;
    }

    public static void setDateChartTrade(ArrayList<Date> dateChartTrade) {
        BackTesting_Database.dateChartTrade = dateChartTrade;
    }
}