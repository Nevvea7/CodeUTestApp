package app.nevvea.nomnom;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Anna on 8/5/15.
 */
public class BlackListAdapter extends CursorAdapter {


    public static class ViewHolder {
        public TextView restNameTextView;

        public ViewHolder(View view) {
            restNameTextView = (TextView) view.findViewById(R.id.blacklist_item_name);
        }
    }

    public BlackListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_blacklist, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.restNameTextView.setText(cursor.getString(BlackListActivity.COL_REST_NAME));
    }
}
