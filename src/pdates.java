import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

public class pdates {

	/*
	 * pdates takes in keywords and the database for dates. It checks the first
	 * word to see whether it is 'since' or 'until'. It then uses that
	 * information to compare all dates withing date_db to see whether they
	 * match the query specs. It does the date comparison by using Start checks
	 * each keyword against the date_db and returns the proper aids in the
	 * string list Answers
	 */
	public static List<String> Start(List<String> keywords, Database date_db) {

		Cursor std_cursor;
		// initialize Answers and isolate the keyword and date
		List<String> Answers = new ArrayList<String>();
		String keyword = keywords.get(0);
		String date = keywords.get(1);
		boolean trump = false;

		try {
			std_cursor = date_db.openCursor(null, null);

			// initialize the until and since variables
			boolean until = false;
			boolean since = false;

			// initialize this key and data stuff
			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry data = new DatabaseEntry();

			// check whether the input is until or since or both
			if (keyword.equals("until")) {
				until = true;
			}
			if (keyword.equals("since")) {
				since = true;
			}

			if (std_cursor.getFirst(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				String aKey = new String(key.getData());
				String aData = new String(data.getData());

				// --Calendar Arithmetic
				// format the date

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

				// date the variables
				Date data_date = sdf.parse(aKey);
				Date input_date = sdf.parse(date);
				// format dates
				sdf.format(data_date);
				sdf.format(input_date);

				// check for since and greater than
				if (data_date.compareTo(input_date) > 0 && since) {
					trump = true;

				} else if (data_date.compareTo(input_date) < 0 && until) {
					trump = true;

				} else if (data_date.compareTo(input_date) == 0) {
					trump = true;
				}

				// --we know trump, we add good data to Answers
				if (trump) {
					Answers.add(aData);
				}
				trump = false;

				// _____________________________Getting next
				key = new DatabaseEntry();
				data = new DatabaseEntry();

				while (std_cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
					aKey = new String(key.getData());
					aData = new String(data.getData());

					// --Calendar Arithmetic

					data_date = sdf.parse(aKey);
					input_date = sdf.parse(date);

					sdf.format(data_date);
					sdf.format(input_date);

					// setting trump, same as before

					if (data_date.compareTo(input_date) > 0 && since) {
						trump = true;

					} else if (data_date.compareTo(input_date) < 0 && until) {
						trump = true;

					} else if (data_date.compareTo(input_date) == 0) {
						trump = true;
					}

					// --
					if (trump) {
						Answers.add(aData);
					}

					trump = false;
				}
			} else {
				System.out.println("The database is empty\n");
			}

			key = new DatabaseEntry();
			data = new DatabaseEntry();
			std_cursor.close();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: can not parse date");
			Answers.clear();
			return Answers;
		}

		// Answers has the info we need
		return Answers;
	}
}
