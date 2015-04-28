import java.io.*;

public class indexes
{
	/*
	 * Calls a linux script to sort the txt files created in phase 1
	 * and get rid of any backslashes in data using a python script
	 * and then uses db_load to store as idx files
	 */
	public static void main(String[] args) {

		Runtime r = Runtime.getRuntime();
		Process p;
		try
		{
			//Calls The Linux Script
			p = r.exec("./sort_script");
			p.waitFor();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
