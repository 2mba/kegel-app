package org.tumba.kegel_app.ui.exercise

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.list_item_exercise_background_mode.view.*
import org.tumba.kegel_app.R

class BackgroundModeSelectorAdapter(context: Context) : ArrayAdapter<ExerciseBackgroundMode>(
    context,
    R.layout.list_item_exercise_background_mode,
    ExerciseBackgroundMode.values()
) {

    private val values: List<String>
    private val proIcon: Drawable?

    init {
        val strings = getExerciseBackgroundModeStringsMap()
        values = ExerciseBackgroundMode.values().map { context.getString(strings.getValue(it)) }
        proIcon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_pro, null)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val item = getItem(position)
        view.value.text = values.getOrNull(position).orEmpty()
        view.value.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            if (item == ExerciseBackgroundMode.FLOATING_VIEW) proIcon else null,
            null
        )
        return view
    }
}