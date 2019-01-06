package Entities.M03_Campaign;

import Entities.Sql;
import Exceptions.CampaignDoesntExistsException;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CampaignDAO {

    private Connection conn = Sql.getConInstance();

    //region API detalle Campaña

    public Campaign getDetails (int id) throws CampaignDoesntExistsException {

        String select = "SELECT * FROM campaign where cam_id = ?";
        Campaign ca = new Campaign();
        try {

            PreparedStatement ps = conn.prepareStatement(select);
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                ca.set_idCampaign(result.getInt("cam_id"));
                ca.set_nameCampaign(result.getString("cam_name"));
                ca.set_descCampaign(result.getString("cam_description"));
                ca.set_startCampaign(result.getDate("cam_start_date"));
                ca.set_endCampaign(result.getDate("cam_end_date"));
                ca.set_statusCampaign(result.getBoolean("cam_status"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new CampaignDoesntExistsException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Sql.bdClose(conn);
        }
        return ca;
    }

    //endregion

    //region Obtener Lista de Campañas
    public void getCampaignList(int id, String select, ArrayList<Campaign> caList) throws CampaignDoesntExistsException {
        try {
            PreparedStatement ps = conn.prepareCall(select);
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            while(result.next()){
                Campaign ca = new Campaign();
                ca.set_idCampaign(result.getInt("cam_id"));
                ca.set_nameCampaign(result.getString("cam_name"));
                ca.set_descCampaign(result.getString("cam_description"));
                ca.set_startCampaign(result.getDate("cam_start_date"));
                ca.set_endCampaign(result.getDate("cam_end_date"));
                ca.set_statusCampaign(result.getBoolean("cam_status"));
                caList.add(ca);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new CampaignDoesntExistsException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Sql.bdClose(conn);
        }
    }

    public void getCampaignList2(int idCompany, int idUser, String select, ArrayList<Campaign> caList) throws CampaignDoesntExistsException {
        try {
            PreparedStatement ps = conn.prepareCall(select);
            ps.setInt(1, idCompany);
            ps.setInt(2, idUser);
            ResultSet result = ps.executeQuery();
            while(result.next()){
                Campaign ca = new Campaign();
                ca.set_idCampaign(result.getInt("cam_id"));
                ca.set_nameCampaign(result.getString("cam_name"));
                ca.set_descCampaign(result.getString("cam_description"));
                ca.set_startCampaign(result.getDate("cam_start_date"));
                ca.set_endCampaign(result.getDate("cam_end_date"));
                ca.set_statusCampaign(result.getBoolean("cam_status"));
                caList.add(ca);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new CampaignDoesntExistsException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Sql.bdClose(conn);
        }
    }


    //endregion

    //region API Obtener Campañas por compañia

    public ArrayList<Campaign> campaignList(int id) throws CampaignDoesntExistsException {
        String select = "{Call m03_getcampaigns(?)}";
        ArrayList<Campaign> caList= new ArrayList<>();
        getCampaignList(id, select, caList);
        return caList;
    }
    //endregion

    //region Obtener Campañas por compañia y usuario

    public ArrayList<Campaign> campaignListByUserCompany(int idCompany, int idUser) throws CampaignDoesntExistsException {
        String select = "{Call m03_getcampaignsbycompany(?,?)}";
        ArrayList<Campaign> caList= new ArrayList<>();
        getCampaignList2(idCompany, idUser, select, caList);
        return caList;
    }

    //region Campaña por Usuario
    public ArrayList<Campaign> campaignListByUser(int id) throws CampaignDoesntExistsException {
        String select = "{Call m03_getcampaignsbyuser(?)}";
        ArrayList<Campaign> caList= new ArrayList<>();
        getCampaignList(id, select, caList);
        return caList;
    }
    //endregion

    //region Todas Las Campañas
    public ArrayList<Campaign> campaignListAll(int id) throws CampaignDoesntExistsException {
        String select = "{call m03_getcampaignall()}";
        ArrayList<Campaign> caList= new ArrayList<>();

        getCampaignList(id, select, caList);
        return caList;
    }
    //endregion

    public boolean changeStatusCampaign (int id) throws CampaignDoesntExistsException {
        Campaign ca = new Campaign();
        
        try {
            ca = getDetails(id);
            ca.set_statusCampaign(!ca.get_statusCampaign());
            String query = "UPDATE public.campaign SET" +
                    " cam_status ="+ca.get_statusCampaign()+
                    " WHERE cam_id =?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,id);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new CampaignDoesntExistsException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Sql.bdClose(conn);
        }
        return ca.get_statusCampaign();
    }

    public Campaign createCampaign(Campaign ca) throws  CampaignDoesntExistsException {
        try {
            PreparedStatement preparedStatement = conn.prepareCall("{Call m03_addcampaign(?,?,?,?,?,?)}");
            preparedStatement.setString(1, ca.get_nameCampaign());
            preparedStatement.setString(2, ca.get_descCampaign());
            preparedStatement.setBoolean(3, ca.get_statusCampaign());
            preparedStatement.setDate(4, new java.sql.Date(ca.get_startCampaign().getTime()));
            preparedStatement.setDate(5, new java.sql.Date(ca.get_endCampaign().getTime()));
            preparedStatement.setInt(6, ca.get_idCompany());
            preparedStatement.execute();
        }catch (SQLException e){
            throw new CampaignDoesntExistsException(e);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ca;
    }



    public Campaign updateCompany (int id,Campaign ca) throws CampaignDoesntExistsException {
        try {
            PreparedStatement preparedStatement = conn.prepareCall("{Call m03_updatecampaign(?,?,?,?,?,?,?)}");
            preparedStatement.setString(1, ca.get_nameCampaign());
            preparedStatement.setString(2, ca.get_descCampaign());
            preparedStatement.setBoolean(3, ca.get_statusCampaign());
            preparedStatement.setDate(4, new java.sql.Date(ca.get_startCampaign().getTime()));
            preparedStatement.setDate(5, new java.sql.Date(ca.get_endCampaign().getTime()));
            preparedStatement.setInt(6, ca.get_idCompany());
            preparedStatement.setInt(7, ca.get_id());
            preparedStatement.execute();
        }catch (SQLException e){
            throw new CampaignDoesntExistsException(e);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ca;
    }

}
