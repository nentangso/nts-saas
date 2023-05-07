package org.nentangso.core.service.utils;

import com.google.common.base.CaseFormat;
import com.google.errorprone.annotations.InlineMe;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class NtsTextUtils {
    private NtsTextUtils() {
    }

    public static String generateAddressCode(String input) {
        if (StringUtils.isBlank(input)) return "";
        String code = NtsTextUtils.removeVietnameseChar(input);
        code = replaceSpecialWithUnderScore(code);
        return StringUtils.upperCase(code);
    }

    private static String replaceSpecialWithUnderScore(String code) {
        if (StringUtils.isBlank(code)) {
            return code;
        }
        if (Pattern.compile("[^A-Za-z0-9_]").matcher(code).find()) {
            return replaceSpecialWithUnderScore(code.replaceAll("[^A-Za-z0-9_]", "_"));
        }
        if (Pattern.compile("_{2}").matcher(code).find()) {
            return replaceSpecialWithUnderScore(code.replaceAll("_+", "_"));
        }
        return code;
    }

    @Deprecated(forRemoval = true, since = "1.0.4")
    @InlineMe(replacement = "NtsTextUtils.unaccentVietnamese(input)", imports = "org.nentangso.core.service.utils.NtsTextUtils")
    public static String removeVietnameseChar(String input) {
        return unaccentVietnamese(input);
    }

    public static String unaccentVietnamese(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }
        input = input.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        input = input.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        input = input.replaceAll("[ìíịỉĩ]", "i");
        input = input.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        input = input.replaceAll("[ùúụủũưừứựửữ]", "u");
        input = input.replaceAll("[ỳýỵỷỹ]", "y");
        input = input.replaceAll("[đ]", "d");
        input = input.replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A");
        input = input.replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E");
        input = input.replaceAll("[ÌÍỊỈĨ]", "I");
        input = input.replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O");
        input = input.replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U");
        input = input.replaceAll("[ỲÝỴỶỸ]", "Y");
        input = input.replaceAll("[Đ]", "D");
        return input;
    }

    public static String toSnakeCase(String input) {
        if (StringUtils.isEmpty(input)) return input;
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, input);
    }

    public static String toCamelCase(String input) {
        if (StringUtils.isEmpty(input)) return input;
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, input);
    }

    public static String joinTags(@Valid @Size(max = 250) Collection<@NotNull String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        return tags.stream()
            .map(StringUtils::trimToNull)
            .filter(Objects::nonNull)
            .distinct()
            .sorted()
            .collect(Collectors.joining(", "));
    }

    public static Set<@NotNull String> splitTags(String tags) {
        if (StringUtils.isBlank(tags)) {
            return Collections.emptySet();
        }
        return Stream.of(tags.split(","))
            .map(StringUtils::trimToNull)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
}
