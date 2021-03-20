package com.unclezs.novel.app.jfx.plugin.packager.model;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Info needed for signing EXEs on Windows
 */
public class WindowsSigning {

	private String storetype;
	private File keystore;
	private File certfile;
	private File keyfile;
	private String storepass;
	private String alias;
	private String keypass;
	private String alg;

	public String getStoretype() {
		return storetype;
	}

	public void setStoretype(String storetype) {
		this.storetype = storetype;
	}

	public File getKeystore() {
		return keystore;
	}

	public void setKeystore(File keystore) {
		this.keystore = keystore;
	}

	public File getCertfile() {
		return certfile;
	}

	public void setCertfile(File certfile) {
		this.certfile = certfile;
	}

	public File getKeyfile() {
		return keyfile;
	}

	public void setKeyfile(File keyfile) {
		this.keyfile = keyfile;
	}

	public String getStorepass() {
		return storepass;
	}

	public void setStorepass(String storepass) {
		this.storepass = storepass;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getKeypass() {
		return keypass;
	}

	public void setKeypass(String keypass) {
		this.keypass = keypass;
	}

	public String getAlg() {
		return alg;
	}

	public void setAlg(String alg) {
		this.alg = alg;
	}

	@Override
	public String toString() {
		String keypass = this.keypass != null ? StringUtils.repeat("*", this.keypass.length()) : "";
		String storepass = this.storepass != null ? StringUtils.repeat("*", this.storepass.length()) : "";
		return "WindowsSigning [storetype=" + storetype + ", keystore=" + keystore + ", certfile=" + certfile
				+ ", keyfile=" + keyfile + ", storepass=" + storepass + ", alias=" + alias + ", keypass=" + keypass + ", alg=" + alg
				+ "]";
	}

}
