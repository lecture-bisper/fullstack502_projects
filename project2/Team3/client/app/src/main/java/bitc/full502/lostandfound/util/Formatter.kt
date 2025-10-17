package bitc.full502.lostandfound.util

import bitc.full502.lostandfound.data.api.ReverseGeoService
import bitc.full502.lostandfound.data.model.ReverseGeoData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatter {
    private val DISPLAY_FMT = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분", Locale.KOREA)

    private val DISPLAY_FMT_YMD = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREA)
    private val ISO_SECONDS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    //    가져온 주소 json 데이터 거르고 문자열 처리하는 함수
    fun buildAddressFromReverse(res: ReverseGeoData?): String? {
        val results = res?.results ?: return null
        if (results.isEmpty()) return null

        val pick = results.firstOrNull { it.name == "roadaddr" }
            ?: results.firstOrNull { it.name == "addr" }
            ?: results.firstOrNull { it.name == "admcode" }
            ?: results.firstOrNull { it.name == "legalcode" }
            ?: results.first()

        val r = pick.region
        val l = pick.land

        // 1) roadaddr/addr류: land + 번지 구성
        if (pick.name == "roadaddr" || pick.name == "addr") {
            val base = listOf(r.area1.name, r.area2.name, r.area3.name, r.area4.name)
                .filter { it.isNotBlank() }
                .joinToString(" ")

            val num1 = l?.number1?.takeIf { it.isNotBlank() } ?: ""
            val num2 = l?.number2?.takeIf { it.isNotBlank() }?.let { "-$it" } ?: ""
            val lot  = listOf(l?.name.orEmpty(), (num1 + num2).trim())
                .filter { it.isNotBlank() }
                .joinToString(" ")

            val bldg = l?.addition0?.value?.takeIf { it.isNotBlank() } ?: ""

            return listOf(base, lot, bldg)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { null }
        }

        // 2) admcode/legalcode: land가 없으니 region만으로 표시
        val adminOnly = listOf(r.area1.name, r.area2.name, r.area3.name, r.area4.name)
            .filter { it.isNotBlank() }
            .joinToString(" ")

        return adminOnly.ifBlank { null }
    }

    //작성일 넘겨야해서 작성일 문자열 만들기 함수 생성
    fun nowString(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        val sdf = android.icu.text.SimpleDateFormat(pattern, java.util.Locale.getDefault())
        return sdf.format(System.currentTimeMillis())
    }

    //    날짜 포매터 함수
    fun formatKoreanDateTime(raw: String?): String {
        if (raw.isNullOrBlank()) return "작성일 없음"
        val out = java.text.SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", java.util.Locale.KOREA)
        out.timeZone = java.util.TimeZone.getDefault()

        val candidates = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        )
        for (p in candidates) {
            try {
                val inFmt = java.text.SimpleDateFormat(p, java.util.Locale.US).apply {
                    if (p.contains("XXX")) timeZone = java.util.TimeZone.getTimeZone("UTC")
                }
                val d = inFmt.parse(raw) ?: continue
                return out.format(d)
            } catch (_: Exception) {}
        }
        return "작성일 없음"
    }


    /** 화면표시(yyyy년 MM월 dd일 HH시 mm분) -> 서버전송(yyyy-MM-dd'T'HH:mm:ss) */
    fun displayToIsoSecondsOrNull(s: String?): String? {
        val raw = s?.trim().orEmpty()
        if (raw.isEmpty()) return null
        return try {
            val ldt = java.time.LocalDateTime.parse(raw, DISPLAY_FMT)
            ldt.format(ISO_SECONDS_FMT)
        } catch (e: Exception) {
            null
        }
    }

    /** 서버 ISO("2025-08-19T10:30:00" 등) -> 화면표시(yyyy년 MM월 dd일 HH시 mm분) */
    fun isoToDisplayOrEmpty(iso: String?): String {
        if (iso.isNullOrBlank()) return ""
        return try {
            // Offset/Z 포함 가능성까지 커버
            val ldt = runCatching {
                java.time.OffsetDateTime.parse(iso, DateTimeFormatter.ISO_DATE_TIME).toLocalDateTime()
            }.getOrElse {
                java.time.LocalDateTime.parse(iso, DateTimeFormatter.ISO_DATE_TIME)
            }
            ldt.format(DISPLAY_FMT)
        } catch (e: Exception) {
            // yyyy-MM-dd만 온 경우 보정
            runCatching {
                java.time.LocalDate.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE)
                    .atStartOfDay()
                    .format(DISPLAY_FMT)
            }.getOrDefault("")
        }
    }

    fun isoToDisplayNotTime(iso: String?): String {
        if (iso.isNullOrBlank()) return ""
        return try {
            // Offset/Z 포함 가능성까지 커버
            val ldt = runCatching {
                java.time.OffsetDateTime.parse(iso, DateTimeFormatter.ISO_DATE_TIME).toLocalDateTime()
            }.getOrElse {
                java.time.LocalDateTime.parse(iso, DateTimeFormatter.ISO_DATE_TIME)
            }
            ldt.format(DISPLAY_FMT_YMD)
        } catch (e: Exception) {
            // yyyy-MM-dd만 온 경우 보정
            runCatching {
                java.time.LocalDate.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE)
                    .atStartOfDay()
                    .format(DISPLAY_FMT_YMD)
            }.getOrDefault("")
        }
    }

}