package com.unclezs.novel.app.jfx.plugin.packager.utils;


import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Object utils
 */
public class ObjectUtils {

	/**
	 * Returns the first non-null object
	 * @param <T> Type
	 * @param values List of objects
	 * @return First non-null object from values list
	 */
    @SuppressWarnings("unchecked")
	public static <T> T defaultIfNull(final T ... values) {
        Optional<T> value = Arrays.stream(values).filter(Objects::nonNull).findFirst();
        return value.orElse(null);
    }

    /**
	 * Returns the first non-blank String
     * @param values List of String
     * @return First non-blank string
     */
	public static String defaultIfBlank(final String ... values) {
        Optional<String> value = Arrays.stream(values).filter(v -> v != null && !StringUtils.isBlank(v)).findFirst();
        return value.orElse(null);
    }

}
