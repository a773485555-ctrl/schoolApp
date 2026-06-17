package com.school.management.core.sync

import javax.inject.Inject

interface SyncScheduler {
    fun triggerImmediateSync()
}

class SyncSchedulerImpl @Inject constructor() : SyncScheduler {
    override fun triggerImmediateSync() {
        // No-op for now
    }
}
