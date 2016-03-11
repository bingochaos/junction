/**
 * SearchSuggestionSampleProvider.java
 * 
 * @Description: 
 * 
 * @File: SearchSuggestionSampleProvider.java
 * 
 * @Package nlsde.junction.serachbar
 * 
 * @Author chaos
 * 
 * @Date 2014-10-21下午12:22:22
 * 
 * @Version V1.0
 */
package nlsde.junction.searchbar;

import android.content.SearchRecentSuggestionsProvider;

/**
 * @author chaos
 *
 */
public class SearchSuggestionSampleProvider extends  SearchRecentSuggestionsProvider {  
	  
	final static String AUTHORITY="SuggestionProvider";  
	final static int MODE=DATABASE_MODE_QUERIES;  

	public SearchSuggestionSampleProvider(){  
		super();  
		setupSuggestions(AUTHORITY, MODE);  
}  
}  
