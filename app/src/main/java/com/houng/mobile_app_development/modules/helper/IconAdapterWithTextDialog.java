package com.houng.mobile_app_development.modules.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.houng.mobile_app_development.R;

public class IconAdapterWithTextDialog extends ArrayAdapter<String> {
    private final String[] items;
    private final int[] icons;
    private final LayoutInflater inflater;

    public IconAdapterWithTextDialog(@NonNull Context context, String[] items, int[] icons) {
        super(context, R.layout.row_text__icon, items);
        this.items = items;
        this.icons = icons;
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_text__icon, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.item_icon);
        TextView textView = convertView.findViewById(R.id.item_text);
        imageView.setImageResource(icons[position]);
        textView.setText(items[position]);

        return convertView;
    }
}
