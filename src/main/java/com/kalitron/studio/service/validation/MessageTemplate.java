package com.kalitron.studio.service.validation;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Renders catalog messages such as {@code "Muro {wallCode}: …"}. Unknown placeholders are kept. */
final class MessageTemplate {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([A-Za-z]+)}");

    private MessageTemplate() {}

    static String render(String template, Map<String, Object> vars) {
        if (template == null) {
            return null;
        }
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            Object value = vars.get(matcher.group(1));
            matcher.appendReplacement(out, Matcher.quoteReplacement(value == null ? matcher.group(0) : String.valueOf(value)));
        }
        matcher.appendTail(out);
        return out.toString();
    }
}
