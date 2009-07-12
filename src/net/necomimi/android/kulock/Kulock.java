package net.necomimi.android.kulock;

import net.necomimi.android.common.ComponentInitializeException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Kulock extends Activity {
    /** Called when the activity is first created. */
    protected boolean isFullscreenOpaque() {
        return true;
    }	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = null;
		try {
			view = new KulockView(this);
		} catch (ComponentInitializeException e) {
			// Log and exit.
			Log.e(this.getClass().getName(), "[ERROR] Component initialization abort.", e);
			this.finish();
		}
        setContentView(view);
    }
}