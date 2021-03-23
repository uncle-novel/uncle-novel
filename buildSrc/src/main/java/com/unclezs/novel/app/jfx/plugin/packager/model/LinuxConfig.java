package com.unclezs.novel.app.jfx.plugin.packager.model;

import com.unclezs.novel.app.jfx.plugin.packager.packagers.Packager;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * JavaPackager GNU/Linux specific configuration
 */
@Data
public class LinuxConfig implements Serializable {
    private static final long serialVersionUID = -1238166997019141904L;

    private boolean generateDeb = true;
    private boolean generateRpm = true;
    private File pngFile;
    private File xpmFile;

    /**
     * Tests GNU/Linux specific config and set defaults if not specified
     *
     * @param packager Packager
     */
    public void setDefaults(Packager packager) {
        // nothing
    }

}
