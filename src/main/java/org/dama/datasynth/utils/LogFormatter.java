package org.dama.datasynth.utils;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by aprat on 17/05/16.
 */
public class LogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return (new Date(record.getMillis())).toString()+" "+record.getMessage()+"\n";
    }
}
