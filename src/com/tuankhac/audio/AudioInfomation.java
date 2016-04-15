package com.tuankhac.audio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.tuankhac.activity.R;

public class AudioInfomation extends Activity {
	TextView audio_name, audio_size, audio_path, audio_duration,
			audio_create_date;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.audio_infomation);
		audio_name = (TextView) findViewById(R.id.audio_name);
		audio_size = (TextView) findViewById(R.id.audio_size);
		audio_path = (TextView) findViewById(R.id.audio_path);
		audio_duration = (TextView) findViewById(R.id.audio_duration);
		audio_create_date = (TextView) findViewById(R.id.audio_create_date);
		
		Intent i = getIntent();
		Bundle b = i.getBundleExtra("INFO");
		AudioDataModel audioModel = (AudioDataModel) b
				.getSerializable("AudioModel");
		audio_name.setText("Name:  " + audioModel.getFileName());
		audio_size.setText("Size:  " + audioModel.getFileSize());
		audio_path.setText("Path:  " + audioModel.getFilePath());
		audio_duration.setText("Duration: " + audioModel.getFileDuration());
		if(!audioModel.getCreateDate().equals(""))
		audio_create_date
				.setText("Create date:  " + audioModel.getCreateDate());
	}
}
