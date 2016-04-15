package com.tuankhac.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.tuankhac.audio.AudioListAdapter;
import com.tuankhac.audio.ShareAudioActivity;
import com.tuankhac.video.GridAdapter;
import com.tuankhac.video.ShareVideoActivity;

@SuppressLint("DefaultLocale")
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnQueryTextListener {

	public static String currentTab = "";
	TabHost tabHost;
	ArrayList<Integer> listSearch;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		listSearch = new ArrayList<>();
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		tabHost = getTabHost();

		// Audio tab
		Intent audio = new Intent().setClass(this, AudioMainActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		final TabSpec tabSpecAudio = tabHost.newTabSpec("Audio")
				.setIndicator("Audio").setContent(audio);

		// Video tab
		Intent video = new Intent().setClass(this, VideoMainActivity.class);
		final TabSpec tabSpecVideo = tabHost.newTabSpec("Video")
				.setIndicator("Video").setContent(video);
		// add all tabs
		tabHost.addTab(tabSpecAudio);
		tabHost.addTab(tabSpecVideo);

		// set Windows tab as default (zero based)
		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				currentTab = tabId;
				if (!tabId.equals("Audio")) {
					ShareAudioActivity.count = 0;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		MenuItem menuItem = menu.findItem(R.id.action_search);
		SearchView searchView = new SearchView(getApplicationContext());
		searchView.setOnQueryTextListener((OnQueryTextListener) this);
		menuItem.setActionView(searchView);
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_share) {
			if (currentTab.equals("Video")) {
				Intent intent = new Intent(this, ShareVideoActivity.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, ShareAudioActivity.class);
				startActivity(intent);
			}
		}

		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public boolean onQueryTextChange(String newText) {
		if (currentTab.equals("Video")) {
			if (newText.isEmpty()) {
				VideoMainActivity.gridAdapter = new GridAdapter(this,
						R.layout.item, VideoMainActivity.videoInfos);
				VideoMainActivity.gridView
						.setAdapter(VideoMainActivity.gridAdapter);
			} else {
				VideoMainActivity.videoSearch.clear();
				for (int i = 0; i < VideoMainActivity.videoInfos.size(); i++) {
					if (VideoMainActivity.videoInfos.get(i).getName()
							.toLowerCase().contains(newText.toLowerCase())) {
						VideoMainActivity.videoSearch
								.add(VideoMainActivity.videoInfos.get(i));
					}
				}
				VideoMainActivity.gridAdapter = new GridAdapter(this,
						R.layout.item, VideoMainActivity.videoSearch);
				VideoMainActivity.gridView
						.setAdapter(VideoMainActivity.gridAdapter);
			}
		} else {
			if (newText.isEmpty()) {
				AudioMainActivity.audioListAdapter = new AudioListAdapter(
						AudioMainActivity.ctx, AudioMainActivity.audioFileList);
				if (AudioListAdapter.mPosition >= 0
						&& AudioMainActivity.audioFileList.size() > 0)
					AudioListAdapter.isPlaying[AudioListAdapter.mPosition] = true;

				AudioMainActivity.lvAudioFileList
						.setAdapter(AudioMainActivity.audioListAdapter);
			} else {
				AudioMainActivity.isSearching = true;
				AudioListAdapter.audioFileSearch.clear();
				for (int i = 0; i < AudioMainActivity.audioFileList.size(); i++)
					if (AudioMainActivity.audioFileList.get(i).getFileName()
							.toLowerCase().contains(newText.toLowerCase())) {
						listSearch.add(i);
						AudioListAdapter.audioFileSearch
								.add(AudioMainActivity.audioFileList.get(i));
					}
				AudioMainActivity.audioListAdapter = new AudioListAdapter(
						AudioMainActivity.ctx, AudioListAdapter.audioFileSearch);

				if (AudioListAdapter.mPosition >= 0
						&& AudioMainActivity.audioFileList.size() > 0)
					AudioListAdapter.isPlaying[AudioListAdapter.mPosition] = true;

				AudioMainActivity.lvAudioFileList
						.setAdapter(AudioMainActivity.audioListAdapter);
			}
		}
		return false;
	}

}
