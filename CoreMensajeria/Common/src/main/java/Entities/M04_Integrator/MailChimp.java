package Entities.M04_Integrator;

/**
* Clase concreta de tipo Integrador que nos permite tener
* acceso a métodos concretos para realizar el envío de mensajes.
* @author Kevin Martinez
* @author Braulio Picon
* @author Alexander Fernandez
* @see Integrator
*/
public class MailChimp extends Integrator implements IIntegrator{

    /**
    * Constructor de la clase MailChimp
    *
    * @param idIntegrator   Id del Integrador.
    * @param threadCapacity Capacidad de Hilos que soporta el integrador
    * @param messageCost    Costo por mensaje
    * @param nameIntegrator Nombre del integrador
    * @param apiIntegrator  Token del Integrador
    * @param enabled        Permite saber el estado en el que se encuentra el integrador
    * @see Integrator
    */
    public MailChimp(int idIntegrator, int threadCapacity, float messageCost, String nameIntegrator,
                     String apiIntegrator, boolean enabled) {
        super(idIntegrator, threadCapacity, messageCost, nameIntegrator, apiIntegrator, enabled);
    }

    /**
    * Metodo que se encarga de enviar el Mensaje
    *
    * @param msg     Mensaje que se va a enviar.
    * @param address Direccion a la que se va a enviar.
    * @param idMsg   id del mensaje.
    * @see MessageIntegrator
    */
    @Override
    public MessageIntegrator sendMessage(String msg, String address, String idMsg) {
        if (this.isEnabled()) {
            try {
                Thread.sleep(2100);
                MessageIntegrator message = new MessageIntegrator(msg, address, idMsg);
                message.send();
                return message;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            MessageIntegrator message = new MessageIntegrator(msg, address, idMsg);
            return message;
        }
        return null;
    }
}
