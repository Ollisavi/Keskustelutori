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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ssofieva
 */
public class ViestiDao {

    Database database;

    public ViestiDao(Database database) {
        this.database = database;
    }

    public List<Viesti> findAllInAloitus(int aloitusId) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM viesti WHERE aloitus = ? ORDER BY julkaisuaika ASC;");
        stmt.setObject(1, aloitusId);
        ResultSet rs = stmt.executeQuery();

        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            String sisalto = rs.getString("sisalto");
            String julkaisuAika = rs.getString("julkaisuaika");
            String julkaisija = rs.getString("julkaisija");
            viestit.add(new Viesti(sisalto, julkaisuAika, julkaisija));
        }

        rs.close();
        stmt.close();
        connection.close();
        return viestit;
    }

    public void addNew(String sisalto, String julkaisija, int aloitus, String aika) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO viesti (sisalto, julkaisuaika, julkaisija, aloitus) VALUES (?,?,?,?);");
        stmt.setObject(1, sisalto);
        stmt.setObject(2, aika);
        stmt.setObject(3, julkaisija);
        stmt.setObject(4, aloitus);

        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }
    
}
