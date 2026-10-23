package com.computrade.course.spring.ai.claude.model;

public enum Tenant {

    COMPUTRADE_PREMIUM("computrade_premium"),
    COMPUTRADE_STANDARD("computrade_standard"),
    GUEST_USER("guest_user");

    private final String id;

    Tenant(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /**
     * Safely converts a raw context string into a typed Tenant Enum instance.
     */
    public static Tenant fromId(String tenantIdString) {
        for (Tenant tenant : Tenant.values()) {
            if (tenant.id.equalsIgnoreCase(tenantIdString)) {
                return tenant;
            }
        }
        return null;
    }
}
