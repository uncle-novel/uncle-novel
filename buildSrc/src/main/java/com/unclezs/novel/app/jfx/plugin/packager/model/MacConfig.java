package com.unclezs.novel.app.jfx.plugin.packager.model;

import com.unclezs.novel.app.jfx.plugin.packager.packagers.Packager;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * JavaPackager Mac OS specific configuration
 */
@Data
public class MacConfig implements Serializable {
	private static final long serialVersionUID = -2268944961932941577L;

	private File icnsFile;
	private File backgroundImage;
	private Integer windowWidth;
	private Integer windowHeight;
	private Integer windowX;
	private Integer windowY;
	private Integer iconSize;
	private Integer textSize;
	private Integer iconX;
	private Integer iconY;
	private Integer appsLinkIconX;
	private Integer appsLinkIconY;
	private File volumeIcon;
	private String volumeName;
	private boolean generateDmg = true;
	private boolean generatePkg = true;
	private boolean relocateJar = true;
	private String appId;
	private String developerId = "-";
	private File entitlements;

	/**
	 * Tests Mac OS X specific config and set defaults if not specified
	 *
	 * @param packager Packager
	 */
	public void setDefaults(Packager packager) {
		this.setWindowX(defaultIfNull(this.getWindowX(), 10));
		this.setWindowY(defaultIfNull(this.getWindowY(), 60));
		this.setWindowWidth(defaultIfNull(this.getWindowWidth(), 540));
		this.setWindowHeight(defaultIfNull(this.getWindowHeight(), 360));
		this.setIconSize(defaultIfNull(this.getIconSize(), 128));
		this.setTextSize(defaultIfNull(this.getTextSize(), 16));
		this.setIconX(defaultIfNull(this.getIconX(), 52));
		this.setIconY(defaultIfNull(this.getIconY(), 116));
		this.setAppsLinkIconX(defaultIfNull(this.getAppsLinkIconX(), 360));
		this.setAppsLinkIconY(defaultIfNull(this.getAppsLinkIconY(), 116));
		this.setAppId(defaultIfNull(this.getAppId(), packager.getMainClass()));
	}
}
