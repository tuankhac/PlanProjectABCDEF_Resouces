package com.tuankhac.activity;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.tuankhac.audio.AudioDataModel;
import com.tuankhac.audio.AudioListAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AudioMainActivity extends Activity implements OnErrorListener,
		OnCompletionListener, Runnable {

	/**
	 * ListView declaration
	 */
	public static ListView lvAudioFileList;

	public static ArrayList<AudioDataModel> audioFileList;

	/**
	 * Adapter declaration for Audio files
	 */
	public static AudioListAdapter audioListAdapter;
	public static boolean isPlayAudio = false;
	public static boolean isSearching = false;

	/**
	 * Constants
	 */
	private final String BROADCAST_CURRENT_POSITION = "PLAY";

	/**
	 * TextView declaration
	 */
	private TextView tvTimeReached, tvTimeRemaining;

	/**
	 * SeekBar declaration
	 */
	private SeekBar sbPlayProgress;

	/**
	 * MediaPlayer object
	 */
	private MediaPlayer mp = new MediaPlayer();

	private int checkPosition = -1;

	// Thread thread = new Thread(this);

	public static Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio_activity_main);
		ctx = this;

		// View initialization
		lvAudioFileList = (ListView) findViewById(R.id.activity_audio_file_list_lv);
		sbPlayProgress = (SeekBar) findViewById(R.id.activity_audio_file_list_pb);
		tvTimeReached = (TextView) findViewById(R.id.activity_audio_file_list_tv_progress);
		tvTimeRemaining = (TextView) findViewById(R.id.activity_audio_file_list_tv_remaining);

		/**
		 * Check for sdcard status
		 */
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			audioFileList = new ArrayList<AudioDataModel>();
			setAdapterForAudios();

		} else {
			Toast.makeText(AudioMainActivity.this, "No sd card found.",
					Toast.LENGTH_LONG).show();
		}

		sbPlayProgress
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						if (!mp.isPlaying())
							seekBar.setProgress(mp.getCurrentPosition());
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {

						if (fromUser && mp.isPlaying()) {
							mp.seekTo(progress);
						}

					}
				});
	}

	/**
	 * Set adapter for audio file in list and play
	 */
	private ArrayList<File> findSongs(File root) {
		ArrayList<File> allSongs = new ArrayList<File>();
		File[] files = root.listFiles();
		for (File singleFile : files) {
			if (singleFile.isDirectory() && !singleFile.isHidden()) {
				allSongs.addAll(findSongs(singleFile));
			} else {
				if (singleFile.getName().endsWith(".mp3")
						|| singleFile.getName().endsWith(".mp4"))
					allSongs.add(singleFile);
			}
		}
		return allSongs;
	}

	private void setAdapterForAudios() {

		AudioDataModel audioFileModel;
		ArrayList<File> mySongs = findSongs(Environment
				.getExternalStorageDirectory());
		for (int i = 0; i < mySongs.size(); i++) {
			audioFileModel = new AudioDataModel();
			MediaPlayer m = new MediaPlayer();
			try {
				m.setDataSource(mySongs.get(i).getPath());
			} catch (IllegalArgumentException | SecurityException
					| IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			MediaMetadataRetriever retrive = new MediaMetadataRetriever();
			retrive.setDataSource(mySongs.get(i).getPath());
			String mDuration = retrive
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			String duration = mediaTime(Integer.parseInt(mDuration));
			audioFileModel.setFileDuration(duration);
			String fileSize = getFileSize(String.valueOf(mySongs.get(i)
					.length()));
			audioFileModel.setFileSize(fileSize);
			audioFileModel.setFileName(mySongs.get(i).getName());
			audioFileModel.setFilePath(mySongs.get(i).getPath());
			audioFileModel.setFileId(i);
			audioFileModel.setCreateDate("");
			audioFileList.add(audioFileModel);
		}

		// if audio data found than visible the list view
		if (audioFileList.size() > 0) {

			audioListAdapter = new AudioListAdapter(AudioMainActivity.this,
					audioFileList);
			lvAudioFileList.setAdapter(audioListAdapter);

		} else {
			Toast.makeText(AudioMainActivity.this, "No audio files found.",
					Toast.LENGTH_LONG).show();
		}

		// Register receiver
		IntentFilter intentFilter = new IntentFilter(BROADCAST_CURRENT_POSITION);
		registerReceiver(mReceiver, intentFilter);
	}

	/**
	 * Convert file size for attachment from bytes to kb or mb
	 * 
	 * @param fileSize
	 *            = file size in string
	 * @return file size in kb or mb
	 */
	public static String getFileSize(String fileSize) {
		int DIVIDER = 1024;
		String result = null;
		DecimalFormat format;
		float size = Float.parseFloat(fileSize);
		if ((size / DIVIDER) > DIVIDER) {
			format = new DecimalFormat(".0");
			result = format.format(((size / DIVIDER)) / DIVIDER) + " MB";
		} else {
			format = new DecimalFormat(".00");
			result = format.format((size / DIVIDER)) + " KB";
		}
		return result;
	}

	/**
	 * BroadCastReceiver for showing progress of audio file
	 * 
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getExtras() != null) {
				int currentProgress = intent.getIntExtra("play_position", 0);
				tvTimeReached.setText(mediaTime((long) currentProgress));
				sbPlayProgress.setProgress(currentProgress);
			}

		}
	};

	/**
	 * Method for converting milliseconds to minute
	 * 
	 * @param milliSecs
	 * @return
	 */
	public static String mediaTime(long milliSecs) {
		StringBuffer sb = new StringBuffer();
		long m = milliSecs / (60 * 1000);
		sb.append(m < 10 ? "0" + m : m);
		sb.append(":");
		long s = (milliSecs % (60 * 1000)) / 1000;
		sb.append(s < 10 ? "0" + s : s);
		return sb.toString();
	}

	/**
	 * Play Audio file from list
	 * 
	 * @param filePath
	 *            = path of file
	 * @param playFile
	 *            = flag for pause and play
	 * 
	 */
	public void playAudioFile(String filePath, boolean playFile, int position) {

		mp.setOnErrorListener(this);
		mp.setOnCompletionListener(this);
		try {
			if (mp.isPlaying() && checkPosition != position) {
				mp.stop();
				mp.reset();

			} else if (mp.isPlaying() && checkPosition == position) {
				mp.pause();
			}
			if (filePath != null && playFile && checkPosition != position) {
				mp.reset();
				Uri uri = Uri.parse(filePath);
				mp.setDataSource(AudioMainActivity.this, uri);
				mp.prepare();

				// set total time to view
				tvTimeRemaining.setText(mediaTime((long) mp.getDuration()));

				// set max progress to seek bar
				sbPlayProgress.setMax(mp.getDuration());
				checkPosition = position;
				mp.start();
				// start thread for updating current progress in seek bar
				new Thread(this).start();

			} else if (filePath != null && playFile
					&& checkPosition == position) {

				mp.start();
				tvTimeRemaining.setText(mediaTime((long) mp.getDuration()));
				new Thread(this).start();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (mp.isPlaying()) {
			mp.stop();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		isPlayAudio = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		isPlayAudio = false;
		audioListAdapter.getHolder().ivFileIcon.setImageDrawable(getResources()
				.getDrawable(R.drawable.btn_stop));
		mp.stop();
		Log.d("onp", "onp");
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.media.MediaPlayer.OnErrorListener#onError(android.media.MediaPlayer,
	 *      int, int)
	 */
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {

		audioListAdapter.changeFileAfterCompletion();
		Toast.makeText(AudioMainActivity.this, "File not supported.",
				Toast.LENGTH_LONG).show();
		return false;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.media.MediaPlayer.OnCompletionListener#onCompletion(android.media.MediaPlayer)
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {

		audioListAdapter.changeFileAfterCompletion();
		tvTimeRemaining.setText("00:00");
		tvTimeReached.setText("00:00");
		AudioListAdapter.mPosition = (AudioListAdapter.mPosition + 1)
				% audioFileList.size();
		if (AudioListAdapter.mPosition == audioFileList.size()) {
			mp.stop();
			mp.release();
		}
		if(audioListAdapter.getRepeat()){
			AudioListAdapter.mPosition -= 1;
		}
		
		AudioListAdapter.isPlaying[AudioListAdapter.mPosition] = true;
		audioListAdapter.previousPlaying = AudioListAdapter.mPosition;
		String nexpath = audioFileList.get(AudioListAdapter.mPosition)
				.getFilePath();
		playAudioFile(nexpath, true, AudioListAdapter.mPosition);

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		int currentPosition = 0;
		int total = mp.getDuration();

		while (mp.isPlaying() && currentPosition < total) {
			currentPosition = mp.getCurrentPosition();

			// broadcast the current progress
			callBroadcast(currentPosition);
			if (isSearching) {
				mp.stop();
				AudioListAdapter.isPlaying[AudioListAdapter.mPosition] = false;
			}
		}
	}

	/**
	 * Call to BroadCast the audio files current position
	 * 
	 * @param currentPosition
	 */
	private void callBroadcast(int currentPosition) {

		Intent intent = new Intent();
		intent.putExtra("play_position", currentPosition);
		intent.setAction(BROADCAST_CURRENT_POSITION);
		sendBroadcast(intent);

	}

}
