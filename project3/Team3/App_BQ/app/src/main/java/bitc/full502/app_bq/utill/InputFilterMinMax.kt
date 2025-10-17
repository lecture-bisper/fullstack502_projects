import android.text.InputFilter
import android.text.Spanned

//입출고 등록시 개수 입력시 보유 재고 수량 이상 입력 방지를 위한 수량 입력 필터 클래스
class InputFilterMinMax(
    private val min: Long,
    private val max: Long
) : InputFilter {
    override fun filter(
//        사용자 입력 텍스트
        source: CharSequence?, start: Int, end: Int,
//        EditText에 들어있는 기존 텍스트
        dest: Spanned?, dstart: Int, dend: Int
    ): CharSequence? {
//        최종 입력값 변수 , 커서 기준으로 잡힘
//        왼쪽 지점은 새 입력이 들어갈 위치 바로 앞까지(시작미지정, 커서 지정되면 기존의 앞까지)
        val newVal = (dest?.substring(0, dstart).orEmpty() +
//                중간은 사용자 입력의 시작과 끝 , orEmpty오류 발생으로 Activity에서 미입력 별도 검증 예정
                source?.subSequence(start, end) +
//                오른쪽 지점은 새입력이 들어간 바로 뒤부터(변경이 끝나는 바로 뒤 인덱스부터, 입력된 길이만큼)
                dest?.substring(dend, dest.length).orEmpty())

//        기존 문자열 지웠을때(다 지웠을 때) 0 반환
        if (newVal.isEmpty()) return "1"
//        복붙이나 천지인 특수문자 입력 고려해 정수로 변환함, 불가시 0 리턴
        val v = newVal.toIntOrNull() ?: return "1"
//        매개변수 min max 내의 경우 입력 허용, 아니면 0 반환
        return if (v in min..max) null else "1"
    }
}
