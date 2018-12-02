package M09_StatisticsManagement;

import Classes.M09_Statistics.SqlEstrella;
import Classes.M09_Statistics.Statistics;
import Classes.Sql;
import Exceptions.CampaignDoesntExistsException;
import Exceptions.CompanyDoesntExistsException;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import  webService.M09_StatisticsManagement.M09_Statistics;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import static  org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


public class M09_StatisticsTest {

    Gson gson = new Gson();
    private Connection conn = SqlEstrella.getConInstance();

    @Test
    void getAllCompaniesTest() throws CompanyDoesntExistsException {
        try {
            M09_Statistics intance = new M09_Statistics();
            Response salida = intance.getAllCompanies(1);
            assertEquals( 200, salida.getStatus() );
            assertNotNull( salida.getEntity());
        }
        catch( Exception e ) {
            e.printStackTrace();
            throw new CompanyDoesntExistsException ( e );
        }

    }

    @Test
    void getAllChannelsTest()  {
        try {
            M09_Statistics intance = new M09_Statistics();
            Response salida = intance.getAllChannels();
            assertEquals( 200, salida.getStatus() );
            assertEquals( salida.getEntity().toString(),
                    "[{\"idChannel\":1,\"nameChannel\":\"SMS\"},{\"idChannel\":2,\"nameChannel\":\"Email\"}]" );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }


    @Test
    void getCompaniesCountTest()  {
        try {
            M09_Statistics intance = new M09_Statistics();

            Response salida = intance.getCompaniesCount();
            assertEquals( 200, salida.getStatus() );
            assertEquals( salida.getEntity().toString(),
                    "{\"x\":[\"Company 1\",\"Company 2\",\"Company 3\",\"Company 4\"],\"y\":[18,7,2,2]}" );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    void getCampaignCountTest()  {
        try {
            M09_Statistics intance = new M09_Statistics();

            Response salida = intance.getCampaignsCount();
            assertEquals( 200, salida.getStatus() );
            assertEquals( salida.getEntity().toString(),
                    "{\"x\":[\"Campaign 1\",\"Campaign 2\",\"Campaign 3\",\"Campaign 4\",\"Campaign 5\"," +
                            "\"Campaign 6\",\"Campaign 7\",\"Campaign 8\",\"Campaign 9\",\"Campaign 10\"," +
                            "\"Campaign 11\",\"Campaign 12\",\"Campaign 13\",\"Campaign 14\"]" +
                            ",\"y\":[6,3,4,2,3,3,1,1,1,1,1,1,1,1]}" );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }


    @Test
    void getChannelsCountTest()  {
        try {
            M09_Statistics intance = new M09_Statistics();

            Response salida = intance.getChannelsCount();
            assertEquals( 200, salida.getStatus() );
            assertEquals( salida.getEntity().toString(),
                    "{\"x\":[\"SMS\",\"Email\"],\"y\":[15,14]}" );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    void setParametersforQueryTest()  {
        try {
            M09_Statistics intance = new M09_Statistics();
            ArrayList<Integer> lista = new ArrayList<>();
            lista.add(1);
            String params = "and me.sen_com_id in";
            assertEquals( intance.setParametersforQuery(lista, params), "and me.sen_com_id in(1)" );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }


    @Test
    void getStatistics()  {
        try {
            M09_Statistics intance = new M09_Statistics();
            ArrayList<Integer> listaCompany  = new ArrayList<>();
            ArrayList<Integer> listaCampaign = new ArrayList<>();
            ArrayList<Integer> listaChannels = new ArrayList<>();
            listaCompany.add(1);
            listaCampaign.add(1);
            listaChannels.add(1);
            Response salida = intance.getStatistics( listaCompany, listaCampaign, listaChannels );

            assertEquals( 200, salida.getStatus() );
            assertEquals( salida.getEntity().toString(),
                    "{\"companies\":{\"x\":[\"Company 1\"],\"y\":[3]}" +
                            ",\"campaigns\":{\"x\":[\"Campaign 1\"],\"y\":[3]}" +
                            ",\"channels\":{\"x\":[\"SMS\"],\"y\":[3]}}" );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }




    @Disabled
    @Test
    void getOverallCountFor()  {
        try {
            M09_Statistics intance = new M09_Statistics();

             /*Response salida = intance.getOverallCountFor();

            assertEquals( salida.getEntity().toString(),
                    "" );
*/
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Disabled
    @Test
    void getCampaignsForCompanyTest()  {
        try {
            M09_Statistics intance = new M09_Statistics();
            ArrayList<Integer> lista = new ArrayList<>();
            lista.add(1);
            lista.add(2);

            Response salida = intance.getCampaignsForCompany( lista );

            assertEquals( salida.getEntity().toString(),
                    "" );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }


}
