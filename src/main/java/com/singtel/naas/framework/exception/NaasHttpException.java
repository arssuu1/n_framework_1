/**
 * 
 */
package com.singtel.naas.framework.exception;

/**
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-31
 */
public class NaasHttpException extends NaasException {

    static final long serialVersionUID = -5061553110328432589L;

    private final int responseCode;
    private final String responseMessage;

    /**
     * @return the responseCode
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * @return the responseMessage
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * @param naasExceptionBuilder
     */
    public NaasHttpException(NaasExceptionBuilder naasExceptionBuilder) {
        super(naasExceptionBuilder);
        this.responseCode = naasExceptionBuilder.responseCode;
        this.responseMessage = naasExceptionBuilder.responseMessage;
    }

}
