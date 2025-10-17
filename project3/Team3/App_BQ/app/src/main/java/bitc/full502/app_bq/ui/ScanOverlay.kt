package bitc.full502.app_bq.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ScanOverlay @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : View(ctx, attrs) {

    private val maskPaint = Paint().apply { color = Color.parseColor("#80000000") } // 바깥 반투명
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    private val strokePaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = resources.displayMetrics.density * 3
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    var boxSizeDp = 260f
    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val layer = canvas.saveLayer(null, null)

        // 1) 전체 어둡게
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), maskPaint)

        // 2) 중앙 박스 영역 계산
        val box = dp(boxSizeDp)
        rect.set(
            (width - box)/2f, (height - box)/2f,
            (width + box)/2f, (height + box)/2f
        )

        // 3) 중앙만 "뚫기"
        val r = dp(10f)
        canvas.drawRoundRect(rect, r, r, clearPaint)
        // 4) 테두리
        canvas.drawRoundRect(rect, r, r, strokePaint)

        canvas.restoreToCount(layer)
    }

    fun getBoxRect(): RectF = RectF(rect)

    private fun dp(v: Float) = v * resources.displayMetrics.density
}
