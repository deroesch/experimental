package com.bnymellon.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

@SuppressWarnings("deprecation")
public class TestEmployee {

    public static void main(String[] args) {

        // Let's get configured...
        AnnotationConfiguration config = new AnnotationConfiguration();
        config.addAnnotatedClass(Employee.class);
        config.configure("hibernate.cfg.xml");

        // Create the table, if missing.
        new SchemaExport(config).create(true, true);

        // Make a session factory
        SessionFactory sFact = config.buildSessionFactory();

        // Create a session
        Session session = sFact.getCurrentSession();

        try {
            // Start a transaction
            session.beginTransaction();

            // Make an employee
            Employee e = new Employee();
            e.setId(10);
            e.setName("Alex Roesch");

            // Flag to save
            session.save(e);

            // Save!
            session.getTransaction().commit();
            
        } finally {

            // Close session.
            if (session.isOpen()) {
                session.close();
            }

            // Close factory
            if (!sFact.isClosed()) {
                sFact.close();
            }
        }

    }

}
