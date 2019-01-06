package webService.M07_Template;

import Entities.M01_Login.Privilege;
import Entities.M07_Template.HandlerPackage.StatusHandler;
import Entities.M07_Template.HandlerPackage.TemplateHandler;
import Entities.M07_Template.Template;
import Exceptions.M07_Template.InvalidParameterException;
import Exceptions.M07_Template.TemplateDoesntExistsException;
import Exceptions.ParameterDoesntExistsException;
import Logic.Command;
import Logic.CommandsFactory;
import com.google.gson.Gson;
import webService.M01_Login.Error;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * M07_Parameter class is an API that is responsible for requesting information
 * about the templates
 */

@Path("/templates")
@Produces(MediaType.APPLICATION_JSON)
public class M07_Template {
    private final String MESSAGE_ERROR_INTERN = "Error Interno";
    private final String MESSAGE_EXCEPTION = "Excepcion";
    private final String MESSAGE_ERROR_PARAMETERDOESNTEXIST= "El parámetro ingresado no existe";

    /**
     * Method that returns all the templates filtered by a user and his company.
     * @param userId id of the user
     * @param companyId id of the company
     * @return ArrayList of templates
     */
    @GET
    public Response getTemplates(@QueryParam("userId") int userId,
                                 @QueryParam("companyId") int companyId){
        Response response;
        Error error;
        try {
            if(companyId==0 || userId==0){
                throw new InvalidParameterException();
            }
            Command c = CommandsFactory.createCommandGetParameters(companyId);
            c.execute();
            response = Response.ok(c.Return()).build();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            error = new Error(e.getMessage());
            response = Response.status(404).entity(error).build();
        }catch (ParameterDoesntExistsException e){
            e.printStackTrace();
            error = new Error(MESSAGE_ERROR_PARAMETERDOESNTEXIST);
            error.addError(MESSAGE_EXCEPTION,e.getMessage());
            response = Response.status(500).entity(error).build();
        } catch (Exception e) {
            e.printStackTrace();
            error = new Error(MESSAGE_ERROR_INTERN);
            error.addError(MESSAGE_EXCEPTION,e.getMessage());
            response = Response.status(500).entity(error).build();
        }
        return response;
    }

    /**
     * this method returns a specific template
     * @param id id of the template requested.
     * @return template
     */
    @GET
    @Path("/{templateId}")//Subsequent Path
    public Response getTemplate(@PathParam("templateId") int id){
        Response response;
        Error error;
        try {
            if(id==0){
                throw new InvalidParameterException();
            }
            Command c = CommandsFactory.createCommandGetTemplate(id);
            c.execute();
            response = Response.ok(c.Return()).build();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            error = new Error(e.getMessage());
            response = Response.status(404).entity(error).build();
        }catch (ParameterDoesntExistsException e){
            e.printStackTrace();
            error = new Error(MESSAGE_ERROR_PARAMETERDOESNTEXIST);
            error.addError(MESSAGE_EXCEPTION,e.getMessage());
            response = Response.status(500).entity(error).build();
        } catch (Exception e) {
            e.printStackTrace();
            error = new Error(MESSAGE_ERROR_INTERN);
            error.addError(MESSAGE_EXCEPTION,e.getMessage());
            response = Response.status(500).entity(error).build();
        }
        return response;
    }

    /**
     *
     this method returns the privileges that a user has over the templates.
     * @param userId id of the user
     * @param companyId id of the company
     * @return
     */
    @GET
    @Path("/privileges")
    public Response getTemplatePrivilegesByUser(@QueryParam("userId") int userId,
                                                @QueryParam("companyId") int companyId){
        Response response;
        Error error;
        try {
            if(userId==0||companyId==0){
                throw new InvalidParameterException();
            }
            Command c = CommandsFactory.createCommandGetTemplatePrivilegesByUser(userId,companyId);
            c.execute();
            response = Response.ok(c.Return()).build();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            error = new Error(e.getMessage());
            response = Response.status(404).entity(error).build();
        }catch (ParameterDoesntExistsException e){
            e.printStackTrace();
            error = new Error(MESSAGE_ERROR_PARAMETERDOESNTEXIST);
            error.addError(MESSAGE_EXCEPTION,e.getMessage());
            response = Response.status(500).entity(error).build();
        } catch (Exception e) {
            e.printStackTrace();
            error = new Error(MESSAGE_ERROR_INTERN);
            error.addError(MESSAGE_EXCEPTION,e.getMessage());
            response = Response.status(500).entity(error).build();
        }
        return response;
    }

    /**
     *
     This method is responsible for updating the status
     of a template in specific.
     * @param templateId id of the template
     * @param userId id of the user
     * @return If the template was saved successfully it returns true,
     * otherwise it returns false.
     */
    @POST
    @Path("/update/{templateId}")//Subsequent Path
    public Response postTemplateStatus(@PathParam("templateId") int templateId, int userId){
        Response response;
        Error error;
        try {
            if(userId==0||templateId==0){
                throw new InvalidParameterException();
            }
            Command c = CommandsFactory.createCommandPostTemplateStatus(templateId,userId);
            c.execute();
            response = Response.ok(c.Return()).build();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
            error = new Error(e.getMessage());
            response = Response.status(404).entity(error).build();
        }catch (ParameterDoesntExistsException e){
            e.printStackTrace();
            error = new Error(MESSAGE_ERROR_PARAMETERDOESNTEXIST);
            error.addError(MESSAGE_EXCEPTION,e.getMessage());
            response = Response.status(500).entity(error).build();
        } catch (Exception e) {
            e.printStackTrace();
            error = new Error(MESSAGE_ERROR_INTERN);
            error.addError(MESSAGE_EXCEPTION,e.getMessage());
            response = Response.status(500).entity(error).build();
        }
        return response;
    }

    //TODO: Arreglar los parametros de estos metodos
    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean postTemplate(String json){
        TemplateHandler templateHandler = new TemplateHandler();
        return templateHandler.postTemplateData(json);
    }

    @PUT
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean updateTemplate(String json){
        TemplateHandler templateHandler = new TemplateHandler();
        return templateHandler.updateTemplateData(json);
    }
}
