open module edu.snhu.library {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.base;
    requires java.annotation;
    requires java.sql;
    requires spring.core;
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.starter.log4j2;
    requires spring.boot.starter.data.mongodb;
    requires spring.data.mongodb;
    requires spring.data.commons;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.apache.logging.log4j;
    requires org.apache.commons.lang3;
    requires static lombok;
    requires spring.boot.autoconfigure;
    requires de.flapdoodle.embed.mongo;
    requires de.flapdoodle.embed.mongo.packageresolver;
    requires de.flapdoodle.embed.process;
    requires de.flapdoodle.os;

    exports edu.snhu.library;
}