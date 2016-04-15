package com.android.watchlikeyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.tuankhac.activity.R;

public class PlayerActivity extends YouTubeBaseActivity implements
		OnInitializedListener {
	private YouTubePlayerView playerView;
	private static final int RECOVERY_DIALOG_REQUEST = 1;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.video_youtube_activity_player);
		playerView = (YouTubePlayerView) findViewById(R.id.player_view);
		playerView.initialize(YoutubeConnector.KEY, PlayerActivity.this);
	}

	@Override
	public void onInitializationFailure(Provider provider,
			YouTubeInitializationResult result) {
		Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean restored) {
		if (!restored) {
			player.cueVideo(getIntent().getStringExtra("VIDEO_ID"));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RECOVERY_DIALOG_REQUEST) {
			// Retry initialization if user performed a recovery action
			playerView.initialize(YoutubeConnector.KEY, this);
		}
	}
}
