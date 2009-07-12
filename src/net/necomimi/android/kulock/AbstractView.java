package net.necomimi.android.kulock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLU;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class AbstractView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {
	protected EGLContext glContext;
	protected ViewAnimator animator;
	protected SurfaceHolder sHolder;
	protected Thread thread;
	protected boolean running;
	int width;
	int height;
	boolean resize;
	int fps;
	protected Context context;

	public AbstractView(Context context) {
		this(context, -1);
	}

	public AbstractView(Context context, int fps) {
		super(context);
		this.context = context;
		this.sHolder = getHolder();
		this.sHolder.addCallback(this);
		this.sHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
		this.fps = fps;
	}	
	
	@Override
	protected void onAttachedToWindow() {
		if (this.animator != null) {
			// If we're animated, start the animation
			this.animator.start();
		}
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		if (this.animator != null) {
			// If we're animated, stop the animation
			this.animator.stop();
		}
		super.onDetachedFromWindow();
	}

	public synchronized void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		this.width = width;
		this.height = height;
		this.resize = true;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		this.thread = new Thread(this);
		this.thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		this.running = false;
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			this.thread = null;
		}
	}

	public void run() {
		EGL10 egl = (EGL10)EGLContext.getEGL();
		EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		int[] version = new int[2];
		egl.eglInitialize(display, version);
		
		int[] configSpec = {
				EGL10.EGL_RED_SIZE, 5,
				EGL10.EGL_GREEN_SIZE, 6,
				EGL10.EGL_BLUE_SIZE, 5,
				EGL10.EGL_DEPTH_SIZE, 16,
				EGL10.EGL_NONE
		};
		
		EGLConfig[] configs = new EGLConfig[1];
		int[] num_config = new int[1];
		egl.eglChooseConfig(display, configSpec, configs, 1, num_config);
		EGLConfig config = configs[0];
		
		EGLContext context = egl.eglCreateContext(display, config,
                EGL10.EGL_NO_CONTEXT, null);
		
		EGLSurface surface = egl.eglCreateWindowSurface(display, config, sHolder, null);
		egl.eglMakeCurrent(display, surface, surface, context);
			
		GL10 gl = (GL10)context.getGL();

		init(gl);
		
		int delta = -1;
		if (fps > 0) {
			delta = 1000/fps;
		}
		long time = System.currentTimeMillis();
		
		running = true;
		while (running) {
			int w, h;
			synchronized(this) {
				w = width;
				h = height;
			}
			if (System.currentTimeMillis()-time < delta) {
				try {
					Thread.sleep(System.currentTimeMillis()-time);
				}
				catch (InterruptedException ex) {}
			}
			drawFrame(gl, w, h);
			egl.eglSwapBuffers(display, surface);

            if (egl.eglGetError() == EGL11.EGL_CONTEXT_LOST) {
                Context c = getContext();
                if (c instanceof Activity) {
                    ((Activity)c).finish();
                }
            }
            time = System.currentTimeMillis();
		}
        egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        egl.eglDestroySurface(display, surface);
        egl.eglDestroyContext(display, context);
        egl.eglTerminate(display);
	}

	private void drawFrame(GL10 gl, int w, int h) {
		if (resize) {
			resize(gl, w, h);
			resize = false;
		}
		drawFrame(gl);
	}
	
	protected void resize(GL10 gl, int w, int h) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0,0,w,h);
		GLU.gluPerspective(gl, 45.0f, ((float)w)/h, 1f, 100f);
	}
	
	protected void init(GL10 gl) {}
	
	protected abstract void drawFrame(GL10 gl);
	
    protected static FloatBuffer makeFloatBuffer(float[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    protected static IntBuffer makeFloatBuffer(int[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
        bb.order(ByteOrder.nativeOrder());
        IntBuffer ib = bb.asIntBuffer();
        ib.put(arr);
        ib.position(0);
        return ib;
    }

}
