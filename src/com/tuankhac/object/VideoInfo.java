package com.tuankhac.object;

import android.graphics.Bitmap;

public class VideoInfo {
	private String path;
	private String type;
	private String name;
	private String time;
	private String resolution;
	private String duration;
	private String size;
	private Bitmap thumbnail;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Bitmap getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time.toString();
	}

	public void setTime(String time) {

		this.time = time;
		// Log.d("debugging   ", this.time);
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
}
