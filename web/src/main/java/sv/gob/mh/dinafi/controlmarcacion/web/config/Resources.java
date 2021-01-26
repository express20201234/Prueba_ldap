package sv.gob.mh.dinafi.controlmarcacion.web.config;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.types.InputStreamType;
import com.querydsl.sql.types.JSR310LocalDateTimeType;
import com.querydsl.sql.types.JSR310LocalDateType;
import com.querydsl.sql.types.JSR310LocalTimeType;
import com.querydsl.sql.types.JSR310ZonedDateTimeType;
import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import sv.gob.mh.dinafi.controlmarcacion.web.ldap.LdapEnum;

@Singleton
public class Resources {

    @Resource(lookup = "java:comp/env/jdbc/myDS")
    @Produces
    private DataSource dataSource;

    @Produces
    public Configuration querydslConfiguration() throws InstantiationException, IllegalAccessException {
        SQLTemplates templates = OracleTemplates.builder().printSchema().build();
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(templates);
        //configuration.register(InputStreamType.class.newInstance());
        configuration.register(com.querydsl.sql.types.BytesType.class.newInstance());
        configuration.register(JSR310LocalDateType.class.newInstance());
        configuration.register(JSR310LocalTimeType.class.newInstance());
        configuration.register(JSR310LocalDateTimeType.class.newInstance());
        configuration.register(JSR310ZonedDateTimeType.class.newInstance());

        return configuration;
    }

    @Produces
    public LdapTemplate getLDAPTemplate(InjectionPoint injectionPoint) {

        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(LdapEnum.LDAP_URL);
        contextSource.setBase(LdapEnum.LDAP_BASE);
        contextSource.setUserDn(LdapEnum.LDAP_USER);
        contextSource.setPassword(LdapEnum.LDAP_PASSWORD);
        contextSource.afterPropertiesSet();
        return new LdapTemplate(contextSource);
    }

}
