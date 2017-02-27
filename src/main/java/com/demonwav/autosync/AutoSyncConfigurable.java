package com.demonwav.autosync;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class AutoSyncConfigurable implements Configurable {

    private final Project project;

    private JPanel panel;
    private JCheckBox enableCheckBox;

    public AutoSyncConfigurable(Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Auto Sync Settings";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return panel;
    }

    @Override
    public boolean isModified() {
        return AutoSyncSettings.getInstance(project).isEnabled() != enableCheckBox.isSelected();
    }

    @Override
    public void apply() throws ConfigurationException {
        AutoSyncSettings.getInstance(project).setEnabled(enableCheckBox.isSelected());
        final JFrame frame = WindowManager.getInstance().getFrame(project);
        frame.removeWindowFocusListener(AutoSyncFocusListener.INSTANCE); // for good measure
        if (AutoSyncSettings.getInstance(project).isEnabled()) {
            frame.addWindowFocusListener(AutoSyncFocusListener.INSTANCE);
        }
    }

    @Override
    public void reset() {
        enableCheckBox.setSelected(AutoSyncSettings.getInstance(project).isEnabled());
    }
}
