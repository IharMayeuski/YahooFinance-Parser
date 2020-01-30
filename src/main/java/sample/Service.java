package sample;

import javafx.scene.control.DatePicker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class Service {
    final int placeDateOnYahoo = 0;
    final int placeCloseRateOnYahoo = 4;

    public String getUrlFromCloseDate(String prefix, DatePicker datePickerClose) {
        String dateStart = Long.toString(getSecsFromDate(datePickerClose) - 24 * 60 * 60);
        String dateFinish = Long.toString(getSecsFromDate(datePickerClose) + 24 * 60 * 60);
        return "https://finance.yahoo.com/quote/"
                + prefix
                + "/history?period1="
                + dateStart
                + "&period2="
                + dateFinish
                + "&interval=1d&filter=history&frequency=1d";
    }

    public long getSecsFromDate(DatePicker datePickerStart) {
        LocalDate date = datePickerStart.getValue();
        LocalDateTime dateTime = date.atStartOfDay();
        ZonedDateTime zdt = dateTime.atZone(ZoneId.of("Europe/London"));
        return zdt.toInstant().toEpochMilli() / 1000;
    }

    public String getValueFromSite(String url, DatePicker datePickerClose) throws IOException, ParseException {
            Document doc = Jsoup.connect(url).get();
            SimpleDateFormat oldDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Element table = doc.select("table").get(placeDateOnYahoo); //select the first table.
            Elements rows = table.select("tr");
            for (Element row : rows) {
                Elements elements = row.select("td");
                if (elements.size() == 7) {
                    String oldDateString = elements.get(0).text();
                    Date date = oldDateFormat.parse(oldDateString);
                    String newDateString = newDateFormat.format(date);
                    if ((datePickerClose.getValue().toString()).equals(newDateString)) {
                        return elements.get(placeCloseRateOnYahoo).text();
                    }
                }
            }
        return "";
    }

    public static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public String getNewFileNameForData(String path, DatePicker datePickerClose) {
        StringBuffer sb = new StringBuffer(path);
        sb.delete(sb.length() - 5, sb.length());
        return (sb + "-result-" + datePickerClose.getValue().toString() + ".xlsx");
    }
}