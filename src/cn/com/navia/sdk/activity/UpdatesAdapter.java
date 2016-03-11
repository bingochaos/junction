package cn.com.navia.sdk.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import nlsde.junction.R;

import cn.com.navia.sdk.bean.RetVal_UpdateItem;
import cn.com.navia.sdk.bean.SpectrumInfo;
import cn.edu.buaa.nlsde.wlan.util.FileUtil;

public class UpdatesAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<SpectrumInfo> specs;
	private OnClick clickCallback;

	public final class ViewHolder {
		public TextView id;
		public TextView buildingId;
		public TextView name;
		public TextView version;
		public Button download;
	}

	public interface OnClick {
		void click(int position, SpectrumInfo info);
	}

	public UpdatesAdapter(List<SpectrumInfo> specs, UpdatesAdapter.OnClick onClick, Context context) {
		this.specs = specs;
		this.mInflater = LayoutInflater.from(context);
		clickCallback = onClick;
	}

	@Override
	public int getCount() {
		return specs.size();
	}

	@Override
	public Object getItem(int position) {
		return specs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_updates_item, null);
			holder.id = (TextView) convertView.findViewById(R.id.update_id);
			holder.buildingId = (TextView) convertView.findViewById(R.id.update_buildingId);
			holder.name = (TextView) convertView.findViewById(R.id.update_name);
			holder.version = (TextView) convertView.findViewById(R.id.update_version);
			holder.download = (Button) convertView.findViewById(R.id.update_download);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SpectrumInfo info = specs.get(position);
        RetVal_UpdateItem updateItem = info.getUpdateItem();

        holder.id.setText(updateItem.getId() + "");
		holder.buildingId.setText(updateItem.getBuilding_id()+"");
		holder.name.setText(updateItem.getName());
		holder.version.setText(updateItem.getVersion() + "");
		boolean exist = FileUtil.exist(info.getSpecFile());	
		holder.download.setEnabled(!exist);

		if (!exist) {
			holder.download.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (clickCallback != null) {
						getClickCallback().click(position, specs.get(position));
					}
				}
			});
		}
		return convertView;
	}

	public OnClick getClickCallback() {
		return clickCallback;
	}

}
