package com.demonwav.autosync

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@State(name = "AutoSyncSettings", storages = arrayOf(Storage("auto_sync.xml")))
class AutoSyncSettings : PersistentStateComponent<AutoSyncSettings.State> {

    data class State(var enabled: Boolean = false)

    private var state = State()

    override fun getState() = state
    override fun loadState(state: State) {
        this.state = state
    }

    var isEnabled
        get() = state.enabled
        set(enabled) {
            state.enabled = enabled
        }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): AutoSyncSettings = ServiceManager.getService(project, AutoSyncSettings::class.java)
    }
}
