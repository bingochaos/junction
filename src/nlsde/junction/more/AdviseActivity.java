package nlsde.junction.more;

import nlsde.junction.R;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class AdviseActivity extends Activity implements TopBarClickListener{


	private TopBar topBar;
	private EditText editText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adviseactivity);
		topBar=(TopBar)findViewById(R.id.topbar_advise);
		topBar.setLeftButton(R.drawable.fanhui);
		topBar.setTopBarClickListener(this);
		editText=(EditText)findViewById(R.id.advise);
	}
	/* (non-Javadoc)
	 * @see nlsde.junction.topbar.TopBarClickListener#leftBtnClick()
	 */
	@Override
	public void leftBtnClick() {
		topBar.setLeftButton(R.drawable.fanhui_click);
		this.finish();
		
	}
	/* (non-Javadoc)
	 * @see nlsde.junction.topbar.TopBarClickListener#rightBtnClick()
	 */
	@Override
	public void rightBtnClick() {
		// TODO Auto-generated method stub
		
	}

	
}
