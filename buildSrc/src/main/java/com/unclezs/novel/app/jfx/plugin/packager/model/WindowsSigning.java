package com.unclezs.novel.app.jfx.plugin.packager.model;

import lombok.Data;

import java.io.File;

/**
 * Info needed for signing EXEs on Windows
 */
@Data
public class WindowsSigning {
	private String storetype;
	private File keystore;
	private File certfile;
	private File keyfile;
	private String storepass;
	private String alias;
	private String keypass;
	private String alg;
}
