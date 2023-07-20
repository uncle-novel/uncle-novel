/*
 * Copyright (c) 2018, 2023, Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openjfx.gradle;

import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.FlatDirectoryArtifactRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openjfx.gradle.JavaFXModule.PREFIX_MODULE;

public class JavaFXOptions {

    private static final String MAVEN_JAVAFX_ARTIFACT_GROUP_ID = "org.openjfx";
    private static final String JAVAFX_SDK_LIB_FOLDER = "lib";

    private final Project project;
    private JavaFXPlatform platform;

    private String version = "17";
    private String sdk;
    private String[] configurations = new String[] { "implementation" };
    private String[] lastUpdatedConfigurations;
    private List<String> modules = new ArrayList<>();
    private FlatDirectoryArtifactRepository customSDKArtifactRepository;

    public JavaFXOptions(Project project) {
        this.project = project;
        this.platform = JavaFXPlatform.detect(project);
    }

    public JavaFXPlatform getPlatform() {
        return platform;
    }

    /**
     * Sets the target platform for the dependencies.
     * @param platform platform classifier.
     * Supported classifiers are linux, linux-aarch64, win/windows, osx/mac/macos or osx-aarch64/mac-aarch64/macos-aarch64.
     */
    public void setPlatform(String platform) {
        this.platform = JavaFXPlatform.fromString(platform);
        updateJavaFXDependencies();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        updateJavaFXDependencies();
    }

    /**
     * If set, the JavaFX modules will be taken from this local
     * repository, and not from Maven Central
     * @param sdk, the path to the local JavaFX SDK folder
     */
    public void setSdk(String sdk) {
        this.sdk = sdk;
        updateJavaFXDependencies();
    }

    public String getSdk() {
        return sdk;
    }

    /**
     * Set the configuration name for dependencies, e.g.
     * 'implementation', 'compileOnly' etc.
     * @param configuration The configuration name for dependencies
     */
    public void setConfiguration(String configuration) {
        setConfigurations(new String[] { configuration });
    }

    /**
     * Set the configurations for dependencies, e.g.
     * 'implementation', 'compileOnly' etc.
     * @param configurations List of configuration names
     */
    public void setConfigurations(String[] configurations) {
        this.configurations = configurations;
        updateJavaFXDependencies();
    }

    public String getConfiguration() {
        return configurations[0];
    }

    public String[] getConfigurations() {
        return configurations;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
        updateJavaFXDependencies();
    }

    public void modules(String...moduleNames) {
        setModules(List.of(moduleNames));
    }

    private void updateJavaFXDependencies() {
        clearJavaFXDependencies();

        String[] configurations = getConfigurations();
        for (String conf : configurations) {
            JavaFXModule.getJavaFXModules(this.modules).stream()
                    .sorted()
                    .forEach(javaFXModule -> {
                        if (customSDKArtifactRepository != null) {
                            project.getDependencies().add(conf, Map.of("name", javaFXModule.getModuleName()));
                        } else {
                            project.getDependencies().add(conf,
                                    String.format("%s:%s:%s:%s", MAVEN_JAVAFX_ARTIFACT_GROUP_ID, javaFXModule.getArtifactName(),
                                            getVersion(), getPlatform().getClassifier()));
                        }
                    });
        }
        lastUpdatedConfigurations = configurations;
    }

    private void clearJavaFXDependencies() {
        if (customSDKArtifactRepository != null) {
            project.getRepositories().remove(customSDKArtifactRepository);
            customSDKArtifactRepository = null;
        }

        if (sdk != null && ! sdk.isEmpty()) {
            Map<String, String> dirs = new HashMap<>();
            dirs.put("name", "customSDKArtifactRepository");
            if (sdk.endsWith(File.separator)) {
                dirs.put("dirs", sdk + JAVAFX_SDK_LIB_FOLDER);
            } else {
                dirs.put("dirs", sdk + File.separator + JAVAFX_SDK_LIB_FOLDER);
            }
            customSDKArtifactRepository = project.getRepositories().flatDir(dirs);
        }

        if (lastUpdatedConfigurations == null) {
            return;
        }

        for (String conf : lastUpdatedConfigurations) {
            var configuration = project.getConfigurations().findByName(conf);
            if (configuration != null) {
                if (customSDKArtifactRepository != null) {
                    configuration.getDependencies()
                            .removeIf(dependency -> dependency.getName().startsWith(PREFIX_MODULE));
                }
                configuration.getDependencies()
                        .removeIf(dependency -> MAVEN_JAVAFX_ARTIFACT_GROUP_ID.equals(dependency.getGroup()));
            }
        }
    }
}
