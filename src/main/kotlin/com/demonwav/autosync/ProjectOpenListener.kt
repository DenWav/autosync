package com.demonwav.autosync

import com.intellij.openapi.components.AbstractProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager

class ProjectOpenListener(project: Project) : AbstractProjectComponent(project) {

    override fun projectOpened() {
        val frame = WindowManager.getInstance().getFrame(myProject)
        // Don't want to add duplicate listeners
        frame.removeWindowFocusListener(AutoSyncFocusListener)
        if (AutoSyncSettings.getInstance(myProject).isEnabled) {
            frame.addWindowFocusListener(AutoSyncFocusListener)
        }
    }
}
