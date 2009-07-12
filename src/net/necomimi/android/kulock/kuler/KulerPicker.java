package net.necomimi.android.kulock.kuler;

import java.util.List;

import net.necomimi.android.common.ComponentInitializeException;
import net.necomimi.android.common.SimpleContainer;

public interface KulerPicker {
	public void init(String key, SimpleContainer container) throws ComponentInitializeException;
	
	public List<KulerEntry> getRecent(int page) throws KulerException;
	public List<KulerEntry> getPopular(int page) throws KulerException;
	public List<KulerEntry> getRating(int page) throws KulerException;
	public List<KulerEntry> getRandom(int page) throws KulerException;
}
