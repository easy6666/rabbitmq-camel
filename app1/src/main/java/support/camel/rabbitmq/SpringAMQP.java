package support.camel.rabbitmq;

import amqp.spring.camel.component.SpringAMQPComponent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yilibin on 2017/6/21.
 */
public class SpringAMQP {

    private String host;
    private String vhost;
    private int port;
    private String username;
    private String password;

    private String bind_key;
    private String quename;
    private String exchangename;


    public SpringAMQP(String host,
                      int port,
                      String username,
                      String password,
                      String bind_key,
                      String queue,
                      String exchange,
                      String vhost)
    {
        this.host = host;
        this.vhost = vhost;
        this.port = port;
        this.username = username;
        this.password = password;
        this.bind_key = bind_key;
        this.quename = queue;
        this.exchangename = exchange;
    }

    public SpringAMQPComponent initCompoment(){

        CachingConnectionFactory cf = new CachingConnectionFactory();
        cf.setHost(host);
        cf.setPort(port);
        cf.setUsername(username);
        cf.setPassword(password);

        cf.setVirtualHost(vhost);
        System.out.println(cf.getHost()+":"+cf.getPort()+":"+cf.getVirtualHost()+":");

        // set up the queue, exchange, binding on the broker
        RabbitAdmin admin = new RabbitAdmin(cf);

        Queue queue = new Queue(quename);
        admin.declareQueue(queue);
        TopicExchange exchange = new TopicExchange(exchangename);
        admin.declareExchange(exchange);
        admin.declareBinding(
                BindingBuilder.bind(queue).to(exchange).with(bind_key));


        SpringAMQPComponent spring_amqp = new SpringAMQPComponent();

        Map<String, ConnectionFactory> connectionFactory = new HashMap<String, ConnectionFactory>();
        connectionFactory.put("DefaultConnection", cf);
        spring_amqp.setConnectionFactory(connectionFactory);

        Map<String, AmqpAdmin> amqpAdministration = new HashMap<String, AmqpAdmin>();
        amqpAdministration.put("DefaultConnection", admin);
        spring_amqp.setAmqpAdministration(amqpAdministration);

        RabbitTemplate template = new RabbitTemplate(cf);
        template.setReplyTimeout(1000);

        Map<String, AmqpTemplate> amqpTemplate = new HashMap<String, AmqpTemplate>();
        amqpTemplate.put("DefaultConnection", template);
        spring_amqp.setAmqpTemplate(amqpTemplate);

        return spring_amqp;
    }

}
