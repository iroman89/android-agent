/*
 * (c) Copyright IBM Corp. 2021
 * (c) Copyright Instana Inc. and contributors 2021
 */

package com.instana.android.core.util

import java.util.*

internal class Debouncer(
    private val millis: Long
) {
    private var timer: Timer? = null

    fun enqueue(action: () -> Unit) {
        timer?.cancel()

        val task = object : TimerTask() {
            override fun run() = action()
        }
        timer = Timer("Debouncer").apply {
            schedule(task, millis)
        }
    }
}
