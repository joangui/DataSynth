package org.dama.datasynth.generators;

import org.dama.datasynth.runtime.Generator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by aprat on 20/04/16.
 */
public class DateBasedIDGenerator extends Generator {

    private String format;
    public void initialize(String dateFormat) {
        this.format = dateFormat;
    }

    public Long run(Long seed, Long id, String date) {
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
