package nlsde.junction.searchbar;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.junction.R;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.SearchView.OnQueryTextListener;

public class BusSearchActivity extends Activity {

	private SearchView mSearchView;
	private ListView mListView;
	private SimpleCursorAdapter mAdapter;
	private Cursor mCursor;
	private Button button;

	static final String[] PROJECTION = new String[] {
			ContactsContract.RawContacts._ID,
			ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY };
	 @Override
	protected void onPause() {
		 MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.bus_search_activity);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				BusSearchActivity.this.finish();

			}
		});
		mCursor = getContentResolver().query(RawContacts.CONTENT_URI,
				PROJECTION, null, null, null);
		mAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, mCursor,
				new String[] { RawContacts.DISPLAY_NAME_PRIMARY },
				new int[] { android.R.id.text1 }, 0);
		mListView = (ListView) findViewById(R.id.search_list_view);
		mListView.setAdapter(mAdapter);

		mSearchView = (SearchView) findViewById(R.id.show_search_view);
		mSearchView.setIconifiedByDefault(true);

		mSearchView.onActionViewExpanded();
		mSearchView.setFocusable(false);
		mSearchView.clearFocus();
		// mSearchView.setIconifiedByDefault(true);
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String queryText) {
				String selection = RawContacts.DISPLAY_NAME_PRIMARY
						+ " LIKE '%" + queryText + "%' " + " OR "
						+ RawContacts.SORT_KEY_PRIMARY + " LIKE '%" + queryText
						+ "%' ";
				// String[] selectionArg = { queryText };
				mCursor = getContentResolver().query(RawContacts.CONTENT_URI,
						PROJECTION, selection, null, null);
				mAdapter.swapCursor(mCursor);
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String queryText) {
				if (mSearchView != null) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm != null) {
						imm.hideSoftInputFromWindow(
								mSearchView.getWindowToken(), 0);
					}
					mSearchView.clearFocus();
				}
				return true;
			}
		});

	}

}
