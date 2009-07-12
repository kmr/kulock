package net.necomimi.android.kulock.kuler;

import java.io.InputStream;

public interface KulerClient {
	public void init(String key);
	public InputStream getRecent(int page) throws KulerException;
	public InputStream getPopular(int page) throws KulerException;
	public InputStream getRating(int page) throws KulerException;
	public InputStream getRandom(int page) throws KulerException;
}
