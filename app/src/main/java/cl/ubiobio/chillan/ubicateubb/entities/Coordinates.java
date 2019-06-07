package cl.ubiobio.chillan.ubicateubb.entities;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/** This class is created for be ocupated in BDD's motor SQLite**/
public class Coordinates {


    private String title;
    private String snippet;
    private double latitude;
    private double longitude;
    private String information;
    private String informationtwo;
    private String link;
    private String dato;
    private Bitmap foto;

    /** initializing Constructor and creating methods getters and setters for posterior use **/
    public Coordinates(String title, String snippet, double latitude, double longitude, String information, String informationtwo,String link,String dato,Bitmap foto) {
        this.title = title;
        this.snippet = snippet;
        this.latitude = latitude;
        this.longitude = longitude;
        this.information=information;
        this.informationtwo=informationtwo;
        this.link=link;
        this.dato=dato;
        this.foto=foto;

    }
    public Coordinates(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getInformationtwo() {
        return informationtwo;
    }

    public void setInformationtwo(String informationtwo) {
        this.informationtwo = informationtwo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
        try {
            byte[] byteCode= Base64.decode(dato,Base64.DEFAULT);
            int alto=100;
            int ancho=100;
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);
            this.foto=Bitmap.createScaledBitmap(bitmap,alto,ancho,true);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public Bitmap getFoto() {
        return foto;
    }

    @Override
    public String toString() {
        return title;
    }
}
