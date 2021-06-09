package API;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class API {
    static String url;
    public static void readFile(List<String> stocks) throws FileNotFoundException {
        Scanner reader = new Scanner(new File("C:\\Users\\danis\\IdeaProjects\\API_School\\src\\API\\Stocks.txt"));
        while (reader.hasNextLine()) {
            stocks.add(reader.nextLine());
        }
    }
    public static boolean check(String stock) {
        File file = new File("C:\\Users\\danis\\OneDrive\\Desktop\\Aktien_Images\\Chart_" + stock + "_" + LocalDate.now() + ".jpg");
        return file.exists();
    }

    static void readURL(String stock) {
        url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + stock + "&outputsize=full&apikey=ZF7R0A6T754HDZGA";
    }

    static void getValue(String URL,ArrayList<LocalDate> date, ArrayList<Double> closeValue,
                         ArrayList<Double> splitValue) throws JSONException, IOException {
        JSONObject json = new JSONObject(IOUtils.toString(new URL(url), Charset.forName("UTF-8")));
        json = json.getJSONObject("Time Series (Daily)");
        for (int i = 0; i < json.names().length(); i++) {
            date.add(LocalDate.parse((CharSequence) json.names().get(i).toString()));
            closeValue.add(json.getJSONObject(LocalDate.parse((CharSequence) json.names().get(i)).toString()).getDouble("4. close"));
            splitValue.add(json.getJSONObject(LocalDate.parse((CharSequence) json.names().get(i)).toString()).getDouble("8. split coefficient"));
        }
    }
}