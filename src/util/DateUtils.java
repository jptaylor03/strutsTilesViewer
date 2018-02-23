package util;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Utilities for date manipulation.
 */
public class DateUtils {

	/********************
	 * Member method(s) *
	 ********************/
	
	/**
	 * Convert a Date object into a String representation.
	 * 
	 * @param input Date containing the date value.
	 * @param format String to specify the date format (optional).
	 * @return String equivalent to the specified input date value.
	 */
	public static String fromDate(Date input, String format) {
		String output = null;
		try {
			if (format == null || format.trim().equals("")) {
				format = "MM/dd/yyyy";
			}
			if (input != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				output = sdf.format(input);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	/**
	 * Convert a String representation of a date into a Date object.
	 * 
	 * @param input String containing the date value.
	 * @param format String to specify the date format (optional).
	 * @return Date object equivalent to the specified input date value.
	 */
	public static Date toDate(String input, String format) {
		Date output = null;
		try {
			if (format == null || format.trim().equals("")) {
				format = "MM/dd/yyyy";
			}
			if (input != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				output = sdf.parse(input);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
}
