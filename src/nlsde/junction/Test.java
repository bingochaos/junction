package nlsde.junction;

import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;

import com.etsy.android.grid.StaggeredGridView;

public class Test extends Activity {
	private StaggeredGridView mGridView;
	private final Random mRandom = new Random();
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		
		mGridView = (StaggeredGridView) findViewById(R.id.test);
		mGridView.setAdapter(new MyAdapter());
	}
	
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return DATA.length;
		}

		@Override
		public Object getItem(int position) {
			return DATA[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(Test.this);
				LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				convertView.setLayoutParams(lp);
			}
			TextView view = (TextView) convertView;
			view.setText(DATA[position]);
			view.setBackgroundColor(COLOR[position % 5]);
			view.setGravity(Gravity.BOTTOM);
			view.setTextColor(Color.WHITE);
			LayoutParams lp = (LayoutParams) view.getLayoutParams();
			lp.height = (int) (getPositionRatio(position) * 200);
			view.setLayoutParams(lp);
			return view;
		}
		
	}
	
	private static final String[] DATA = new String[] {
		"Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
        "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale",
        "Aisy Cendre", "Allgauer Emmentaler", "Alverca", "Ambert", "American Cheese",
        "Ami du Chambertin", "Anejo Enchilado", "Anneau du Vic-Bilh", "Anthoriro", "Appenzell",
        "Aragon", "Ardi Gasna", "Ardrahan", "Armenian String", "Aromes au Gene de Marc",
        "Asadero", "Asiago", "Aubisque Pyrenees", "Autun", "Avaxtskyr", "Baby Swiss",
        "Babybel", "Baguette Laonnaise", "Bakers", "Baladi", "Balaton", "Bandal", "Banon",
        "Barry's Bay Cheddar", "Basing", "Basket Cheese", "Bath Cheese", "Bavarian Bergkase",
        "Baylough", "Beaufort", "Beauvoorde"
	};
	
	private static final int[] COLOR = new int[] {
		0xff33b5e5, 0xffaa66cc, 0xff99cc00, 0xffffbb33, 0xffff4444
	};
	
	private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 the width
    }

}
