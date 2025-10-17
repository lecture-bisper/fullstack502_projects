package bitc.full502.sceneshare.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class OmdbMapUtils {

  private static final int V255 = 255;
  private static final DateTimeFormatter OMDB_RELEASED =
      DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

  public static String cap255(String s) {
    if (isBlankOrNA(s)) return null;
    return s.length() > V255 ? s.substring(0, V255) : s;
  }

  public static boolean isBlankOrNA(String s) {
    return s == null || s.isBlank() || "N/A".equalsIgnoreCase(s.trim());
  }

  public static Double toDoubleOrNull(String s) {
    if (isBlankOrNA(s)) return null;
    try { return Double.valueOf(s.trim()); } catch (Exception e) { return null; }
  }

  /** "05 May 2017" -> LocalDateTime(2017-05-05T00:00) */
  public static LocalDateTime toReleasedDateTime(String released) {
    if (isBlankOrNA(released)) return null;
    try {
      LocalDate d = LocalDate.parse(released.trim(), OMDB_RELEASED);
      return d.atStartOfDay();
    } catch (Exception e) {
      return null;
    }
  }

  /** "136 min" -> "136" (문자열) */
  public static String toMinutesString(String runtime) {
    if (isBlankOrNA(runtime)) return null;
    String digits = runtime.replaceAll("\\D", ""); // 숫자만
    return digits.isEmpty() ? null : digits;
  }

  /** 포스터 URL 정리 (없으면 기본 이미지) */
  public static String posterOrDefault(String poster) {
    return isBlankOrNA(poster) ? "/img/no-image.svg" : cap255(poster);
  }
}
