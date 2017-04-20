import amqp.spring.camel.component.SpringAMQPComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaiseryi on 2014/12/10.
 */
public class CamelSend {
    public static void main(String args[]) throws Exception {

        /*

        CamelContext context = new DefaultCamelContext();
        Component comp = new SpringAMQPComponent();
        context.addComponent("spring-amqp", comp);
        context.addRoutes(new RouteBuilder() {

            public void configure() {
                from("jetty://http://127.0.0.1:8888/myservice")
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                exchange.getOut().setBody("Page not found");
                                exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
                            }
                        }).to("rabbitmq://localhost/myExchange");
            }
        });
        //amqp:myQueue:topic:foo.bar
        context.start();

        */


        CachingConnectionFactory cf = new CachingConnectionFactory();
        cf.setHost("localhost");
        cf.setPort(5672);
        cf.setUsername("guest");
        cf.setPassword("guest");
        //cf.setVirtualHost("/");
        RabbitAdmin admin = new RabbitAdmin(cf);
        RabbitTemplate template = new RabbitTemplate(cf);
        //template.convertAndSend("myExchange", "binding_key", "Hello, world!");
        //Thread.sleep(1000);

        SpringAMQPComponent spring_amqp = new SpringAMQPComponent();

        Map<String, ConnectionFactory> connectionFactory = new HashMap<String, ConnectionFactory>();
        connectionFactory.put("DefaultConnection", cf);
        spring_amqp.setConnectionFactory(connectionFactory);

        Map<String, AmqpAdmin> amqpAdministration = new HashMap<String, AmqpAdmin>();
        amqpAdministration.put("DefaultConnection", admin);
        spring_amqp.setAmqpAdministration(amqpAdministration);

        Map<String, AmqpTemplate> amqpTemplate = new HashMap<String, AmqpTemplate>();
        amqpTemplate.put("DefaultConnection", template);
        spring_amqp.setAmqpTemplate(amqpTemplate);


/**
 *
 * 采用camel方式发送给rabbitmq jetty的消息
 *
 */
        CamelContext context = new DefaultCamelContext();
        context.addComponent("spring-amqp", spring_amqp);
        context.addRoutes(new RouteBuilder() {

            public void configure() {
                from("netty4-http:http://127.0.0.1:8888/myservice")
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                exchange.getOut().setBody("Page not found");
                                exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
                            }
                        })
                        //.setHeader("ROUTING_KEY", simple("binding_key"))
                        //.setHeader("EXCHANGE_NAME", simple("myExchange"))
                        .to("spring-amqp:myExchange:myQueue:binding_key?type=topic&autodelete=false&durable=true")
                        .removeHeader("ROUTING_KEY");

            }
        });
        context.start();

/**
 *
 * 采用SpringAMQP组件的接口方式发送给rabbitmq jetty的消息
 *
 */

/**
        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer(cf);

        //RabbitTemplate template = new RabbitTemplate(cf);
        template.convertAndSend("myExchange", "binding_key", "Hello, world!");
        Thread.sleep(1000);
        container.stop();
 **/

    }
}
