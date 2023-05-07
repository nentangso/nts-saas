package org.nentangso.core.config;

/**
 * Application constants.
 */
public class NtsConstants {
    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DEFAULT_LOCATE = "vi";

    public static final int DEFAULT_VERSION = 0;
    public static final int DEFAULT_BUSINESS_VERSION = 1;

    public static final String EVENT_TYPE_CREATE = "create";
    public static final String EVENT_TYPE_UPDATE = "update";
    public static final String EVENT_TYPE_DELETE = "delete";
    public static final String AGGREGATE_EMAIL_MESSAGE = "email";
    public static final String AGGREGATE_WEB_HOOK = "webHook";
    public static final String EXTRA_EMAIL_PATTERN = ".+@.+\\..+$";
    public static final String HEADER_RETRIES = "x_retries";

    public static final int MAX_ITEMS_PER_PAGE = 250;
    public static final int ITEMS_PER_PAGE = 50;

    private NtsConstants() {
    }
}
