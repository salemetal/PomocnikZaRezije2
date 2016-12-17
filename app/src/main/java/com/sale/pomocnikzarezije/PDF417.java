package com.sale.pomocnikzarezije;

/**
 * Created by Sale on 16.10.2016..
 */

public class PDF417 {

    private float iznos;
    private String platitelj;
    private String adresaPlatitelja;
    private String primatelj;
    private String adresaPrimatelja;
    private String ibanPrimatelja;
    private String model;
    private String pozivNaBrojPrimatelja;
    private String sifraNamjene;
    private String opisPlacanja;

    public PDF417(String contents) {

        try {
            byte[] bytes = contents.getBytes("ISO-8859-1");
            contents = new String(bytes, "UTF-8");

        } catch (Exception ex)
        {

        }

        String[] tokens = contents.split("\n");

        iznos = (Float.parseFloat(tokens[2].trim()))/100; //format je tipa 00000000000000000100 za 1kn
        platitelj = tokens[3];
        adresaPlatitelja = tokens[4].trim() + ", " + tokens[5].trim();
        primatelj = tokens[6].trim();
        adresaPrimatelja = tokens[7].trim() + ", " + tokens[8].trim();
        ibanPrimatelja = tokens[9].trim();
        model = tokens[10].trim();
        pozivNaBrojPrimatelja = tokens[11].trim();
        sifraNamjene = tokens[12].trim();
        opisPlacanja = tokens[13].trim();
    }

    public  PDF417()
    {

    }

    public String getPlatitelj() {
        return platitelj;
    }

    public void setPlatitelj(String platitelj) {
        this.platitelj = platitelj;
    }

    public String getAdresaPlatitelja() {
        return adresaPlatitelja;
    }

    public void setAdresaPlatitelja(String adresaPlatitelja) {
        this.adresaPlatitelja = adresaPlatitelja;
    }

    public String getPrimatelj() {
        return primatelj;
    }

    public void setPrimatelj(String primatelj) {
        this.primatelj = primatelj;
    }

    public String getAdresaPrimatelja() {
        return adresaPrimatelja;
    }

    public void setAdresaPrimatelja(String adresaPrimatelja) {
        this.adresaPrimatelja = adresaPrimatelja;
    }

    public String getIbanPrimatelja() {
        return ibanPrimatelja;
    }

    public void setIbanPrimatelja(String ibanPrimatelja) {
        this.ibanPrimatelja = ibanPrimatelja;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPozivNaBrojPrimatelja() {
        return pozivNaBrojPrimatelja;
    }

    public void setPozivNaBrojPrimatelja(String pozivNaBrojPrimatelja) {
        this.pozivNaBrojPrimatelja = pozivNaBrojPrimatelja;
    }

    public String getSifraNamjene() {
        return sifraNamjene;
    }

    public void setSifraNamjene(String sifraNamjene) {
        this.sifraNamjene = sifraNamjene;
    }

    public String getOpisPlacanja() {
        return opisPlacanja;
    }

    public void setOpisPlacanja(String opisPlacanja) {
        this.opisPlacanja = opisPlacanja;
    }

    public float getAmount() {
        return iznos;
    }

    public void setAmount(float iznos) {
        this.iznos = iznos;
    }



}
