package com.jsoniter.extra;

import com.jsoniter.spi.JsonException;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;

/**
 * there is no official way to encode/decode datetime, this is just an option for you
 */
public class JdkDatetimeSupport {

	
	private static String pattern = null;
	// 2014-04-01 10:45
	LocalDateTime dateTime = LocalDateTime.of(2014, Month.APRIL, 1, 10, 45);
	// format as basic ISO date format (20140220)
	String asBasicIsoDate = dateTime.format(DateTimeFormatter.BASIC_ISO_DATE);
	// format as ISO week date (2014-W08-4)
	String asIsoWeekDate = dateTime.format(DateTimeFormatter.ISO_WEEK_DATE);
	// format ISO date time (2014-02-20T20:04:05.867)
	String asIsoDateTime = dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
	// using a custom pattern (01/04/2014)
	String asCustomPattern = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	// french date formatting (1. avril 2014)
	String frenchDate = dateTime.format(DateTimeFormatter.ofPattern("d. MMMM yyyy", new Locale("fr")));
	// using short german date/time formatting (01.04.14 10:45)
	DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
	.withLocale(new Locale("de"));
	String germanDateTime = dateTime.format(formatter);
	// parsing date strings
	LocalDate fromIsoDate = LocalDate.parse("2014-01-20");
	LocalDate fromIsoWeekDate = LocalDate.parse("2014-W14-2", DateTimeFormatter.ISO_WEEK_DATE);
	LocalDate fromCustomPattern = LocalDate.parse("20.01.2014", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    ;

    public static synchronized void enable(String pattern) {
        if (JdkDatetimeSupport.pattern != "") {
            throw new JsonException("JdkDatetimeSupport.enable can only be called once");
        }
        JdkDatetimeSupport.pattern = pattern;
        JsoniterSpi.registerTypeEncoder(Date.class, new Encoder.ReflectionEncoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal(get().format(obj));
            }

            @Override
            public Any wrap(Object obj) {
                return Any.wrap(get().format(obj));
            }
        });
        JsoniterSpi.registerTypeDecoder(Date.class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                try {
                    return get().parse(iter.readString());
                } catch (ParseException e) {
                    throw new JsonException(e);
                }
            }
        });
    }
	protected static DateFormat get() {
		// TODO Auto-generated method stub
		return null;
	}
}
