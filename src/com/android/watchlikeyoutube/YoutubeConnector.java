package com.android.watchlikeyoutube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.tuankhac.activity.R;
import com.android.watchlikeyoutube.VideoItem;

public class YoutubeConnector {
	private YouTube youtube;
	private YouTube.Search.List query;

	// Your developer key goes here
	public static final String KEY = "AIzaSyBEi8abO73H6elO3WnlOabtyCcJ9-UUN8M";
	
	public YoutubeConnector(Context context) {
		youtube = new YouTube.Builder(new NetHttpTransport(),
				new JacksonFactory(), new HttpRequestInitializer() {
					@Override
					public void initialize(HttpRequest hr) throws IOException {
					}
				}).setApplicationName(context.getString(R.string.app_name))
				.build();

		try {
			query = youtube.search().list("id,snippet");
			query.setKey(KEY);
			query.setType("video");
			query.setMaxResults((long) 50);
			query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");

		} catch (IOException e) {
			Log.d("YC", "Could not initialize: " + e);
		}
	}

	public List<VideoItem> search(String keywords) {
		query.setQ(keywords);
		try {
			SearchListResponse response = query.execute();
			List<SearchResult> results = response.getItems();

			List<VideoItem> items = new ArrayList<VideoItem>();
			for (SearchResult result : results) {
				VideoItem item = new VideoItem();
				item.setTitle(result.getSnippet().getTitle());
				item.setDescription(result.getSnippet().getDescription());
				item.setThumbnailURL(result.getSnippet().getThumbnails()
						.getDefault().getUrl());
				item.setId(result.getId().getVideoId());
				items.add(item);
			}
			return items;
		} catch (IOException e) {
			Log.d("YC", "Could not search: " + e);
			return null;
		}
	}
}
