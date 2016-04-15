package com.tuankhac.video;

import java.io.File;
import java.util.ArrayList;

import com.tuankhac.activity.VideoMainActivity;
import com.tuankhac.activity.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

public class ShareVideoActivity extends Activity {

	private CheckBox cbAll;
	private TextView tvSelected;
	private GridView gridView;
	private GridAdapter gridAdapter;
	private Button btnCancel, btnShare;

	// temp
	View viewTemp;
	CheckBox checkBoxTemp;
	public static int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_activity_share);
		initView();
		initListener();
	}

	private void initListener() {
		cbAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (cbAll.isChecked()) {
					for (int i = 0; i < gridView.getChildCount(); i++) {
						viewTemp = gridView.getChildAt(i);
						checkBoxTemp = (CheckBox) viewTemp
								.findViewById(R.id.cb_item);
						checkBoxTemp.setChecked(true);
					}
					count = gridView.getChildCount();
					btnShare.setEnabled(true);
				} else {
					for (int i = 0; i < gridView.getChildCount(); i++) {
						viewTemp = gridView.getChildAt(i);
						checkBoxTemp = (CheckBox) viewTemp
								.findViewById(R.id.cb_item);
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
				for (int i = 0; i < gridView.getChildCount(); i++) {
					viewTemp = gridView.getChildAt(i);
					checkBoxTemp = (CheckBox) viewTemp
							.findViewById(R.id.cb_item);
					if (checkBoxTemp.isChecked()) {
						uris.add(Uri.fromFile(new File(
								VideoMainActivity.videoInfos.get(i).getPath())));
					}
				}
				Intent intentShare = new Intent();
				intentShare.setAction(Intent.ACTION_SEND);
				intentShare.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris);
				intentShare.setType("video/*");
				startActivity(Intent.createChooser(intentShare,	"Share video through..."));
			}
		});

	}

	private void initView() {
		cbAll = (CheckBox) findViewById(R.id.cb_checkall);
		tvSelected = (TextView) findViewById(R.id.tv_selected);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnShare = (Button) findViewById(R.id.btn_Share);
		btnShare.setEnabled(false);
		gridView = (GridView) findViewById(R.id.gv_video);
		gridAdapter = new GridAdapter(ShareVideoActivity.this,
				R.layout.item_checkbox, VideoMainActivity.videoInfos);
		gridView.setAdapter(gridAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_share_video, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
