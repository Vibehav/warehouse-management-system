package com.project.wms.auth.util;

import java.util.Set;

public final class PermissionCatalog {

    private PermissionCatalog() {
    }

    public static final Set<String> ADMIN_PERMISSIONS = Set.of(
            "USER_MANAGE",
            "SUPPLIER_ONBOARD",
            "SUPPLIER_VIEW",
            "WAREHOUSE_CONFIG_MANAGE",
            "WAREHOUSE_VIEW",
            "LOCATION_MANAGE",
            "LOCATION_VIEW",
            "CATALOGUE_MANAGE",
            "CATALOGUE_VIEW",
            "INBOUND_RECEIVE",
            "INVENTORY_VIEW",
            "INVENTORY_VIEW_OWN",
            "FULFILLMENT_ORDER_CREATE",
            "FULFILLMENT_ORDER_VIEW_OWN",
            "FULFILLMENT_RESERVE_ALLOCATE"
    );
}
