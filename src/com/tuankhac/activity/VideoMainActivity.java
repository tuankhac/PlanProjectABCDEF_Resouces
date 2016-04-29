package com.tuankhac.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.watchlikeyoutube.SearchActivity;
import com.library.object.SweetAlertDialog;
import com.tuankhac.object.VideoInfo;
import com.tuankhac.video.DiscoverVideo;
import com.tuankhac.video.GridAdapter;
import com.tuankhac.video.PlayerActivity;
import com.tuankhac.video.ServiceGetFile;

@SuppressLint("DefaultLocale")
public class VideoMainActivity extends Activity {
	public static GridView gridView;
	public static GridAdapter gridAdapter;
	public static ArrayList<VideoInfo> videoInfos;
	public static ArrayList<VideoInfo> videoSearch;
	public static VideoMainActivity mainActivity;

	private ListView lvMenu;
	private ArrayAdapter<String> arrayAdapter;
	private String[] menus = { "Share", "Delete", "Detail" };
	private File file;

	private ImageButton watch_on_youtube;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.video_activity_main);
		mainActivity = this;
		watch_on_youtube = (ImageButton) findViewById(R.id.watch_like_youtube);

		try {
			initView();
			initData();
			initListen();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/backupperf.csv");
		if (file.exists()) {
			Toast.makeText(VideoMainActivity.this, "Exist", Toast.LENGTH_LONG)
					.show();
			if (file.delete())
				Toast.makeText(VideoMainActivity.this, "Deleted",
						Toast.LENGTH_LONG).show();
		}
		watch_on_youtube.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(VideoMainActivity.this,
						SearchActivity.class);
				startActivity(i);
			}
		});
	}

	private void initData() throws ExecutionException, InterruptedException {
		DiscoverVideo discoverVideo = new DiscoverVideo(VideoMainActivity.this);
		String targetPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		file = new File(targetPath);
		discoverVideo.execute(file);
	}

	private void initListen() {
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(VideoMainActivity.this,
						PlayerActivity.class);
				intent.putExtra("URL", videoInfos.get(position).getPath());
				intent.putExtra("NAME", videoInfos.get(position).getName());
				startActivity(intent);
			}
		});

		gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Dialog dialog = new Dialog(VideoMainActivity.this);
				dialog.setContentView(R.layout.dialog_custom);
				lvMenu = (ListView) dialog.findViewById(R.id.lv_menu);
				lvMenu.setAdapter(arrayAdapter);
				dialog.setTitle(videoInfos.get(position).getName());
				lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int i, long id) {
						final VideoInfo videoInfo = videoInfos.get(position);
						switch (i) {
						case 0:
							Intent intentShare = new Intent();
							intentShare.setAction(Intent.ACTION_SEND);
							intentShare.putExtra(Intent.EXTRA_STREAM,
									Uri.fromFile(new File(videoInfo.getPath())));
							intentShare.setType("video/*");
							startActivity(Intent.createChooser(intentShare,
									"Share video to..."));
							break;
						case 1:
							new SweetAlertDialog(VideoMainActivity.this,
									SweetAlertDialog.WARNING_TYPE)
									.setTitleText("Are you sure ?")
									.setContentText(
											"Won't be able to recover this video!")
									.setConfirmText("Yes, delete it !")
									.setConfirmClickListener(
											new SweetAlertDialog.OnSweetClickListener() {
												@Override
												public void onClick(
														SweetAlertDialog sweetAlertDialog) {
													boolean result = deleteFile(videoInfo
															.getPath());
													if (!result) {
														sweetAlertDialog
																.setTitleText(
																		"Cannot Delete")
																.setContentText(
																		"Your video can't delete .Something wrong!")
																.setConfirmText(
																		"Ok")
																.setConfirmClickListener(
																		null)
																.changeAlertType(
																		SweetAlertDialog.ERROR_TYPE);
													} else {
														sweetAlertDialog
																.setTitleText(
																		"Deleted")
																.setContentText(
																		"Your video has been deleted !")
																.setConfirmText(
																		"Ok")
																.setConfirmClickListener(
																		null)
																.changeAlertType(
																		SweetAlertDialog.SUCCESS_TYPE);

														videoInfos
																.remove(videoInfo);
														gridAdapter
																.notifyDataSetChanged();
													}
												}
											}).show();
							break;
						case 2:
							AlertDialog.Builder builder = new AlertDialog.Builder(
									VideoMainActivity.this);
							builder.setTitle("Detail");
							StringBuilder stringBuilder = new StringBuilder();
							stringBuilder.append("Name : "
									+ videoInfo.getName());
							stringBuilder.append("\nTime : "
									+ videoInfo.getTime());
							stringBuilder.append("\nType : "
									+ videoInfo.getType());
							Log.i(" videoinfo", videoInfo.getTime());
							stringBuilder.append("\nResolution : "
									+ videoInfo.getResolution());
							stringBuilder.append("\nDuration : "
									+ videoInfo.getDuration());
							stringBuilder.append("\nSize : "
									+ videoInfo.getSize());
							builder.setMessage(stringBuilder.toString());
							builder.show();
							break;
						}
					}
				});
				dialog.show();
				return true;
			}
		});
	}

	private void initView() {
		gridView = (GridView) findViewById(R.id.gv_video);
		videoInfos = new ArrayList<VideoInfo>();
		videoSearch = new ArrayList<VideoInfo>();
		gridAdapter = new GridAdapter(VideoMainActivity.this, R.layout.item,
				videoInfos);
		gridView.setAdapter(gridAdapter);
		// ActionBar actionBar = getActionBar();
		// actionBar.setDisplayShowHomeEnabled(true);
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, menus);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.menu_main, menu);
		// MenuItem menuItem = menu.findItem(R.id.action_search);
		// SearchView searchView = new SearchView(getApplicationContext());
		// searchView.setOnQueryTextListener((OnQueryTextListener) this);
		// menuItem.setActionView(searchView);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// if (item.getItemId() == R.id.action_share) {
		// Intent intent = new Intent(VideoMainActivity.this,
		// ShareVideoActivity.class);
		// startActivity(intent);
		// }
		return true;
	}

	public boolean deleteFile(String path) {
		File file = new File(path);
		if (getContentResolver().delete(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				MediaStore.MediaColumns.DATA + "='" + path + "'", null) == 1) {
			if (file.exists()) {
				Boolean d = file.delete();
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
						Uri.fromFile(file)));
				return d;
			}
			return true;
		}
		return false;
	}
}
