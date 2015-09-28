package com.tw.jersey;

import com.tw.jersey.utils.ConfigUtil;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ServerLauncher extends JettyServerLauncher{

    public static final int PORT = Integer.parseInt(ConfigUtil.getProperty("server.port"));

    public static void main(String[] args) throws Exception {
        launch(new ServerLauncher());
    }

    @Override
    protected void configServletContextHandler(ServletContextHandler handler) {
        handler.addServlet(new ServletHolder(new DefaultServlet()), "/*");
    }

    @Override
    protected String[] getJerseyProviderPackages() {
        return new String[]{
            "com.tw.jersey"
        };
    }

    @Override
    protected String getSpringContextLocation() {
        return "classpath:context.xml";
    }

    @Override
    protected int getServerPort() {
        return PORT;
    }
}
