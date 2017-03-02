package com.demonwav.autosync

import com.intellij.ide.IdeBundle
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil.escapeMnemonics
import com.intellij.openapi.util.text.StringUtil.firstLast
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.NewVirtualFile
import com.intellij.openapi.vfs.newvfs.RefreshQueue
import com.intellij.openapi.wm.WindowManager
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

object AutoSyncFocusListener : WindowFocusListener {

    val pastSyncs = ConcurrentHashMap<Project, Instant>()
    val runningSyncs = HashSet<Project>()

    override fun windowGainedFocus(e: WindowEvent) {
        val project = WindowManager.getInstance().allProjectFrames.firstOrNull { it === e.component }?.project ?: return
        val settings = AutoSyncSettings.getInstance(project)
        if (settings.isEnabled) {
            performSync(project, settings)
        }
    }

    override fun windowLostFocus(e: WindowEvent) {}

    private fun performSync(project: Project, settings: AutoSyncSettings) {
        if (project.isDisposed) {
            return
        }

        val time = pastSyncs[project]
        pastSyncs[project] = Instant.now()

        if (time != null && time.plus(Duration.ofMinutes(settings.timeBetweenSyncs)).isAfter(Instant.now())) {
            pastSyncs[project] = Instant.now()
            return
        }

        runningSyncs.add(project)
        val files = mutableListOf<VirtualFile>()
        runWriteAction {
            for (url in settings.excludedUrls) {
                val file = VirtualFileManager.getInstance().findFileByUrl(url) as? NewVirtualFile ?: continue
                files.add(file)
                if (file.isDirectory) {
                    file.markDirtyRecursively()
                } else {
                    file.markDirty()
                }
            }
        }

        RefreshQueue.getInstance().refresh(true, true, Runnable {
            postRefresh(project, files)
        }, *files.toTypedArray())
    }

    private fun postRefresh(project: Project, files: List<VirtualFile>) {
        val dirtyScopeManager = VcsDirtyScopeManager.getInstance(project)
        for (file in files) {
            if (file.isDirectory) {
                dirtyScopeManager.dirDirtyRecursively(file)
            } else {
                dirtyScopeManager.fileDirty(file)
            }
        }

        WindowManager.getInstance().getStatusBar(project)?.info = IdeBundle.message(
            "action.sync.completed.successfully",
            getMessage(files)
        )
        runningSyncs.remove(project)
    }

    private fun getMessage(files: List<VirtualFile>): String {
        return if (files.size == 1) IdeBundle.message("action.synchronize.file", escapeMnemonics(firstLast(files[0].name, 20))) else
            IdeBundle.message("action.synchronize.selected.files")
    }
}
