package ryhmatyo.ryhmatyo;

import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws Exception {
        
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }        
        String tietokantaOsoite = "jdbc:sqlite:keskustelut.db";
        if (System.getenv("DATABASE_URL") != null) {
            tietokantaOsoite = System.getenv("DATABASE_URL");
        }
        
        Database database = new Database(tietokantaOsoite);        
        ViestiDao viestiDao = new ViestiDao(database);
        AloitusDao aloitusDao = new AloitusDao(database, viestiDao);
        AlueDao alueDao = new AlueDao(database, aloitusDao);
        Palvelin palvelin = new Palvelin(alueDao, aloitusDao, viestiDao);

        uusiTietoKantaPostgrella(database);
        palvelin.kaynnista();
        
    }
    
    public static void uusiTietoKantaPostgrella(Database database){
        database.uusi();
    }
}