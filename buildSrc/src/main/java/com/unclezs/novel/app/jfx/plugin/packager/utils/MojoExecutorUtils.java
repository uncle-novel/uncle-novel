package com.unclezs.novel.app.jfx.plugin.packager.utils;

import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.twdata.maven.mojoexecutor.MojoExecutor.element;

/**
 * Mojo executor utils
 */
public class MojoExecutorUtils {

	public static List<Element> mapToElementsList(Map<String, String> map) {
		List<Element> elements = new ArrayList<>();
		map.entrySet().forEach(entry -> elements.add(element(entry.getKey(), entry.getValue())));
		return elements;
	}

	public static Element [] mapToElementsArray(Map<String, String> map) {
		List<Element> elements = mapToElementsList(map);
		return elements.toArray(new Element[elements.size()]);
	}

	public static Element mapToElement(String name, Map<String, String> map) {
		return element(name, mapToElementsArray(map));
	}

}
