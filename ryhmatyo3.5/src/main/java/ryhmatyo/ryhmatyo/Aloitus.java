package ryhmatyo.ryhmatyo;

import java.util.List;

public class Aloitus {

    private String aloitusOtsikko;
    private List<Viesti> viestit;
    private int id;

    public Aloitus(String aloitusOtsikko, List<Viesti> viestit, int id) {
        this.aloitusOtsikko = aloitusOtsikko;
        this.viestit = viestit;
        this.id=id;
    }

    public void lisaaViesti(Viesti viesti) {        
        this.viestit.add(viesti);
    }

    public void setAloitusOtsikko(String aloitusOtsikko) {
        this.aloitusOtsikko = aloitusOtsikko;
    }

    public String getAloitusOtsikko() {
        return this.aloitusOtsikko;
    }

    public List<Viesti> getViestit() {
        return viestit;
    }
    
    public int getId() {
        return id;
    }

    public int laskeViestit() {
        return viestit.size();
    }

    public String viimeisinViesti() {
        return this.viestit.get(this.viestit.size() -1 ).getAika();
    }

    @Override
    public String toString() {
        return this.aloitusOtsikko + "\t\t" + this.laskeViestit() + "\t\t" + this.viimeisinViesti();
    }

}
