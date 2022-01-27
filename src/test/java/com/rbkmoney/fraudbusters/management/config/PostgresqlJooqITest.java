package com.rbkmoney.fraudbusters.management.config;

import dev.vality.testcontainers.annotations.postgresql.PostgresqlTestcontainer;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PostgresqlTestcontainer
@JooqTest
public @interface PostgresqlJooqITest {
}
