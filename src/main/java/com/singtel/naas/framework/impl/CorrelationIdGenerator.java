/**
 * 
 */
package com.singtel.naas.framework.impl;

import java.util.UUID;

public class CorrelationIdGenerator {
    public String getUuid() {
        return UUID.randomUUID().toString();
    }
}
