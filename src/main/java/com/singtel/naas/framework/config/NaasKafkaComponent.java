package com.singtel.naas.framework.config;

import com.singtel.naas.framework.constant.NaaSKafkaConstants;
import com.singtel.naas.framework.interceptor.NaasKafkaConsumerInterceptor;
import com.singtel.naas.framework.interceptor.NaasKafkaProducerInterceptor;
import com.singtel.naas.framework.serialization.StringHeaderDeserializer;
import org.apache.camel.component.kafka.KafkaConfiguration;
import org.apache.camel.component.kafka.KafkaEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.util.Properties;


public class NaasKafkaComponent {

    @Autowired
    protected Environment env;

//    @Autowired
//    private BuildProperties buildProperties;

    protected KafkaEndpoint createKafkaProducer(String topicName) {
        KafkaEndpoint kafkaEndpoint = new KafkaEndpoint();
        final KafkaConfiguration config = new KafkaConfiguration();
        String broker = env.getProperty(NaaSKafkaConstants.KAFKA_BROKER);
        config.setBrokers(broker);
        config.setTopic(topicName);
        config.setKeySerializerClass(NaaSKafkaConstants.STRINGSERIALIZER);
        config.setKeyDeserializer(NaaSKafkaConstants.STRINGDESERIALIZER);
        config.setValueDeserializer(NaaSKafkaConstants.STRINGDESERIALIZER);
        config.setRequestRequiredAcks(env.getProperty(NaaSKafkaConstants.PRODUCER_REQUEST_REQUIRED_ACKS, String.class, NaaSKafkaConstants.PRODUCER_REQUEST_REQUIRED_ACKS_DEFAULT));
        config.setRetries(env.getProperty(NaaSKafkaConstants.PRODUCER_RETRIES, Integer.class, NaaSKafkaConstants.PRODUCER_RETRIES_DEFAULT));
        config.setRetryBackoffMs(env.getProperty(NaaSKafkaConstants.PRODUCER_RETRY_BACKOFF_MS, Integer.class, NaaSKafkaConstants.PRODUCER_RETRY_BACKOFF_MS_DEFAULT));
        config.setEnableIdempotence(env.getProperty(NaaSKafkaConstants.PRODUCER_ENABLE_IDEMPOTENCE, Boolean.class, Boolean.TRUE));
        config.setMaxInFlightRequest(env.getProperty(NaaSKafkaConstants.PRODUCER_MAX_INFLIGHT_REQUEST_PER_CONN, Integer.class, NaaSKafkaConstants.PRODUCER_MAX_INFLIGHT_REQUEST_PER_CONN_DEFAULT));
        config.setRequestTimeoutMs(env.getProperty(NaaSKafkaConstants.PRODUCER_REQUEST_TIMEOUT, Integer.class, NaaSKafkaConstants.PRODUCER_DEFAULT_REQUEST_TIMEOUT));
        config.setConnectionMaxIdleMs(env.getProperty(NaaSKafkaConstants.CONNECTIONS_MAX_IDLE_TIMEOUT, Integer.class, NaaSKafkaConstants.CONNECTIONS_DEFAULT_MAX_IDLE_TIMEOUT));
        
        
        configureKafkaSecurity(config);

        MethodInterceptor handler = new Handler(config, env);
        KafkaConfiguration proxyConfig = (KafkaConfiguration) Enhancer.create(KafkaConfiguration.class, handler);
        kafkaEndpoint.setConfiguration(proxyConfig);
        config.setInterceptorClasses(NaasKafkaProducerInterceptor.class.getName());
        kafkaEndpoint.setEndpointUriIfNotSpecified("naaskafkaproducer:" + topicName);
        return kafkaEndpoint;
    }

    protected KafkaEndpoint createKafkaConsumer(String topicName, String group) {
        KafkaEndpoint kafkaEndpoint = new KafkaEndpoint();
        KafkaConfiguration config = new KafkaConfiguration();
        String broker = env.getProperty(NaaSKafkaConstants.KAFKA_BROKER);
        config.setBrokers(broker);
        config.setAutoOffsetReset(env.getProperty(NaaSKafkaConstants.CONSUMER_AUTO_OFFSET_RESET, String.class, NaaSKafkaConstants.EARLIEST));
        config.setConsumersCount(env.getProperty(NaaSKafkaConstants.CONSUMER_COUNTS, Integer.class, NaaSKafkaConstants.DEFAULT_CONSUMER_COUNTS));
        config.setGroupId(group);
        config.setTopic(topicName);
        config.setKeyDeserializer(NaaSKafkaConstants.STRINGDESERIALIZER);
        config.setValueDeserializer(NaaSKafkaConstants.STRINGDESERIALIZER);
        config.setInterceptorClasses(NaasKafkaConsumerInterceptor.class.getName());
        config.setKafkaHeaderDeserializer(new StringHeaderDeserializer());
//        final String clientId = "consumer-" + env.getProperty(NaaSKafkaConstants.POD_NAME, String.class, buildProperties.getArtifact()) + "-" + RandomStringUtils.randomAlphabetic(5);
//        config.setClientId(clientId);
        config.setMaxPollRecords(env.getProperty(NaaSKafkaConstants.MAX_POLL_RECORDS, Integer.class, NaaSKafkaConstants.DEFAULT_MAX_POLL_RECORDS));
        config.setAutoCommitIntervalMs(env.getProperty(NaaSKafkaConstants.AUTO_COMMIT_INTERVAL, Integer.class, NaaSKafkaConstants.DEFAULT_AUTO_COMMIT_INTERVAL));
        
        configureKafkaSecurity(config);

        kafkaEndpoint.setEndpointUriIfNotSpecified("naaskafkaconsumer:" + topicName);
        kafkaEndpoint.setConfiguration(config);
        return kafkaEndpoint;
    }

    protected void configureKafkaSecurity(KafkaConfiguration config) {
        if (Boolean.TRUE
                .equals(env.getProperty(NaaSKafkaConstants.ENABLE_KAFKA_SECURITY, Boolean.class, Boolean.FALSE))) {
            config.setSecurityProtocol(env.getProperty(NaaSKafkaConstants.SECURITY_PROTOCOL, NaaSKafkaConstants.SASL_PLAINTEXT));
            config.setSaslMechanism(env.getProperty(NaaSKafkaConstants.SASL_MECHANISN, NaaSKafkaConstants.PLAIN));
            config.setSaslJaasConfig(env.getProperty(NaaSKafkaConstants.JAAS_CONFIG));
            config.setSslTruststoreLocation(env.getProperty(NaaSKafkaConstants.SSL_TRUSTSTORE_PATH));
            config.setSslTruststorePassword(env.getProperty(NaaSKafkaConstants.SSL_TRUSTSTORE_PASS));
            config.setSslTruststoreType(env.getProperty(NaaSKafkaConstants.SSL_TRUSTSTORE_TYPE, "JKS"));
        }
    }

    protected KafkaEndpoint createKafkaConsumer(String topicName) {
        return createKafkaConsumer(topicName, env.getProperty(NaaSKafkaConstants.KAFKACONSUMERGROUPID));
    }

    @Bean
    public KafkaEndpoint endpointForAuditProducer() {
        return createKafkaProducer(env.getProperty(NaaSKafkaConstants.AUDITTOPIC));
    }

    @Bean
    public KafkaEndpoint endpointForErrorProducer() {
        return createKafkaProducer(env.getProperty(NaaSKafkaConstants.ERRORTOPIC));
    }

    @Bean
    public KafkaEndpoint endpointForRetryAuditProducer() {
        return createKafkaProducer(env.getProperty(NaaSKafkaConstants.AUDITTOPIC));
    }

    @Bean
    public KafkaEndpoint endpointForRetryErrorProducer() {
        return createKafkaProducer(env.getProperty(NaaSKafkaConstants.ERRORTOPIC));
    }

    @Bean
    public KafkaEndpoint endpointForRetryProducer() {
        return createKafkaProducer(env.getProperty(NaaSKafkaConstants.CONSUMERTOPIC));
    }

    /**
     * the stock class <code>org.apache.camel.component.kafka.KafkaConfiguration</code> does not support the following configs:<br/>
     * - client.dns.lookup<br/>
     * - delivery.timeout.max<br/>
     * This Handler class will override the method createProducerProperties() to add these missing configs
     */
    static class Handler implements MethodInterceptor {
        private final KafkaConfiguration original;
        private final Environment env;
        public Handler(KafkaConfiguration original, Environment env) {
            this.original = original;
            this.env = env; 
        }
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (method.getName().equalsIgnoreCase("createProducerProperties")) {
                Properties props = (Properties) method.invoke(original, args);
                props.setProperty("client.dns.lookup", env.getProperty(NaaSKafkaConstants.CLIENT_DNS_LOOKUP, String.class, NaaSKafkaConstants.DEFAULT_CLIENT_DNS_LOOKUP));
                props.setProperty("delivery.timeout.ms", env.getProperty(NaaSKafkaConstants.DELIVERY_TIMEOUT_MAX, String.class, NaaSKafkaConstants.DEFAULT_DELIVERY_TIMEOUT_MAX));
                return props;
            } else {
                return method.invoke(original, args);
            }
        }
    }
}
