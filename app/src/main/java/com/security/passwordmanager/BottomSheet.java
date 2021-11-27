package com.security.passwordmanager;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheet {

    public static final int VIEW_EDIT = 0;
    public static final int VIEW_COPY = 1;
    public static final int VIEW_DELETE = 2;

    private final BottomSheetDialog bottomSheetDialog;
    private final View bottomSheetView;

    private final Support mSupport;
    private final Context mContext;

    private final int[] id = new int[]{
            R.id.main_bottom_sheet_edit_password,
            R.id.main_bottom_sheet_copy_info,
            R.id.main_bottom_sheet_delete_password
    };

    public BottomSheet(@NonNull Context context, @NonNull View root) {
        this.mContext = context.getApplicationContext();

        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetView = LayoutInflater.from(context).inflate(
                R.layout.main_bottom_sheet,
                root.findViewById(R.id.main_bottom_sheet_container)
        );
        mSupport = Support.get(context);
    }

    public BottomSheet(@NonNull Activity activity) {
        this(activity, activity.getWindow().getDecorView());
    }

    public void setHeading(String headText, String subtitleText) {
        LinearLayout head = bottomSheetView.findViewById(R.id.main_bottom_sheet_heading);
        head.setVisibility(View.VISIBLE);

        TextView headName = head
                .findViewById(R.id.list_item_text_view_name);

        TextView headUrl = head
                .findViewById(R.id.list_item_text_view_url);

        headName.setText(headText);
        headUrl.setText(subtitleText);

        headName.setTextColor(mSupport.getFontColor());
        headUrl.setTextColor(mContext.getColor(android.R.color.darker_gray));
    }

    public void updateImageAndText(@StringRes int[] names, @DrawableRes int[] images) {
        int min = Math.min(names.length, images.length);
        int count = Math.min(min, id.length);

        for (int i = 0; i < count; i++)
            updateImageAndText(i, names[i], images[i]);
    }



    public void updateImageAndText(@BottomSheetType int type, @StringRes int text, @DrawableRes Integer image) {
        LinearLayout layout = bottomSheetView.findViewById(id[type]);
        ImageView imageView = layout.findViewById(R.id.bottom_sheet_field_image_view);
        Button button = layout.findViewById(R.id.bottom_sheet_field_button);
        if (image != null)
            imageView.setImageDrawable(ContextCompat
                    .getDrawable(mContext, image));

        imageView.setImageTintList(ColorStateList.valueOf(mSupport.getFontColor()));
        imageView.setContentDescription(mContext.getString(text));

        button.setText(text);
        button.setTextColor(mSupport.getFontColor());
    }


    public void setOnClickListener(@BottomSheetType int type, View.OnClickListener listener) {
        Button button = bottomSheetView
                .findViewById(id[type])
                .findViewById(R.id.bottom_sheet_field_button);
        button.setOnClickListener(listener);
    }

    public void start() {
        bottomSheetView.setBackgroundColor(mSupport.getBackgroundColor());
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void stop() {
        bottomSheetDialog.dismiss();
    }

    @IntDef({VIEW_EDIT, VIEW_COPY, VIEW_DELETE})
    public @interface BottomSheetType {}
}
