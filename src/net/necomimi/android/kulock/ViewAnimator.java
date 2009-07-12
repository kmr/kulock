package net.necomimi.android.kulock;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

public class ViewAnimator extends Handler {
	boolean running;
	View view;
	long nextTime;
	int diff;
	
	public static final int NEXT = 0;
	
	public ViewAnimator(View view) {
		this(view, -1);
	}
	
	public ViewAnimator(View view, int fps) {
		this.running = false;
		this.view = view;
		this.diff = 1000/fps;
	}
	
	public void start() {
		if (!this.running) {
			this.running = true;
			Message msg = obtainMessage(NEXT);
			sendMessageAtTime(msg, SystemClock.uptimeMillis());
		}
	}

	public void stop() {
		this.running = false;
	}
	
	public void handleMessage(Message msg) {
		if (this.running && msg.what == NEXT) {
			this.view.invalidate();
			msg = obtainMessage(NEXT);
			long current = SystemClock.uptimeMillis();
			if (this.nextTime < current) {
				this.nextTime = current + this.diff;
			}
			sendMessageAtTime(msg, this.nextTime);
			this.nextTime += this.diff;
		}
	}
}
