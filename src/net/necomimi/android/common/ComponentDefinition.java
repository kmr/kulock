package net.necomimi.android.common;

import java.io.Serializable;

public class ComponentDefinition implements Serializable {
	private static final long serialVersionUID = 2380872956073592046L;
	private final String id;
	private final String interfaceName;
	private final String className;
	private final String scope;
	public static final String SCOPE_STATELESS = "stateless";
	public static final String SCOPE_STATEFULL = "stateful";
	
	public ComponentDefinition(String id,
			                   String interfaceName,
			                   String className,
			                   String scope) {
		this.id = id;
		this.interfaceName = interfaceName;
		this.className = className;
		this.scope = scope;
	}

	public String getId() {
		return id;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getClassName() {
		return className;
	}

	public String getScope() {
		return scope;
	}
	
	public boolean isStateful() {
		if (SCOPE_STATEFULL.equals(this.scope)) {
			return true;
		}
		return false;
	}

	public boolean isStateless() {
		if (SCOPE_STATELESS.equals(this.scope)) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return this.id + ":" + this.interfaceName + ":" + this.className + ":" + this.scope;
	}
}
