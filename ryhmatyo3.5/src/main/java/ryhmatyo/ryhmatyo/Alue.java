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

    public Alue(String alueOtsikko, List<Aloitus> aloitukset) {
        this.alueOtsikko = alueOtsikko;
        this.aloitukset = aloitukset;

    }

    public void lisaaAloitus(Aloitus aloitus) {

        boolean lisatty = false;
        String uudenViimeisin = aloitus.viimeisinViesti();
        for (int n = 0; n < aloitukset.size(); n++) {
            if (aloitukset.get(n).viimeisinViesti().compareToIgnoreCase(uudenViimeisin) < 0) {
                aloitukset.add(n, aloitus);
                lisatty = true;
                break;
            }
        }
        if (!lisatty) {
            this.aloitukset.add(aloitus);
        }
    }

    public Alue(String alueOtsikko) {
        this.alueOtsikko = alueOtsikko;
        this.aloitukset = new ArrayList<>();
    }

    public String getOtsikko() {
        return alueOtsikko;
    }

    public List<Aloitus> getAloitukset() {
//        Collections.sort(aloitukset, new Comparator<Aloitus>() {
//            @Override
//            public int compare(Aloitus t, Aloitus t1) {
//                return t1.viimeisinViesti().compareTo(t.viimeisinViesti());
//            }
//
//        });
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
