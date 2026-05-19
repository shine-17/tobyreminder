package cozy.ai.reminder.service;

/**
 * HTML 특수문자 이스케이프 유틸리티.
 * XSS 공격 방지를 위해 사용자 입력 문자열을 살균한다.
 */
public final class HtmlSanitizer {

    private HtmlSanitizer() {
    }

    /**
     * HTML 특수문자를 이스케이프한다.
     * null 입력은 null을 반환한다.
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
