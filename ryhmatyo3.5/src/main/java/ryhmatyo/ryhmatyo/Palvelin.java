package ryhmatyo.ryhmatyo;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import static spark.Spark.*;

/**
 *
 * @author jussi
 */
//Palvelin pitää koko tietokannan välimuistissa, koska get metodeita ei saatu päivittymään muuten.
public class Palvelin {

    private List<Alue> alueet;
    private final AlueDao alueDao;
    private final AloitusDao aloitusDao;
    private final ViestiDao viestiDao;
    private final HTMLKasaaja kasaaja;
    private final HashMap<String, String> etusivunHakija;
    //Seuraavat HashMapit tarvitaan get metodeiden päivittämiseen.
    private final HashMap<Alue, String> alueHakija;
    private final HashMap<Aloitus, String> aloitusHakija;

    public Palvelin(AlueDao alueDao, AloitusDao aloitusDao, ViestiDao viestiDao) {
        this.alueDao = alueDao;
        this.aloitusDao = aloitusDao;
        this.viestiDao = viestiDao;
        this.kasaaja = new HTMLKasaaja(new ThymeleafTemplateEngine());
        this.etusivunHakija = new HashMap<>();
        this.alueHakija = new HashMap<>();
        this.aloitusHakija = new HashMap<>();
    }

    public void kaynnista() {

        try {
            this.alueet = alueDao.findAll();
        } catch (SQLException ex) {
            System.out.println("Palvelimen käynnistyksessä ei pystytty hakemaan alueita.");
            Logger.getLogger(Palvelin.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        //Juontamismetodit palauttavat selaimille HTML sivut sekä keräävät käyttäjän antamat tiedot, kuten
        //uuden viestin lisäämisen.
        juonnaEtusivu();

        //Etusivu metodi hakee etusivunHakijalta palautettavan HTML tiedoston avaimella "etusivu".
        etusivunHakija.put("etusivu", kasaaja.kasaaEtusivu(alueet));
        for (Alue alue : alueet) {
            juonnaAlue(alue);
            alueHakija.put(alue, kasaaja.kasaaAlue(alue));

            for (Aloitus aloitus : alue.getAloitukset()) {
                juonnaAloitus(aloitus, alue);
                aloitusHakija.put(aloitus, kasaaja.kasaaAloitus(aloitus, alue));
            }

        }
    }

    private void juonnaEtusivu() {

        //Get metodi tuottaa sen osan sivua, joka näkyy selaimessa.
        get("/etusivu", (req, res) -> {
            return etusivunHakija.get("etusivu");
        });

        //Post osio vastaa uuden tiedon lisäämisestä tietokantaan.
        post("/etusivu", (req, res) -> {

            String alueOtsikko = req.queryParams("uusiAlue");

            //Jos uuden alueen lisääminen onnistuu, tehdään näin.
            if (lisaaAlue(alueOtsikko)) {
                return "<head> <META HTTP-EQUIV=\"refresh\" CONTENT=\"0\" "
                        + "<body>  </body>"
                        + "</head>";
            }

            //Muuten tehdään näin.
            return "<head> <META HTTP-EQUIV=\"refresh\" CONTENT=\"3\" "
                    + "<body> Alueen lisäys epäonnistui. <div align=\"center\" >\n"
                    + "                <img src=\"http://orig02.deviantart.net/6c66/f/2011/240/9/3/crying_sweetie_belle_by_creshosk-d486ruz.gif\" alt=\"\" >\n"
                    + "                </img>\n"
                    + "            </div> </body> </head>";
        });

    }

    public void juonnaAlue(Alue alue) {

        get("*/alue=" + alue.getId(), (req, res) -> {
            return alueHakija.get(alue);
        });

        post("*/alue=" + alue.getId(), (req, res) -> {
            String aloitusOtsikko = req.queryParams("uusiAloitus");
            String julkaisija = req.queryParams("julkaisija");
            String aloitusSisalto = req.queryParams("sisalto");

            if (lisaaAloitus(aloitusOtsikko, julkaisija, aloitusSisalto, alue)) {
                return "<head> <META HTTP-EQUIV=\"refresh\" CONTENT=\"0\" "
                        + "<body>  </body>"
                        + "</head>";
            }

            return "<head> <META HTTP-EQUIV=\"refresh\" CONTENT=\"3\" "
                    + "<body> Aloituksen lisäys epäonnistui. <div align=\"center\" >\n"
                    + "                <img src=\"http://orig02.deviantart.net/6c66/f/2011/240/9/3/crying_sweetie_belle_by_creshosk-d486ruz.gif\" alt=\"\" >\n"
                    + "                </img>\n"
                    + "            </div> </body> </head>";
        });
    }

    public void juonnaAloitus(Aloitus aloitus, Alue alue) {

        get("*/*/aloitus=" + aloitus.getId(), (req, res) -> {
            return aloitusHakija.get(aloitus);
        });

        post("*/*/aloitus=" + aloitus.getId(), (req, res) -> {
            String julkaisija = req.queryParams("julkaisija");
            String sisalto = req.queryParams("sisalto");

            if (lisaaViesti(julkaisija, sisalto, aloitus, alue)) {
                return "<head> <META HTTP-EQUIV=\"refresh\" CONTENT=\"0\" "
                        + "<body>  </body>"
                        + "</head>";
            }

            return "<head> <META HTTP-EQUIV=\"refresh\" CONTENT=\"3\" "
                    + "<body> Viestin lisäys epäonnistui. <div align=\"center\" >\n"
                    + "                <img src=\"http://orig02.deviantart.net/6c66/f/2011/240/9/3/crying_sweetie_belle_by_creshosk-d486ruz.gif\" alt=\"\" >\n"
                    + "                </img>\n"
                    + "            </div> </body> </head>";

        });
    }

    public boolean lisaaAlue(String alueOtsikko) {
        //Poistetaan tyhjät lyönnit alusta ja lopusta ja varmistetaan, että tulos ei ole tyhjä String.        
        alueOtsikko=alueOtsikko.trim();
        if (alueOtsikko.equals("")){
            return false;
        }

        int id;
        try {
            //Lisätään alue tietokantaan.
            alueDao.addNew(alueOtsikko);
            id = alueDao.findNewestId();
        } catch (SQLException ex) {
            //Jos ei onnistunut, ei tehdä mitään muutakaan.
            System.out.println("Alueen lisäys tietokantaan epäonnistui.");
            Logger.getLogger(Palvelin.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        //Luodaan uusi Alue otus, joka lisätään jo tunnettuihin alue-otuksiin.
        ArrayList<Aloitus> uudetAloitukset = new ArrayList<>();
        Alue alue = new Alue(alueOtsikko, uudetAloitukset, id);
        alueet.add(alue);
        //Järjestetään, jotta alueet palautuvat selaimelle aakkosjärjestyksessä.
        Collections.sort(alueet, (Alue t, Alue t1) -> -t1.getOtsikko().compareTo(t.getOtsikko()));

        //Lisätään alue nettiin.
        juonnaAlue(alue);
        //Päivitetään nettisivujen näkymät.
        alueHakija.put(alue, kasaaja.kasaaAlue(alue));
        etusivunHakija.put("etusivu", kasaaja.kasaaEtusivu(alueet));

        return true;
    }

    public boolean lisaaAloitus(String aloitusOtsikko, String julkaisija, String aloitusSisalto, Alue alue) throws SQLException {
        aloitusOtsikko=aloitusOtsikko.trim();
        if (aloitusOtsikko.equals("")){
            return false;
        }
        julkaisija=julkaisija.trim();
        if (julkaisija.equals("")){
            return false;
        }
        
        for (Aloitus aloitus : alue.getAloitukset()) {
            if (aloitus.getAloitusOtsikko().equals(aloitusOtsikko)) {
                return false;
            }
        }

        try {
            aloitusDao.addNew(aloitusOtsikko, alue.getId());
            viestiDao.addNew(aloitusSisalto, julkaisija, aloitusDao.findNewestId(), getAika());
        } catch (SQLException ex) {
            System.out.println("Aloituksen tietokantaan lisääminen epäonnistui.");
            Logger.getLogger(Palvelin.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        Viesti aloitusViesti = new Viesti(aloitusSisalto, getAika(), julkaisija);
        List<Viesti> uudenAloituksenViestit = new ArrayList<>();
        uudenAloituksenViestit.add(aloitusViesti);
        Aloitus aloitus = new Aloitus(aloitusOtsikko, uudenAloituksenViestit, aloitusDao.findNewestId());
        alue.lisaaAloitus(aloitus);
        juonnaAloitus(aloitus, alue);

        aloitusHakija.put(aloitus, kasaaja.kasaaAloitus(aloitus, alue));
        alueHakija.put(alue, kasaaja.kasaaAlue(alue));
        etusivunHakija.put("etusivu", kasaaja.kasaaEtusivu(alueet));

        return true;
    }

    public boolean lisaaViesti(String julkaisija, String sisalto, Aloitus aloitus, Alue alue) {
        julkaisija=julkaisija.trim();
        if (julkaisija.equals("")){
            return false;
        }
        
        try {
            viestiDao.addNew(sisalto, julkaisija, aloitus.getId(), getAika());
        } catch (SQLException ex) {
            System.out.println("Viestin lisääminen tietokantaan epäonnistui.");
            Logger.getLogger(Palvelin.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        aloitus.lisaaViesti(new Viesti(sisalto, getAika(), julkaisija));
        Collections.sort(alue.getAloitukset(), (Aloitus t, Aloitus t1) -> t1.viimeisinViesti().compareTo(t.viimeisinViesti()));
        
        aloitusHakija.put(aloitus, kasaaja.kasaaAloitus(aloitus, alue));
        alueHakija.put(alue, kasaaja.kasaaAlue(alue));
        etusivunHakija.put("etusivu", kasaaja.kasaaEtusivu(alueet));

        
        return true;
    }

    public String getAika() {
        Date date = new Date();
        date.setTime(date.getTime() + 10800000);
        SimpleDateFormat muoto = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String aika = muoto.format(date);
        return aika;
    }
    
}
