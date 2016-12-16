package in.thefleet.cropme;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.thefleet.cropme.model.Fleet;

public class FleetJSONParser {
    public static List<Fleet> parseFeed(String content) {
        //Proceed only if url retrieves data for not getting null pointer exception
        if (content != null) {

            try {
                JSONObject jobj = new JSONObject(content);

                List<Fleet> fleetList = new ArrayList<>();
                JSONArray ar = jobj.optJSONArray("getFleetsResult");
                for (int i = 0; i < ar.length(); i++) {

                    JSONObject obj = ar.getJSONObject(i);
                    Fleet fleet = new Fleet();

                    fleet.setFleetID(obj.getInt("Fleet_ID"));
                    fleet.setRegNo(obj.getString("Fleet_Reg_No"));
                    fleet.setGroupName(obj.getString("Group_Name"));
                 //   fleet.setMakeName(obj.getString("Make_Name"));
                  //  fleet.setModelName(obj.getString("Model_Name"));

                    fleetList.add(fleet);

                }

                return fleetList;

            }catch(JSONException e){
                Log.d("FleetJSONParser", "In Exception");
                e.printStackTrace();
                return null;
            }

        } else {
            return null;
        }

    }

}

