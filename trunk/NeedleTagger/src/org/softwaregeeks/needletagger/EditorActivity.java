package org.softwaregeeks.needletagger;

import org.softwaregeeks.needletagger.alsong.AlsongLyricCrawler;
import org.softwaregeeks.needletagger.common.ActivityHelper;
import org.softwaregeeks.needletagger.common.ConfigurationManager;
import org.softwaregeeks.needletagger.common.Music;
import org.softwaregeeks.needletagger.common.MusicManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditorActivity extends Activity {

	private static final int MESSAGE_GET_IMAGE = 200;
	private static final int MESSAGE_GET_INFORMATION = 201;
	private static final int MESSAGE_SAVE = 203;

	private static final int DIALOG_CHOICE = 100;
	private static final int DIALOG_CHOICE_INFORMATION = 101;

	private Thread processThread;
	private Handler processHandler;

	private Music music = new Music();

	private Button alsongButton;
	private Button okButton;
	private EditText trackEditText;
	private EditText artistEditText;
	private EditText albumEditText;
	private TextView pathTextView;
	private MusicArtworkView musicArtworkView;
	private OnClickListener onClickListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);
		setInit();
		setHandler();
	}

	private void setInit() {
		MediaIntentReceiver.offNotify(this);
		ActivityHelper.setNavigationBar(this);
		ActivityHelper.setHeaderProgressBar(this, false);

		parseIntent(getIntent(), this.music);
		setOnClickListener();
		setComponent();
		setComponentInformation(this.music);
		setArtwork();
	}

	private void setHandler() {
		processHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				if (msg != null) {
					switch (msg.what) {
					case MESSAGE_GET_IMAGE: {
						Bitmap bitmap = (Bitmap) msg.obj;
						if (bitmap != null) {
							music.setArtwork(bitmap);
							bitmap = musicArtworkView.getResizedBitmap(bitmap);
							musicArtworkView.setBitmap(bitmap);
							musicArtworkView.onDraw();
							musicArtworkView.invalidate();
						} else {
							Toast.makeText(EditorActivity.this, getString(R.string.dialogArtworkNoCaption),
									Toast.LENGTH_LONG).show();
						}
					}
						break;
					case MESSAGE_GET_INFORMATION: {
						AlsongLyricCrawler alsongLyricCrawler = (AlsongLyricCrawler) msg.obj;
						if (alsongLyricCrawler.isProcessed()) {
							music.setTrack(alsongLyricCrawler.getTitle());
							music.setArtist(alsongLyricCrawler.getArtist());
							music.setAlbum(alsongLyricCrawler.getAlbum());
							setComponentInformation(music);
						} else {
							Toast.makeText(EditorActivity.this, getString(R.string.dialogInformationNoCaption),
									Toast.LENGTH_LONG).show();
						}
					}
						break;
					case MESSAGE_SAVE:
					{
						MusicManager.playMusic(EditorActivity.this);
						Toast.makeText(EditorActivity.this, getString(R.string.saveMessage),
								Toast.LENGTH_LONG).show();
					}
						break;
					}

					ActivityHelper.setEndProcess(EditorActivity.this);
				}
			}
		};
	}

	public void setOnClickListener() {
		onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.title:
				{
					trackEditText.setInputType(1);
				}
				break;
				case R.id.album:
				{
					albumEditText.setInputType(1);
				}
				break;
				case R.id.artist:
				{
					artistEditText.setInputType(1);
				}
				break;
				case R.id.artwork: {
					showDialog(DIALOG_CHOICE);
				}
					break;
				case R.id.alsong: {
					showDialog(DIALOG_CHOICE_INFORMATION);
				}
					break;
				case R.id.ok: {
					save();
				}
					break;
				}
			}
		};
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CHOICE: {
			return new AlertDialog.Builder(EditorActivity.this).setMessage(R.string.dialogArtworkCaption)
					.setPositiveButton(R.string.dialogOkCaption, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							getArtworkFromBugs();
						}
					}).setNegativeButton(R.string.dialogCancelCaption, null).create();
		}
		case DIALOG_CHOICE_INFORMATION: {
			return new AlertDialog.Builder(EditorActivity.this).setMessage(R.string.dialogInformationCaption)
					.setPositiveButton(R.string.dialogOkCaption, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							getID3TagFromAlsong();
						}
					}).setNegativeButton(R.string.dialogCancelCaption, null).create();
		}
		}

		return null;
	}

	private void setArtwork() {
		musicArtworkView = (MusicArtworkView) findViewById(R.id.artwork);
		musicArtworkView.setSongId(music.getId());
		musicArtworkView.onDraw();
		musicArtworkView.invalidate();
		musicArtworkView.setOnClickListener(onClickListener);
	}

	public void parseIntent(Intent intent, Music music) {
		Long id = intent.getLongExtra("id", 0);
		String track = intent.getStringExtra("track");
		String artist = intent.getStringExtra("artist");
		String path = intent.getStringExtra("path");
		String album = intent.getStringExtra("album");
		Long albumId = intent.getLongExtra("albumId",0);
		
		music.setId(id);
		music.setTrack(track);
		music.setArtist(artist);
		music.setAlbum(album);
		music.setPath(path);
		music.setAlbumId(albumId);
	}

	public void setComponent() {
		trackEditText = (EditText) findViewById(R.id.title);
		artistEditText = (EditText) findViewById(R.id.artist);
		albumEditText = (EditText) findViewById(R.id.album);
		pathTextView = (TextView) findViewById(R.id.path);
		
		trackEditText.setOnClickListener(onClickListener);
		artistEditText.setOnClickListener(onClickListener);
		albumEditText.setOnClickListener(onClickListener);
		trackEditText.setInputType(0);
		artistEditText.setInputType(0);
		albumEditText.setInputType(0);
		
		okButton = (Button) findViewById(R.id.ok);
		okButton.setTypeface(ConfigurationManager.getFont(getAssets()));
		okButton.setOnClickListener(onClickListener);

		alsongButton = (Button) findViewById(R.id.alsong);
		alsongButton.setTypeface(ConfigurationManager.getFont(getAssets()));
		alsongButton.setOnClickListener(onClickListener);
	}

	public void setComponentInformation(Music music) {
		trackEditText.setText(music.getTrack());
		artistEditText.setText(music.getArtist());
		albumEditText.setText(music.getAlbum());
		pathTextView.setText(music.getPath());
	}

	public void getArtworkFromBugs() {
		ActivityHelper.setStartProcess(this);
		processThread = new Thread(new Runnable() {
			@Override
			public void run() {
				String keyword = trackEditText.getText().toString() + " " + artistEditText.getText().toString();
				Bitmap bitmap = MusicManager.getBugsImage(keyword);

				Message message = Message.obtain(processHandler, MESSAGE_GET_IMAGE, bitmap);
				processHandler.sendMessage(message);
			}
		});
		processThread.start();
	}

	public void getID3TagFromAlsong() {
		ActivityHelper.setStartProcess(this);
		processThread = new Thread(new Runnable() {
			@Override
			public void run() {
				String path = pathTextView.getText().toString();

				AlsongLyricCrawler alsongLyricCrawler = new AlsongLyricCrawler();
				alsongLyricCrawler.initLyric(path);

				Message message = Message.obtain(processHandler, MESSAGE_GET_INFORMATION, alsongLyricCrawler);
				processHandler.sendMessage(message);
			}
		});
		processThread.start();
	}

	public void save() {
		
		MusicManager.stopMusic(this);
		ActivityHelper.setStartProcess(this);
		processThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Music updatedMusic = new Music(music);
				updatedMusic.setTrack(trackEditText.getText().toString());
				updatedMusic.setArtist(artistEditText.getText().toString());
				updatedMusic.setAlbum(albumEditText.getText().toString());
				MusicManager.updateMusicMetadata(EditorActivity.this, updatedMusic);
				
				// Run MediaScanner
				MusicManager.performMediaScanner(EditorActivity.this);
				
				Message message = Message.obtain(processHandler, MESSAGE_SAVE, null);
				processHandler.sendMessage(message);
			}
		});
		processThread.start();
	}
}