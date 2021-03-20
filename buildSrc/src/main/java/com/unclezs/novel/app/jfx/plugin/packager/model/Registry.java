package com.unclezs.novel.app.jfx.plugin.packager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Windows Registry entries to be created when installing using Setup
 */
public class Registry implements Serializable {
	private static final long serialVersionUID = 8310081277297116023L;

	private List<RegistryEntry> entries = new ArrayList<>();

	public List<RegistryEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<RegistryEntry> entries) {
		this.entries = entries;
	}

	@Override
	public String toString() {
		return "Registry [entries=" + entries + "]";
	}

}
