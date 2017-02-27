package com.demonwav.autosync

import com.google.common.collect.Sets
import com.intellij.ide.FrameStateListener
import com.intellij.ide.IdeBundle
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager
import com.intellij.openapi.vfs.newvfs.NewVirtualFile
import com.intellij.openapi.vfs.newvfs.RefreshQueue
import com.intellij.openapi.wm.WindowManager
import java.util.HashSet

object AutoSyncFrameStateListener : FrameStateListener {
    val projects: HashSet<Project> = Sets.newHashSet<Project>()

    override fun onFrameDeactivated() {}

    override fun onFrameActivated() {
        projects.removeIf { it.isDisposed }
        projects.forEach(this::performSync)
    }

    private fun performSync(project: Project) {
        runWriteAction {
            (project.baseDir as? NewVirtualFile)?.markDirtyRecursively()
        }

        RefreshQueue.getInstance().refresh(true, true, Runnable {
            postRefresh(project)
        }, project.baseDir)
    }

    private fun postRefresh(project: Project) {
        val dirtyScopeManager = VcsDirtyScopeManager.getInstance(project)
        dirtyScopeManager.dirDirtyRecursively(project.baseDir)

        WindowManager.getInstance().getStatusBar(project)
            ?.info = IdeBundle.message("action.async.completed.successfully", IdeBundle.message("action.synchronize.file"))
    }
}
