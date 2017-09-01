/**
 * Created by kaiseryi on 2014/12/9.
 */
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

public class Recv {

    private static final String EXCHANGE_NAME = "myExchange1";

    public static void main(String[] argv)
            throws Exception {


        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("myQueue",true,false,false,null);
        String queueName = channel.queueDeclare().getQueue();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        channel.queueBind(queueName, EXCHANGE_NAME, "binding_key");
        //channel.queueBind(queueName, EXCHANGE_NAME, "binding_key2");


        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            String routingKey = delivery.getEnvelope().getRoutingKey();

            System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");
        }


    }
}