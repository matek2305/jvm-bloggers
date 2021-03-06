package com.jvm_bloggers

import javax.transaction.Transactional

import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

@ContextConfiguration(classes = [JvmBloggersApplication], loader = SpringApplicationContextLoader)
@ActiveProfiles("test")
@Transactional
public abstract class SpringContextAwareSpecification extends Specification {

    static String PASSWORD = "secretPassword"

    def setupSpec() {
        System.setProperty("jasypt.encryptor.password", PASSWORD)
    }

}
