package com.rexosphere.leoconnect.util

import android.app.Activity
import java.lang.ref.WeakReference

object ActivityProvider {
    private var currentActivityRef: WeakReference<Activity>? = null

    var currentActivity: Activity?
        get() = currentActivityRef?.get()
        set(value) {
            currentActivityRef = if (value != null) WeakReference(value) else null
        }
}
