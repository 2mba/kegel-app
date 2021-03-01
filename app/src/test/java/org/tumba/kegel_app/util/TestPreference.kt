package org.tumba.kegel_app.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.tumba.kegel_app.core.system.Preference

class TestPreference<T>(override var value: T, override val key: String = "") : Preference<T> {

    override fun asFlow(): Flow<T> = flowOf(value)
}