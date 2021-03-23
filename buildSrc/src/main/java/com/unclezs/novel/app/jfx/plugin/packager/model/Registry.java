package com.unclezs.novel.app.jfx.plugin.packager.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Windows Registry entries to be created when installing using Setup
 */
@Data
public class Registry implements Serializable {
    private static final long serialVersionUID = 8310081277297116023L;
    private List<RegistryEntry> entries = new ArrayList<>();
}
