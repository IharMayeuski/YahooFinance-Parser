package by.parser.sample;

import javafx.scene.control.DatePicker;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.util.HashMap;
import java.util.Map;

class ServiceMain {
    private final int placeDateOnYahoo = 0;
    private final int placeCloseRateOnYahoo = 4;
    private final String NOT_FOUND = "--";

    Map<Integer, String> getElementsFromSourceFile(String path) {
        Map<Integer, String> myElements = new HashMap<>();
        try {
            FileInputStream sourceFile = new FileInputStream(new File(path));
            Workbook workbook = new XSSFWorkbook(sourceFile);
            Sheet sheet = workbook.getSheet("Sheet1");
            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                Row rowOne = sheet.getRow(j);
                Cell cell = rowOne.getCell(2);
                String prefix = cell.getStringCellValue();
                Integer elemAddress = rowOne.getRowNum();
                myElements.put(elemAddress, prefix);
            }
            sourceFile.close();
        } catch (IOException e) {
            System.out.println("can't receive info from url: " + path);
        }
        return myElements;
    }

    Map<Map.Entry<Integer, String>, String> getValueFromYahoo(Map<Integer, String> myElements, DatePicker datePickerClose) {
        Map<Map.Entry<Integer, String>, String> elementsForFillInFill = new HashMap<>();
        for (final Map.Entry<Integer, String> entry : myElements.entrySet()) {
            String url = getUrlFromCloseDate(entry.getValue(), datePickerClose);
            try {
                String course = getValueFromSite(url, datePickerClose);
                elementsForFillInFill.put(entry, course);
            } catch (Exception e) {
                elementsForFillInFill.put(entry, NOT_FOUND);
            }
        }
        return elementsForFillInFill;
    }

    void writeResultInFile(
            File fileSource,
            DatePicker datePickerClose,
            Map<Map.Entry<Integer, String>, String> elementsForFillInFill
    ) {
        try {
            String fileName = getNewFileNameForData(fileSource.getPath(), datePickerClose);
            File fileForCopy = new File(fileName);
            copyFileUsingStream(fileSource, fileForCopy);

            FileInputStream newFile = new FileInputStream(new File(fileForCopy.getPath()));
            Workbook workbook = new XSSFWorkbook(newFile);
            Sheet sheet = workbook.getSheet("Sheet1");
            for (final Map.Entry<Map.Entry<Integer, String>, String> oneElement : elementsForFillInFill.entrySet()) {
                Row oneRow = sheet.getRow(oneElement.getKey().getKey());
                sheet.getRow(oneRow.getRowNum()).createCell(11).setCellValue(oneElement.getValue());
            }
            newFile.close();
            FileOutputStream outputStream = new FileOutputStream(fileForCopy.getPath());
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception e) {
            System.out.println("can't write info in file!");
        }
    }

    private String getUrlFromCloseDate(String prefix, DatePicker datePickerClose) {
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

    private long getSecsFromDate(DatePicker datePicker) {
        LocalDate date = datePicker.getValue();
        LocalDateTime dateTime = date.atStartOfDay();
        ZonedDateTime zdt = dateTime.atZone(ZoneId.of("Europe/London"));
        return zdt.toInstant().toEpochMilli() / 1000;
    }

    private String getValueFromSite(String url, DatePicker datePickerClose) throws IOException, ParseException {
        Document doc = Jsoup.connect(url).get();
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Element table = doc.select("table").get(placeDateOnYahoo);
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
        return NOT_FOUND;
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    private String getNewFileNameForData(String path, DatePicker datePickerClose) {
        StringBuilder sb = new StringBuilder(path);
        sb.delete(sb.length() - 5, sb.length());
        return (sb + "-result-" + datePickerClose.getValue().toString() + ".xlsx");
    }

    String getTimeForOutput(long timeStart, long timeFinish) {
        long totalTimeInSec = timeFinish - timeStart;
        String unswer = "";
        if (totalTimeInSec >= 60) {
            long mins = totalTimeInSec / 60;
            long secs = totalTimeInSec % 60;
            if (mins > 1) {
                unswer = mins + " mins, " + secs + " secs.";
            } else {
                unswer = mins + " min, " + secs + " secs.";
            }
        } else {
            unswer = totalTimeInSec + " secs";
        }
        return unswer;
    }
}