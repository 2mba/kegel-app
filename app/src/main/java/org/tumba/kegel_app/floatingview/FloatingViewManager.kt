package org.tumba.kegel_app.floatingview

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import org.tumba.kegel_app.MainActivity
import org.tumba.kegel_app.R
import org.tumba.kegel_app.databinding.LayoutSystemOverlayExerciseBinding
import org.tumba.kegel_app.domain.ExerciseState
import org.tumba.kegel_app.ui.common.ExerciseNameProvider
import javax.inject.Inject


class FloatingViewManager @Inject constructor(
    private val exerciseNameProvider: ExerciseNameProvider,
    private val context: Context
) {

    private var floatingView: LayoutSystemOverlayExerciseBinding? = null

    fun showFloatingView() {
        EasyFloat.with(context.applicationContext)
            .setTag(TAG_FLOATING_VIEW_EXERCISE)
            .setDragEnable(true)
            .setShowPattern(ShowPattern.BACKGROUND)
            .setSidePattern(SidePattern.RESULT_SIDE)
            .setLayout(R.layout.layout_system_overlay_exercise)
            .registerCallback {
                createResult { _, _, view ->
                    if (view != null) {
                        floatingView = LayoutSystemOverlayExerciseBinding.bind((view as ViewGroup).children.first())
                        floatingView?.root?.setOnClickListener { openApp() }
                    }
                }
                dismiss { floatingView = null }
            }
            .show()
    }

    fun hideFloatingView() {
        EasyFloat.dismiss(TAG_FLOATING_VIEW_EXERCISE)
        floatingView = null
    }

    fun updateFloatingViewState(state: ExerciseState) {
        val textView = floatingView?.textView ?: return
        textView.text = exerciseNameProvider.exerciseName(state)
        textView.backgroundTintList = ColorStateList.valueOf(
            ResourcesCompat.getColor(textView.resources, getExerciseProgressColor(state), null)
        )
    }

    private fun openApp() {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        startActivity(context, intent, null)
    }

    @ColorRes
    private fun getExerciseProgressColor(state: ExerciseState): Int {
        return when (state) {
            is ExerciseState.Preparation -> R.color.floatingViewExerciseColorPreparation
            is ExerciseState.Holding -> R.color.floatingViewExerciseColorHolding
            is ExerciseState.Relax -> R.color.floatingViewExerciseColorRelax
            is ExerciseState.Pause -> R.color.floatingViewExerciseColorPaused
            is ExerciseState.Finish -> R.color.floatingViewExerciseColorFinish
            else -> R.color.transparent
        }
    }

    companion object {
        private const val TAG_FLOATING_VIEW_EXERCISE = "TAG_FLOATING_VIEW_EXERCISE"
    }
}