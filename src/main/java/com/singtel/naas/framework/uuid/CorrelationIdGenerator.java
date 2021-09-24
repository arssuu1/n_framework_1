/**
 * 
 */
package com.singtel.naas.framework.uuid;

import java.util.UUID;


/**
 * @author Ooi Chio Chuan (chiochuan.ooi@singtel.com)
 * 
 * created on 2019-10-29
 */
@Deprecated
public class CorrelationIdGenerator {
    public String getUuid() {
        return UUID.randomUUID().toString();
    }
}
