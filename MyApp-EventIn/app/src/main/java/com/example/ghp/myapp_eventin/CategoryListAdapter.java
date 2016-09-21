package com.example.ghp.myapp_eventin;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ghp on 28-Jul-16.
 */
public class CategoryListAdapter extends ArrayAdapter<Category> {

    ArrayList<Category> categories;
    Context context;

    public CategoryListAdapter(Activity context, ArrayList<Category> categories) {
        super(context, R.layout.list_of_categories, categories);
        this.categories = categories;
        this.context = context;
    }

    public Category get(int position) {
        return categories.get(position); // or maybe mList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_of_categories, null, true);

        TextView CategoryName = (TextView) row.findViewById(R.id.category);

        CategoryName.setText(Html.fromHtml(categories.get(position).Name));


        return row;
    }


}
