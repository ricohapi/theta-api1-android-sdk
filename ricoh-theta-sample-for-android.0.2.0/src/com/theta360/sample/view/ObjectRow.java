package com.theta360.sample.view;

import android.view.View.OnClickListener;

public class ObjectRow {
	private int objectHandle;
	private boolean isPhoto;
	private byte[] thumbnail;
	private String fileName;
	private String captureDate;
	private OnClickListener onClickListener;

	public int getObjectHandle() {
		return objectHandle;
	}

	public void setObjectHandle(int objectHandle) {
		this.objectHandle = objectHandle;
	}

	public boolean isPhoto() {
		return isPhoto;
	}

	public void setIsPhoto(boolean isPhoto) {
		this.isPhoto = isPhoto;
	}

	public byte[] getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCaptureDate() {
		return captureDate;
	}

	public void setCaptureDate(String captureDate) {
		this.captureDate = captureDate;
	}

	public OnClickListener getOnClickListener() {
		return onClickListener;
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

}
