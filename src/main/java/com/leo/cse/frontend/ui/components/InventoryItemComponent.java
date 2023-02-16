package com.leo.cse.frontend.ui.components;

import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.centerHorizontal;
import static com.leo.cse.frontend.ui.layout.constraints.ConstraintsUtils.topMargin;

import com.leo.cse.dto.InventoryItem;
import com.leo.cse.frontend.ui.Gravity;
import com.leo.cse.frontend.ui.components.text.TextButton;
import com.leo.cse.frontend.ui.components.visual.ImageComponent;
import com.leo.cse.frontend.ui.layout.StackLayout;

import java.awt.Dimension;
import java.awt.Image;
import java.util.function.Consumer;

public class InventoryItemComponent extends StackLayout {
    private ImageComponent image;
    private TextButton button;

    private InventoryItem item;

    public InventoryItemComponent() {
        super();
        initComponent();
    }

    private void initComponent() {
        final TextButton button = new TextButton();
        button.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        button.setPadding(5, 5, 5, 5);
        button.setMinimumSize(new Dimension(Integer.MAX_VALUE, 61));
        this.button = button;

        final ImageComponent image = new ImageComponent();
        image.setPreferredSize(new Dimension(64, 32));
        this.image = image;

        add(button);
        add(image, centerHorizontal(topMargin(5)));
    }

    public void setOnClickListener(Consumer<InventoryItem> clickListener) {
        button.setOnClickListener(() -> {
            clickListener.accept(item);
        });
    }

    public void setText(String text) {
        button.setText(text);
    }

    public void setImage(Image image) {
        this.image.setImage(image);
    }

    public void bind(InventoryItem item) {
        this.item = item;
        if (item == null || item.id == 0) {
            setText("Add Item");
            setImage(null);
        } else {
            setText(item.toString());
            setImage(item.image);
        }
    }
}
