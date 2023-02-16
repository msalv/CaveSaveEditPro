package com.leo.cse.frontend.editor.pages;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.alignRight;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.centerVertical;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.constraints;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.leftMargin;

import com.leo.cse.backend.exe.GameResourcesLoadingState;
import com.leo.cse.backend.exe.OnGameResourcesLoadingStateChangeListener;
import com.leo.cse.backend.profile.ProfileFields;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.backend.profile.ProfileStateChangeListener;
import com.leo.cse.backend.profile.ProfileStateEvent;
import com.leo.cse.backend.profile.undo.ProfileEdit;
import com.leo.cse.backend.res.GameResourcesManager;
import com.leo.cse.frontend.Config;
import com.leo.cse.frontend.Resources;
import com.leo.cse.backend.mci.MCI;
import com.leo.cse.dto.Flag;
import com.leo.cse.dto.FlagsDataSet;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.compound.LabeledCheckBox;
import com.leo.cse.frontend.ui.components.list.RetroScrollBarUI;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.editor.cells.FlagCellRenderer;
import com.leo.cse.frontend.editor.cells.FlagsHeaderRow;
import com.leo.cse.frontend.editor.cells.FlagsRow;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.JContainer;
import com.leo.cse.util.StringUtils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class FlagsPage extends JContainer implements ProfileStateChangeListener, OnGameResourcesLoadingStateChangeListener {
    private final FlagsHeaderRow header;
    private final JScrollPane scrollPane;
    private final JList<Flag> list;
    private final Component footer;
    private final FlagsRow.CellRenderer<Flag> rowRenderer = new FlagCellRenderer();

    private final ProfileManager profileManager;
    private final GameResourcesManager resourcesManager;

    private final FlagsDataSet<Flag> dataSet = new FlagsDataSet<>();

    public FlagsPage(ProfileManager profileManager, GameResourcesManager resourcesManager) {
        super();

        this.profileManager = profileManager;
        this.resourcesManager = resourcesManager;

        header = new FlagsHeaderRow();
        header.setPreferredSize(new Dimension(Integer.MAX_VALUE, 22));

        list = new JList<>();
        list.setCellRenderer(rowRenderer);
        list.setFixedCellHeight(23);

        final MouseListener mouseListener = new MouseListener();
        list.addMouseMotionListener(mouseListener);
        list.addMouseListener(mouseListener);

        scrollPane = new JScrollPane(list);
        scrollPane.setMinimumSize(new Dimension(0, Integer.MAX_VALUE));
        scrollPane.setBorder(new LineBorder(ThemeData.getForegroundColor()));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new RetroScrollBarUI());

        footer = createFooter();

        add(header);
        add(scrollPane);
        add(footer);

        rebuildDataSet();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        profileManager.addListener(this);
        resourcesManager.addListener(this);
    }

    @Override
    public void removeNotify() {
        profileManager.removeListener(this);
        resourcesManager.removeListener(this);
        super.removeNotify();
    }

    private Component createFooter() {
        final HorizontalLayout footer = new HorizontalLayout();

        footer.setBorder(new EmptyBorder(0, 18,0,18));
        footer.setMinimumSize(new Dimension(Integer.MAX_VALUE, 26));

        final LabeledCheckBox hideSystemFlagsCheckBox = new LabeledCheckBox();
        hideSystemFlagsCheckBox.setText("Hide System Flags");

        final LabeledCheckBox hideUndefinedFlagsCheckBox = new LabeledCheckBox();
        hideUndefinedFlagsCheckBox.setText("Hide Undefined Flags");

        final TextLabel infoLabel = new TextLabel();
        infoLabel.setFont(Resources.getFont());
        infoLabel.setSingleLine(true);
        infoLabel.setTextColor(ThemeData.getForegroundColor());
        infoLabel.setText("Shift - x10 scroll, Control - x100 scroll, Shift+Ctrl - x1000 scroll");
        infoLabel.setVisible(false); // scroll with steps is not implemented

        footer.add(hideUndefinedFlagsCheckBox, centerVertical(constraints()));
        footer.add(hideSystemFlagsCheckBox, centerVertical(leftMargin(16)));
        footer.add(infoLabel, centerVertical(alignRight()));

        hideUndefinedFlagsCheckBox.setChecked(shouldHideUndefinedFlags());
        hideSystemFlagsCheckBox.setChecked(shouldHideSystemFlags());

        hideSystemFlagsCheckBox.setOnCheckedStateListener(((button, isChecked) -> {
            Config.setBoolean(Config.KEY_HIDE_SYSTEM_FLAGS, isChecked);
            rebuildDataSet();
        }));

        hideUndefinedFlagsCheckBox.setOnCheckedStateListener(((button, isChecked) -> {
            Config.setBoolean(Config.KEY_HIDE_UNDEFINED_FLAGS, isChecked);
            rebuildDataSet();
        }));

        return footer;
    }

    private boolean shouldHideUndefinedFlags() {
        return Config.getBoolean(Config.KEY_HIDE_UNDEFINED_FLAGS, true);
    }

    private boolean shouldHideSystemFlags() {
        return Config.getBoolean(Config.KEY_HIDE_SYSTEM_FLAGS, true);
    }

    private void rebuildDataSet() {
        dataSet.clear();

        final MCI mci = profileManager.getCurrentMCI();

        for (int i = 0; i < Flag.FLAGS_COUNT; ++i) {
            final String desc = Flag.getDescription(mci, i);
            if (shouldHideUndefinedFlags() && StringUtils.isNullOrEmpty(desc)) {
                continue;
            }

            if (shouldHideSystemFlags() && !Flag.isValid(mci, i)) {
                continue;
            }

            final boolean value = Boolean.TRUE.equals(profileManager.getField(ProfileFields.FIELD_FLAGS, i));

            final Flag flag = new Flag(i, desc, value);
            dataSet.add(flag, flag.id);
        }

        list.setListData(dataSet.getItems());
    }

    @Override
    public void onMeasure(Container container, int maxWidth, int maxHeight) {
        final Insets insets = container.getInsets();
        int availWidth = maxWidth - (insets.right + insets.left);
        int totalHeight = maxHeight - (insets.bottom + insets.top);
        int availHeight = totalHeight;

        final Dimension menuSize = measureChild(header, availWidth, availHeight);
        availHeight -= menuSize.height;

        if (footer.isVisible()) {
            final Dimension footerSize = measureChild(footer, availWidth, availHeight);
            availHeight -= footerSize.height;
        }

        // no need to measure scrollPane since it will occupy all the available space

        setMeasuredDimensions(availWidth, totalHeight);
    }

    @Override
    public void onLayout(Container container) {
        final Insets insets = container.getInsets();
        int left = insets.left;
        int right = container.getWidth() - insets.right;
        int top = insets.top;
        int bottom = container.getHeight() - insets.bottom;

        final Dimension headerSize = getChildDimension(header);

        header.setBounds(
                left,
                top,
                headerSize.width,
                headerSize.height
        );

        top += headerSize.height;

        if (footer.isVisible()) {
            final Dimension footerSize = getChildDimension(footer);

            footer.setBounds(
                    left,
                    bottom - footerSize.height,
                    footerSize.width,
                    footerSize.height
            );

            bottom -= footerSize.height;
        }

        scrollPane.setBounds(
                left,
                top,
                right - left,
                bottom - top
        );
    }

    @Override
    public void onProfileStateChanged(ProfileStateEvent event, Object payload) {
        if (event == ProfileStateEvent.SLOT_CHANGED) {
            rebuildDataSet();
        } else if (isFlagChangedEvent(event, payload)) {
            onFlagChanged((ProfileEdit) payload);
        }
    }

    private boolean isFlagChangedEvent(ProfileStateEvent event, Object payload) {
        if (event == ProfileStateEvent.MODIFIED) {
            if (payload instanceof ProfileEdit) {
                final ProfileEdit edit = (ProfileEdit) payload;
                return ProfileFields.FIELD_FLAGS.equals(edit.getField());
            }
        }
        return false;
    }

    private void onFlagChanged(ProfileEdit edit) {
        final int flagId = edit.getIndex();
        if (dataSet.containsFlag(flagId)) {
            final Flag flag = dataSet.getFlagById(flagId);
            if (!Objects.equals(flag.value, edit.newValue())) {
                final int index = dataSet.indexOf(flagId);
                if (index >= 0 && index < dataSet.size()) {
                    dataSet.set(index, new Flag(flag.id, flag.description, !flag.value), flag.id);
                    list.repaint();
                }
            }
        }
    }

    @Override
    public void onGameResourcesLoadingStateChanged(GameResourcesLoadingState state, Object payload) {
        if (state == GameResourcesLoadingState.DONE || state == GameResourcesLoadingState.NONE) {
            rebuildDataSet();
        }
    }

    private class MouseListener extends MouseAdapter {
        private final Point point = new Point();

        @Override
        public void mouseMoved(MouseEvent e) {
            point.setLocation(e.getX(), e.getY());

            final int index = list.locationToIndex(point);

            if (rowRenderer.setHoveredIndex(index)) {
                list.repaint();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (rowRenderer.clearHover()) {
                list.repaint();
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            point.setLocation(e.getX(), e.getY());

            final int index = list.locationToIndex(point);
            if (index >= 0 && index < dataSet.size()) {
                final Flag old = dataSet.get(index);
                profileManager.setField(ProfileFields.FIELD_FLAGS, old.id, !old.value);
            }
        }
    }
}
