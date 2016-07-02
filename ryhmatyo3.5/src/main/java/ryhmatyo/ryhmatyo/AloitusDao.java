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
public class AloitusDao {

    Database database;
    ViestiDao viestiDao;

    public AloitusDao(Database database, ViestiDao viestiDao) {
        this.database = database;
        this.viestiDao = viestiDao;
    }

    public List<Aloitus> findAllInAlue(String alueOtsikko) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM aloitus, viesti "
                + "WHERE alue_otsikko = ? AND viesti.aloitus = aloitus.id"
                + " GROUP BY aloitus.id, viesti.id_viesti ORDER BY viesti.julkaisuaika DESC;");
        stmt.setObject(1, alueOtsikko);
        ResultSet rs = stmt.executeQuery();

        List<Aloitus> aloitukset = new ArrayList<>();
        while (rs.next()) {
            String aloitusOtsikko = rs.getString("aloitus_otsikko");
            int id = rs.getInt("id");
            List<Viesti> viestit = viestiDao.findAllInAloitus(id);

            aloitukset.add(new Aloitus(aloitusOtsikko, viestit, id));
        }

        rs.close();
        stmt.close();
        connection.close();
        return aloitukset;
    }

    public void addNew(String aloitusOtsikko, String alueOtsikko) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO aloitus (aloitus_otsikko, alue_otsikko) VALUES (?,?);");
        stmt.setObject(1, aloitusOtsikko);
        stmt.setObject(2, alueOtsikko);

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
