package com.tuankhac.audio;

import java.io.Serializable;

public class AudioDataModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private String fileName = "";
	private String fileSize = "";
	private String filePath = "";
	private int fileId = 0;
	private String fileDuration = "";
	private boolean selected = false;
	private String create_date = "";

	public String getFileDuration() {
		return fileDuration;
	}

	public void setFileDuration(String fileDuration) {
		this.fileDuration = fileDuration;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setCreateDate(String createDate) {
		this.create_date = createDate;
	}

	public String getCreateDate() {
		return create_date;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}