package com.unclezs.novel.app.jfx.plugin.packager.model;

import java.io.Serializable;

/**
 * Windows Registry entry
 */
public class RegistryEntry implements Serializable {
	private static final long serialVersionUID = 447936480111873679L;

	/**
	 * Windows registry key: HKCU, HKLM, ...
	 */
	private String key;

	/**
	 * Windows Registry value name
	 */
	private String valueName;

	/**
	 * Windows Registry value type
	 */
	private ValueType valueType = ValueType.REG_SZ;

	/**
	 * Windows Registry value data
	 */
	private String valueData = "";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	public String getValueData() {
		return valueData;
	}

	public void setValueData(String valueData) {
		this.valueData = valueData;
	}

	public String getRoot() {
		return key.split(":")[0];
	}

	public String getSubkey() {
		String subkey = key.split(":")[1];
		return subkey.startsWith("/") ? subkey.substring(1) : subkey;
	}

	/**
	 * Returns value type as Inno Setup expects
	 * https://jrsoftware.org/ishelp/index.php?topic=registrysection
	 */
	public String getValueTypeAsInnoSetupString() {
		switch(valueType) {
		case REG_BINARY: return "binary";
		case REG_DWORD: return "dword";
		case REG_EXPAND_SZ: return "expandsz";
		case REG_MULTI_SZ: return "multisz";
		case REG_QWORD: return "qword";
		case REG_SZ: return "string";
		default: return "none";
		}
	}

	@Override
	public String toString() {
		return "RegistryEntry [key=" + key + ", valueName=" + valueName + ", valueType=" + valueType + ", valueData="
				+ valueData + "]";
	}

}
