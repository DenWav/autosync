package com.demonwav.autosync

import com.intellij.ide.FrameStateListener
import com.intellij.ide.SaveAndSyncHandler
import com.intellij.openapi.vfs.VirtualFileManager

object AutoSyncFrameStateListener : FrameStateListener {
    override fun onFrameDeactivated() {}

    override fun onFrameActivated() {
        //FileDocumentManager.getInstance().saveAllDocuments()
        SaveAndSyncHandler.getInstance().refreshOpenFiles()
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true)
    }
}
