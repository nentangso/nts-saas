package org.nentangso.core.service.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class NtsGlobalIdUtils {
    private NtsGlobalIdUtils() {
    }

    private static final Pattern RESOURCE_NAME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");
    private static final String PREFIX_REGEX = "gid:\\/\\/shop\\/";
    private static final String PREFIX = "gid://shop/";

    /**
     * GraphQL global id to legacy resource id
     *
     * @param id the global id
     * @param resourceName the name of resource
     * @return the legacy id
     */
    public static Long toLocalId(String id, String resourceName) {
        if (!RESOURCE_NAME_PATTERN.matcher(resourceName).matches()) {
            throw new IllegalArgumentException("resourceName");
        }
        if (StringUtils.isBlank(id)) {
            return null;
        }
        var pattern = "^" + PREFIX_REGEX + resourceName + "\\/([0-9]+)$";
        var matcher = Pattern.compile(pattern).matcher(id);
        if (!matcher.matches() || matcher.groupCount() != 1) {
            throw new IllegalArgumentException("globalId");
        }
        return Long.parseLong(matcher.group(1));
    }

    /**
     * GraphQL global ids to legacy resource ids
     *
     * @param ids the global ids
     * @param resourceName the name of resource
     * @return the list of legacy ids
     */
    public static List<Long> toLocalIds(List<String> ids, String resourceName) {
        return Optional.ofNullable(ids).orElseGet(Collections::emptyList)
            .stream()
            .map(id -> toLocalId(id, resourceName))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * GraphQL global ids to legacy resource ids
     *
     * @param ids the global ids
     * @param resourceName the name of resource
     * @return the set of legacy ids
     */
    public static Set<Long> toLocalIds(Set<String> ids, String resourceName) {
        return Optional.ofNullable(ids).orElseGet(Collections::emptySet)
            .stream()
            .map(globalId -> toLocalId(globalId, resourceName))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    /**
     * Legacy resource id to GraphQL global id
     *
     * @param legacyResourceId the legacy id
     * @param resourceName the name of resource
     * @return the global id
     */
    public static String toGlobalId(Long legacyResourceId, String resourceName) {
        if (!RESOURCE_NAME_PATTERN.matcher(resourceName).matches()) {
            throw new IllegalArgumentException("resourceName");
        }
        if (legacyResourceId == null) {
            return null;
        }
        if (legacyResourceId <= 0) {
            throw new IllegalArgumentException("legacyResourceId");
        }
        return String.format("%s%s/%d", PREFIX, resourceName, legacyResourceId);
    }
}
