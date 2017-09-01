import amqp.spring.camel.component.SpringAMQPComponent;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import support.camel.rabbitmq.SpringAMQP;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by kaiseryi on 2014/12/10.
 */
public class CamelRecv {
    public static void main(String args[]) throws Exception {

        SpringAMQPComponent spring_amqp = new SpringAMQP(
                "localhost",
                5672,
                "guest",
                "guest",
                "binding_key",
                "myQueue",
                "myExchange",
                "/"
                ).initCompoment();


        CamelContext context = new DefaultCamelContext();

        context.addComponent("spring-amqp", spring_amqp);

        context.addRoutes(new RouteBuilder() {

            public void configure() {
                from("spring-amqp:myExchange:myQueue:binding_key?type=topic&autodelete=false&durable=true")
                        .process(new Processor() {
                            public void process(org.apache.camel.Exchange exchange) throws Exception {
                                exchange.getOut().setBody("echo back");
                                exchange.getOut().setHeader(org.apache.camel.Exchange.HTTP_RESPONSE_CODE, 404);
                                System.err.println(exchange.getOut().getBody());

                            }
                        });
            }
        });
        context.start();




    }
}
