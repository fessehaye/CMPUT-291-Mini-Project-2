import java.util.ArrayList;
import java.util.List;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

public class prices {
	/*
	 * prices is passed the list string keyword and the price databases. It
	 * checks the sign of the price query to see whether it is < or >. It then
	 * compares each database price to the inputed price and adds each
	 * appropriate aid that matches the query to the list string Key_of_Query
	 * and returns that to be intersected with appropriate ads from all other
	 * methods
	 */
	public static List<String> Start(List<String> keyword, Database price_db) {

		// set key and data
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		// these booleans tell is what the input is
		boolean greater_than = false;
		boolean less_than = false;

		// check to see whether the input is > or <
		String sign = keyword.get(1);
		if (sign.equals(">")) {
			greater_than = true;
		}
		if (sign.equals("<")) {
			less_than = true;
		}
		// number is the price to check against
		String number = keyword.get(2);
		List<String> Key_of_Query = new ArrayList<String>();

		Cursor std_cursor;
		try {
			std_cursor = price_db.openCursor(null, null);
			// get the data
			if (std_cursor.getFirst(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				// aKey is the price, aData is the aid
				String aKey = new String(key.getData());
				String aData = new String(data.getData());

				// decide whether the data matches the query
				if (greater_than
						&& (Integer.parseInt(aKey) > Integer.parseInt(number))) {
					Key_of_Query.add(aData);
				}

				if (less_than
						&& (Integer.parseInt(aKey) < Integer.parseInt(number))) {
					Key_of_Query.add(aData);
				}

				key = new DatabaseEntry();
				data = new DatabaseEntry();

				// same thing as before but this is getNext as a loop
				while (std_cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

					aKey = new String(key.getData());
					aData = new String(data.getData());
					// same as before except getNext
					if (greater_than
							&& (Integer.parseInt(aKey) > Integer
									.parseInt(number))) {
						Key_of_Query.add(aData);
					}
					if (less_than
							&& (Integer.parseInt(aKey) < Integer
									.parseInt(number))) {
						Key_of_Query.add(aData);
					}
					key = new DatabaseEntry();
					data = new DatabaseEntry();
				}
			} else {
				System.out.println("The database is empty\n");
			}

			std_cursor.close();
		}
		// catch if the user inputed a float instead of int
		catch (NumberFormatException e) {
			System.out
					.println("Error: Invalid FORMAT USE ONLY INTEGERS");
			Key_of_Query.clear();
			return Key_of_Query;
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return a list of aids
		return Key_of_Query;

	}

}
