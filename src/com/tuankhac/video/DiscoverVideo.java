package com.tuankhac.video;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;

import com.library.object.SweetAlertDialog;
import com.tuankhac.activity.VideoMainActivity;
import com.tuankhac.object.VideoInfo;

public class DiscoverVideo extends
		AsyncTask<File, String, ArrayList<VideoInfo>> {
	private VideoMainActivity activity;

	public DiscoverVideo(VideoMainActivity activity) {
		this.activity = activity;
		//this.activity.startService(new Intent(activity,ServiceGetFile.class));
	}

	SweetAlertDialog loading;

	private ArrayList<File> filelist = new ArrayList<File>();

	@Override
	protected void onPreExecute() {
		loading = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
		loading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
		loading.setTitleText("Loading");
		loading.setCancelable(false);
		loading.show();
	}

	private ArrayList<File> getAllFile(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory() && !file.isHidden()) {
				getAllFile(file);
			} else if (file.getName().endsWith(".mp4")
					|| file.getName().endsWith(".wmv")
					|| file.getName().endsWith(".3gp"))

				filelist.add(file);
			if (isCancelled())
				break;
		}
		return filelist;
	}

	@Override
	protected ArrayList<VideoInfo> doInBackground(File... params) {
		ArrayList<VideoInfo> arrVideo = new ArrayList<VideoInfo>();
		getAllFile(params[0]);
		
		//filelist = ServiceGetFile.filelist;
		
		VideoInfo temp = null;
		for (File file : filelist) {
			temp = new VideoInfo();
			temp.setPath(file.getPath());
			temp.setName(file.getName());
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			try {
				retriever.setDataSource(file.getPath());
			} catch (IllegalArgumentException | IllegalStateException e) {
				e.printStackTrace();
			}
			String tmp = retriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			long duration = Integer.parseInt((tmp != null) ? tmp : "0");
			temp.setDuration(String.format(
					"%d:%d",
					TimeUnit.MILLISECONDS.toMinutes(duration),
					TimeUnit.MILLISECONDS.toSeconds(duration)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes(duration))));

			long size = file.length();
			DecimalFormat decimalFormat = new DecimalFormat();
			temp.setSize(decimalFormat.format(size * 1.0 / (1024 * 1024))
					+ "Mb");

			String time = retriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
			temp.setTime(time);

			String type = retriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
			temp.setType(type);

			String width = retriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
			String height = retriever
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
			temp.setResolution(width + "x" + height);

			Bitmap bitmap = retriever.getFrameAtTime(-1L);
			temp.setThumbnail(ThumbnailUtils.extractThumbnail(bitmap, 200, 200));
			arrVideo.add(temp);
			retriever.release();
		}

		return arrVideo;
	}

	@Override
	protected void onPostExecute(ArrayList<VideoInfo> videoInfos) {
		VideoMainActivity.videoInfos.clear();
		VideoMainActivity.videoInfos.addAll(videoInfos);
		VideoMainActivity.gridAdapter.notifyDataSetChanged();
		loading.cancel();
	}
}
