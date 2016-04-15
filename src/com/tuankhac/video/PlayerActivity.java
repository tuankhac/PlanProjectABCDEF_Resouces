package com.tuankhac.video;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.tuankhac.activity.R;
import com.tuankhac.videotrimmer.VideoPlayerState;
import com.tuankhac.videotrimmer.VideoTrimmingService;
import com.tuankhac.videotrimmer.utils.FileUtils;
import com.tuankhac.videotrimmer.utils.TimeUtils;

public class PlayerActivity extends Activity {

	protected final int LOADING_DIALOG = 1;
	protected final int MESSAGE_DIALOG = 2;
	protected final int VALIDATION_DIALOG = 3;

	// initial all element of layout
	private VideoView videoView;
	private MediaController mediaController;
	private LinearLayout layoutVolum;
	private LinearLayout layoutBrightness;
	private TextView tvVolum;
	private TextView tvBrightness;
	private AudioManager mAudioManager;
	private GestureDetector mGestureDetector;
	private ImageView imgScreen;
	private ImageView btn_trim_start, btn_start, btn_end, btn_reset, btn_trim;
	private TextView tv_show_time;

	private int mVolume = -1;
	private float mBrightness = -1f;
	private int mMaxVolume;
	private String path;
	private int position;
	
	// initial for class to trim video
	private VideoPlayerState videoPlayerState = new VideoPlayerState();
	protected Handler completionHander = new Handler() {
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			Log.i("VideoView", "Recieved message");
			videoPlayerState.setMessageText(msg.getData().getString("text"));
			removeDialog(LOADING_DIALOG);
			showDialog(MESSAGE_DIALOG);
			stopService(videoTrimmingServiceIntent());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_activity_player);

		// init some element for trim
		@SuppressWarnings("deprecation")
		Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
		Object lastState = lastNonConfigurationInstance;
		if (lastState != null) {
			videoPlayerState = (VideoPlayerState) lastState;
		} else {
			Bundle extras = getIntent().getExtras();
			videoPlayerState.setFilename(extras.getString("videofilename"));
		}

		initView();
		path = getIntent().getStringExtra("URL");
		position = getIntent().getIntExtra("POSITION", 0);

		mediaController.setMediaPlayer(videoView);
		videoView.setMediaController(mediaController);
		videoView.setVideoPath(path);

		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
			}
		});
		videoView.requestFocus();
		videoView.seekTo(position);
		videoView.start();

		imgScreen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PlayerActivity.this,
						PopupPlayer.class);
				intent.putExtra("URL", path);
				int position = (int) videoView.getCurrentPosition();
				intent.putExtra("POSITION", position);
				intent.putExtra("NAME", getIntent().getStringExtra("NAME"));

				startService(intent);
				finish();
				// MainActivity.mainActivity.finish();
			}
		});
		btn_trim_start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btn_start.setVisibility(View.VISIBLE);
				btn_end.setVisibility(View.VISIBLE);
				btn_reset.setVisibility(View.VISIBLE);
				btn_trim.setVisibility(View.VISIBLE);
				tv_show_time.setVisibility(View.VISIBLE);
			}
		});

		btn_start.setOnClickListener(startClickListener());
		btn_end.setOnClickListener(stopClickListener());
		btn_reset.setOnClickListener(resetClickListener());
		btn_trim.setOnClickListener(trimClickListener());
		refreshDetailView();
	}

	private void initView() {
		stopService(new Intent(PlayerActivity.this, PopupPlayer.class));
		videoView = (VideoView) findViewById(R.id.myvideoview);
		mediaController = new MediaController(PlayerActivity.this);
		layoutBrightness = (LinearLayout) findViewById(R.id.layout_brightnes);
		layoutVolum = (LinearLayout) findViewById(R.id.layout_volum);
		tvVolum = (TextView) findViewById(R.id.tv_volum);
		tvBrightness = (TextView) findViewById(R.id.tv_brightness);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mGestureDetector = new GestureDetector(PlayerActivity.this,
				new MyGestureListener());
		imgScreen = (ImageView) findViewById(R.id.img_reduceScreen);
		btn_trim_start = (ImageView) this.findViewById(R.id.img_trim_start);
		btn_start = (ImageView) this.findViewById(R.id.start);
		btn_end = (ImageView) this.findViewById(R.id.end);
		btn_reset = (ImageView) this.findViewById(R.id.reset);
		btn_trim = (ImageView) this.findViewById(R.id.trim);
		tv_show_time = (TextView) findViewById(R.id.tv_show_time);

		inVisibleView();

	}

	private void inVisibleView() {
		imgScreen.postDelayed(new Runnable() {
			@Override
			public void run() {
				imgScreen.setVisibility(View.INVISIBLE);
				btn_trim_start.setVisibility(View.INVISIBLE);
				layoutBrightness.setVisibility(View.INVISIBLE);
				layoutVolum.setVisibility(View.INVISIBLE);
			}
		}, 2000);
		btn_start.postDelayed(new Runnable() {
			@Override
			public void run() {
					btn_start.setVisibility(View.INVISIBLE);
					btn_end.setVisibility(View.INVISIBLE);
					btn_reset.setVisibility(View.INVISIBLE);
					btn_trim.setVisibility(View.INVISIBLE);
					tv_show_time.setVisibility(View.INVISIBLE);
			}
		}, 20000);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		imgScreen.setVisibility(View.VISIBLE);
		btn_trim_start.setVisibility(View.VISIBLE);
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		}

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			layoutBrightness.setVisibility(View.GONE);
			layoutVolum.setVisibility(View.GONE);
			inVisibleView();
			endGesture();
			break;
		case MotionEvent.ACTION_DOWN:
			layoutBrightness.setVisibility(View.GONE);
			layoutVolum.setVisibility(View.GONE);
			inVisibleView();
			endGesture();
			break;
		}

		return super.onTouchEvent(event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		videoView.seekTo(videoPlayerState.getCurrentTime());
	}

	@Override
	protected void onPause() {
		super.onPause();
		videoPlayerState.setCurrentTime(videoView.getCurrentPosition());
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Log.i("VideoView", "In on retain");
		return videoPlayerState;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		Log.i("VideoView", "In on prepare dialog");

		switch (id) {
		case MESSAGE_DIALOG:
			((AlertDialog) dialog)
					.setMessage(videoPlayerState.getMessageText());
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;

		switch (id) {
		case VALIDATION_DIALOG:
			dialog = simpleAlertDialog("Invalid video timings selected for trimming. Please make sure your start time is less than the stop time.");
			break;
		case LOADING_DIALOG:
			dialog = ProgressDialog.show(PlayerActivity.this, "",
					"Trimming...", true, true);
			break;
		case MESSAGE_DIALOG:
			dialog = simpleAlertDialog("");
			break;
		default:
			dialog = null;
		}

		return dialog;
	}

	private OnClickListener trimClickListener() {
		return new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				videoView.pause();

				if (!videoPlayerState.isValid()) {
					showDialog(VALIDATION_DIALOG);
					return;
				}
				Intent intent = videoTrimmingServiceIntent();

				String inputFileName = path;
				intent.putExtra("inputFileName", inputFileName);
				intent.putExtra("outputFileName",
						FileUtils.getTargetFileName(inputFileName));
				intent.putExtra("start", videoPlayerState.getStart() / 1000);
				intent.putExtra("duration",
						videoPlayerState.getDuration() / 1000);

				intent.putExtra("messenger", new Messenger(completionHander));
				startService(intent);
				showDialog(LOADING_DIALOG);
			}
		};
	}

	private Intent videoTrimmingServiceIntent() {
		return new Intent(PlayerActivity.this, VideoTrimmingService.class);
	}

	private OnClickListener resetClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				videoPlayerState.reset();
				refreshDetailView();
			}
		};
	}

	private OnClickListener stopClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int stop = videoView.getCurrentPosition();
				videoPlayerState.setStop(stop);
				refreshDetailView();
			}
		};
	}

	private OnClickListener startClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				videoPlayerState.setStart(videoView.getCurrentPosition());
				refreshDetailView();
			}
		};
	}

	private Dialog simpleAlertDialog(String message) {
		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setCancelable(true)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int id) {
						PlayerActivity.this.removeDialog(MESSAGE_DIALOG);
						PlayerActivity.this.removeDialog(LOADING_DIALOG);
					}
				});
		dialog = builder.create();
		return dialog;
	}

	private void refreshDetailView() {
		String start = TimeUtils.toFormattedTime(videoPlayerState.getStart());
		String stop = TimeUtils.toFormattedTime(videoPlayerState.getStop());
		tv_show_time.setText("From " + start + "\nto " + stop);
	}

	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;

	}

	private class MyGestureListener extends
			GestureDetector.SimpleOnGestureListener {

		/**  */
		@SuppressWarnings("deprecation")
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			imgScreen.setVisibility(View.VISIBLE);

			float change = e2.getY() - e1.getY();
			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();

			if (e1.getX() > windowWidth / 2)
				onVolumeSlide(change / windowHeight);
			if (e1.getX() < windowWidth / 2)
				onBrightnessSlide(change / windowHeight);

			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	private void onBrightnessSlide(float percent) {
		layoutBrightness.setVisibility(View.VISIBLE);
		if (mBrightness < 0) {
			mBrightness = getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;

		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness - percent;
		if (lpa.screenBrightness <= 1.0f && lpa.screenBrightness >= 0.01f) {
			tvBrightness.setText((int) Math.abs(lpa.screenBrightness * 100)
					+ "");
		}
		if (lpa.screenBrightness > 1.0f) {
			lpa.screenBrightness = 1.0f;
			tvBrightness.setText("100");
		}

		else if (lpa.screenBrightness < 0.01f) {
			lpa.screenBrightness = 0.01f;
			tvBrightness.setText("1");
		}

		getWindow().setAttributes(lpa);

	}

	private void onVolumeSlide(float percent) {
		layoutVolum.setVisibility(View.VISIBLE);
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;
		}

		int index = -(int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume) {
			index = mMaxVolume;
		} else if (index < 0) {
			index = 0;
		}
		tvVolum.setText(index + "");

		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

	}

}
