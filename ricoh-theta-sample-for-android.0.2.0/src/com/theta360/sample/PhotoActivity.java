package com.theta360.sample;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.theta360.lib.PtpipInitiator;
import com.theta360.lib.ptpip.entity.ObjectInfo;
import com.theta360.lib.ptpip.entity.PtpObject;
import com.theta360.lib.ptpip.eventlistener.DataReceivedListener;
import com.theta360.lib.rexif.ExifReader;
import com.theta360.lib.rexif.entity.OmniInfo;
import com.theta360.sample.view.LogView;

public class PhotoActivity extends Activity {
	private static final String CAMERA_IP_ADDRESS = "CAMERA_IP_ADDRESS";
	private static final String OBJECT_HANDLE = "OBJECT_HANDLE";
	private static final int INVALID_OBJECT_HANDLE = 0x00000000;
	private static final String THUMBNAIL = "THUMBNAIL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);

		Intent intent = getIntent();
		String cameraIpAddress = intent.getStringExtra(CAMERA_IP_ADDRESS);
		int objectHandle = intent.getIntExtra(OBJECT_HANDLE, INVALID_OBJECT_HANDLE);
		byte[] byteThumbnail = intent.getByteArrayExtra(THUMBNAIL);

		ByteArrayInputStream inputStreamThumbnail = new ByteArrayInputStream(byteThumbnail);
		Drawable thumbnail = BitmapDrawable.createFromStream(inputStreamThumbnail, null);

		new LoadPhotoTask(cameraIpAddress, objectHandle, thumbnail).execute();
	}

	private class LoadPhotoTask extends AsyncTask<Void, Object, PtpObject> {
		private ImageView photoView;
		private LogView logViewer;
		private ProgressBar progressBar;
		private String cameraIpAddress;
		private int objectHandle;
		private Drawable thumbnail;

		public LoadPhotoTask(String cameraIpAddress, int objectHandle, Drawable thumbnail) {
			this.photoView = (ImageView) findViewById(R.id.photo_iamge);
			this.logViewer = (LogView) findViewById(R.id.photo_info);
			this.progressBar = (ProgressBar) findViewById(R.id.loading_photo_progress_bar);
			this.cameraIpAddress = cameraIpAddress;
			this.objectHandle = objectHandle;
			this.thumbnail = thumbnail;
		}

		@Override
		protected void onPreExecute() {
			photoView.setAlpha(0.5f);
			photoView.setImageDrawable(thumbnail);
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected PtpObject doInBackground(Void... params) {
			try {

				PtpipInitiator camera = new PtpipInitiator(cameraIpAddress);

				ObjectInfo objectInfo = camera.getObjectInfo(objectHandle);
				publishProgress("<ObjectInfo: object_handle=" + objectHandle + ", protection_status=" + objectInfo.getProtectionStatus() + ", filename=" + objectInfo.getFilename() + ", capture_date=" + objectInfo.getCaptureDate()
						+ ", object_compressed_size=" + objectInfo.getObjectCompressedSize() + ", thumb_compresses_size=" + objectInfo.getThumbCompressedSize() + ", thumb_pix_width=" + objectInfo.getThumbPixWidth() + ", thumb_pix_height="
						+ objectInfo.getThumbPixHeight() + ", image_pix_width=" + objectInfo.getImagePixWidth() + ", image_pix_height=" + objectInfo.getImagePixHeight() + ", object_format=" + objectInfo.getObjectFormat() + ", parent_object="
						+ objectInfo.getParentObject() + ", association_type=" + objectInfo.getAssociationType() + ">");

				int resizedWidth = 2048;
				int resizedHeight = 1024;
				PtpObject resizedImageObject = camera.getResizedImageObject(objectHandle, resizedWidth, resizedHeight, new DataReceivedListener() {
					@Override
					public void onDataPacketReceived(int progress) {
						publishProgress(progress);
					}
				});
				return resizedImageObject;

			} catch (Throwable throwable) {
				String errorLog = Log.getStackTraceString(throwable);
				publishProgress(errorLog);
				return null;
			}
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			for (Object param : values) {
				if (param instanceof Integer) {
					progressBar.setProgress((Integer) param);
				} else if (param instanceof String) {
					logViewer.append((String) param);
				}
			}
		}

		@Override
		protected void onPostExecute(PtpObject resizedImageObject) {
			if (resizedImageObject != null) {

				byte[] dataObject = resizedImageObject.getDataObject();
				ByteArrayInputStream inputStreamPhoto = new ByteArrayInputStream(dataObject);
				Drawable photo = BitmapDrawable.createFromStream(inputStreamPhoto, null);

				photoView.setAlpha(1f);
				photoView.setImageDrawable(photo);
				progressBar.setVisibility(View.GONE);

				FileOutputStream output;
				try {
					String tmpFileForReadingExif = "tmpFileForReadingExif";
					output = openFileOutput(tmpFileForReadingExif, MODE_PRIVATE);
					output.write(dataObject);
					output.flush();
					output.close();

					File tmpFile = getFileStreamPath(tmpFileForReadingExif);
					ExifReader exif;
					exif = new ExifReader(tmpFile);

					OmniInfo omniInfo = exif.getOmniInfo();
					Double yaw = omniInfo.getOrientationAngle();
					Double pitch = omniInfo.getElevationAngle();
					Double roll = omniInfo.getHorizontalAngle();
					logViewer.append("<Angle: yaw=" + yaw + ", pitch=" + pitch + ", roll=" + roll + ">");

				} catch (Throwable throwable) {
					String errorLog = Log.getStackTraceString(throwable);
					logViewer.append(errorLog);
				}
			}
		}
	}

	public static void startActivity(Activity activity, String cameraIpAddress, int objectHandle, byte[] thumbnail) {
		Intent intent = new Intent(activity, PhotoActivity.class);
		intent.putExtra(CAMERA_IP_ADDRESS, cameraIpAddress);
		intent.putExtra(OBJECT_HANDLE, objectHandle);
		intent.putExtra(THUMBNAIL, thumbnail);
		activity.startActivity(intent);
	}
}
