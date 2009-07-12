package net.necomimi.android.kulock.kuler;

import java.io.Serializable;
import java.util.Stack;

public class KulerEntry implements Serializable {
	private static final long serialVersionUID = 7335824162538733403L;
	private String id;
	private Stack<String> colors;
	
	public KulerEntry() {
		this.colors = new Stack<String>();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Stack<String> getColors() {
		return colors;
	}
	public void setColors(Stack<String> colors) {
		this.colors = colors;
	}
}
