package ryhmatyo.ryhmatyo;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }

    public Connection getConnection() throws SQLException {
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (URISyntaxException | SQLException t) {
                System.out.println("Virhe yhteyden muodostamisessa tietokantaan.");
            }
        }

        return DriverManager.getConnection(databaseAddress);
    }

    //Tietokannan uusiminen tai taulujen luominen tyhjään tietokantaan
    public void uusi() {
        ArrayList<String> lista = new ArrayList<>();

//        lista.add("DROP TABLE viesti;");
//        lista.add("DROP TABLE aloitus;");
//        lista.add("DROP TABLE alue;");
        if (this.databaseAddress.contains("postgres")) {
            //Jos ollaan heroussa ja käytössä postresql:
            lista.add("CREATE TABLE alue ("
                    + "alue_otsikko varchar(16) PRIMARY KEY UNIQUE);");
            lista.add("CREATE TABLE aloitus ("
                    + "id SERIAL PRIMARY KEY, "
                    + "aloitus_otsikko varchar(32) NOT NULL, "
                    + "alue_otsikko varchar(16) NOT NULL, "
                    + "FOREIGN KEY (alue_otsikko) REFERENCES alue(alue_otsikko));");
            lista.add("CREATE TABLE viesti ("
                    + "id_viesti SERIAL PRIMARY KEY, "
                    + "sisalto varchar(2048) NOT NULL, "
                    + "julkaisuaika varchar(32) NOT NULL, "
                    + "julkaisija varchar(16) NOT NULL, "
                    + "aloitus integer NOT NULL, "
                    + "FOREIGN KEY (aloitus) REFERENCES Aloitus(id));");
        } else {
            //Jos käytössä jdbc / SQL:
            lista.add("CREATE TABLE viesti ("
                    + "id_viesti integer PRIMARY KEY, "
                    + "sisalto varchar(2048) NOT NULL, "
                    + "julkaisuaika varhar(32) NOT NULL, "
                    + "julkaisija varchar(16) NOT NULL, "
                    + "aloitus integer NOT NULL, "
                    + "FOREIGN KEY (aloitus) REFERENCES Aloitus(id));");
            lista.add("CREATE TABLE aloitus ("
                    + "id integer PRIMARY KEY, "
                    + "aloitus_otsikko varchar(32) NOT NULL, "
                    + "alue_otsikko varchar(16) NOT NULL, "
                    + "FOREIGN KEY (alue_otsikko) REFERENCES alue(alue_otsikko));");
            lista.add("CREATE TABLE alue ("
                    + "alue_otsikko varchar(16) PRIMARY KEY);");
        }
        Connection conn;
        Statement stmt;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
        } catch (java.sql.SQLException e) {
            System.out.println("Tietokantaan ei saatu yhteyttä.");
            return;
        }

        //Toteutetaan käskyt.
        for (String kasky : lista) {
            try {
                stmt.executeUpdate(kasky);
            } catch (java.sql.SQLException e) {
                System.out.println("Virhe tietokannan uusimisessa.");
            }
        }

        try {
            conn.close();
            stmt.close();
        } catch (SQLException ex) {

        }
    }


}
