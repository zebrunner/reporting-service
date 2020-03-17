package com.zebrunner.reporting.persistence.utils;

import com.zebrunner.reporting.domain.db.Tenancy;
import org.slf4j.MDC;

/**
 * TenancyContext - stores client tenant ID.
 * 
 * @author akhursevich
 */
public class TenancyContext {

    private static final ThreadLocal<String> tenant = new InheritableThreadLocal<>();

    public static void setTenantName(String tenantName) {
        tenantName = tenantName != null ? tenantName.toLowerCase() : null;
        tenant.set(tenantName);
        MDC.put("tenant", tenantName);
    }

    public static String getTenantName() {
        String tenantName = tenant.get();
        return tenantName != null ? tenantName : Tenancy.getDefaultTenant();
    }
}
