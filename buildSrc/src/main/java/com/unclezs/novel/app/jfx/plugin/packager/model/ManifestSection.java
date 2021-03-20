package com.unclezs.novel.app.jfx.plugin.packager.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manifest section
 */
public class ManifestSection implements Serializable {
	private static final long serialVersionUID = 118641813298011799L;

	private String name;
	private Map<String, String> entries = new HashMap<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getEntries() {
		return entries;
	}

	public void setEntries(Map<String, String> entries) {
		this.entries = entries;
	}

	@Override
	public String toString() {
		return "ManifestSection [name=" + name + ", entries=" + entries + "]";
	}

}
