package com.tuankhac.video;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuankhac.activity.R;
import com.tuankhac.object.VideoInfo;
public class GridAdapter extends ArrayAdapter<VideoInfo> {
	private Context context;
	private int resource;
	private ArrayList<VideoInfo> arrVideo;
	TextView tvSelected;
	Button btnShare;

	public GridAdapter(Context context, int resource,
			ArrayList<VideoInfo> arrVideo) {
		super(context, resource);
		this.context = context;
		this.resource = resource;
		this.arrVideo = arrVideo;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater layoutInflater = ((Activity) context)
					.getLayoutInflater();
			convertView = layoutInflater.inflate(resource, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.imgThumbnail = (ImageView) convertView
					.findViewById(R.id.thumbnail);
			viewHolder.tvDuration = (TextView) convertView
					.findViewById(R.id.tv_duration);
			viewHolder.tvName = (TextView) convertView
					.findViewById(R.id.tv_videoname);
			if (resource == R.layout.item_checkbox) {
				viewHolder.cbItem = (CheckBox) convertView
						.findViewById(R.id.cb_item);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		tvSelected = (TextView) ((Activity) context)
				.findViewById(R.id.tv_selected);
		btnShare = (Button) ((Activity) context).findViewById(R.id.btn_Share);
		
		
//		Picasso.with(context.getApplicationContext()).load(url).into(viewHolder.imgThumbnail);
		viewHolder.imgThumbnail.setImageBitmap(arrVideo.get(position)
				.getThumbnail());
		
		viewHolder.tvDuration.setText(arrVideo.get(position).getDuration()
				+ " mins");
		viewHolder.tvName.setText(arrVideo.get(position).getName());

		if (resource == R.layout.item_checkbox) {
			viewHolder.cbItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (viewHolder.cbItem.isChecked()) {
						++ShareVideoActivity.count;
					} else {
						--ShareVideoActivity.count;
					}

					if (ShareVideoActivity.count == 0) {
						btnShare.setEnabled(false);
					} else {
						btnShare.setEnabled(true);
					}
					tvSelected.setText(ShareVideoActivity.count + " selected");
				}
			});
			// Log.i("tag", " width "+convertView.getWidth());
			// Log.i("tag", " height "+convertView.getHeight());
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (viewHolder.cbItem.isChecked()) {
						viewHolder.cbItem.setChecked(false);
						--ShareVideoActivity.count;
					} else {
						viewHolder.cbItem.setChecked(true);
						++ShareVideoActivity.count;
					}

					if (ShareVideoActivity.count == 0) {
						btnShare.setEnabled(false);
					} else {
						btnShare.setEnabled(true);
					}
					tvSelected.setText(ShareVideoActivity.count + " selected");
				}
			});
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return arrVideo.size();
	}

	static class ViewHolder {
		ImageView imgThumbnail;
		TextView tvName;
		TextView tvDuration;
		CheckBox cbItem;
	}
}
