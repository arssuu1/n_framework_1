/**
 * 
 */
package com.singtel.naas.framework.exception;

import java.util.Date;
import org.apache.camel.Exchange;
import com.singtel.naas.framework.constant.IConstants;
import com.singtel.naas.framework.exception.base.NaasErrorCode;
import com.singtel.naas.framework.exception.base.NaasErrorSeverity;
import com.singtel.naas.framework.exception.base.NaasErrorType;


/**
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-31
 */
public class NaasExceptionBuilder {
    protected String serviceName;
    protected String operationName;
    protected NaasErrorCode errorCode;
    protected NaasErrorSeverity errorSeverity = NaasErrorSeverity.ERROR;
    protected NaasErrorType errorType;
    protected String correlationId;
    protected String transactionId;
    protected String providerError;
    protected String errorDescription;
    protected Date errorTime;
    protected String originalRequest;
    protected Exception originException;
    protected int responseCode = 0;
    protected String responseMessage;

    public NaasExceptionBuilder(String serviceName, NaasErrorType errorType, String correlationId,
        String transactionId) {
        this.correlationId = correlationId;
        this.transactionId = transactionId;
        this.serviceName = serviceName;
        this.errorType = errorType;
        this.errorSeverity = NaasErrorSeverity.ERROR;
        this.errorTime = new Date();
    }

    public NaasExceptionBuilder(Exchange exchange, NaasErrorType errorType) {
        this.correlationId = exchange.getIn().getHeader(IConstants.INTERNAL_CORRELATION_ID, String.class);
        this.transactionId = exchange.getIn().getHeader(IConstants.EXTERNAL_TRANSACTION_ID, String.class);
        this.serviceName = exchange.getContext().getGlobalOption(IConstants.SERVICE_NAME);
        this.originException = exchange.getException();
        this.errorType = errorType;
        this.errorTime = new Date();
    }

    public NaasExceptionBuilder withSeverity(NaasErrorSeverity severity) {
        this.errorSeverity = severity;
        return this;
    }

    public NaasExceptionBuilder withOriginRequest(String request) {
        this.originalRequest = request;
        return this;
    }

    public NaasExceptionBuilder withOperationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    public NaasExceptionBuilder causeByProviderError(String providerError) {
        this.providerError = providerError;
        return this;
    }

    public NaasExceptionBuilder causeByNaasDefinedError(NaasErrorCode errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        return this;
    }

    public NaasExceptionBuilder causeByProviderError(String providerError, String errorDescription) {
        this.providerError = providerError;
        this.errorDescription = errorDescription;
        return this;
    }

    public NaasExceptionBuilder withHttpResponse(int responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        return this;
    }

    public NaasException buildException() {
        return new NaasException(this);
    }

    public NaasHttpException buildHttpException() {
        if (responseCode == 0 || responseMessage == null) {
            throw new IllegalArgumentException("response code, message was not define");
        }

        return new NaasHttpException(this);
    }
}
