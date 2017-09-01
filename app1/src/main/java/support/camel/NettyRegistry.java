package support.camel;

import org.apache.camel.component.netty4.NettyServerBootstrapConfiguration;
import org.apache.camel.impl.JndiRegistry;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Created by yilibin on 2017/6/21.
 */
public class NettyRegistry {

    public NettyRegistry(){

    }

    public JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = new JndiRegistry(createJndiContext());

        NettyServerBootstrapConfiguration nettyHttpBootstrapOptions = new NettyServerBootstrapConfiguration();
        nettyHttpBootstrapOptions.setBacklog(200);
        nettyHttpBootstrapOptions.setConnectTimeout(1000);
        nettyHttpBootstrapOptions.setKeepAlive(true);
        nettyHttpBootstrapOptions.setWorkerCount(4);

        jndi.bind("nettyHttpBootstrapOptions", nettyHttpBootstrapOptions);
        return jndi;
    }

    protected Context createJndiContext() throws Exception {
        Properties properties = new Properties();

        // jndi.properties is optional
        InputStream in = getClass().getClassLoader().getResourceAsStream("jndi.properties");
        if (in != null) {
            properties.load(in);
        } else {
            properties.put("java.naming.factory.initial", "org.apache.camel.util.jndi.CamelInitialContextFactory");
        }
        return new InitialContext(new Hashtable<Object, Object>(properties));
    }
}
