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
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ssofieva
 */
public class AloitusDao {

    Database database;
    ViestiDao viestiDao;

    public AloitusDao(Database database, ViestiDao viestiDao) {
        this.database = database;
        this.viestiDao = viestiDao;
    }

    public List<Aloitus> findAllInAlue(int alueId) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM aloitus WHERE alue_id = ?; ");
        stmt.setObject(1, alueId);
        ResultSet rs = stmt.executeQuery();

        List<Aloitus> aloitukset = new ArrayList<>();
        while (rs.next()) {
            String aloitusOtsikko = rs.getString("aloitus_otsikko");
            int id = rs.getInt("id");
            List<Viesti> viestit = viestiDao.findAllInAloitus(id);

            aloitukset.add(new Aloitus(aloitusOtsikko, viestit, id));
        }
        Collections.sort(aloitukset, (Aloitus t, Aloitus t1) -> t1.viimeisinViesti().compareTo(t.viimeisinViesti()));        

        rs.close();
        stmt.close();
        connection.close();
        return aloitukset;
    }

    public void addNew(String aloitusOtsikko, int alueId) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO aloitus (aloitus_otsikko, alue_id) VALUES (?,?);");
        stmt.setObject(1, aloitusOtsikko);
        stmt.setObject(2, alueId);

        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }

    public int findNewestId() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT id FROM aloitus ORDER BY id DESC LIMIT 1;");
        ResultSet rs = stmt.executeQuery();

        boolean hasOne = rs.next();
        if (!hasOne) {
            return -1;
        }

        int id = rs.getInt("id");
        rs.close();
        stmt.close();
        connection.close();

        return id;
    }
}
