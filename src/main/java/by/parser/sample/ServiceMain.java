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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.*;

import static by.parser.sample.Constant.*;

class ServiceMain {
    class ValueFromYahoo implements Callable {
        private Map.Entry<Integer, String> entry;
        private DatePicker datePickerClose;

        public ValueFromYahoo(Map.Entry<Integer, String> entry, DatePicker datePickerClose) {
            this.entry = entry;
            this.datePickerClose = datePickerClose;
        }

        public Map<Integer, Document> call() {
            Map<Integer, Document> elementsForFillInFill = new HashMap<>();
            try {
                String url = getUrlFromCloseDate(entry.getValue(), datePickerClose);
                Document doc = Jsoup.connect(url).get();
                elementsForFillInFill.put(entry.getKey(), doc);
            } catch (IOException e) {
                elementsForFillInFill.put(entry.getKey(), null);
            }
            return elementsForFillInFill;
        }
    }

    Map<Integer, String> getValueFromYahoo(Map<Integer, String> myElements, DatePicker datePickerClose) {
        Map<Integer, Document> documentMap = new HashMap<>();
        ExecutorService pool = Executors.newFixedThreadPool(THREAD);
        Set<Future<Map<Integer, Document>>> set = new HashSet<>();

        for (final Map.Entry<Integer, String> entry : myElements.entrySet()) {
            Callable<Map<Integer, Document>> callable = new ValueFromYahoo(entry, datePickerClose);
            Future<Map<Integer, Document>> future = pool.submit(callable);
            set.add(future);
        }

        for (Future<Map<Integer, Document>> future : set) {
            try {
                Map<Integer, Document> oneFuture = future.get();
                for (Map.Entry<Integer, Document> myEntry : oneFuture.entrySet()) {
                    documentMap.put(myEntry.getKey(), myEntry.getValue());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return getValueFromDoc(documentMap, datePickerClose);
    }

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
            e.printStackTrace();
        }
        return myElements;
    }

    void writeResultInFile(File fileSource, DatePicker datePickerClose, Map<Integer, String> elementsForFillInFill) {
        try {
            String fileName = getNewFileNameForData(fileSource.getPath(), datePickerClose);
            File fileForCopy = new File(fileName);
            copyFileUsingStream(fileSource, fileForCopy);

            FileInputStream newFile = new FileInputStream(new File(fileForCopy.getPath()));
            Workbook workbook = new XSSFWorkbook(newFile);
            Sheet sheet = workbook.getSheet("Sheet1");
            for (final Map.Entry<Integer, String> oneElement : elementsForFillInFill.entrySet()) {
                Row oneRow = sheet.getRow(oneElement.getKey());
                sheet.getRow(oneRow.getRowNum()).createCell(11).setCellValue(oneElement.getValue());
            }
            newFile.close();
            FileOutputStream outputStream = new FileOutputStream(fileForCopy.getPath());
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
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

    private String getValueFromSite(Document doc, DatePicker datePickerClose) throws Exception{
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
        String unswer;
        if (totalTimeInSec >= 60) {
            long mins = totalTimeInSec / 60;
            long secs = totalTimeInSec % 60;
            unswer = mins > 1
                    ? mins + " mins, " + secs + " secs."
                    : mins + " min, " + secs + " secs.";
        } else {
            unswer = totalTimeInSec + " secs";
        }
        return unswer;
    }

    private Map<Integer, String> getValueFromDoc (Map<Integer, Document> documentMap, DatePicker datePickerClose) {
        Map<Integer, String> elementsForFillInFill = new HashMap<>();
        for (Map.Entry<Integer, Document> oneDoc: documentMap.entrySet()) {
            try {
                String course = getValueFromSite(oneDoc.getValue(), datePickerClose);
                elementsForFillInFill.put(oneDoc.getKey(), course);
            } catch (Exception e) {
                elementsForFillInFill.put(oneDoc.getKey(), NOT_FOUND);
            }
        }
        return elementsForFillInFill;
    }
}