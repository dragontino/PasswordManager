package com.security.passwordmanager;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.security.passwordmanager.settings.Support;

import java.util.ArrayList;

public class BottomSheet {

    public static final int VIEW_EDIT = 0;
    public static final int VIEW_COPY = 1;
    public static final int VIEW_DELETE = 2;

    private final BottomSheetDialog bottomSheetDialog;
    private final View bottomSheetView;

    private final Support mSupport;
    private final Context mContext;

    private final ArrayList<Integer> id;
//            R.id.main_bottom_sheet_edit_password,
//            R.id.main_bottom_sheet_copy_info,
//            R.id.main_bottom_sheet_delete_password
//    };

    public BottomSheet(@NonNull Context context, @NonNull View root) {
        this.mContext = context.getApplicationContext();

        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetView = LayoutInflater.from(context).inflate(
                R.layout.main_bottom_sheet,
                root.findViewById(R.id.main_bottom_sheet_container)
        );

        id = new ArrayList<>();
        id.add(R.id.main_bottom_sheet_edit_password);
        id.add(R.id.main_bottom_sheet_copy_info);
        id.add(R.id.main_bottom_sheet_delete_password);

        mSupport = Support.getInstance(context);
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
                .findViewById(R.id.list_item_text_view_subtitle);

        headName.setText(headText);
        headUrl.setText(subtitleText);

        headName.setTextColor(mSupport.getFontColor());
        headUrl.setTextColor(mContext.getColor(android.R.color.darker_gray));
    }

//    public void addView(
//            @DrawableRes int image, @StringRes int name, View.OnClickListener listener) {
//
//        for (int i = 0; i < 3; i++)
//            bottomSheetView.findViewById(id.get(i)).setVisibility(View.GONE);
//
//        View view = LayoutInflater.from(mContext)
//                .inflate(R.layout.bottom_sheet_field, (ViewGroup) bottomSheetView);
//
//        int type = id.size();
//        id.add(type);
//        view.setId(type);
//        updateImageAndText(type, name, image);
//        setOnClickListener(type, listener);
//    }

    public void updateImageAndText(@StringRes int[] names, @DrawableRes int[] images) {
        int min = Math.min(names.length, images.length);
        int count = Math.min(min, id.size());

        for (int i = 0; i < count; i++)
            updateImageAndText(i, names[i], images[i]);

        if (count < id.size()) for (int i = count; i < id.size(); i++)
            bottomSheetView.findViewById(id.get(i)).setVisibility(View.GONE);
    }



    public void updateImageAndText(int type, @StringRes int text, @DrawableRes Integer image) {
        LinearLayout layout = bottomSheetView.findViewById(id.get(type));
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
                .findViewById(id.get(type))
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
