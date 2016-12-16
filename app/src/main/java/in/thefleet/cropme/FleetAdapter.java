package in.thefleet.cropme;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import in.thefleet.cropme.model.Fleet;

public class FleetAdapter  extends CursorAdapter {

    public FleetAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.item_fleet,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (cursor.getCount() > 0) {

            String regNo = cursor.getString(
                    cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_REGNO));

            TextView tv = (TextView) view.findViewById(R.id.tvRegNo);
            tv.setText(regNo);

            String fleetId = cursor.getString(
                    cursor.getColumnIndex(FleetsDBOpenHelper.FLEETS_KEY));

            TextView tv3 = (TextView) view.findViewById(R.id.tvFleetId);
            tv3.setText(fleetId);

        }
    }

}
