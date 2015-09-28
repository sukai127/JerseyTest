package com.tw.jersey;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;

import java.util.Arrays;
import java.util.List;

public abstract class JettyServerLauncher {
    private static final String CONTEXT_PATH = "/";
    private static Logger log = Logger.getLogger(JettyServerLauncher.class);
    private final Server jettyServer;

    public JettyServerLauncher() {
        jettyServer = createJettyServer();
    }

    public static void launch(final JettyServerLauncher serverLauncher) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                serverLauncher.shutdown();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverLauncher.runForever();
                } catch (Exception e) {
                    log.error("Failed to launch jetty server due to: ", e);
                }
            }
        }).start();
    }

    private void shutdown() {
        try {
            jettyServer.stop();
        } catch (Exception e) {
            log.warn("Failed to stop server normally.", e);
        }
    }

    private void runForever() throws Exception {
        jettyServer.start();
        jettyServer.join();
    }

    private Server createJettyServer() {
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        // spring context loader
        ContextLoaderListener contextLoaderListener = new ContextLoaderListener();
        handler.addEventListener(contextLoaderListener);
        handler.setInitParameter("contextConfigLocation", getSpringContextLocation());

        handler.setContextPath(CONTEXT_PATH);

        handler.addServlet(createJerseyServletHolder(), "/rest/*");

        configServletContextHandler(handler);

        Server server = new Server(getServerPort());
        server.setHandler(handler);
        return server;
    }

    private ServletHolder createJerseyServletHolder() {
        ServletHolder jerseyServletHolder = new ServletHolder(new ServletContainer());
        jerseyServletHolder.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");

        List<String> providerPackages = Lists.newArrayList("com.tw.jersey.api");
        providerPackages.addAll(Arrays.asList(getJerseyProviderPackages()));

        jerseyServletHolder.setInitParameter("com.sun.jersey.config.property.packages",
                Joiner.on(",").join(providerPackages));

        jerseyServletHolder.setInitOrder(1);

        return jerseyServletHolder;
    }

    protected void configServletContextHandler(final ServletContextHandler handler) {
    }

    protected abstract String[] getJerseyProviderPackages();

    protected abstract String getSpringContextLocation();

    protected abstract int getServerPort();
}
