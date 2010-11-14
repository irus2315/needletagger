package org.softwaregeeks.needletagger;

import org.softwaregeeks.needletagger.common.ConfigurationManager;
import org.softwaregeeks.needletagger.utils.MediaContentProviderHelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaIntentReceiver extends BroadcastReceiver {
	
	public int NOTIFICATION_ID = 300000;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		ConfigurationManager.load(context);
		if( !ConfigurationManager.isPlayerLink() )
			return;
		
		String intentAction = intent.getAction();
		Long id = null;
		String track = null;
		String artist = null;
		String album = null;
		boolean isPlaying = false;
		
		if( intentAction.startsWith("com.htc.music") || intentAction.startsWith("com.android.music") )
		{
			id = (long) intent.getIntExtra("id",0);
			track = intent.getStringExtra("track");
			artist = intent.getStringExtra("artist");
			album = intent.getStringExtra("album");
			isPlaying = intent.getBooleanExtra("isplaying",false);
			
			if( intentAction.startsWith("com.android.music") && id != 0L )
				isPlaying = false;
			else
				isPlaying = true;
		}
		else if( intentAction.startsWith("com.sec.android") )
		{
		}
		else
		{
			return;
		}
		
		String path = MediaContentProviderHelper.getSongPath(context,track);
		
		Intent sendIntent = new Intent(context,EditorActivity.class);
		sendIntent.putExtra("id",id);
		sendIntent.putExtra("track",track);
		sendIntent.putExtra("artist",artist);
		sendIntent.putExtra("album",album);
		sendIntent.putExtra("path",path);
		
		if(isPlaying)
			onNotify(context, sendIntent);
		else
			offNotify(context);
	}

	private void onNotify(Context context, Intent sendIntent)
	{
		final PendingIntent pendingIntent = PendingIntent.getActivity(context,NOTIFICATION_ID, sendIntent,Intent.FLAG_ACTIVITY_NEW_TASK);
		final Notification notification = new Notification(R.drawable.icon_tag,"NeedleTagger", System.currentTimeMillis());
		notification.setLatestEventInfo(context,"NeedleTagger","MP3 IDTag Editor",pendingIntent);
		final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, notification);
	}
	
	private void offNotify(Context context)
	{
		final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
	}
}