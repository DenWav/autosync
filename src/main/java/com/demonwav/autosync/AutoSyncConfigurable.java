package com.demonwav.autosync;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class AutoSyncConfigurable implements Configurable {

    private static final SpinnerNumberModel model = new SpinnerNumberModel(15, 0, 60, 1);

    private final Project project;

    private JPanel panel;
    private JCheckBox enableCheckBox;
    private JSpinner timeSpinner;

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
        timeSpinner.setModel(model);
        return panel;
    }

    @Override
    public boolean isModified() {
        final AutoSyncSettings settings = AutoSyncSettings.getInstance(project);
        return settings.isEnabled() != enableCheckBox.isSelected() ||
            settings.getTimeBetweenSyncs() != ((Number) timeSpinner.getValue()).longValue();
    }

    @Override
    public void apply() throws ConfigurationException {
        final AutoSyncSettings settings = AutoSyncSettings.getInstance(project);
        settings.setEnabled(enableCheckBox.isSelected());
        settings.setTimeBetweenSyncs(((Number) timeSpinner.getValue()).longValue());

        final JFrame frame = WindowManager.getInstance().getFrame(project);
        frame.removeWindowFocusListener(AutoSyncFocusListener.INSTANCE); // for good measure

        if (settings.isEnabled()) {
            frame.addWindowFocusListener(AutoSyncFocusListener.INSTANCE);
        }
    }

    @Override
    public void reset() {
        final AutoSyncSettings settings = AutoSyncSettings.getInstance(project);
        enableCheckBox.setSelected(settings.isEnabled());
        timeSpinner.setValue(settings.getTimeBetweenSyncs());
    }
}
