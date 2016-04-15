package com.tuankhac.video;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.tuankhac.activity.R;

public class PopupPlayer extends Service {
	private WindowManager windowManager;
	private RelativeLayout popupLayout;
	private RelativeLayout layoutTop;
	private VideoView videoView;
	private ImageView imgFullScreen;
	private ImageView imgTurnOff;
	private TextView tvVideoName;
	private CheckBox btnPlay;
	private int position;

	private String url;
	private String name;

	int stopPosition = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressLint({ "InflateParams", "RtlHardcoded",
			"ClickableViewAccessibility" })
	@Override
	public void onCreate() {
		super.onCreate();
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		popupLayout = (RelativeLayout) layoutInflater.inflate(
				R.layout.video_activity_popup, null);
		initView();
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.height = 280;
		params.width = 500;
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 100;
		params.y = 100;

		windowManager.addView(popupLayout, params);
		popupLayout.setOnTouchListener(new View.OnTouchListener() {
			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					initialX = params.x;
					initialY = params.y;
					initialTouchX = event.getRawX();
					initialTouchY = event.getRawY();
					return true;
				case MotionEvent.ACTION_UP:
					layoutTop.setVisibility(View.VISIBLE);
					tvVideoName.setVisibility(View.VISIBLE);
					btnPlay.setVisibility(View.VISIBLE);
					inVisibleView();
					return true;
				case MotionEvent.ACTION_MOVE:
					params.x = initialX
							+ (int) (event.getRawX() - initialTouchX);
					params.y = initialY
							+ (int) (event.getRawY() - initialTouchY);
					windowManager.updateViewLayout(popupLayout, params);
					return true;
				}
				return false;
			}
		});
		initListener();

	}

	private void initListener() {
		imgFullScreen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PopupPlayer.this,
						PlayerActivity.class);
				intent.putExtra("URL", url);
				position = (int) videoView.getCurrentPosition();
				intent.putExtra("POSITION", position);
				intent.putExtra("NAME", name);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				stopSelf();
			}
		});

		imgTurnOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopSelf();
			}
		});

		btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnPlay.isChecked()) {
					videoView.pause();
					stopPosition = (int) videoView.getCurrentPosition();
				} else {
					videoView.seekTo(stopPosition);
					videoView.start();
				}
			}
		});

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		url = intent.getStringExtra("URL");
		name = intent.getStringExtra("NAME");
		tvVideoName.setText(name);
		position = intent.getIntExtra("POSITION", 0);
		Log.d("URL", url);
		initData();
		return START_REDELIVER_INTENT;
	}

	private void initData() {
		videoView.setVideoPath(url);
		videoView.requestFocus();
		videoView.seekTo(position);
		videoView.start();
	}

	private void initView() {
		videoView = (VideoView) popupLayout.findViewById(R.id.popup_player);
		imgFullScreen = (ImageView) popupLayout
				.findViewById(R.id.img_fullscreen);
		imgTurnOff = (ImageView) popupLayout.findViewById(R.id.img_turnoff);
		tvVideoName = (TextView) popupLayout.findViewById(R.id.tv_videoname);
		layoutTop = (RelativeLayout) popupLayout.findViewById(R.id.layout_top);
		btnPlay = (CheckBox) popupLayout.findViewById(R.id.btn_play);
		inVisibleView();
	}

	private void inVisibleView() {
		layoutTop.postDelayed(new Runnable() {
			@Override
			public void run() {
				layoutTop.setVisibility(View.INVISIBLE);
			}
		}, 3000);

		tvVideoName.postDelayed(new Runnable() {
			@Override
			public void run() {
				tvVideoName.setVisibility(View.INVISIBLE);
			}
		}, 3000);

		btnPlay.postDelayed(new Runnable() {
			@Override
			public void run() {
				btnPlay.setVisibility(View.INVISIBLE);
			}
		}, 3000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (popupLayout != null) {
			windowManager.removeView(popupLayout);
		}
	}

}
