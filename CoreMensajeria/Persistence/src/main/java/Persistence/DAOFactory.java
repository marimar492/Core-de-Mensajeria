package Persistence;

//import Persistence.M01_Login.GetUserDao;
import Persistence.M06_DataOrigin.DAOApplication;
import Persistence.M04_Integrator.DAOIntegrator;
import Persistence.M05_Channel.DAOChannel;
import Persistence.M07_Template.*;
import Persistence.M01_Login.DAOUser;
import Persistence.Postgres.M09_Statistics.*;
import Persistence.M08_SendMessage.DAOSentMessage;
import Persistence.M02_Company.DAOCompany;
import Persistence.M03_Campaign.DAOCampaign;

public class DAOFactory {

    //region M01

    static public DAOUser instanciateDaoUser () {
        return new DAOUser();
    }

    //endregion

    //region M04
    static public DAOIntegrator instanciateDaoIntegrator(){return new DAOIntegrator();}
    static public DAOChannel instanciateDaoChannel(){ return new DAOChannel(); }
    //endregion

    //region M09

    //static public DAOStatisticEstrella instanciateDaoStatisticsEstrella(){ return new DAOStatisticEstrella(); }
    //static public DAOStatistic instanciateDAOStatistic(){ return new DAOSPostgrestatistic(); }

    //endregion

   static public DAOCompany instanciateDaoCompany ( ) { return new DAOCompany(); }

   static public DAOCampaign instanciateDaoCampaign ( ) { return new DAOCampaign(); }

   //region M06

    static public DAOApplication instanciateDaoApplication ( ) { return new DAOApplication(); }

    //endregion
   //region M07

    static public DAOMessage  instaciateDaoMessage( ){
        return new DAOMessage();
    }

    static public DAOTemplate instaciateDaoTemplate( ){
        return new DAOTemplate();
    }

    static public DAOPlanning instaciateDaoPlanning( ){
        return new DAOPlanning();
    }

    static public DAOParameter instaciateDaoParameter( ){
        return new DAOParameter();
    }

    static public DAOStatus createDAOStatus(){return new DAOStatus();}

    // end region

    //region M08

    static public DAOSentMessage instanciateDaoSentMessage( ) { return new DAOSentMessage(); }

    //end region


}
