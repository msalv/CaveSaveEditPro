package com.leo.cse.frontend.ui.components;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.centerHorizontal;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.constraints;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.dto.WarpSlot;
import com.leo.cse.frontend.Resources;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.ThemeData;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.text.TextLabel;
import com.leo.cse.frontend.ui.components.visual.ImageComponent;
import com.leo.cse.frontend.ui.components.visual.RectComponent;
import com.leo.cse.frontend.ui.layout.StackLayout;
import com.leo.cse.frontend.ui.layout.VerticalLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.function.Consumer;

import javax.swing.border.EmptyBorder;

public class WarpSlotComponent extends LabeledGroup {
    private ImageComponent image;
    private TextLabel title;
    private TextButton locationButton;

    private WarpSlot warpSlot;

    public WarpSlotComponent() {
        super();

        setBorder(new EmptyBorder(0, 0, 12, 0));
        setTopContentMargin(-6);

        initContent();
    }

    private void initContent() {
        final VerticalLayout layout = new VerticalLayout();

        final StackLayout header = new StackLayout();

        image = new ImageComponent();
        image.setPreferredSize(new Dimension(64, 32));
        image.setPlaceholderColor(ThemeData.getHoverColor());
        image.setEnabled(false);

        title = new TextLabel();
        title.setSingleLine(true);
        title.setFont(Resources.getFont());
        title.setForeground(ThemeData.getForegroundColor());
        title.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        title.setMinimumSize(new Dimension(Integer.MAX_VALUE, 82));
        title.setPadding(10, 0, 10, 10);

        header.add(image, centerHorizontal(topMargin(16)));
        header.add(title);

        final Component separator = new RectComponent();
        separator.setMinimumSize(new Dimension(Integer.MAX_VALUE, 1));

        final TextLabel destLabel = new TextLabel();
        destLabel.setFont(Resources.getFont());
        destLabel.setForeground(ThemeData.getForegroundColor());
        destLabel.setSingleLine(true);
        destLabel.setText("Location:");

        locationButton = new TextButton();
        locationButton.setGravity(Gravity.LEFT);
        locationButton.setMinimumSize(new Dimension(Integer.MAX_VALUE, 0));

        layout.add(header);
        layout.add(separator, topMargin(-1));
        layout.add(destLabel, constraints(10, 8, 10, 0));
        layout.add(locationButton, constraints(10, 6, 10, 0));

        setContent(layout);
    }

    public void setOnClickListener(Consumer<WarpSlot> clickListener) {
        title.setOnClickListener(() -> {
            clickListener.accept(warpSlot);
        });
    }

    public void setOnLocationButtonClickListener(Consumer<WarpSlot> clickListener) {
        locationButton.setOnClickListener(() -> {
            clickListener.accept(warpSlot);
        });
    }

    public void bind(WarpSlot warpSlot) {
        this.warpSlot = warpSlot;
        image.setImage(warpSlot.image);
        title.setText(String.format("%d - %s", warpSlot.id, warpSlot.name));
        locationButton.setText(String.format("%d - %s", warpSlot.locationId, warpSlot.locationName));
    }
}
