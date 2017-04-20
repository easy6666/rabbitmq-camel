import amqp.spring.camel.component.SpringAMQPComponent;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by kaiseryi on 2014/12/10.
 */
public class CamelRecv {
    public static void main(String args[]) throws Exception {

        CachingConnectionFactory cf = new CachingConnectionFactory();
        cf.setHost("localhost");
        cf.setPort(5672);
        cf.setUsername("guest");
        cf.setPassword("guest");
        //cf.setVirtualHost("/");
        System.out.println(cf.getHost()+":"+cf.getPort()+":"+cf.getVirtualHost()+":");
        // set up the queue, exchange, binding on the broker
        RabbitAdmin admin = new RabbitAdmin(cf);
        Queue queue = new Queue("myQueue");
        admin.declareQueue(queue);
        TopicExchange exchange = new TopicExchange("myExchange");
        admin.declareExchange(exchange);
        admin.declareBinding(
                BindingBuilder.bind(queue).to(exchange).with("binding_key"));

//

        SpringAMQPComponent spring_amqp = new SpringAMQPComponent();

        Map<String, ConnectionFactory> connectionFactory = new HashMap<String, ConnectionFactory>();
        connectionFactory.put("DefaultConnection", cf);
        spring_amqp.setConnectionFactory(connectionFactory);

        Map<String, AmqpAdmin> amqpAdministration = new HashMap<String, AmqpAdmin>();
        amqpAdministration.put("DefaultConnection", admin);
        spring_amqp.setAmqpAdministration(amqpAdministration);


/**
 *
 * 采用camel方式接受rabbitmq的消息
 *
 */
        CamelContext context = new DefaultCamelContext();

        context.addComponent("spring-amqp", spring_amqp);

        context.addRoutes(new RouteBuilder() {

            public void configure() {
                from("spring-amqp:myExchange:myQueue:binding_key?type=topic&autodelete=false&durable=true")
                        .process(new Processor() {
                            public void process(org.apache.camel.Exchange exchange) throws Exception {
                                exchange.getOut().setBody("Page not found");
                                exchange.getOut().setHeader(org.apache.camel.Exchange.HTTP_RESPONSE_CODE, 404);
                                System.err.println("Msg received");
                            }
                        });
            }
        });
        context.start();


/**
 *
 * 采用SpringAMQP组件的接口方式接受rabbitmq的消息
 *
 */

/**
        // set up the listener and container
        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer(cf);
        Object listener = new Object() {
            public void handleMessage(String foo) {
                System.err.println(foo);
            }
        };
        MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
        container.setMessageListener(adapter);
        container.setQueueNames("myQueue");
        container.start();
*/


    }
}
