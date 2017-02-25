package com.demonwav.autosync

import com.intellij.ide.FrameStateManager
import com.intellij.openapi.components.AbstractProjectComponent
import com.intellij.openapi.project.Project

class ProjectOpenListener(project: Project) : AbstractProjectComponent(project) {

    override fun projectOpened() {
        if (AutoSyncSettings.getInstance(myProject).isEnabled) {
            // Don't want to add duplicate listeners
            FrameStateManager.getInstance().removeListener(AutoSyncFrameStateListener)
            FrameStateManager.getInstance().addListener(AutoSyncFrameStateListener)
        }
    }

    override fun projectClosed() {
        FrameStateManager.getInstance().removeListener(AutoSyncFrameStateListener)
    }
}
