package com.tuankhac.audio;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.tuankhac.activity.AudioMainActivity;
import com.tuankhac.activity.R;

public class ShareAudioActivity extends Activity {

	private CheckBox cbAll;
	private TextView tvSelected;
	private ListView listView;
	private Button btnCancel, btnShare;

	// temp
	AudioListAdapter gridAdapter;
	View viewTemp;
	CheckBox checkBoxTemp;
	
	public static int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio_share_activity);
		initView();
		initListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		tvSelected.setText(count + " selected");
		if (count > 0) {
			btnShare.setEnabled(true);
		} else {
			btnShare.setEnabled(false);
		}
	}

	private void initListener() {
		cbAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (cbAll.isChecked()) {
					for (int i = 0; i < listView.getChildCount(); i++) {
						viewTemp = listView.getChildAt(i);
						checkBoxTemp = (CheckBox) viewTemp
								.findViewById(R.id.row_audio_list_item_cbox_select);
						checkBoxTemp.setChecked(true);
					}
					count = listView.getChildCount();
					btnShare.setEnabled(true);
				} else {
					for (int i = 0; i < listView.getChildCount(); i++) {
						viewTemp = listView.getChildAt(i);
						checkBoxTemp = (CheckBox) viewTemp
								.findViewById(R.id.row_audio_list_item_cbox_select);
						checkBoxTemp.setChecked(false);
					}
					count = 0;
					btnShare.setEnabled(false);
				}

				tvSelected.setText(count + " selected");
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btnShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<Uri> uris = new ArrayList<Uri>();
				for (int i = 0; i < listView.getChildCount(); i++) {
					viewTemp = listView.getChildAt(i);
					checkBoxTemp = (CheckBox) viewTemp.findViewById(R.id.row_audio_list_item_cbox_select);
					if (checkBoxTemp.isChecked()) {
						uris.add(Uri.fromFile(new File(
								AudioMainActivity.audioFileList.get(i)
										.getFilePath())));
					}
				}
				Intent intentShare = new Intent();
				intentShare.setAction(Intent.ACTION_SEND_MULTIPLE);
				intentShare.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
						uris);
				intentShare.setType("video/*");
				startActivity(Intent.createChooser(intentShare,
						"Share audio through..."));
			}
		});

	}

	private void initView() {
		cbAll = (CheckBox) findViewById(R.id.cb_checkall_audio);
		tvSelected = (TextView) findViewById(R.id.tv_selected_audio);
		btnCancel = (Button) findViewById(R.id.btn_cancel_audio);
		btnShare = (Button) findViewById(R.id.btn_Share_audio);
		btnShare.setEnabled(false);
		listView = (ListView) findViewById(R.id.gv_audio);
		gridAdapter = AudioMainActivity.audioListAdapter;
		listView.setAdapter(gridAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.menu_share_video, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}
}
