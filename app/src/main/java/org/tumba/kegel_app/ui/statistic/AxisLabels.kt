package org.tumba.kegel_app.ui.statistic

import android.graphics.Canvas
import android.graphics.Paint
import com.db.williamchart.Labels
import com.db.williamchart.data.Label

class AxisLabels(private val topSpace: Float): Labels {

    override fun draw(canvas: Canvas, paint: Paint, xLabels: List<Label>) {
        xLabels.forEach {
            canvas.drawText(
                it.label,
                it.screenPositionX,
                it.screenPositionY + topSpace,
                paint
            )
        }
    }
}