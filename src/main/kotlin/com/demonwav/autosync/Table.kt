package com.demonwav.autosync

import com.intellij.openapi.roots.ui.CellAppearanceEx
import com.intellij.openapi.roots.ui.FileAppearanceService
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.ColoredTableCellRenderer
import com.intellij.util.ui.ItemRemovable
import javax.swing.BorderFactory
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

class TableModel : DefaultTableModel(), ItemRemovable {
    override fun getColumnName(column: Int) = null
    override fun getColumnClass(columnIndex: Int) = TableItem::class.java
    override fun getColumnCount() = 1
    override fun isCellEditable(row: Int, column: Int) = false
    fun getValueAt(row: Int) = getValueAt(row, 0) as TableItem
    fun addTableItem(item: TableItem) = addRow(arrayOf(item))
}

class TableItem {
    val url: String
    val cellAppearance: CellAppearanceEx

    constructor(file: VirtualFile) {
        url = file.url
        cellAppearance = FileAppearanceService.getInstance().forVirtualFile(file)
    }

    constructor(url: String) {
        this.url = url

        val file = VirtualFileManager.getInstance().findFileByUrl(url)
        cellAppearance = if (file != null) {
            FileAppearanceService.getInstance().forVirtualFile(file)
        } else {
            FileAppearanceService.getInstance().forInvalidUrl(url)
        }
    }
}

class Renderer : ColoredTableCellRenderer() {
    override fun customizeCellRenderer(table: JTable?, value: Any?, selected: Boolean, hasFocus: Boolean, row: Int, column: Int) {
        setPaintFocusBorder(false)
        setFocusBorderAroundIcon(true)
        border = NO_FOCUS_BORDER

        (value as TableItem).cellAppearance.customize(this)
    }

    companion object {
        private val NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1)
    }
}
