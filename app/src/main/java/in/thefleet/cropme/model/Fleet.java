package in.thefleet.cropme.model;


import android.graphics.Bitmap;

public class Fleet {
    private int Fleet_ID;
    private String Fleet_Reg_No;
    private String Group_Name;
    private String Make_Name;
    private String Model_Name;

    public int getFleetID() {
        return Fleet_ID;
    }
    public void setFleetID(int Fleet_ID) {
        this.Fleet_ID = Fleet_ID;
    }

    public String getRegNo() {return Fleet_Reg_No; }
    public void setRegNo(String Fleet_Reg_No) {
        this.Fleet_Reg_No = Fleet_Reg_No;
    }

    public String getGroupName() {
        return Group_Name;
    }
    public void setGroupName(String Group_Name) {
        this.Group_Name = Group_Name;
    }

    public String getMakeName() {
        return  Make_Name;
    }
    public void setMakeName(String Make_Name) {
        this.Make_Name = Make_Name;
    }

    public String getModelName() {
        return Model_Name;
    }
    public void setModelName(String Model_Name) {
        this.Model_Name = Model_Name;
    }

}
