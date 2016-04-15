package com.android.watchlikeyoutube;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;
import com.tuankhac.activity.R;

public class SearchActivity extends Activity {

	private EditText searchInput;
	private ListView videosFound;
	private Handler handler;
	private List<VideoItem> searchResults;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_youtube_activity_search);

		searchInput = (EditText) findViewById(R.id.search_input);
		videosFound = (ListView) findViewById(R.id.videos_found);

		handler = new Handler();
		addClickListener();
		searchInput
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							searchOnYoutube(v.getText().toString());
							return false;
						}
						return true;
					}
				});
	}

	private void addClickListener() {
		videosFound
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> av, View v, int pos,
							long id) {
						Intent intent = new Intent(getApplicationContext(),
								PlayerActivity.class);
						intent.putExtra("VIDEO_ID", searchResults.get(pos)
								.getId());
						// startActivity(intent);
						startActivity(YouTubeStandalonePlayer
								.createVideoIntent(SearchActivity.this,
										YoutubeConnector.KEY, searchResults
												.get(pos).getId(), 0, true,
										true));
					}

				});
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
	}

	private void searchOnYoutube(final String keywords) {
		if (isNetworkConnected()) {
			new Thread() {
				public void run() {
					YoutubeConnector yc = new YoutubeConnector(
							SearchActivity.this);
					searchResults = yc.search(keywords);
					handler.post(new Runnable() {
						public void run() {
							updateVideosFound();
						}
					});
				}
			}.start();
		} else
			Toast.makeText(SearchActivity.this, "check connection internet",
					Toast.LENGTH_SHORT).show();
	}

	private void updateVideosFound() {
		ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(
				getApplicationContext(), R.layout.video_youtube_item,
				searchResults) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							R.layout.video_youtube_item, parent, false);
				}
				ImageView thumbnail = (ImageView) convertView
						.findViewById(R.id.video_thumbnail);
				TextView title = (TextView) convertView
						.findViewById(R.id.video_title);
				TextView description = (TextView) convertView
						.findViewById(R.id.video_description);

				VideoItem searchResult = searchResults.get(position);
				String url = searchResult.getThumbnailURL();
				Picasso.with(getApplicationContext()).load(url).into(thumbnail);
				title.setText(searchResult.getTitle());
				description.setText(searchResult.getDescription());
				return convertView;
			}
		};

		videosFound.setAdapter(adapter);
	}

}
