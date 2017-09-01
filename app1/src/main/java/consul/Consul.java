package consul;

import amqp.spring.camel.component.SpringAMQPComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import support.camel.NettyRegistry;
import support.camel.rabbitmq.SpringAMQP;

/**
 * Created by yilibin on 2017/7/18.
 */
public class Consul {
    public static void main(String[] args) throws Exception{


        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {

            public void configure() {

                from("netty4-http:http://127.0.0.1:8888/keytest1?")
                .to("consul:kv-put");



            }
        });
        context.start();


    }
}
