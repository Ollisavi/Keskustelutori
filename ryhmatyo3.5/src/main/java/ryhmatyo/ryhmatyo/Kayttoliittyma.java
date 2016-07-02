package ryhmatyo.ryhmatyo;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Kayttoliittyma {

    private ViestiDao viestiDao;
    private AloitusDao aloitusDao;
    private AlueDao alueDao;
    private Scanner lukija;

    public Kayttoliittyma(ViestiDao vd, AloitusDao alod, AlueDao alud) {
        this.viestiDao = vd;
        this.aloitusDao = alod;
        this.alueDao = alud;
        this.lukija = new Scanner(System.in);
    }

    public void kaynnista() throws SQLException {
        System.out.println("Tervetuloa keskustelufoorumille! \nValitse kesksutelualue tai luo uusi:");
        valikkonakyma();
    }

    private void valikkonakyma() throws SQLException {
        tulostaAlueet();
        Alue valittuAlue = valitseAlue();   //käyttäjä valitsee yhden avattavan alueen
        tulostaAloitukset(valittuAlue);     //tulostetaan valitun alueen aloitukset
        tulostaViestit(valitseAloitus(valittuAlue));    //käyttäjä alitsee aloituksen, jonka viestit tulostetaan
        valitseViestitoiminto();    //käyttäjä voi lisätä uuden viestin tai palata ylempään valikkoon
    }

    private int kysy(String kysymys) {
        System.out.println(kysymys);
        int vastaus = Integer.parseInt(lukija.nextLine());
        return vastaus;
    }

    private void tulostaAlueet() throws SQLException {
        List<Alue> alueet = alueDao.findAll();
        System.out.println("Nro \tOtsikko \tViestejä \tViimeisin viesti");
        for (int i = 0; i < alueet.size(); i++) {
            System.out.print(i + 1 + "\t");
            System.out.println(alueet.get(i).toString());
        }
        System.out.println(alueet.size() + 1 + "\tLuo uusi");
    }

    private Alue valitseAlue() throws SQLException {
        Alue alue = null;
        List<Alue> alueet = alueDao.findAll();
        int index = kysy("Mikä valitaan (anna numero)?");
        if (index == alueet.size() + 1) {
            luoUusiAlue();
        } else if (index <= alueet.size() && index > 0) {
            alue = alueet.get(index - 1);

        } else {
            System.out.println("Tuntematon.");
        }
        return alue;
    }

    private void luoUusiAlue() throws SQLException {
        String alueOtsikko = kysyTieto("Anna uuden alueen otsikko:");
        alueDao.addNew(alueOtsikko);
        kaynnista();
    }
    
    private String kysyTieto(String kysymys) {
        System.out.println(kysymys);
        String vastaus = lukija.nextLine();
        return vastaus;
    }

    private void tulostaAloitukset(Alue alue) throws SQLException {
        if (alue == null) {
            kaynnista();
        } else {
            List<Aloitus> aloitukset = aloitusDao.findAllInAlue(alue.getId());
            if (aloitukset.isEmpty()) {
                System.out.println("Alueella ei ole yhtään keskustelunaloitusta.");
                System.out.println(aloitukset.size() + 1 + "\tLuo uusi");
                System.out.println(aloitukset.size() + 2 + "\tPalaa aluevalikkoon");
            } else {
                System.out.println("Alueella on seuraavat keskustelunaloitukset. Avaa keskustelu, luo uusi tai palaa aluevalikkoon (anna numero): ");
                System.out.println("Nro \tOtsikko \tViestejä \tViimeisin viesti");
                for (int i = 0; i < aloitukset.size(); i++) {
                    System.out.print(i + 1 + "\t");
                    System.out.println(aloitukset.get(i).toString());
                }
                System.out.println(aloitukset.size() + 1 + "\tLuo uusi");
                System.out.println(aloitukset.size() + 2 + "\tPalaa aluevalikkoon");
            }
        }
    }

    private Aloitus valitseAloitus(Alue alue) throws SQLException {
        Aloitus aloitus = null;
        List<Aloitus> aloitukset = aloitusDao.findAllInAlue(alue.getId());
        int index = kysy("Mikä valitaan (anna numero)?");
        if (index == aloitukset.size() + 1) {
            luoUusiAloitus();
        } else if (index == aloitukset.size() + 2) {
            //palaa aluevalikkoon
        } else if (index <= aloitukset.size() && index > 0) {
            aloitus = aloitukset.get(index - 1);

        } else {
            System.out.println("Tuntematon.");
        }
        return aloitus;
    }

    private void luoUusiAloitus() {
        //kysy tarvittavat kysymykset ja sitten:
        //aloitusDao.addNew();
        //viestiDao.addNew();
    }

    private void tulostaViestit(Aloitus aloitus) throws SQLException {
        if (aloitus == null) {
            kaynnista();
        } else {
            List<Viesti> viestit = viestiDao.findAllInAloitus(aloitus.getId());

            System.out.println("Keskustelu aiheesta: " + aloitus.getAloitusOtsikko());
            for (int i = 0; i < viestit.size(); i++) {
                System.out.println(viestit.get(i).toString());            }
            System.out.println("1\tLisää viesti");
            System.out.println("2\tPalaa aluevalikkoon");
        }

    }

    private void valitseViestitoiminto() throws SQLException {
        int valinta = kysy("Mitä tehdään?");
        if (valinta == 1) {
            luoUusiViesti();
        } else if (valinta == 2) {
            kaynnista();
        } else {
            System.out.println("Tuntematon komento.");
        }
    }

    private void luoUusiViesti() {
        //kysy tarvittavat kysymykset ja sitten:
        //viestiDao.addNew();
    }

}
