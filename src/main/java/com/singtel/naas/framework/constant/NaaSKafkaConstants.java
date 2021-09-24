package com.singtel.naas.framework.constant;

public class NaaSKafkaConstants {
    public static final String KAFKA_BROKER = "kafka.broker";
    public static final String ACKS_CONFIG = "kafka.acks";
    public static final String CONSUMERTOPIC = "kafka.consumer.topic";
    public static final String PRODUCERTOPIC = "kafka.producer.topic";
    public static final String ERRORTOPIC = "kafka.error.topic";
    public static final String TCP_ERRORTOPIC = "kafka.error.tcptopic";
    public static final String HTTP_ERRORTOPIC = "kafka.error.httptopic";
    public static final String AUDITTOPIC = "kafka.audit.topic";
    public static final String KAFKACONSUMERGROUPID = "kafka.consumer.group.id";
    public static final String EARLIEST = "earliest";
    public static final String STRINGENCODER = "kafka.serializer.StringEncoder";
    public static final int CONSUMERCOUNT_VALUE_ONE = 1;
    public static final String STRINGSERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
    public static final String STRINGDESERIALIZER = "org.apache.kafka.common.serialization.StringDeserializer";
    public static final String UPDATE_STATUS_TOPIC = "kafka.updatestatus.topic";
    public static final String WORKER_POOL_CORE_COUNT = "kafka.worker.pool.core.count";
    public static final String WORKER_POOL_MAX_COUNT = "kafka.worker.pool.max.count";
    public static final String SECURITY_PROTOCOL = "kafka.security.protocol";
    public static final String ENABLE_KAFKA_SECURITY = "kafka.security.enable";
    public static final String JAAS_CONFIG = "kafka.security.jaas.config";
    public static final String SASL_MECHANISN = "kafka.security.sasl.mechanism";
    public static final String KAFKA_USERNAME = "kafka.security.sasl.username";
    public static final String KAFKA_PASS_WD = "kafka.security.sasl.password";
    public static final String SSL_TRUSTSTORE_PATH = "kafka.security.ssl.truststore.location";
    public static final String SSL_TRUSTSTORE_TYPE = "kafka.security.ssl.truststore.type";
    public static final String SSL_TRUSTSTORE_PASS = "kafka.security.ssl.truststore.password";
    public static final String PLAIN = "PLAIN";
    public static final String SASL_PLAINTEXT = "SASL_PLAINTEXT";
    public static final String PRODUCER_REQUEST_REQUIRED_ACKS = "kafka.producer.requestRequiredAcks";
    public static final String PRODUCER_REQUEST_REQUIRED_ACKS_DEFAULT = "all";
    public static final String PRODUCER_RETRIES = "kafka.producer.retries";
    public static final Integer PRODUCER_RETRIES_DEFAULT = 5;
    public static final String PRODUCER_ENABLE_IDEMPOTENCE = "kafka.producer.enableIdempotance";
    public static final String PRODUCER_RETRY_BACKOFF_MS = "kafka.producer.retryBackoffMs";
    public static final Integer PRODUCER_RETRY_BACKOFF_MS_DEFAULT = 50;
    public static final String PRODUCER_MAX_INFLIGHT_REQUEST_PER_CONN = "kafka.producer.maxInflightRequestsPerConn";
    public static final Integer PRODUCER_MAX_INFLIGHT_REQUEST_PER_CONN_DEFAULT = 1;
    public static final String POD_NAME = "metadata.podName";
    public static final Integer DEFAULT_MAX_POLL_RECORDS = 1;
    public static final String MAX_POLL_RECORDS = "kafka.consumer.maxPollRecords";
    public static final String AUTO_COMMIT_INTERVAL = "kafka.consumer.autoCommitInterval";
    public static final Integer DEFAULT_AUTO_COMMIT_INTERVAL = 1000;
    public static final String PRODUCER_REQUEST_TIMEOUT = "kafka.producer.requestTimeoutMs";
    public static final Integer PRODUCER_DEFAULT_REQUEST_TIMEOUT = 10000;
    public static final String CLIENT_DNS_LOOKUP = "kafka.clientDnsLookup";
    public static final String DEFAULT_CLIENT_DNS_LOOKUP = "use_all_dns_ips";
    public static final String DELIVERY_TIMEOUT_MAX = "kafka.producer.deliveryTimeoutMs";
    public static final String DEFAULT_DELIVERY_TIMEOUT_MAX = "120000";
    public static final String CONNECTIONS_MAX_IDLE_TIMEOUT = "kafka.producer.connectionsIdleTimeoutMs";
    public static final Integer CONNECTIONS_DEFAULT_MAX_IDLE_TIMEOUT = 21600000;
    public static final String CONSUMER_COUNTS = "kafka.consumer.consumers";
    public static final Integer DEFAULT_CONSUMER_COUNTS = 1;
    public static final String CONSUMER_AUTO_OFFSET_RESET = "kafka.consumer.autoOffsetReset";

    private NaaSKafkaConstants() {
        throw new IllegalStateException("Utility class");
    }
}