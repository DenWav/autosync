package com.demonwav.autosync

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@State(name = "AutoSyncSettings", storages = [Storage("auto_sync.xml")])
class AutoSyncSettings : PersistentStateComponent<AutoSyncSettings.State> {

    data class State(var modified: Boolean = false,
                     var enabled: Boolean = false,
                     var timeBetweenSyncs: Long = 15,
                     var includedDirs: MutableList<String> = mutableListOf())

    private var state = State()

    override fun getState() = state
    override fun loadState(state: State) {
        this.state = state
    }

    var modified
        get() = state.modified
        set(modified) {
            state.modified = modified
        }

    var isEnabled
        get() = state.enabled
        set(enabled) {
            state.enabled = enabled
        }

    var timeBetweenSyncs
        get() = state.timeBetweenSyncs
        set(timeBetweenSyncs) {
            state.timeBetweenSyncs = timeBetweenSyncs
        }

    var excludedUrls
        get() = state.includedDirs
        set(excludedUrls) {
            state.includedDirs = excludedUrls
        }

    companion object {
        fun getInstance(project: Project): AutoSyncSettings {
            val service = ServiceManager.getService(project, AutoSyncSettings::class.java)
            if (!service.modified && service.excludedUrls.isEmpty()) {
                service.excludedUrls.add(project.baseDir.url)
            }
            return service
        }
    }
}
