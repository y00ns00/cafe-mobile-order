package ys.cafe.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 날짜/시간 포맷 유틸리티 클래스
 * LocalDate, LocalDateTime을 일관된 형식으로 포맷팅
 */
public class DateTimeFormatUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeFormatUtil() {
        // 유틸리티 클래스는 인스턴스화 방지
    }

    /**
     * LocalDate를 "yyyy-MM-dd" 형식으로 포맷팅
     *
     * @param date 포맷팅할 LocalDate
     * @return "yyyy-MM-dd" 형식의 문자열
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * LocalDateTime을 "yyyy-MM-dd HH:mm:ss" 형식으로 포맷팅
     *
     * @param dateTime 포맷팅할 LocalDateTime
     * @return "yyyy-MM-dd HH:mm:ss" 형식의 문자열
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * LocalDateTime을 "yyyy-MM-dd" 형식으로 포맷팅 (시간 제외)
     *
     * @param dateTime 포맷팅할 LocalDateTime
     * @return "yyyy-MM-dd" 형식의 문자열
     */
    public static String formatDateOnly(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_FORMATTER);
    }
}
