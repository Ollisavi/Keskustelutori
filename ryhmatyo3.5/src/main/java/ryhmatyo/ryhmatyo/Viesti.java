package ryhmatyo.ryhmatyo;

public class Viesti {

    private String sisalto;
    private String julkaisuaika;
    private String julkaisija;

    public Viesti(String sisalto, String julkaisuaika, String julkaisija) {
        this.sisalto = sisalto;
        this.julkaisuaika = julkaisuaika;
        this.julkaisija = julkaisija;
    }
    
    public String getAika() {
        return this.julkaisuaika;
    }

    public String getSisalto() {
        return this.sisalto;
    }

    public String getJulkaisija() {
        return this.julkaisija + ":";
    }
    
    @Override
    public String toString() {
        return this.julkaisija + "\t\t" + this.sisalto + "\t(" + this.julkaisuaika + ")";
    }
}
