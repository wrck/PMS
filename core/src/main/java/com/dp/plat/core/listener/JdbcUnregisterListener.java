package com.dp.plat.core.listener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class JdbcUnregisterListener implements ServletContextListener {
	
	private final static Logger log = LoggerFactory.getLogger(JdbcUnregisterListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//ServletContextListener.super.contextInitialized(sce);
	}

	@Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            log.info("Calling MySQL AbandonedConnectionCleanupThread checkedShutdown");
            // Or com.mysql.jdbc.AbandonedConnectionCleanupThread
            Class cls = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");
            Method method = cls.getMethod("checkedShutdown");
            method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Cannot call MySQL AbandonedConnectionCleanupThread.checkedShutdown!", e);
        }
        // Now deregister JDBC drivers in this context's ClassLoader:
        // Get the webapp's ClassLoader
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        // Loop through all drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == cl) {
                // This driver was registered by the webapp's ClassLoader, so deregister it:
                try {
                    log.info("Deregistering JDBC driver {}", driver);
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException ex) {
                    log.error("Error deregistering JDBC driver {}", driver, ex);
                }
            } else {
                // driver was not registered by the webapp's ClassLoader and may be in use elsewhere
                log.trace("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader", driver);
            }
        }
    }
}
