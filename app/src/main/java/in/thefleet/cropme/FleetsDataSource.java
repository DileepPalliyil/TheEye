package in.thefleet.cropme;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class FleetsDataSource extends ContentProvider {

    private static final String TAG = "FleetsDataSource";

    private static final String AUTHORITY = "in.thefleet.cropme.fleetsdatasource";
    private static final String BASE_PATH = "fleetspic";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int FLEETS = 1;
    private static final int FLEETS_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static  {
        uriMatcher.addURI(AUTHORITY,BASE_PATH,FLEETS);
        uriMatcher.addURI(AUTHORITY,BASE_PATH + "/#",FLEETS_ID);

    }
    private SQLiteDatabase database;
    FleetsDBOpenHelper dbhelper;


    @Override
    public boolean onCreate() {
        dbhelper = new FleetsDBOpenHelper(getContext());
        database = dbhelper.getWritableDatabase();
        Log.i(TAG, "Database opened");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == FLEETS_ID) {
            selection = FleetsDBOpenHelper.FLEETS_ID + "=" + uri.getLastPathSegment();
        }
        // Log.d(TAG,"Selection: "+selection);
        return database.query(FleetsDBOpenHelper.TABLE_FLEETS,FleetsDBOpenHelper.ALL_COLUMNS,selection,null,null,null,
                FleetsDBOpenHelper.FLEETS_REGNO+ " DESC");
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(FleetsDBOpenHelper.TABLE_FLEETS,null,values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(FleetsDBOpenHelper.TABLE_FLEETS,selection,selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[]selectionArgs) {
        return database.update(FleetsDBOpenHelper.TABLE_FLEETS,values,selection,selectionArgs);
    }
}
