package org.softwaregeeks.needletagger;

import org.softwaregeeks.needletagger.common.LogManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

public class ExceptionReportDialog extends Activity {
	private static final int MESSAGE_GET_SEND = 201;

	private Thread processThread;
	private Handler processHandler;

	private String stackTrace;
	ProgressDialog progressDialog;
	private Activity activity;

	public ExceptionReportDialog(Activity activity, String stackTrace) {
		this.activity = activity;
		this.stackTrace = stackTrace;
		createLoginDialog();
		setHandler();
		sendMessage();
	}

	public void createLoginDialog() {
		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle(activity.getString(R.string.exceptionTitleCaption));
		progressDialog.setMessage(activity.getString(R.string.exceptionCaption));
		progressDialog.show();
	}

	private void setHandler() {
		processHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg != null) {
					progressDialog.hide();
				}
			}
		};
	}

	public void sendMessage() {

		processThread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean isProcessed = LogManager.sendLogMessage(stackTrace, "");
				Message postMessage = Message.obtain(processHandler, MESSAGE_GET_SEND, isProcessed);
				processHandler.sendMessage(postMessage);
			}
		});
		processThread.start();
	}
}