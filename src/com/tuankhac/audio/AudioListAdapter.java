package com.tuankhac.audio;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuankhac.activity.AudioMainActivity;
import com.tuankhac.activity.R;

@SuppressLint("InflateParams")
public class AudioListAdapter extends BaseAdapter {

	/**
	 * Context
	 */
	private Context mContext;

	/**
	 * LayoutInflater declaration
	 */
	private LayoutInflater inflater = null;

	/**
	 * Holder class declaration
	 */
	private Holder holder = null;
	private boolean isRepeat = false;
	private boolean isFirstTime;
	private ImageView ivPlayingView = null;

	/**
	 * ArrayList to store selected files
	 */
	private ArrayList<String> selectedFileList;
	private ArrayList<AudioDataModel> audioFileList;
	private boolean[] isCheckedBox = new boolean[AudioMainActivity.audioFileList
			.size()];

	/**
	 * Variable declaration
	 */
	public static boolean[] isPlaying;

	public static ArrayList<AudioDataModel> audioFileSearch = new ArrayList<>();

	public static int mPosition = -1;
	public int previousPlaying = -1;
	

	/**
	 * Constructor for calling this Adapter
	 * 
	 * @param context
	 * @param audioFileList
	 */
	public AudioListAdapter(Context context,
			ArrayList<AudioDataModel> audioFileList) {
		this.mContext = context;
		this.audioFileList = audioFileList;
		selectedFileList = new ArrayList<String>();
		isFirstTime = true;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		isPlaying = new boolean[audioFileList.size()];
		for (int i = 0; i < isPlaying.length; i++) {
			isPlaying[i] = false;
			isCheckedBox[i] = false;
		}
	}

	@Override
	public int getCount() {
		return audioFileList.size();
	}

	@Override
	public Object getItem(int position) {
		return audioFileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public ArrayList<String> getSelectedFileList() {
		return selectedFileList;
	}

	public Holder getHolder() {
		return holder;
	}
	public boolean getRepeat(){
		return isRepeat;
	}
	private void unLockAction(boolean value) {
		getHolder().btn_show_info.setEnabled(value);
		getHolder().btn_repeat.setEnabled(value);
		getHolder().ivFileIcon.setEnabled(value);
		getHolder().checkBox.setEnabled(value);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			// Initialize views
			holder = new Holder();
			convertView = inflater.inflate(R.layout.raw_audio_list_items, null);
			holder.tvFileName = (TextView) convertView
					.findViewById(R.id.row_audio_list_item_tv_file_name);
			holder.tvFileSize = (TextView) convertView
					.findViewById(R.id.row_audio_list_item_tv_file_size);
			holder.ivFileIcon = (ImageView) convertView
					.findViewById(R.id.row_audio_list_item_iv_img);
			holder.checkBox = (CheckBox) convertView
					.findViewById(R.id.row_audio_list_item_cbox_select);
			holder.btn_show_info = (ImageView) convertView
					.findViewById(R.id.btn_show_info);
			holder.btn_repeat = (ImageView) convertView
					.findViewById(R.id.btn_repeat);

			if (AudioMainActivity.isPlayAudio)
				unLockAction(true);
			else
				unLockAction(false);

			convertView.setTag(holder);
			holder.checkBox.setTag(position);

		} else {

			holder = (Holder) convertView.getTag();
			((Holder) convertView.getTag()).checkBox.setTag(position);

		}

		// get the map object from list
		AudioDataModel audioFileModel = audioFileList.get(position);

		// set data to view
		holder.tvFileName.setText(audioFileModel.getFileName());
		holder.tvFileSize.setText(audioFileModel.getFileSize());

		if (!isPlaying[position] && mPosition != position) {
			holder.ivFileIcon.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.btn_play));
		} else if (!isPlaying[position] && mPosition == position) {
			holder.ivFileIcon.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.btn_stop));
		}
		if (isPlaying[position]) {
			holder.ivFileIcon.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.btn_pause));
		}

		if (Boolean.valueOf(audioFileModel.isSelected())) {
			holder.checkBox.setChecked(true);
		} else {
			holder.checkBox.setChecked(false);
		}
		// đăng kí context menu
		if (getHolder().btn_repeat != null)
			((Activity) mContext)
					.registerForContextMenu(getHolder().btn_repeat);
		holder.ivFileIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AudioMainActivity.isSearching = false;
				mPosition = position;
				ivPlayingView = ((ImageView) v);
				if (position != previousPlaying) {
					if (!isPlaying[position]) {
						isPlaying[position] = true;
						((ImageView) v).setImageDrawable(mContext
								.getResources().getDrawable(
										R.drawable.btn_pause));
						((AudioMainActivity) mContext).playAudioFile(
								audioFileList.get(position).getFilePath(),
								true, mPosition);
					}

					if (!isFirstTime) {
						isPlaying[previousPlaying] = false;
					} else {
						isFirstTime = false;
					}

				} else {
					if (isPlaying[position]) {
						isPlaying[position] = false;
						((ImageView) v).setImageDrawable(mContext
								.getResources()
								.getDrawable(R.drawable.btn_stop));
						((AudioMainActivity) mContext).playAudioFile("", false,
								mPosition);

					} else {
						isPlaying[position] = true;
						((ImageView) v).setImageDrawable(mContext
								.getResources().getDrawable(
										R.drawable.btn_pause));
						((AudioMainActivity) mContext).playAudioFile(
								audioFileList.get(position).getFilePath(),
								true, mPosition);
					}

				}
				previousPlaying = position;
				notifyDataSetChanged();
			}
		});

		holder.checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int chkPos = (Integer) v.getTag();
				audioFileList.get(chkPos).setSelected((((CheckBox) v).isChecked()));
				if (selectedFileList.contains(String.valueOf(chkPos))) {
					selectedFileList.remove(String.valueOf(chkPos));
					isCheckedBox[position] = false;
					if (ShareAudioActivity.count < 0)
						ShareAudioActivity.count = 0;
					else
						 --ShareAudioActivity.count;
				} else {
					selectedFileList.add(String.valueOf(chkPos));
					isCheckedBox[position] = true;
					if (ShareAudioActivity.count < 0)
						ShareAudioActivity.count = 0;
					else
						 ++ShareAudioActivity.count;
				}
			}
		});
		
		holder.btn_show_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isPlaying[position] = false;
				Intent i = new Intent(mContext, AudioInfomation.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("AudioModel",
						audioFileList.get(position));
				i.putExtra("INFO", bundle);
				mContext.startActivity(i);
			}
		});

		holder.btn_repeat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isRepeat = !isRepeat;
			}
		});
		convertView.setId(audioFileModel.getFileId());

		return convertView;
	}

	public void changeFileAfterCompletion() {
		isPlaying[mPosition] = false;
		ivPlayingView.setImageDrawable(mContext.getResources().getDrawable(
				R.drawable.btn_play));
		notifyDataSetChanged();
	}

	/**
	 * Holder class
	 */
	 public class Holder {
		TextView tvFileName, tvFileSize;
		public ImageView ivFileIcon;
		public CheckBox checkBox;
		public ImageView btn_show_info, btn_repeat;

	}

}
