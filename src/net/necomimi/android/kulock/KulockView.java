package net.necomimi.android.kulock;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import net.necomimi.android.common.ComponentInitializeException;
import net.necomimi.android.common.SimpleContainer;
import net.necomimi.android.kulock.kuler.KulerEntry;
import net.necomimi.android.kulock.kuler.KulerException;
import net.necomimi.android.kulock.kuler.KulerPicker;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.opengl.GLU;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

public class KulockView extends AbstractView {
	private static final String ID_KULER_PICKER = "kuler_picker";
	
//    private static final float[] square =
//    	new float[] { -0.3f, -0.1f, 0.0f,
//                       0.3f, -0.1f, 0.0f,
//                      -0.3f,  0.1f, 0.0f,
//                       0.3f,  0.1f, 0.0f };
//    FloatBuffer squareBuff;
    FloatBuffer[] squareBuff;
    Calendar mCalendar;
    private final static String m12 = "h:mm:ss aa";
    private final static String m24 = "k:mm:ss";
    private FormatChangeObserver mFormatChangeObserver;

    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;
    private KulerPicker picker;
    private int page = 0;
    
    private List<KulerEntry> colors;
    
    String mFormat;
    
	public KulockView(Context context) throws ComponentInitializeException {
		super(context);
//        squareBuff = makeFloatBuffer(square);			
		squareBuff = new FloatBuffer[5];
		float currentPositionY = -0.3f;
		for (int i = 0; i < 5; i++) {
			float[] square =
		    	new float[] { -0.3f, currentPositionY, 0.0f,
		                       0.3f, currentPositionY, 0.0f,
		                      -0.3f, currentPositionY + 0.1f, 0.0f,
		                       0.3f, currentPositionY + 0.1f, 0.0f };
	        squareBuff[i] = makeFloatBuffer(square);			
	        currentPositionY += 0.1f;
		}
        this.colors = Collections.synchronizedList(new ArrayList<KulerEntry>());
        initContainer();
	}

	private void initContainer() throws ComponentInitializeException {
		Resources res = this.context.getResources();
        mCalendar = Calendar.getInstance();
		SimpleContainer container = SimpleContainer.getContainer(this.context);
		this.picker = (KulerPicker)container.getConponent(ID_KULER_PICKER);
		this.picker.init(res.getString(R.string.api_key), container);
	}
	
    protected void init(GL10 gl) {
    	try {
    		List<KulerEntry> entry = this.picker.getRecent(page);
			this.colors.addAll(entry);
		} catch (KulerException e) {
			Log.e(this.getClass().getName(), "[ERROR] Kuler request.", e);
		}
		
		renderColorPattern();
    	
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, 0.0f,1.2f,0.0f,1.0f);
    }
    
    private void renderColorPattern() {
    	
    }
	
	@Override
	protected void drawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0,0,-1);
        
        KulerEntry color = this.colors.get(0);
        for (int i = 0; i<color.getColors().size(); i++) {
            drawColorBar(gl, color.getColors().get(i), i);
        }
	}

	private void drawColorBar(GL10 gl, String color, int index) {
		String r = color.substring(0, 2);
		String g = color.substring(2, 4);
		String b = color.substring(4, 6);
		gl.glColor4f(Integer.parseInt(r, 16) / 255f,
				     Integer.parseInt(g, 16) / 255f,
				     Integer.parseInt(b, 16) / 255f, 1);
//        gl.glColor4f(1, 0, 0, 0.5f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, squareBuff[index]);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	}

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
                public void run() {
                    if (mTickerStopped) return;
                    mCalendar.setTimeInMillis(System.currentTimeMillis());
//                    setText(DateFormat.format(mFormat, mCalendar));
                    invalidate();
                    long now = SystemClock.uptimeMillis();
                    long next = now + (1000 - now % 1000);
                    mHandler.postAtTime(mTicker, next);
                }
            };
        mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }

    private boolean get24HourMode() {
        return android.text.format.DateFormat.is24HourFormat(getContext());
    }
    
    private void setFormat() {
        if (get24HourMode()) {
            mFormat = m24;
        } else {
            mFormat = m12;
        }
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            setFormat();
        }
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN:
	    	if (this.colors.size() > 0) {
	    		this.colors.remove(0);
	    	}
	    	if (this.colors.size() < 10) {
	        	try {
	        		List<KulerEntry> entry = this.picker.getRecent(++page);
	    			this.colors.addAll(entry);
	    		} catch (KulerException e) {
	    			Log.e(this.getClass().getName(), "[ERROR] Kuler request.", e);
	    		}
	    	}
	    }
	    
	    return super.onTouchEvent(event);
	}	

}
