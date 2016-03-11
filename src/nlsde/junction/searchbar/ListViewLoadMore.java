/**
 * ListViewLoadMore.java
 * 
 * @Description: 
 * 
 * @File: ListViewLoadMore.java
 * 
 * @Package nlsde.junction.searchbar
 * 
 * @Author chaos
 * 
 * @Date 2014-12-4下午7:03:32
 * 
 * @Version V1.0
 */
package nlsde.junction.searchbar;

import nlsde.junction.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;



/**
 * @author Administrator
 *
 */
public class ListViewLoadMore extends ListView implements OnScrollListener {

		View footView;
		int lastItem; // 最后一项
		int totalItemCount; // 此刻一共有多少项
		boolean isLoading=false;
		IsLoadingListener isLoadingListener;

		public ListViewLoadMore(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			initView(context);
		}

		public ListViewLoadMore(Context context, AttributeSet attrs) {
			super(context, attrs);
			initView(context);
		}

		public ListViewLoadMore(Context context) {
			super(context);
			initView(context);
		}

		/**
		 * 初始化footView
		 * 
		 * @param context
		 */
		void initView(Context context) {
			LayoutInflater inflater = LayoutInflater.from(context);
			footView = inflater.inflate(R.layout.foot_layout, null);
			addFooterView(footView);
			footView.findViewById(R.id.foot_layout).setVisibility(View.GONE);
			setOnScrollListener(this);
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			if (lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
				if (!isLoading) {
					isLoading=true;
					footView.findViewById(R.id.foot_layout).setVisibility(View.VISIBLE);
					isLoadingListener.onLoad();
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			lastItem = firstVisibleItem + visibleItemCount;
			this.totalItemCount = totalItemCount;
		}
		
		public void setOnLoadingListener(IsLoadingListener isLoadingListener){
			this.isLoadingListener=isLoadingListener;
		}
		
		public interface IsLoadingListener{
			public void onLoad();
		}
		
		public void complateLoad(){
			isLoading=false;
			footView.findViewById(R.id.foot_layout).setVisibility(View.GONE);
		}

	}
