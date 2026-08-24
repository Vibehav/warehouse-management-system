# Multi-Warehouse Management System

A modular monolith WMS built as a backend engineering portfolio project. Models a 3PL operator: suppliers own inventory, we store and fulfill it for business customers (distributors, retail stores, dark stores). Not e-commerce — no cart, no payment, no checkout.

## Architecture

- **Java 21**, **Spring Boot 4.1.0**, **PostgreSQL**
- Modular monolith — package-by-module (`auth`, `warehouse`, `inventory`, `catalogue`, `supplier`, `fulfillment`, `common`), each with its own sub-packages
- IDs: `BIGINT` (identity)
- Schema: `ddl-auto: update` for this build phase
- Every entity is created through the API itself.

## Design Patterns

| Pattern | Where                                                                                                                                                                         |
|---|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **State** | `InventoryLot` lifecycle (`RECEIVED→AVAILABLE→SHIPPED`, `DAMAGED`/`EXPIRED` branches);<br/> `FulfillmentOrder` lifecycle (`CREATED→ALLOCATED→...→DELIVERED`, `CANCELLED` branch) |
| **Strategy** | `FirstAvailableStrategy`, `NearestAvailableStrategy`, both capable of splitting one lot across multiple bins                                               |
| **Adapter** | Reserved for Shipment's carrier integrations — module not yet built                                                                                                           |
| **Facade** | `InboundPutawayFacade` (receive → place → confirm); `FulfillmentOrderFacade` (create → allocate → cancel)                                                                     |
| **Builder** | Hand-written for `InventoryLot` and `FulfillmentOrder` — validates invariants, not a Lombok `@Builder`                                                                        |


## API Reference

---
**Base path:** `http://localhost:8080`

All endpoints below are prefixed with the module base path shown in each section heading. Every endpoint except the four under Auth requires a valid `Authorization: Bearer <access_token>` header; `@PreAuthorize` then checks the specific permission listed.
## Auth (RBAC)

- **Access token**: short-lived JWT (10 min), permissions embedded as claims, verified by signature + expiry only — zero DB calls per ordinary request
- **Refresh token**: long-lived (14 days) opaque random string, only its SHA-256 hash stored server-side, rotated on every use (old row revoked + `replacedBy` chain, new row issued), delivered via `HttpOnly`/`Secure`/`SameSite=Lax` cookie scoped to `/api/auth`
- **Logout**: revokes the refresh token only — the current access token remains valid until its own short natural expiry. Deliberate tradeoff: guarantees no *new* access tokens, not instant invalidation of one already issued.
- **Roles**: `ADMIN`, `WAREHOUSE_MANAGER`, `WAREHOUSE_STAFF`, `SUPPLIER`, `BUSINESS_CUSTOMER` — one role per user, one optional `warehouse` scope (staff) or `supplier` scope (supplier reps), matching real org structure for this domain
- **Bootstrap**: the first-ever registered user is auto-assigned `ADMIN`; self-registration closes after that — every subsequent user is created by an admin via `POST /api/users`
- **Soft delete**: deactivation also revokes all of that user's outstanding refresh tokens, closing the "deactivated but still has a live session" gap
- Data-driven: `Permission`/`Role`/`role_permission` are tables, not hardcoded enums — new permissions are inserts, not deploys

### Auth — `/api/auth`

| Method | Path | Permission | Purpose                                                                           |
|---|---|---|-----------------------------------------------------------------------------------|
| POST | `/register` | public | First-ever call creates the sole `ADMIN`; every call after that returns 403       |
| POST | `/login` | public | Verify credentials, issue access token (body) + refresh token (`HttpOnly` cookie) |
| POST | `/refresh` | public (cookie-authenticated) | Rotate refresh token, issue a new access token                                    |
| POST | `/logout` | public (cookie-authenticated) | Revoke the refresh token, clear the cookie                                        |

### Users — `/api/users`

| Method | Path | Permission | Purpose                                                                       |
|---|---|---|-------------------------------------------------------------------------------|
| POST | `/` | `USER_MANAGE` | Admin creates a login for warehouse staff, a supplier, or a business customer |
| DELETE | `/{id}` | `USER_MANAGE` | Soft-delete a user and revoke all their outstanding refresh tokens            |
| PATCH | `/{id}/restore` | `USER_MANAGE` | Reactivate a user; they must log in again to create a new session              |

### Suppliers — `/api/suppliers`

| Method | Path | Permission | Purpose |
|---|---|---|---|
| POST | `/` | `SUPPLIER_ONBOARD` | Register a new supplier/brand |
| GET | `/` | `SUPPLIER_VIEW` | List suppliers |
| GET | `/{id}` | `SUPPLIER_VIEW` | Fetch one supplier |
| DELETE | `/{id}` | `SUPPLIER_ONBOARD` | Soft-delete a supplier |
| PATCH | `/{id}/restore` | `SUPPLIER_ONBOARD` | Reinstate a soft-deleted supplier |

### Warehouses — `/api/warehouses`

| Method | Path | Permission | Purpose |
|---|---|---|---|
| POST | `/` | `WAREHOUSE_CONFIG_MANAGE` | Create a warehouse |
| GET | `/` | `WAREHOUSE_VIEW` | List warehouses |
| GET | `/{id}` | `WAREHOUSE_VIEW`  | Fetch one warehouse |
| PUT | `/{id}` | `WAREHOUSE_CONFIG_MANAGE` | Update warehouse details |
| PATCH | `/{id}/deactivate` | `WAREHOUSE_CONFIG_MANAGE` | Deactivate a warehouse |
| PATCH | `/{id}/activate` | `WAREHOUSE_CONFIG_MANAGE` | Reactivate a warehouse |

### Locations — `/api/warehouses/{warehouseId}/locations`

| Method | Path | Permission | Purpose |
|---|---|---|---|
| POST | `/` | `LOCATION_MANAGE` | Create a bin in a warehouse |
| GET | `/` | `LOCATION_VIEW` | List locations in a warehouse |
| GET | `/{locationId}` | `LOCATION_VIEW` | Fetch one location |
| PATCH | `/{locationId}/activate` | `LOCATION_MANAGE` | Reactivate a bin |
| PATCH | `/{locationId}/deactivate` | `LOCATION_MANAGE` | Deactivate a bin |
| PATCH | `/{locationId}/block` | `LOCATION_MANAGE` | Block a bin from new placements (e.g. maintenance) |
| PATCH | `/{locationId}/unblock` | `LOCATION_MANAGE` | Unblock a bin |

### Product SKUs — `/api/product-skus`

| Method | Path | Permission | Purpose |
|---|---|---|---|
| POST | `/` | `CATALOGUE_MANAGE` | Create a SKU |
| GET | `/{id}` | `CATALOGUE_VIEW` | Fetch one SKU |
| GET | `/` | `CATALOGUE_VIEW` | List SKUs |
| PUT | `/{id}` | `CATALOGUE_MANAGE` | Update SKU details |
| DELETE | `/{id}` | `CATALOGUE_MANAGE` | Soft-delete a SKU |
| PATCH | `/{id}/restore` | `CATALOGUE_MANAGE` | Reinstate a soft-deleted SKU |

### Placement Rules — `/api/placement-rules`

| Method | Path | Permission | Purpose |
|---|---|---|---|
| POST | `/` | `WAREHOUSE_CONFIG_MANAGE` | Create a rule (`CATEGORY_MATCH` or `DEFAULT`) mapping to a placement strategy |
| GET | `/` | `WAREHOUSE_CONFIG_MANAGE` | List all rules |
| GET | `/{id}` | `WAREHOUSE_CONFIG_MANAGE` | Fetch one rule |
| PATCH | `/{id}/activate` | `WAREHOUSE_CONFIG_MANAGE` | Reactivate a rule |
| PATCH | `/{id}/deactivate` | `WAREHOUSE_CONFIG_MANAGE` | Deactivate a rule |

### Inbound — `/api/inbound`

| Method | Path | Permission | Purpose |
|---|---|---|---|
| POST | `/receive` | `INBOUND_RECEIVE` | Create a lot, run placement, return bin assignment(s) for the scanner |
| POST | `/confirm` | `INBOUND_RECEIVE` | Operator confirms a scanned lot+bin; lot flips to `AVAILABLE` once every assignment is confirmed |

### Reservations — `/api/inventory/reservations`

| Method | Path | Permission | Purpose |
|---|---|---|---|
| POST | `/` | `FULFILLMENT_RESERVE_ALLOCATE` | Reserve stock for a SKU across balances (FEFO/FIFO), independent of any order |
| POST | `/release` | `FULFILLMENT_RESERVE_ALLOCATE` | Give back previously reserved quantity on one balance |

### Fulfillment Orders — `/api/fulfillment-orders`

| Method | Path | Permission | Purpose |
|---|---|---|---|
| POST | `/` | `FULFILLMENT_ORDER_CREATE` | Business customer creates a multi-line order (no stock touched yet) |
| POST | `/{id}/allocate` | `FULFILLMENT_RESERVE_ALLOCATE` | Reserve stock for every line, all-or-nothing; transitions `CREATED → ALLOCATED` |
| POST | `/{id}/cancel` | `FULFILLMENT_RESERVE_ALLOCATE` | Release all reservations on the order; transitions to `CANCELLED` |
| GET | `/mine` | `FULFILLMENT_ORDER_VIEW_OWN` | Business customer views their own orders only |


## Placement Flow

1. Receive → create `InventoryLot`
2. `CandidateLocationService` filters eligible bins (zone match, active, not blocked, has capacity)
3. `PlacementRuleResolver` picks a strategy by evaluating `PlacementRule` rows in priority order, with a genuine `DEFAULT` catch-all
4. Strategy allocates — splits across multiple bins if one bin can't hold the full quantity
5. `InventoryBalance` row(s) created, one per allocation
6. Scanner displays the assignment(s) — never decides them
7. Operator scans lot + bin barcodes; backend validates the scanned location actually matches an assignment before confirming (prevents a client from confirming a balance it never scanned)
8. Once every balance for a lot is confirmed, the lot transitions `RECEIVED → AVAILABLE`

## Fulfillment Flow

1. `FulfillmentOrderFacade.createOrder()` — order + lines only, no stock touched
2. `FulfillmentOrderFacade.allocateOrder()` — for every line, `FulfillmentReservationService` reserves across candidate balances (FEFO for perishables / FIFO otherwise, per `ProductSku.rotationPolicy`), records each draw as a `FulfillmentOrderLineReservation`, then transitions `CREATED → ALLOCATED`. All-or-nothing — any line failing rolls back the whole transaction.
3. `FulfillmentOrderFacade.cancelOrder()` — releases every recorded reservation, then transitions to `CANCELLED`

`DELIVERED` exist as enum values and resolvable (terminal, no-op) states, but have no real transition logic yet — they belong to the Shipment module, which isn't built.

## Project Structure

Package-by-module, not package-by-layer every module owns its full vertical slice (`domain`, `repository`, `service`, `controller`, `dto`, `exception`, and `enums` where relevant).

```
com.project.wms
├── WmsApplication.java
│
├── auth/                     Authentication, RBAC, users
│   ├── config/                JwtProperties, SecurityConfig, PasswordEncoderConfig
│   ├── controller/             AuthController, UserController
│   ├── dto/
│   ├── entity/                 User, Role, Permission, RefreshToken
│   ├── exception/
│   ├── repository/
│   ├── security/               JwtTokenProvider, JwtAuthenticationFilter, TokenHasher
│   └── service/                 AuthService, RefreshTokenService, UserService
│
├── warehouse/                Warehouses, locations, placement
│   ├── controller/             WarehouseController, LocationController, PlacementRuleController
│   ├── domain/                  Warehouse, Location, PlacementRule, LocationAllocation
│   ├── dto/                     locationDTO/, placementDTO/, warehouseDTO/
│   ├── exception/
│   ├── repository/
│   └── service/
│       └── strategy/            PlacementStrategy, FirstAvailableStrategy, NearestAvailableStrategy
│
├── inventory/                 Lots, balances, reservation, putaway
│   ├── controller/              InboundController, ReservationController, InventoryViewController
│   ├── domain/
│   │   └── state/                InventoryLotState + 5 concrete states + resolver
│   ├── dto/                      fulfillmentDTO/ (reserve/release requests)
│   ├── enums/                    LotState
│   ├── exception/
│   ├── expiryscheduler/          ExpirySweepScheduler
│   ├── facade/                   InboundPutawayFacade
│   ├── repository/
│   └── service/                   InventoryBalanceService
│
├── fulfillment/               Customer orders
│   ├── controller/              FulfillmentOrderController
│   ├── domain/
│   │   └── state/                 FulfillmentOrderState + concrete states + resolver
│   ├── dto/
│   ├── enums/                    OrderStatus
│   ├── exception/
│   ├── facade/                    FulfillmentOrderFacade
│   ├── repository/
│   └── service/                    FulfillmentReservationService
│
├── catalogue/                 Product SKUs
│   ├── controller/  domain/  dto/  enums/  exception/  repository/  service/
│
├── supplier/                  Supplier master data
│   ├── controller/  domain/  dto/  exception/  repository/  service/
│
└── common/                    Shared, cross-module concerns only
    ├── enums/                  ConditionType, ProductCategory, StorageZoneType
    └── exception/              ErrorResponse, GlobalExceptionHandler
```


## Concurrency

Optimistic locking (`@Version`) on `InventoryBalance` — the row actually contended during reservation. No automatic retry-on-conflict: at this project's real scale, a genuine write race is not a practical risk, so a conflict surfaces as a normal `409` rather than being silently retried. This tradeoff is deliberate, not an oversight.

## Scheduled Jobs

Spring `@Scheduled` . Nightly expiry sweep transitions `AVAILABLE` lots past `expiryDate` to `EXPIRED` **through the State pattern**, not a raw update.
