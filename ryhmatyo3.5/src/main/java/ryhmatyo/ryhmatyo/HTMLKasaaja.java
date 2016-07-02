package ryhmatyo.ryhmatyo;

import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

/**
 *
 * @author jussi
 */
//Tämä otus tuottaa HTML tiedostot String oliona, jonka palvelin palauttaa selaimelle.
public class HTMLKasaaja {

    private final ThymeleafTemplateEngine thlMoottori;

    public HTMLKasaaja(ThymeleafTemplateEngine thlMoottori) {
        this.thlMoottori = thlMoottori;
    }

//Esimerkiksi tämä metodi syöttää etusivu.html tiedostoon metodin parametriksi annetut alueet.
    public String kasaaEtusivu(List<Alue> alueet) {

        //HashMappiin map laitetaan ne tiedot, jotka välitetään HTML tiedostoon.
        HashMap map = new HashMap<>();
        map.put("alueet", alueet);
        map.put("etusivu", "/etusivu/alue=");

        //Palautuksena saadaan tavallinen String otus.
        return thlMoottori.render(new ModelAndView(map, "etusivu"));
    }

    public String kasaaAlue(Alue alue) {
        HashMap map = new HashMap<>();
        map.put("aloitukset", alue.getAloitukset());
        map.put("otsikko", alue.getOtsikko());
        String url = "/etusivu/alue=" + alue.getOtsikko();
        map.put("url", url);
        map.put("linkinAlku", url + "/aloitus=");

        return thlMoottori.render(new ModelAndView(map, "alue"));
    }

    public String kasaaAloitus(Aloitus aloitus, Alue aloituksenAlue) {
        HashMap map = new HashMap<>();
        map.put("viestit", aloitus.getViestit());
        map.put("otsikko", aloitus.getAloitusOtsikko());
        map.put("url", "/etusivu/alue=" + aloituksenAlue.getOtsikko() + "/aloitus=" + aloitus.getId());
        map.put("takaisin", "/etusivu/alue=" + aloituksenAlue.getOtsikko());

        return thlMoottori.render(new ModelAndView(map, "aloitus"));
    }

}
