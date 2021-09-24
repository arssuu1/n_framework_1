/**
 * 
 */
package com.singtel.naas.framework.exception.base;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;


/**
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-29
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "serviceName",
    "consumer",
    "correlationId",
    "transactionId",
    "errorCode",
    "errorDescription",
    "errorType",
    "isProviderError",
    "originRequest",
    "responseMessage"
})
public class NaasErrorDetail {

    @JsonProperty("serviceName")
    private String serviceName;

    @JsonProperty("consumer")
    private String consumer;

    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("errorType")
    private String errorType;

    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("errorDescription")
    private String errorDescription;

    @JsonProperty("isProviderError")
    private boolean isProviderError;

    @JsonProperty("originRequest")
    private String originRequest;

    @JsonProperty("responseMessage")
    private String responseMessage;

    /**
     * @return the errorCode
     */
    @JsonGetter("errorCode")
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    @JsonSetter("errorCode")
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorDescription
     */
    @JsonGetter("errorDescription")
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * @param errorDescription the errorDescription to set
     */
    @JsonSetter("errorDescription")
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    /**
     * @return the serviceName
     */
    @JsonGetter("serviceName")
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    @JsonSetter("serviceName")
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the consumer
     */
    public String getConsumer() {
        return consumer;
    }

    /**
     * @param consumer the consumer to set
     */
    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    /**
     * @return the correlationId
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * @param correlationId the correlationId to set
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the errorType
     */
    public String getErrorType() {
        return errorType;
    }

    /**
     * @param errorType the errorType to set
     */
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    /**
     * @return the isProviderError
     */
    public boolean isProviderError() {
        return isProviderError;
    }

    /**
     * @param isProviderError the isProviderError to set
     */
    public void setProviderError(boolean isProviderError) {
        this.isProviderError = isProviderError;
    }

    /**
     * @return the originRequest
     */
    @JsonGetter("originRequest")
    public String getOriginRequest() {
        return originRequest;
    }

    /**
     * @param originRequest the originRequest to set
     */
    @JsonSetter("originRequest")
    public void setOriginRequest(String originRequest) {
        this.originRequest = originRequest;
    }

    /**
     * @return the responseMessage
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * @param responseMessage the responseMessage to set
     */
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

}
