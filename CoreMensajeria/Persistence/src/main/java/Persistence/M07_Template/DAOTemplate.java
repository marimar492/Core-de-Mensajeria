package Persistence.M07_Template;

import Entities.Entity;
import Entities.EntityFactory;
import Entities.M01_Login.Privilege;
import Entities.M01_Login.User;
import Entities.M03_Campaign.Campaign;
import Entities.M04_Integrator.Integrator;
import Entities.M05_Channel.Channel;
import Entities.M06_DataOrigin.Application;
import Exceptions.ApplicationNotFoundException;
import Persistence.M06_DataOrigin.DAOApplication;
import Entities.M07_Template.MessagePackage.Message;
import Entities.M07_Template.PlanningPackage.Planning;
import Entities.M07_Template.StatusPackage.Status;
import Entities.M07_Template.Template;
import Exceptions.M07_Template.TemplateDoesntExistsException;
import Exceptions.MessageDoesntExistsException;
import Exceptions.ParameterDoesntExistsException;
import Persistence.DAO;
import Persistence.DAOFactory;
import Persistence.Factory.DAOAbstractFactory;
import com.google.gson.*;
import Persistence.M03_Campaign.DAOCampaign;
import Persistence.M01_Login.DAOUser;
import Persistence.M04_Integrator.DAOIntegrator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Clase para acceder a datos de la Plantilla en BD
 */
public class DAOTemplate extends DAO implements IDAOTemplate {

    final String CREATE_TEMPLATE_WITH_APP= "{CALL m07_posttemplate(?,?,?)}";
    final String CREATE_TEMPLATE_WITHOUT_APP= "{CALL m07_posttemplate2(?,?)}";
    final String GET_TEMPLATE= "{CALL m07_gettemplate(?)}";
    final String GET_ALL_TEMPLATES= "{ CALL m07_select_all_templates()}";
    final String GET_ALL_TEMPLATES_BY_CAMPAIGN= "{ call m07_select_templates_by_campaign(?) }";
    final String GET__CAMPAIGN_BY_TEMPLATE = "{ CALL m07_getcampaignbytemplate(?) }";
    final String GET_CAMPAIGN_BY_USER_COMPANY = "{call m07_select_campaign_by_user_company(?,?,?)}";
    final String GET_APPLICATION_BY_TEMPLATE = "{call m07_select_applicantion_by_template(?)}";
    final String GET_CHANNEL_BY_TEMPLATE = "{call m07_select_channels_by_template(?)}";
    final String GET_PRIVILEGES_TEMPLATE = "{call m07_select_privileges_by_user_company(?,?)}";
    final String UPDATE_TEMPLATE_WITH_APP = "{CALL m07_updatetemplate(?,?,?)}";
    final String UPDATE_TEMPLATE_WITHOUT_APP = "{CALL m07_updatetemplate2(?,?)}";
    final String CREATE_CHANNEL_INTEGRATOR = "{CALL m07_postChannelIntegrator(?,?,?)}";
    final String DELETE = "{CALL m07_deletetemplate(?)}";
    final String DELETE_CHANNEL_INTEGRATOR = "{CALL m07_deleteChannelIntegrator(?)}";
    final static Logger log = LogManager.getLogger("CoreMensajeria");

    @Override
    public void create(Entity e) {
    }

    @Override
    public Entity read(Entity e) {
        return null;
    }

    @Override
    public Entity update(Entity e) {
        return null;
    }

    /**
     * Metodo para traer string en especifivo.
     * @param json json con info
     * @return Entidad con Plantilla si exitoso,
     * otherwise it returns false.
     */
    public Entity postTemplateData(String json) throws ParameterDoesntExistsException, TemplateDoesntExistsException, MessageDoesntExistsException {
        try {
            //region Instrumentation Debug
            log.debug("Entrando a el metodo postTemplateData("+json+")" );
            //endregion
            Gson gson = new Gson();
            //recibimos el objeto json
            JsonParser parser = new JsonParser();
            JsonObject gsonObj = parser.parse(json).getAsJsonObject();
            //hay que extraer campaña y aplicacion, parametros por defecto
            //se crea el template y se retorna su id
            int templateId = this.postTemplate(gsonObj.get("campaign").getAsInt(),gsonObj.get("applicationId").getAsInt(), gsonObj.get("userId").getAsInt());
            //se establece el template  como no aprobado

            IDAOStatus daoStatus = DAOAbstractFactory.getFactory().createDAOStatus();
            //StatusHandler.postTemplateStatusNoAprovado(templateId);
            daoStatus.postTemplateStatusNotApproved(templateId);
            //insertamos los nuevos parametros
            String[] parameters = gson.fromJson(gsonObj.get("newParameters").getAsJsonArray(),String[].class);

            IDAOParameter daoParameter = DAOAbstractFactory.getFactory().createDaoParameter();
            daoParameter.postParameter(parameters,gsonObj.get("company").getAsInt());
            //obtenemos el valor del mensaje,y parametros
            parameters = gson.fromJson(gsonObj.get("parameters").getAsJsonArray(),String[].class);

            String message = gsonObj.get("message").getAsString();
            IDAOMessage daoMessage = DAOAbstractFactory.getFactory().createDaoMessage();
            daoMessage.postMessage(message,gsonObj.get("company").getAsInt(),parameters,templateId);

            //obtenemos los valores de los canales e integradores
            JsonArray channelIntegrator = gsonObj.get("channel_integrator").getAsJsonArray();
            postChannelIntegrator(channelIntegrator,templateId);

            //planning
            String[] planning = gson.fromJson(gsonObj.get("planning").getAsJsonArray(),String[].class);
            IDAOPlanning daoPlanning = DAOAbstractFactory.getFactory().createDaoPlanning();
            daoPlanning.postPlanning(planning,templateId);
            //region Instrumentation Info
            log.info("Se ejecuto el metodo postTemplateData() exitosamente");
            //endregion
            //region Instrumentation Debug
            log.debug("Saliendo de el metodo postTemplateData() con retorno: "+ this.get(templateId));
            //endregion
            return this.get(templateId);
        } catch (Exception e){
            //region Instrumentation Error
            log.error("El metodo postTemplateData("+json+") arrojo la excepcion:" + e.getMessage());
            //endregion
            throw e;
        }
    }


    @Override
    public int postTemplate(int campaignId,int applicationId, int userId) {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo postTemplate("+ campaignId+","+applicationId+","+userId+")" );
        //endregion

        Connection _conn = this.getBdConnect();

        PreparedStatement preparedStatement = null;

        try {

            if( applicationId != 0){
                preparedStatement = _conn.prepareCall( CREATE_TEMPLATE_WITH_APP );
                preparedStatement.setInt(1, campaignId );
                preparedStatement.setInt(2, applicationId );
                preparedStatement.setInt(3, userId );
                //region Instrumentation Debug
                log.info("Se ejecuto el metodo postTemplate("+campaignId+","+applicationId+","+userId+")" );
                //endregion
            }
            else{
                preparedStatement = _conn.prepareCall( CREATE_TEMPLATE_WITHOUT_APP );
                preparedStatement.setInt(1, campaignId );
                preparedStatement.setInt(2, userId );
                //region Instrumentation Debug
                log.info("Se ejecuto el metodo postTemplate("+campaignId+","+applicationId+","+userId+")" );
                //endregion
            }

           ResultSet _rs = preparedStatement.executeQuery();

            if( _rs.next() )
                return _rs.getInt(1);

        } catch ( SQLException e1 ) {
            e1.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo postTemplate("+campaignId+","+applicationId+","+userId+") arrojo la excepcion:" + e1.getMessage());
            //endregion
        }

        this.closeConnection();
        //region Instrumentation Debug
        log.debug("Saliendo del metodo postTemplate("+ campaignId+","+applicationId+","+userId+")" );
        //endregion
        return 0;
    }

    /**
     * Get specific  template
     * @param templateId
     * @return template
     */
    @Override
    public Entity get(int templateId) throws TemplateDoesntExistsException, MessageDoesntExistsException, ParameterDoesntExistsException {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo get("+ templateId+")" );
        //endregion
        //Entity to Return
        Entity _t  = null;
        Connection _conn = this.getBdConnect();

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = _conn.prepareCall( GET_TEMPLATE );
            preparedStatement.setInt( 1, templateId );
            ResultSet _rs = preparedStatement.executeQuery();
            //region Instrumentation Debug
            log.info("Se ejecuto el metodo postTemplate("+templateId+")" );
            //endregion
            if(_rs.next()){
                _t = this.createTemplate(_rs);
            }else{
                throw new TemplateDoesntExistsException("Error: la plantilla " + templateId + " no existe");
            }

        }
        catch (SQLException ex){
            ex.printStackTrace();
            throw new TemplateDoesntExistsException
                    ("Error: la plantilla " + templateId + " no existe", ex, templateId);
        } catch ( MessageDoesntExistsException ex ){
            ex.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo postTemplate("+templateId+") arrojo la excepcion:" + ex.getMessage());
            //endregion
            throw new MessageDoesntExistsException();
        } catch ( ParameterDoesntExistsException ex ){
            ex.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo postTemplate("+templateId+") arrojo la excepcion:" + ex.getMessage());
            //endregion
        }

        this.closeConnection();
        //region Instrumentation Debug
        log.debug("Saliendo del metodo get("+ templateId+") retornando:" + _t);
        //endregion
        return _t;
    }

    /**
     * Get all templates
     * @return
     */
    @Override
    public ArrayList<Entity> getAll() throws MessageDoesntExistsException, ParameterDoesntExistsException {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo getAll()" );
        //endregion
        //Entity to Return
        ArrayList<Entity> _t  = new ArrayList<>();
        Connection _conn = this.getBdConnect();

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = _conn.prepareCall( GET_ALL_TEMPLATES );
            ResultSet _rs = preparedStatement.executeQuery();

            while( _rs.next() ){
                Entity _template = this.createTemplate(_rs);
                _t.add(_template);
            }

            //region Instrumentation Debug
            log.info("Se ejecuto el metodo getAll() exitosamente" );
            //endregion
        }
        catch (SQLException el){
            //region Instrumentation Error
            log.error("El metodo postTemplate() arrojo la excepcion:" + el.getMessage());
            //endregion
            el.printStackTrace();
        }

        this.closeConnection();
        //region Instrumentation Debug
        log.debug("Saliendo del metodo getAll() con retorno" +_t );
        //endregion
        return _t;
    }

    /**
     * Busca campaña por plantilla
     * @param templateId
     * @return
     */
    @Override
    public Campaign getCampaignByTemplate(int templateId) {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo getCampaignByTemplate("+ templateId+")" );
        //endregion
        Connection _conn = getBdConnect();
        Campaign campaign = new Campaign();
        PreparedStatement preparedStatement = null;

        try{
            preparedStatement = _conn.prepareCall( GET__CAMPAIGN_BY_TEMPLATE );
            preparedStatement.setInt(1, templateId );
            ResultSet _rs = preparedStatement.executeQuery();

            if( _rs.next() ){

                //Se busca una campaña por it
                DAOCampaign daoCampaign = DAOFactory.instanciateDaoCampaign();
                Campaign c = EntityFactory.CreateCampaignId(_rs.getInt("tem_campaign_id"));
                //obtener objeto campana con el id de campana del query anterior
                campaign = daoCampaign.campaignById(c);

            }
            //region Instrumentation Debug
            log.info("Se ejecuto el metodo getCampaignByTemplate("+templateId+") exitosamente" );
            //endregion
        } catch (SQLException e){
            e.printStackTrace();
            throw new TemplateDoesntExistsException
                    ("Error: la plantilla " + templateId + " no existe", e, templateId);
        }catch (Exception e){
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo getCampaignByTemplate("+templateId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        } finally {
            closeConnection();
            //region Instrumentation Debug
            log.debug("Saliendo del metodo getCampaignByTemplate("+ templateId+") con retorno: " +campaign);
            //endregion
            return campaign;
        }

    }


    /**
     * Busca plantillas por campaña
     * @param userId
     * @param companyId
     * @return
     */
    @Override
    public ArrayList<Template> getTemplatesByCampaign(int userId, int companyId) {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo getTemplatesByCampaign("+userId+","+companyId+")" );
        //endregion
        ArrayList<Template> templateArrayList = new ArrayList<>();
        ArrayList<Campaign> campaignArrayList = new ArrayList<>();
        Connection _conn = getBdConnect();
        try{
            //Se busca la campaña con el id del usuario y el id de la compañia
            campaignArrayList = this.getCampaignsByUserOrCompany(userId,companyId);
            for(int x = 0; x < campaignArrayList.size(); x++){
                //Se busca todas las plantillas por la campaña encontrada
                PreparedStatement preparedStatement = _conn.prepareCall(GET_ALL_TEMPLATES_BY_CAMPAIGN);
                preparedStatement.setInt(1,campaignArrayList.get(x).get_idCampaign());
                ResultSet _rs = preparedStatement.executeQuery();
                while(_rs.next()){
                    Template template = (Template) this.createTemplate(_rs);
                    templateArrayList.add(template);
                }
            }
            //region Instrumentation Info
            log.info("Se ejecuto el metodo getTemplatesByCampaign("+userId+","+companyId+") exitosamente" );
            //endregion
        }catch (SQLException e) {
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo getTemplatesByCampaign("+userId+","+companyId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        }catch (Exception e){
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo getTemplatesByCampaign("+userId+","+companyId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        }finally {
            closeConnection();
            //region Instrumentation Debug
            log.debug("Saliendo dell metodo getTemplatesByCampaign("+userId+","+companyId+") con retorno: " +templateArrayList);
            //endregion
            return templateArrayList;
        }
    }

    /**
     * Busca aplicacion por plantilla
     * @param templateId
     * @return
     */
    @Override
    public Application getApplicationByTemplate(int templateId) {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo getApplicationByTemplate("+templateId+")" );
        //endregion
        Application _application = new Application();
        Connection _conn = getBdConnect();

        PreparedStatement _ps = null;

        try {
            //Se busca aplicacion por plantilla
            _ps = _conn.prepareCall( GET_APPLICATION_BY_TEMPLATE );
            _ps.setInt( 1, templateId );
            ResultSet _rs = _ps.executeQuery();
            _rs.next();
            DAOApplication _daoApplication = DAOFactory.instanciateDaoApplication();
            _application = _daoApplication.getApplication(_rs.getInt("applicationId"));
            //region Instrumentation Info
            log.info("Se ejecuto el metodo getApplicationByTemplate("+templateId+") exitosamente" );
            //endregion
        }
        catch (SQLException el){
            el.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo getApplicationByTemplate("+templateId+") arrojo la excepcion:" + el.getMessage());
            //endregion
        }catch ( ApplicationNotFoundException e ){
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo getApplicationByTemplate("+templateId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        }catch (Exception e){
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo getApplicationByTemplate("+templateId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        }

        closeConnection();
        //region Instrumentation Debug
        log.debug("Saliendo del metodo getApplicationByTemplate("+templateId+") con retorno: " +_application);
        //endregion
        return _application;
    }

    /**
     * Buscar Canales por Plantilla
     * @param templateId
     * @return
     */
    @Override
    public ArrayList<Channel> getChannelsByTemplate(int templateId) {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo getChannelsByTemplate("+templateId+")" );
        //endregion
        ArrayList<Channel> channels = new ArrayList<>();
        Connection _conn = getBdConnect();
        try {

            //Busca el canal
            PreparedStatement preparedStatement = _conn.prepareCall( GET_CHANNEL_BY_TEMPLATE );
            preparedStatement.setInt(1,templateId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while( resultSet.next() ){
                ArrayList<Entity> integrators = new ArrayList<>();
                DAOIntegrator integratorDAO = DAOFactory.instanciateDaoIntegrator();
                Integrator integrator = (Integrator) integratorDAO.getConcreteIntegrator(
                        resultSet.getInt("ci_integrator_id")
                );

                integrators.add(integrator);
                Channel channel = EntityFactory.createChannel(
                        resultSet.getInt("ci_channel_id"),
                        resultSet.getString("cha_name"),
                        resultSet.getString("cha_description"),
                        integrators
                );
                channels.add(channel);
            }
            //region Instrumentation Info
            log.info("Se ejecuto el metodo getChannelsByTemplate("+templateId+") exitosamente" );
            //endregion
        } catch (SQLException e) {
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo getChannelsByTemplate("+templateId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        } catch (Exception e){
            e.printStackTrace();
        }finally{
            closeConnection();
            //region Instrumentation Debug
            log.debug("Saliendo del metodo getChannelsByTemplate("+templateId+") con retorno:"+channels);
            //endregion
            return channels;
        }
    }

    /**
     * Buscar privilegios
     * @param userId
     * @param companyId
     * @return
     */
    @Override
    public ArrayList<Privilege> getTemplatePrivilegesByUser(int userId, int companyId) {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo getTemplatePrivilegesByUser("+userId+","+companyId+")" );
        //endregion
        ArrayList<Privilege> privileges = new ArrayList<>();
        Connection _conn = getBdConnect();
        try {
            //Se busca los privilegios por usuario y compañia
            PreparedStatement preparedStatement = _conn.prepareCall(GET_PRIVILEGES_TEMPLATE);
            preparedStatement.setInt(1,userId);
            preparedStatement.setInt(2,companyId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Privilege privilege = new Privilege();
                privilege.set_idPrivileges(resultSet.getInt("pri_id"));
                privilege.set_codePrivileges(resultSet.getString("pri_code"));
                privilege.set_actionPrivileges(resultSet.getString("pri_action"));
                privileges.add(privilege);
            }
            //region Instrumentation Info
            log.info("Se ejecuto el metodo getTemplatePrivilegesByUser("+userId+","+companyId+") exitosamente" );
            //endregion
        } catch (SQLException e) {
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo getTemplatePrivilegesByUser("+userId+","+companyId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        }
        closeConnection();
        //region Instrumentation Debug
        log.debug("Saliendo del metodo getTemplatePrivilegesByUser("+userId+","+companyId+") con retorno: "+privileges );
        //endregion
        return privileges;
    }


    /**
     * Buscar campaña por Usuario o Compañia
     * @param userId
     * @param companyId
     * @return
     */
    @Override
    public ArrayList<Campaign> getCampaignsByUserOrCompany(int userId, int companyId){
        //region Instrumentation Debug
        log.debug("Entrando a el metodo getCampaignsByUserOrCompany("+userId+","+companyId+")" );
        //endregion
        ArrayList<Campaign> campaignArrayList = new ArrayList<>();
        Connection _conn = getBdConnect();
        try{
            PreparedStatement _ps = _conn.prepareCall(GET_CAMPAIGN_BY_USER_COMPANY);
            //Si existen los dos Id's, se busca por los dos, si existe solo el id de usuario se busca solo por usuario
            if((userId!=0)&&(companyId!=0)){
                _ps.setInt(1,0);
                _ps.setInt(2,userId);
                _ps.setInt(3,companyId);
            }else if(userId!=0){
                _ps.setInt(1,userId);
                _ps.setInt(2,0);
                _ps.setInt(3,0);
            }else{
                return null;
            }
            ResultSet _rs = _ps.executeQuery();
            while(_rs.next()){
                Campaign campaign = EntityFactory.CreateFullCampaign(_rs.getInt("cam_id"),
                        _rs.getString("cam_name"),_rs.getString("cam_description"),
                        _rs.getBoolean("cam_status"),_rs.getDate("cam_start_date"),
                        _rs.getDate("cam_end_date"),companyId);
                campaignArrayList.add(campaign);
            }
            //region Instrumentation Info
            log.info("Se ejecuto el metodo getCampaignsByUserOrCompany("+userId+","+companyId+") exitosamente" );
            //endregion
        }catch(SQLException e){
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo getCampaignsByUserOrCompany("+userId+","+companyId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        } catch (Exception e){
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo getCampaignsByUserOrCompany("+userId+","+companyId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        } finally {
            closeConnection();
            //region Instrumentation Debug
            log.debug("Saliendo del metodo getCampaignsByUserOrCompany("+userId+","+companyId+") con retorno:" +campaignArrayList );
            //endregion
            return campaignArrayList;
        }
    }

    /**
     * Guarda relacion con canales e integradores
     * @param channelIntegratorList
     * @param templateId
     */
    private void postChannelIntegrator(JsonArray channelIntegratorList, int templateId) {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo postChannelIntegrator("+channelIntegratorList+","+templateId+")" );
        //endregion
        JsonObject channelIntegrator;
        int channel;
        int integrator;
        Connection _conn = getBdConnect();
        PreparedStatement _ps = null;
        try {
            for (JsonElement list : channelIntegratorList){
                channelIntegrator = list.getAsJsonObject();
                channel = channelIntegrator.get("channel").getAsJsonObject().get("_id").getAsInt();
                integrator = channelIntegrator.get("integrator").getAsJsonObject().get("_id").getAsInt();
                _ps = _conn.prepareCall(CREATE_CHANNEL_INTEGRATOR);
                _ps.setInt(1,templateId);
                _ps.setInt(2,channel);
                _ps.setInt(3,integrator);

                _ps.execute();
            }
            //region Instrumentation Info
            log.info("Se ejecuto el metodo postChannelIntegrator("+channelIntegratorList+","+templateId+") exitosamente" );
            //endregion
        }catch (SQLException e) {
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo postChannelIntegrator("+channelIntegratorList+","+templateId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        }catch(Exception e){
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo postChannelIntegrator("+channelIntegratorList+","+templateId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        } finally {
            //region Instrumentation Debug
            log.debug("Saliendo del metodo postChannelIntegrator("+channelIntegratorList+","+templateId+")" );
            //endregion
            closeConnection();
        }
    }

    /**
     * Modificar plantilla
     * @param json
     * @return
     * otherwise it returns false.
     */
    public boolean updateTemplateData(String json){
        boolean rest = false;
        try {

            //region Instrumentation Debug
            log.debug("Entrando a el metodo updateTemplateData("+json+")" );
            //endregion
            Gson gson = new Gson();
            //recibimos el objeto json
            JsonParser parser = new JsonParser();
            JsonObject gsonObj = parser.parse(json).getAsJsonObject();
            updateTemplate(gsonObj.get("campaign").getAsInt(), gsonObj.get("applicationId").getAsInt(), gsonObj.get("templateId").getAsInt());

            //insertamos los nuevos parametros
            String[] parameters = gson.fromJson(gsonObj.get("newParameters").getAsJsonArray(),String[].class);
            IDAOParameter daoParameter = DAOAbstractFactory.getFactory().createDaoParameter();
            daoParameter.postParameter(parameters,gsonObj.get("company").getAsInt());

            //update de mensaje
            parameters = gson.fromJson(gsonObj.get("parameters").getAsJsonArray(),String[].class);

            IDAOMessage daoMessage = DAOAbstractFactory.getFactory().createDaoMessage();
            daoMessage.updateMessage(gsonObj.get("message").getAsString(),gsonObj.get("templateId").getAsInt(),parameters,gsonObj.get("company").getAsInt());

            //update de Channel Integrator
            JsonArray channelIntegrator = gsonObj.get("channel_integrator").getAsJsonArray();
            updateChannelIntegrator(channelIntegrator,gsonObj.get("templateId").getAsInt());
            //planning
            String[] planning = gson.fromJson(gsonObj.get("planning").getAsJsonArray(),String[].class);
            IDAOPlanning daoPlanning = DAOAbstractFactory.getFactory().createDaoPlanning();
            daoPlanning.updatePlanning(planning,gsonObj.get("templateId").getAsInt());


            rest= true;
            //region Instrumentation Info
            log.info("Se ejecuto el metodo updateTemplateData("+json+") exitosamente" );
            //endregion
        } catch (Exception e){
            System.out.println(e);
            rest = false;
            //region Instrumentation Error
            log.error("El metodo updateTemplateData("+json+") arrojo la excepcion:" + e.getMessage());
            //endregion
        }
        //region Instrumentation Debug
        log.debug("Saliendo del metodo updateTemplateData("+json+") con retorno:" +true);
        //endregion
        return rest;
    }


    /**
     * Metodo que modifica tabla de plantilla
     * @param campaignId
     * @param applicationId
     * @param templateId
     */
    @Override
    public void updateTemplate(int campaignId, int applicationId, int templateId ) {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo updateTemplate("+campaignId+","+applicationId+","+templateId+")" );
        //endregion
        Connection _conn = getBdConnect();

        PreparedStatement preparedStatement = null;

        try {
            //Buscar manera de quitar este IF
            if( applicationId > 0){
                preparedStatement = _conn.prepareCall( UPDATE_TEMPLATE_WITH_APP );
                preparedStatement.setInt(1, campaignId );
                preparedStatement.setInt(2, applicationId );
                preparedStatement.setInt(3, templateId );
            }
            else{
                preparedStatement = _conn.prepareCall( UPDATE_TEMPLATE_WITHOUT_APP );
                preparedStatement.setInt(1, campaignId );
                preparedStatement.setInt(2, templateId );
            }

            preparedStatement.execute();
            //region Instrumentation Info
            log.info("Se ejecuto el metodo updateTemplate("+campaignId+","+applicationId+","+templateId+") exitosamente" );
            //endregion
        }catch (SQLException e) {
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo updateTemplate("+campaignId+","+applicationId+","+templateId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        }catch(Exception e){
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo updateTemplate("+campaignId+","+applicationId+","+templateId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        }
        closeConnection();
        //region Instrumentation Debug
        log.debug("Saliendo del metodo updateTemplate("+campaignId+","+applicationId+","+templateId+")" );
        //endregion
    }

    /**
     * Moficiar relacion con canal e integrador
     * @param channelIntegratorList
     * @param templateId
     */
    private void updateChannelIntegrator(JsonArray channelIntegratorList, int templateId) {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo updateChannelIntegrator("+channelIntegratorList+","+templateId+")" );
        //endregion
        Connection _conn = this.getBdConnect();
        PreparedStatement _ps = null;
        try{
            _ps = _conn.prepareCall(DELETE_CHANNEL_INTEGRATOR);
            _ps.setInt(1,templateId);
            _ps.execute();
            postChannelIntegrator(channelIntegratorList,templateId);
            //region Instrumentation Info
            log.info("Se ejecuto el metodo updateChannelIntegrator("+channelIntegratorList+","+templateId+") exitosamente" );
            //endregion
        } catch (SQLException e) {
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo updateChannelIntegrator("+channelIntegratorList+","+templateId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        } catch(Exception e){
            e.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo updateChannelIntegrator("+channelIntegratorList+","+templateId+") arrojo la excepcion:" + e.getMessage());
            //endregion
        } finally {
            this.closeConnection();
        }
        //region Instrumentation Debug
        log.debug("Saliendo del metodo updateChannelIntegrator("+channelIntegratorList+","+templateId+")" );
        //endregion
    }

    /**
     * Borra una plantilla
     * @param id
     */
    @Override
    public void deleteTemplate(int id){
        //region Instrumentation Debug
        log.debug("Entrando a el metodo deleteTemplate("+id+")" );
        //endregion
        try{
            Connection _conn = this.getBdConnect();
            PreparedStatement _ps = _conn.prepareCall(DELETE);
            _ps.setInt(1,id);
            _ps.execute();
            //region Instrumentation Info
            log.info("Se ejecuto el metodo deleteTemplate("+id+") exitosamente" );
            //endregion
        }catch( SQLException ex ){
            ex.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo deleteTemplate("+id+") arrojo la excepcion:" + ex.getMessage());
            //endregion
        }
        //region Instrumentation Debug
        log.debug("Salinedo del metodo deleteTemplate("+id+")" );
        //endregion
    }

    /**
     * Funcion para crear un entidad Plantilla
     * @param _rs
     * @return
     */
    private Entity createTemplate(ResultSet _rs) throws ParameterDoesntExistsException, MessageDoesntExistsException{
        //region Instrumentation Debug
        log.debug("Entrando a el metodo createTemplate("+_rs+")" );
        //endregion
        Entity _t = null;
        try{
            int templateId = _rs.getInt("tem_id");

            //Campaign
            Campaign _campaign = this.getCampaignByTemplate( templateId );

            //application
            Application _app = this.getApplicationByTemplate( templateId );

            //Message
            IDAOMessage _daoMessage = DAOAbstractFactory.getFactory().createDaoMessage();
            Message _message = ( Message )_daoMessage.getMessage( templateId );

            //user
            DAOUser _userDao = DAOFactory.instanciateDaoUser();
            User _user = _userDao.findByUsernameId( _rs.getInt("tem_user_id") );

            //Channel
            ArrayList<Channel> _channels = this.getChannelsByTemplate( templateId );

            //Planning
            IDAOPlanning _daoPlanning = DAOAbstractFactory.getFactory().createDaoPlanning();
            Planning _planning = (Planning) _daoPlanning.getPlanning( templateId );

            //Status
            Status _status = Status.createStatus(_rs.getInt("tem_id"),
                    _rs.getString("sta_name"));

             _t = EntityFactory.CreateTemplate(
                    _rs.getInt("tem_id"),
                    _message,
                    _rs.getDate("tem_creation_date"),
                    _status,
                    _channels,
                    _campaign,
                    _app,
                    _user,
                    _planning
            );
            //region Instrumentation Info
            log.info("Se ejecuto el metodo createTemplate("+_rs+") exitosamente" );
            //endregion
        }catch( SQLException ex ){
            ex.printStackTrace();
            //region Instrumentation Error
            log.error("El metodo createTemplate("+_rs+") arrojo la excepcion:" + ex.getMessage());
            //endregion
        }catch (ParameterDoesntExistsException e) {
            //region Instrumentation Error
            log.error("El metodo createTemplate("+_rs+") arrojo la excepcion:" + e.getMessage());
            //endregion
            throw new ParameterDoesntExistsException( e );
        }catch (MessageDoesntExistsException e){
            //region Instrumentation Error
            log.error("El metodo createTemplate("+_rs+") arrojo la excepcion:" + e.getMessage());
            //endregion
            throw new MessageDoesntExistsException( e );
        }

        //region Instrumentation Debug
        log.debug("Saliendo del metodo createTemplate("+_rs+") con retorno: "+_t );
        //endregion
        return _t;
    }
}
