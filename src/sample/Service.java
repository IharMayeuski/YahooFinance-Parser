package sample;

import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
        return "https://finance.yahoo.com/quote/"
                + prefix
                + "/history?period1="
                + getSecsFromDate(datePickerStart)
                + "&period2="
                + getSecsFromDate(datePickerEnd)
                + "&interval=1d&filter=history&frequency=1d";
    }

    public long getSecsFromDate(DatePicker datePickerStart) {
        LocalDate date = datePickerStart.getValue();
        LocalDateTime dateTime = date.atStartOfDay();
        ZonedDateTime zdt = dateTime.atZone(ZoneId.of("Europe/London"));
        return zdt.toInstant().toEpochMilli() / 1000;
    }
}
