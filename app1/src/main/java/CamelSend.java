import amqp.spring.camel.component.SpringAMQPComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.netty4.NettyServerBootstrapConfiguration;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import support.camel.NettyRegistry;
import support.camel.rabbitmq.SpringAMQP;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import static sun.jvm.hotspot.runtime.PerfMemory.end;

/**
 * Created by kaiseryi on 2014/12/10.
 */
public class CamelSend {



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


        CamelContext context = new DefaultCamelContext(new NettyRegistry().createRegistry());
        context.addComponent("spring-amqp", spring_amqp);


        context.addRoutes(new RouteBuilder() {

            public void configure() {

                from("netty4-http:http://127.0.0.1:8888/myservice?bootstrapConfiguration=#nettyHttpBootstrapOptions")
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                exchange.getOut().setBody(exchange.getOut().getBody()+"send request");
                                exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
                                System.err.println(exchange.getOut());
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
 *      CachingConnectionFactory cf = new CachingConnectionFactory();
        cf.setHost("localhost");
        cf.setPort(5672);
        cf.setUsername("guest");
        cf.setPassword("guest");
        cf.setVirtualHost("/");

        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer(cf);
        //RabbitTemplate template = new RabbitTemplate(cf);
        template.convertAndSend("myExchange", "binding_key", "Hello, world!");
        Thread.sleep(1000);
        container.stop();
 **/


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
