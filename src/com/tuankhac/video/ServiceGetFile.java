package com.tuankhac.video;


import java.io.File;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

public class ServiceGetFile extends Service {
	static ArrayList<File> filelist = new ArrayList<File>();
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
	}

	@Override
	public void onDestroy() {
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		String targetPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		File file = new File(targetPath);
		getAllFile(file);
	}
	private ArrayList<File> getAllFile(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory() && !file.isHidden()) {
				getAllFile(file);
			} else if (file.getName().endsWith(".mp4")
					|| file.getName().endsWith(".wmv")
					|| file.getName().endsWith(".3gp"))

				filelist.add(file);
		}
		return filelist;
	}
}
