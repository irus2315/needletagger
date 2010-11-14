package org.softwaregeeks.needletagger;

import java.util.ArrayList;

import org.softwaregeeks.needletagger.common.ActivityHelper;
import org.softwaregeeks.needletagger.common.ConfigurationManager;
import org.softwaregeeks.needletagger.common.Music;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class MusicListActivity extends Activity
{
	private String keyword;
	private OnClickListener onClickListener;
	
	private ImageButton buttonSearch;
	private EditText editKeyword;
	
	private ArrayList<Music> musicList = new ArrayList<Music>();
	private MusicListAdapter listAdapter;
	private ListView listView;
	
	private MusicListService service = new MusicListService();
	private Thread dataLoadThread;
	private Handler handler;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		setInit();
		setListView();
		setHandler();
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		loadData();
	}



	private void setInit()
	{
		ConfigurationManager.load(this);
		
		setOnClickListener();
		ActivityHelper.setNavigationBar(this);
		
		listView = (ListView) findViewById(R.id.listView);
		editKeyword = (EditText)findViewById(R.id.keyword);
		buttonSearch = (ImageButton)findViewById(R.id.search);
		buttonSearch.setOnClickListener(onClickListener);
	}
	
	public void setOnClickListener()
	{
		onClickListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switch (v.getId())
				{
				case R.id.search:
					{
						keyword = editKeyword.getText().toString();
						loadData();
					}
				}
			}
		};
	}
	
	private void setListView()
	{
		listAdapter = new MusicListAdapter(this,R.layout.list_row, musicList);
		listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new OnItemClickListener()
        {
        	@SuppressWarnings("rawtypes")
			@Override
			public void onItemClick(AdapterView parent, View view, int position, long id)
			{
				Music music = musicList.get(position);
				if( music == null )
					return;
				
				Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
				intent.putExtra("id",music.getId());
				intent.putExtra("track",music.getTrack());
				intent.putExtra("artist",music.getArtist());
				intent.putExtra("album",music.getAlbum());
				intent.putExtra("path",music.getPath());
				intent.putExtra("albumId",music.getAlbumId());
				startActivity(intent);
				overridePendingTransition(0,0);
			}
		});
	}
	
	private void updateListView()
	{
		listAdapter.notifyDataSetChanged();
	}
	
	private void setHandler()
	{
		handler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				
				if( msg != null )
				{
					switch (msg.what)
					{
					case 0:
						@SuppressWarnings("unchecked")
						ArrayList<Music> list = (ArrayList<Music>)msg.obj;
						if( list != null )
						{
							musicList.clear();
							musicList.addAll(list);
							updateListView();
						}
						ActivityHelper.setHeaderProgressBar(MusicListActivity.this,false);
					break;
					}
				}
			}
		};
	}
	
	private void loadData()
	{
		service.setNetworkHandler(handler);
		service.setContext(this);
		service.setKeyword(keyword);
		dataLoadThread = new Thread(service);
		dataLoadThread.start();
		
		ActivityHelper.setHeaderProgressBar(this,true);
	}
}
