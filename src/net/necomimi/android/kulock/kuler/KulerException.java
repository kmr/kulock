package net.necomimi.android.kulock.kuler;

public class KulerException extends Exception {
	private static final long serialVersionUID = 1751623700494232263L;

	public KulerException() {
		super();
	}

	public KulerException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public KulerException(String detailMessage) {
		super(detailMessage);
	}

	public KulerException(Throwable throwable) {
		super(throwable);
	}

}
