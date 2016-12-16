package in.thefleet.cropme;

public class Globals{
    private static Globals instance;

    // Global variable
    private String fleetSelected;
    private String imei;
    private String imageTyp;

    public String getImageTyp() {
        return imageTyp;
    }

    public void setImageTyp(String imageTyp) {
        this.imageTyp = imageTyp;
    }

    // Restrict the constructor from being instantiated
    private Globals(){}

    public void setfleetSelected(String f){
        this.fleetSelected=f;
    }
    public String getfleetSelected(){
        return this.fleetSelected;
    }

    public String getImei() {return imei;}

    public void setImei(String imei) {this.imei = imei;}

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
