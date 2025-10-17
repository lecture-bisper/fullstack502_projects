package bitc.full502.sceneshare.common;

public class ImdbUtils {

  /** "tt3896198" -> 3896198 (int) */
  public static int parseImdbIdToInt(String imdbId) {
    if (imdbId == null) {
      throw new IllegalArgumentException("imdbId is null");
    }
    String digits = imdbId.replaceAll("\\D", ""); // 숫자만 남김
    if (digits.isEmpty()) {
      throw new IllegalArgumentException("imdbId has no digits: " + imdbId);
    }
    return Integer.parseInt(digits);              // ✅ int만 사용
  }
}
