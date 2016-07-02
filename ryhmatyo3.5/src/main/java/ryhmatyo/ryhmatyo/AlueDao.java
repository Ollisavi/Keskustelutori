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
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM alue ORDER BY LOWER(alue_otsikko) ASC;");

        ResultSet rs = stmt.executeQuery();
        List<Alue> alueet = new ArrayList<>();
        while (rs.next()) {
            String haettuOtsikko = rs.getString("alue_otsikko");
            int alue_id = rs.getInt("alue_id");
            List<Aloitus> aloitukset = aloitusDao.findAllInAlue(alue_id);

            alueet.add(new Alue(haettuOtsikko, aloitukset, alue_id));
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

    public int findNewestId() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT alue_id FROM alue ORDER BY alue_id DESC LIMIT 1;");
        ResultSet rs = stmt.executeQuery();

        boolean hasOne = rs.next();
        if (!hasOne) {
            return -1;
        }

        int alue_id = rs.getInt("alue_id");
        rs.close();
        stmt.close();
        connection.close();

        return alue_id;
    }
    
}
