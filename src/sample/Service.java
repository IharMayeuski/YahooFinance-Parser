package sample;

import com.sun.rowset.internal.Row;
import javafx.scene.control.Cell;
import javafx.scene.control.DatePicker;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;


public class Service {

    public boolean checkDates(DatePicker datePickerStart, DatePicker datePickerEnd, DatePicker datePickerClose) {
        if (nonNull(datePickerStart.getValue())
                && nonNull(datePickerEnd.getValue())
                && nonNull(datePickerClose.getValue())
        ) {
            long start = getSecsFromDate(datePickerStart);
            long end = getSecsFromDate(datePickerEnd);
            long close = getSecsFromDate(datePickerClose);
            if (start > end || start > close || close > end) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public String getUrl(String prefix, DatePicker datePickerStart, DatePicker datePickerEnd) {
        // todo ссылка на таблицу
        return "https://finance.yahoo.com/quote/"
                + prefix
                + "/history?period1="
                + getSecsFromDate(datePickerStart)
                + "&period2="
                + getSecsFromDate(datePickerEnd)
                + "&interval=1d&filter=history&frequency=1d";

        //todo загружаем файл
       /* return "https://query1.finance.yahoo.com/v7/finance/download/"
                + prefix
                + "?period1="
                + getSecsFromDate(datePickerStart)
                + "&period2="
                + getSecsFromDate(datePickerEnd)
                + "&interval=1d&events=history&crumb=FzpQol7UMAy";*/
    }

    public long getSecsFromDate(DatePicker datePickerStart) {
        LocalDate date = datePickerStart.getValue();
        LocalDateTime dateTime = date.atStartOfDay();
        ZonedDateTime zdt = dateTime.atZone(ZoneId.of("Europe/London"));
        return zdt.toInstant().toEpochMilli() / 1000;
    }

    public String matcher(String text, String find) {
        Pattern pattern = Pattern.compile(find);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}

//    public void workWithDownloadFile(String url) {
        /*try {
            URL urlUrl = new URL(url);
            File f = new File(urlUrl.getFile());
            System.out.println(url);
            FileInputStream file = new FileInputStream(f);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            while (sheet.iterator().hasNext()) {
                Row row = sheet.iterator().next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    //Check the cell type and format accordingly
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                            System.out.print(cell.getNumericCellValue() + "\t");
                            break;
                        case Cell.CELL_TYPE_STRING:
                            System.out.print(cell.getStringCellValue() + "\t");
                            break;
                    }
                }
                System.out.println("");
            }
            file.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
//    }

