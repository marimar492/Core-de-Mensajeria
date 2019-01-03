package Persistence.M02_Company;

import Entities.Entity;
import Entities.Factory.EntityFactory;
import Entities.M01_Login.User;
import Entities.M02_Company.Company;
import Entities.M06_DataOrigin.PathHandler;
import Entities.Sql;
import Exceptions.CompanyDoesntExistsException;
import Exceptions.ParameterCantBeNullException;
import Persistence.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAOCompany  implements IDAOCompany {

    private Connection _conn = Sql.getConInstance();

    final String UPDATE_COMPANY_STATUS = "{CALL m02_changecompanystatus(?,?)}";
    final String SELECT_COMPANY_BY_ID = "{CALL  m02_getcompanybyid(?)}";
    final String SELECT_COMPANY_BY_RESPONSIBLE ="{CALL m02_getcompaniesbyresponsible(?)}";
    final String SELECT_COMPANIES_BY_USER = "{Call m02_getcompanies(?)}";
    final String CREATE_COMPANY=  "{Call m02_addcompany(?,?,?,?,?)}";
    final String SELECT_ALL_COMPANIES = "{Call m02_getcompaniesall}";


    @Override
    public Entity company(Entity e) throws SQLException {

    return null;
    }

    @Override
    public Boolean changeStatus(Entity e) {

        Company _co = ( Company ) e;
        _conn = Sql.getConInstance();
        PreparedStatement _preparedStatement = null;

        try {
            _preparedStatement = _conn.prepareCall( UPDATE_COMPANY_STATUS );
            _preparedStatement.setBoolean( 1, _co.get_status() );
            _preparedStatement.setInt( 2, _co.get_id() );
            _preparedStatement.execute();

        } catch ( SQLException e1 ) {
            e1.printStackTrace();
        }

        return _co.get_status();

    }

    @Override
    public ArrayList<Entity> companiesByResponsible(Entity e) {
        ArrayList<Entity> _coList= new ArrayList<>();
        User _user = ( User ) e;

        try {
            PreparedStatement  preparedStatement = _conn.prepareCall(SELECT_COMPANY_BY_RESPONSIBLE);
            preparedStatement.setInt( 1, _user.get_idUser() );
            ResultSet _result = preparedStatement.executeQuery();
            while ( _result.next() ) {
                _coList.add( getCompany( _result ) );
            }
        }
        catch (SQLException exc) {
            exc.printStackTrace();
        }
        return  _coList;
    }


    public Company getCompany( ResultSet _result ) throws SQLException {

        Company _company = EntityFactory.CreateFullCompany(
                _result.getInt( "com_id" ),
                _result.getString( "com_name" ),
                _result.getString( "com_description" ),
                _result.getBoolean( "com_status" ),
                _result.getString( "com_route_link" ) );
        return _company;
    }

    @Override
    public Entity companyById( Entity e ) {
        Company _company = ( Company ) e;

            try {
                PreparedStatement  _preparedStatement = _conn.prepareCall( SELECT_COMPANY_BY_ID );
                _preparedStatement.setInt( 1, _company.get_id() );
                ResultSet _result = _preparedStatement.executeQuery();
                while ( _result.next() ) {
                    _company = getCompany( _result );
                }
            }
            catch (SQLException exc) {
                exc.printStackTrace();
            }
            return _company;

    }

    @Override
    public ArrayList<Entity> companiesByUser( Entity e ) {
        ArrayList<Entity> _coList= new ArrayList<>();
        User _company = ( User ) e;
        try {
            PreparedStatement _ps = _conn.prepareCall(SELECT_COMPANIES_BY_USER);
            _ps.setInt(1, _company.get_id());
            ResultSet _result = _ps.executeQuery();
            while(_result.next()){
                _coList.add( getCompany( _result ) );
            }
        }
        catch ( Exception exc ) {
            exc.printStackTrace();
        }
        return _coList;
    }


    @Override
    public ArrayList<Entity> companiesEnabled() {
        return null;
    }

    @Override
    public ArrayList<Entity> allCompanies() {
        ArrayList<Entity> _coList = new ArrayList<>();
        try {
            PreparedStatement _ps = _conn.prepareCall(SELECT_ALL_COMPANIES);
            ResultSet _result = _ps.executeQuery();
            while(_result.next()){
                _coList.add( getCompany( _result ) );
            }
        }
        catch ( Exception exc ) {
            exc.printStackTrace();
        }
        return _coList;
    }


    @Override
    public void create( Entity e ) {
        Company _co = (Company) e;
        PathHandler _ph  = new PathHandler();
        try {

            PreparedStatement preparedStatement = _conn.prepareCall(CREATE_COMPANY);
            preparedStatement.setString(1, _co.get_name());
            preparedStatement.setString(2, _co.get_desc());
            preparedStatement.setBoolean(3, _co.get_status());
            preparedStatement.setString(4, _ph.generatePath(_co));
            preparedStatement.setInt(5, _co.get_idUser());
            preparedStatement.execute();

        }catch (Exception exc){
            exc.printStackTrace();
        }
    }

    @Override
    public Entity read(Entity e) {return null; }

    @Override
    public Entity update(Entity e) {
        return null;
    }
}



    //region API Obtener Compañias por usuario
/*
    public ArrayList<Company> companyList(int id) throws CompanyDoesntExistsException {
        ArrayList<Company> coList= new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareCall("{Call m02_getcompanies(?)}");
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            while(result.next()){
                Company co = new Company();
                co.set_idCompany(result.getInt("com_id"));
                co.set_name(result.getString("com_name"));
                co.set_desc(result.getString("com_description"));
                co.set_status(result.getBoolean("com_status"));
                co.set_link(result.getString("com_route_link"));
                coList.add(co);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new CompanyDoesntExistsException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
        }return coList;
    }
    //endregion

    public ArrayList<Company> companyListResponsible(int id) throws CompanyDoesntExistsException {
        ArrayList<Company> coList= new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareCall("{Call m02_getcompaniesbyresponsible(?)}");
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            while(result.next()){
                Company co = new Company();
                co.set_idCompany(result.getInt("com_id"));
                co.set_name(result.getString("com_name"));
                co.set_desc(result.getString("com_description"));
                co.set_status(result.getBoolean("com_status"));
                co.set_link(result.getString("com_route_link"));
                coList.add(co);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new CompanyDoesntExistsException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
        }return coList;
    }


    //region Todas Las Campañas
    public ArrayList<Company> companyListAll() throws CompanyDoesntExistsException {
        ArrayList<Company> coList = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareCall("{Call m02_getcompaniesall()}");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Company co = new Company();
                co.set_idCompany(result.getInt("com_id"));
                co.set_name(result.getString("com_name"));
                co.set_desc(result.getString("com_description"));
                co.set_status(result.getBoolean("com_status"));
                co.set_link(result.getString("com_route_link"));
                coList.add(co);
            }
        }
            catch (SQLException e) {
                e.printStackTrace();
                throw new CompanyDoesntExistsException(e);
            }
        return coList;
    }
    //endregion

    //region API Detalles Compañia

    public Company getDetails (int id) throws CompanyDoesntExistsException {
        String select = "SELECT * FROM company where com_id = ?";
        Company co = new Company();
        try {

            PreparedStatement ps = conn.prepareStatement(select);
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                co.set_idCompany(result.getInt("com_id"));
                co.set_name(result.getString("com_name"));
                co.set_desc(result.getString("com_description"));
                co.set_status(result.getBoolean("com_status"));
                co.set_link(result.getString("com_route_link"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new CompanyDoesntExistsException(e);
        }

        return co;
    }

    //endregion

    public boolean changeStatus(int id) throws CompanyDoesntExistsException {
            Company co = new Company();

        try {
            co = getDetails(id);
            co.set_status(!co.get_status());
            String query = "UPDATE public.company SET" +
                    " com_status ="+co.get_status()+
                    " WHERE com_id =?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,id);
            ps.executeUpdate();

        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new CompanyDoesntExistsException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return co.get_status();
    }

    public Company createCompany (Company co) throws CompanyDoesntExistsException, ParameterCantBeNullException {
        PathHandler ph  = new PathHandler();
        try {

            PreparedStatement preparedStatement = conn.prepareCall("{Call m02_addcompany}");
            preparedStatement.setInt(1, co.get_idCompany());
            preparedStatement.setString(2, co.get_name());
            preparedStatement.setString(3, co.get_desc());
            preparedStatement.setBoolean(4, co.get_status());
            preparedStatement.setString(5, ph.generatePath(co));
            preparedStatement.execute();
        }catch (SQLException e){
            throw new CompanyDoesntExistsException(e);
        }catch (Exception e){
            e.printStackTrace();
        }
        return co;
    }
*/

