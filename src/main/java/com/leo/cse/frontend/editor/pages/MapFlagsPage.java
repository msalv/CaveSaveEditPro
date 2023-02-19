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
import com.leo.cse.frontend.Resources;
import com.leo.cse.backend.mci.MCI;
import com.leo.cse.dto.FlagsDataSet;
import com.leo.cse.dto.MapFlag;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.list.RetroScrollBarUI;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.dialogs.MapFlagsInfoDialog;
import com.leo.cse.frontend.editor.cells.FlagsHeaderRow;
import com.leo.cse.frontend.editor.cells.FlagsRow;
import com.leo.cse.frontend.editor.cells.MapFlagCellRenderer;
import com.leo.cse.frontend.ui.layout.HorizontalLayout;
import com.leo.cse.frontend.ui.layout.JContainer;

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

public class MapFlagsPage extends JContainer implements
        ProfileStateChangeListener,
        OnGameResourcesLoadingStateChangeListener {
    private final FlagsHeaderRow header;
    private final JScrollPane scrollPane;
    private final JList<MapFlag> list;
    private final Component footer;
    private final FlagsRow.CellRenderer<MapFlag> rowRenderer = new MapFlagCellRenderer();

    private final ProfileManager profileManager;
    private final GameResourcesManager gameResourcesManager;
    private final FlagsDataSet<MapFlag> dataSet = new FlagsDataSet<>();

    public MapFlagsPage(ProfileManager profileManager, GameResourcesManager gameResourcesManager) {
        super();

        this.profileManager = profileManager;
        this.gameResourcesManager = gameResourcesManager;

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
        scrollPane.setViewportBorder(null);

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
        gameResourcesManager.addListener(this);
    }

    @Override
    public void removeNotify() {
        profileManager.removeListener(this);
        gameResourcesManager.removeListener(this);
        super.removeNotify();
    }

    private Component createFooter() {
        final HorizontalLayout footer = new HorizontalLayout();

        footer.setBorder(new EmptyBorder(0, 18,0,18));
        footer.setMinimumSize(new Dimension(Integer.MAX_VALUE, 26));

        final TextLabel helpLabel = new TextLabel();
        helpLabel.setFont(Resources.getFont());
        helpLabel.setSingleLine(true);
        helpLabel.setTextColor(ThemeData.getForegroundColor());
        helpLabel.setText("\u263C What are map flags?");

        final TextButton helpButton = new TextButton();
        helpButton.setText("Learn more");
        helpButton.setOnClickListener(() -> {
            final MapFlagsInfoDialog dialog = new MapFlagsInfoDialog(null, this);
            dialog.setVisible(true);
        });

        footer.add(helpLabel, centerVertical(constraints()));
        footer.add(helpButton, centerVertical(leftMargin(10)));

        return footer;
    }

    private void rebuildDataSet() {
        dataSet.clear();

        final MCI mci = profileManager.getCurrentMCI();

        for (int i = 0; i < MapFlag.FLAGS_COUNT; ++i) {
            final String desc = MapFlag.getDescription(mci, gameResourcesManager, i);
            final boolean value = Boolean.TRUE.equals(profileManager.getField(ProfileFields.FIELD_MAP_FLAGS, i));
            dataSet.add(new MapFlag(i, desc, value), i);
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
                return ProfileFields.FIELD_MAP_FLAGS.equals(edit.getField());
            }
        }
        return false;
    }

    private void onFlagChanged(ProfileEdit edit) {
        final int flagId = edit.getIndex();
        if (dataSet.containsFlag(flagId)) {
            final MapFlag flag = dataSet.getFlagById(flagId);
            if (!Objects.equals(flag.value, edit.newValue())) {
                final int index = dataSet.indexOf(flagId);
                if (index >= 0 && index < dataSet.size()) {
                    dataSet.set(index, new MapFlag(flag.id, flag.description, !flag.value), flag.id);
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
                final MapFlag old = dataSet.get(index);
                profileManager.setField(ProfileFields.FIELD_MAP_FLAGS, old.id, !old.value);
            }
        }
    }
}
