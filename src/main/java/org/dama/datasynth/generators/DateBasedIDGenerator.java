package org.dama.datasynth.generators;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by aprat on 20/04/16.
 * Creates unique identifiers whose values are correlated with a data.
 */
public class DateBasedIDGenerator extends Generator {

    private String format;

    /**
     * Initializes the generator.
     * @param dateFormat The format of the input dates.
     */
    public void initialize(String dateFormat) {
        this.format = dateFormat;
    }

    /**
     * Returns a new unique identifier correlated by date.
     * @param id The internal and unique identifier
     * @param date The date to build the new identifier
     * @return A new unique identifier whose values is correlated by date.
     */
    public Long run(Long id, String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Long milliseconds = 0L;
        try {
            milliseconds = formatter.parse(date).getTime();
        } catch (ParseException pE) {
            pE.printStackTrace();
        }
        long finalId = (id << 20) | (milliseconds >> 44);
        return finalId;
    }
}
