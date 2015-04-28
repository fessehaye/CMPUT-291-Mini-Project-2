import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class format {

	/**
	 * intersect_list takes A list of list of strings and takes the
	 *  set operator intersect among all the lists with the array.
	 */
	public static List<String> intersect_list(List<List<String>> ABC) {
		// TODO Auto-generated method stub
		List<String> Intersect = ABC.get(0);
		//do the formating
		for(List<String> Part : ABC){
			Intersect.retainAll(Part);
		}//List A & List B & ...etc
		return Intersect;
	}
	/*
	 * format string takes a string command and breaks it up to a
	 * list of individual queries to make multi query requests easier by going 
	 * through each individual list of queries broken up by keywords:
	 * [word]
	 * [price, </>, int]
	 * [since/until , date] 
	 */
	public static List<List<String>> format_string(String command) {
		//init the list containg lists of apropriate ads, to take the intersect
		List<List<String>> ListsOfLists =new ArrayList<List<String>>();
		//String  simon = "camera since    2013/03/10     until 2013/03/13     price < 40 price > 20 ";
		String query_strings[] = command.trim().split("\\s+");

		//this list will hold the information that we need
		List<String> query_strings_array = new ArrayList<String>(Arrays.asList(query_strings)); 
		
		//loop through all elements checking for keywords
		for(int i = 0;i<query_strings_array.size();i++){
			List<String> temp = new ArrayList<String>();
			String temp_word = query_strings_array.get(i);
			//check for since and until to know if its a date query
			if(temp_word.equals("since")||temp_word.equals("until")){
				//exception handling
				if ((i + 1) == query_strings_array.size()){
					System.out.println("Error: Invalid date format. Please format date as: yyyy/mm/dd and try again, thanks!");
					ListsOfLists.clear();
					return ListsOfLists;}
				//more exception handling
				if (query_strings_array.get(i + 1).length() != 10){
					System.out.println("Error: Invalid date format. Please format date as: yyyy/mm/dd and try again, thanks!");
					ListsOfLists.clear();
					return ListsOfLists;
				}
				//add to temp
				temp.add(temp_word);
				i++;
				//add from our string array
				temp.add(query_strings_array.get(i));
			}
			//now check for the price query
			//for condition price<? or price>?
			else if (temp_word.startsWith("price")){
				if(temp_word.equals("price")){
					//some exception handling for bad queries
					if ((i + 1) == query_strings_array.size() || (i + 2) == query_strings_array.size()){
						System.out.println("Error: Invalid price format. Please format price as: 'price </> number' and try again, thanks!");
						ListsOfLists.clear();
						return ListsOfLists;}
					//exception handling for floats in price
					String int_check = query_strings_array.get(i + 2);
					try{
						Double.parseDouble(int_check);}
					catch(NumberFormatException nfe)
					{System.out.println("Error: Invalid price format. Please format price as: 'price </> number' and try again, thanks!");
					ListsOfLists.clear();
					return ListsOfLists;}

					
					//add the temp_word
					temp.add(temp_word);
					i++;
					//check for the > or <
					if ((query_strings_array.get(i).startsWith("<")||query_strings_array.get(i).startsWith(">"))&&query_strings_array.get(i).length() > 1){
						temp.add(query_strings_array.get(i).substring(0,1));
						temp.add(query_strings_array.get(i).substring(1));
					}
					else{
					temp.add(query_strings_array.get(i));
					i++;
					temp.add(query_strings_array.get(i));
					}
				}
				
				//for condition price< ? or price> ?
				else if (temp_word.equals("price<")||temp_word.equals("price>")){
					temp.add("price");
					temp.add(temp_word.substring(temp_word.length()-1));
					i++;
					temp.add(query_strings_array.get(i));
				}//for condition price<? or price>?
				else if (temp_word.contains("price<")||temp_word.contains("price>")){
					temp.add("price");
					temp.add(temp_word.substring(5,6));
					temp.add(temp_word.substring(6));
				}
			}
			else{
				temp.add(temp_word);
			}
			ListsOfLists.add(temp);
		}
		return (ListsOfLists);
	}
}

