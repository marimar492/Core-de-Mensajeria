package Logic.M09_Statistics;

import Entities.Entity;
import Logic.Command;
import Persistence.Factory.DAOAbstractFactory;
import Persistence.IDAO_StatisticEstrella;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetCompanyStatisticCommand extends Command<Entity> {

    private IDAO_StatisticEstrella dao;
    private Entity statistic;
    final static Logger log = LogManager.getLogger("CoreMensajeria");

    @Override
    public void execute() throws Exception {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo execute() de GetCompanyStatisticCommand" );
        //endregion
        DAOAbstractFactory factory = DAOAbstractFactory.getFactory();
        dao = factory.instanciateDaoStatisticsEstrella();
        statistic = dao.getCompanyStatistic();
        //region Instrumentation Info
        log.info("Se ejecuto el metodo execute() exitosamente");
        //endregion
        //region Instrumentation Debug
        log.debug("Saliendo de el metodo execute() de GetCompanyStatisticCommand" );
        //endregion
    }

    @Override
    public Entity Return() {
        //region Instrumentation Debug
        log.debug("Entrando a el metodo return() de GetCompanyStatisticCommand" );
        //endregion
        //region Instrumentation Info
        log.info("Se ejecuto el metodo Return() exitosamente");
        //endregion
        //region Instrumentation Debug
        log.debug("Saliendo de el metodo Return() de GetCompanyStatisticCommand" );
        //endregion
        return statistic;
    }
}
