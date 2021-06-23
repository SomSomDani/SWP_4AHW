package API;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class API_Database{
    static String url = "jdbc:mysql://localhost:3306/api?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC"; //Pfad einfügen

    public static void connect() throws SQLException {
        Connection conn = null;
        try {
            conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            System.out.println("Connection to MySQL has been established.");
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
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

    public static void createNewTable(String stock) {
        String use = "use api;";                        // Database auf weniger Tables
        String sql = "CREATE table if not exists " + stock + " (\n"
                + "datum Date primary key unique," + "close double);";
        String avgsql = "CREATE TABLE IF NOT EXISTS " + stock + "avg (\n"
                + "datum Date primary key unique," + "gleitenderDurchschnitt double)";
        String splitsql = "Create table if not exists " + stock + "corrected (\n"
                + "datum Date primary key unique," + "close double," + " splitCoefficient double)";

        try {
            Connection conn = DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            Statement stmt = conn.createStatement();
            stmt.execute(use);
            stmt.execute(sql);
            stmt.execute(avgsql);
            stmt.execute(splitsql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void insert(String stock, List<LocalDate> date, List<Double> closeValue) {
        String sql = "INSERT IGNORE INTO " + stock + "(datum, close) VALUES('?', ?);";
        try {
            Connection  conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < closeValue.size(); i++) {
                sql = "INSERT IGNORE INTO " + stock + "(datum, close) VALUES(\"" + date.get(i).toString() + "\"," + closeValue.get(i) + ");";
                pstmt.execute(sql);
            }
            disconnect(conn);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void splitInsert(String stock,List<LocalDate> date, List<Double> closeValue, List<Double> splitValue ) {
        String sql = "INSERT ignore INTO " + stock + "corrected (datum, close, splitCoefficient) Value ('?',?,?);";
        try {
            Connection  conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < splitValue.size(); i++) {
                sql = "INSERT ignore INTO " + stock + "corrected (datum, close, splitCoefficient) " +
                        "VALUES(\"" + date.get(i).toString() + "\"," + closeValue.get(i) + "," + splitValue.get(i) + ");";
                pstmt.execute(sql);
            }
            disconnect(conn);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void split(String stock, List<LocalDate> date, List<Double>closeValue, List<Double> splitValue, List<Double> splitCorrected) {
        String sql = "Select * from " + stock + "corrected order by datum desc;";
        try {
            date = new ArrayList<>();
            closeValue = new ArrayList<>();
            splitValue = new ArrayList<>();
            Connection  conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
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
            update(stock,date,closeValue,splitCorrected);
            disconnect(conn);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void update(String stock, List<LocalDate> date, List<Double> closeValue, List<Double> splitCorrected) {
        String sql = "update " + stock + " set close = ? where datum = '?';";
        try {
            Connection  conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < date.size(); i++) {
                sql = "update " + stock + " set close = " + splitCorrected.get(i) + " where datum = \"" + date.get(i).toString() + "\";";
                pstmt.execute(sql);
            }
            disconnect(conn);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // calculate average
    public static void Average(String stock, List<LocalDate> date, List<Double>movingAverage) {
        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;
        try {
            String url = "jdbc:mysql://localhost:3306/api?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
            conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            stmt = conn.createStatement();
            String sql;
            for (LocalDate avg : date) {
                sql = "Select avg(close) from " + stock + " where (datum < \'" + avg.toString() + "\') and (datum >= \'" + avg.minusDays(200).toString() + "\') order by datum desc;";
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                        movingAverage.add(rs.getDouble(i + 1));
                    }
                }
            }
            disconnect(conn);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // insert the average close value into the second table
    public static void insertAVG(String stock, List<LocalDate> date, List<Double> movingAverage) {
        String sqlAVG = "INSERT ignore INTO " + stock + "avg (datum, gleitenderDurchschnitt) VALUES('?', ?)";
        try {
            Connection  conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            PreparedStatement pstmt = conn.prepareStatement(sqlAVG);
            for (int i = 0; i < movingAverage.size(); i++) {
                sqlAVG = "INSERT ignore INTO " + stock + "avg (datum, gleitenderDurchschnitt) VALUES(\"" + date.get(i).toString() + "\"," + movingAverage.get(i) + ");";
                pstmt.execute(sqlAVG);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // selecting all values from database
    public static void selectAll(String stock, List<String> dateDB, List<Double> closeDB, List<Double> avgDB) {
        String sql = "SELECT * FROM " + stock + " order by datum;";
        String sqlAVG = "SELECT * FROM " + stock + "AVG order by datum;";
        try {
            Connection conn =  DriverManager.getConnection(url, "root", "Destiny@hi!.com");
            Statement stmt = conn.createStatement();
            Statement stmtAVG = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSet rsAVG = stmtAVG.executeQuery(sqlAVG);

            //System.out.println("Datum               Close Werte             Durchschnitt");
            while (rs.next() && rsAVG.next()) {
                //System.out.println(
                rs.getString("datum");
                rs.getDouble("close");
                rsAVG.getDouble("gleitenderDurchschnitt");
                //);
                Double avgTemp = rsAVG.getDouble("gleitenderDurchschnitt");
                dateDB.add(rsAVG.getString("datum"));
                closeDB.add(rs.getDouble("close"));
                avgDB.add(avgTemp == 0 ? null : avgTemp);
            }
            dateDB.sort(null);
            System.out.println("Database filled");
            disconnect(conn);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}