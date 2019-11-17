package org.tumba.kegel_app.core.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

interface IViewCreationStrategy {

    fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    companion object {
        val NONE = object : IViewCreationStrategy {
            override fun createView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                return null
            }
        }
    }
}

class ResourceCreationStrategy(
    @LayoutRes private val layout: Int
) : IViewCreationStrategy {

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout, container, false)
    }
}
