/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ryhmatyo.ryhmatyo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ssofieva
 */
public class AlueDao {

    Database database;
    AloitusDao aloitusDao;

    public AlueDao(Database database, AloitusDao aloitusDao) {
        this.database = database;
        this.aloitusDao = aloitusDao;
    }

    public List<Alue> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM alue ORDER BY alue_otsikko ASC;");

        ResultSet rs = stmt.executeQuery();
        List<Alue> alueet = new ArrayList<>();
        while (rs.next()) {
            String haettuOtsikko = rs.getString("alue_otsikko");
            List<Aloitus> aloitukset = aloitusDao.findAllInAlue(haettuOtsikko);

            alueet.add(new Alue(haettuOtsikko, aloitukset));
        }

        rs.close();
        stmt.close();
        connection.close();

        return alueet;
    }

    public void addNew(String alueOtsikko) throws SQLException {
        try (
                Connection connection = database.getConnection()) {
            PreparedStatement stmt;
            stmt = connection.prepareStatement("INSERT INTO alue (alue_otsikko) VALUES (?);");
            stmt.setObject(1, alueOtsikko);

            stmt.executeUpdate();
            stmt.close();
            connection.close();
        }
    }
}
