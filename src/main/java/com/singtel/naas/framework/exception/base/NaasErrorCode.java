/**
 * 
 */
package com.singtel.naas.framework.exception.base;

/**
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-29
 */
public enum NaasErrorCode {

    INVALID_REQUEST_PAYLOAD,
    INVALID_REQUEST_SCHEMA,
    INVALID_OPERATION,
    INVALID_REQUEST_HEADER,

    EMPTY_SEQUENCE_ID,
    AUTHENTICATION_FAILED,
    PROVIDER_UNAVAILABLE,
    GENERATE_PROVIDER_ERROR,

    DUPLICATE_SEQUENCE_REQUEST,
    UNKNOWN_EXCEPTION,;
}
