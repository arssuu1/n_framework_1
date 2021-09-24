package com.singtel.naas.framework.exception;

import java.util.Date;
import com.singtel.naas.framework.exception.base.NaasErrorCode;
import com.singtel.naas.framework.exception.base.NaasErrorSeverity;
import com.singtel.naas.framework.exception.base.NaasErrorType;


/**
 * 
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-01
 */
public class NaasException extends Exception {
    static final long serialVersionUID = -5678309403875171791L;

    private final String serviceName;
    private final String operationName;
    private final NaasErrorCode errorCode;
    private final NaasErrorSeverity errorSeverity;
    private final NaasErrorType errorType;
    private final String correlationId;
    private final String transactionId;
    private final String errorDescription;
    private final Date errorTime;
    private final String originalRequest;
    private final String providerError;
    private final Exception originException;

    public NaasException(NaasExceptionBuilder naasExceptionBuilder) {
        super();
        this.serviceName = naasExceptionBuilder.serviceName;
        this.operationName = naasExceptionBuilder.operationName;
        this.correlationId = naasExceptionBuilder.correlationId;
        this.transactionId = naasExceptionBuilder.transactionId;
        this.errorType = naasExceptionBuilder.errorType;
        this.errorCode = naasExceptionBuilder.errorCode;
        this.providerError = naasExceptionBuilder.providerError;
        this.errorDescription = naasExceptionBuilder.errorDescription;
        this.errorSeverity = naasExceptionBuilder.errorSeverity;
        this.originalRequest = naasExceptionBuilder.originalRequest;
        this.errorTime = naasExceptionBuilder.errorTime;
        this.originException = naasExceptionBuilder.originException;
    }

    /**
     * @return the errorCode
     */
    public NaasErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * @return the correlationId
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @return the errorTime
     */
    public Date getErrorTime() {
        return errorTime;
    }

    /**
     * @return the errorDescription
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * @return the operationName
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return the errorSeverity
     */
    public NaasErrorSeverity getErrorSeverity() {
        return errorSeverity;
    }

    /**
     * @return the originalRequest
     */
    public String getOriginalRequest() {
        return originalRequest;
    }

    /**
     * @return the errorType
     */
    public NaasErrorType getErrorType() {
        return errorType;
    }

    /**
     * @return the providerError
     */
    public String getProviderError() {
        return providerError;
    }

    /**
     * @return the originException
     */
    public Exception getOriginException() {
        return originException;
    }

}
