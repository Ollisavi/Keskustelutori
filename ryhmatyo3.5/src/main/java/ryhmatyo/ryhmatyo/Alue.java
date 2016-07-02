/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ryhmatyo.ryhmatyo;

import java.util.*;

/**
 *
 * @author ssofieva
 */
public class Alue {

    private String alueOtsikko;
    private List<Aloitus> aloitukset;
    private int id;

    public Alue(String alueOtsikko, List<Aloitus> aloitukset, int id) {
        this.alueOtsikko = alueOtsikko;
        this.aloitukset = aloitukset;
        this.id=id;

    }

    public void lisaaAloitus(Aloitus aloitus) {
        this.aloitukset.add(aloitus);
        Collections.sort(this.aloitukset, (Aloitus t, Aloitus t1) -> t1.viimeisinViesti().compareTo(t.viimeisinViesti()));

    }

    public String getOtsikko() {
        return alueOtsikko;
    }
    
    public int getId(){
        return this.id;
    }

    public List<Aloitus> getAloitukset() {
        return this.aloitukset;
    }

    public void setOtsikko(String alueOtsikko) {
        this.alueOtsikko = alueOtsikko;
    }

    public int laskeViestit() {
        int viesteja = 0;
        for (Aloitus aloitus : aloitukset) {
            viesteja += aloitus.laskeViestit();
        }
        return viesteja;
    }

    public String viimeisinViesti() {
        String viimeisinViesti = "0";
        for (Aloitus a : aloitukset) {
            if (a.viimeisinViesti().compareTo(viimeisinViesti) > 0) {
                viimeisinViesti = a.viimeisinViesti();
            }
        }
        if (viimeisinViesti.equals("0")) {
            viimeisinViesti = "";
        }

        return viimeisinViesti;
    }
    
    public void clear(){
        return;
    }

    @Override
    public String toString() {
        return this.alueOtsikko + "\t\t" + this.laskeViestit() + "\t\t" + this.viimeisinViesti();
    }

}
