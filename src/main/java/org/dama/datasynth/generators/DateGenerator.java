package org.dama.datasynth.generators;

import org.apache.avro.test.Simple;
import org.dama.datasynth.runtime.Generator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by aprat on 19/04/16.
 */
public class DateGenerator extends Generator {

    private long dateStart;
    private long dateEnd;
    private String format;
    private String timeZone = "GMT";
    public void initialize(String format, String dateStart, String dateEnd) {
        this.format = format;
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            Date date = formatter.parse(dateStart);
            calendar.setTime(date);
            this. dateStart = calendar.getTimeInMillis();

            date = formatter.parse(dateEnd);
            calendar.setTime(date);
            this. dateEnd = calendar.getTimeInMillis();
        } catch (ParseException pE) {
            pE.printStackTrace();
        }
    }


    public String run() {
        Random rand = new Random();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        calendar.setTime(new Date((long)((dateEnd - dateStart)*rand.nextDouble() + dateStart)));
        return formatter.format(calendar.getTime());
    }

}
