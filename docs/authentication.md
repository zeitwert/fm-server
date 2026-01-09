# Authentication Flow

This document describes the authentication mechanism for the FM application, including the decoupled login sequence that separates credential validation from tenant/account selection.

## Overview

The authentication flow is designed as a multi-step process:

1. **Authentication** - Validate user credentials (email/password)
2. **Tenant Selection** - Choose which tenant to work with (if user has access to multiple)
3. **Account Selection** - Choose which account to access (if tenant has multiple accounts)
4. **Session Activation** - Finalize the session with selected tenant/account

This decoupled approach allows the UI to present a wizard-style login experience while maintaining secure session management on the backend.

## UI Flow Diagram

```mermaid
flowchart TD
    Start[App Load] --> CheckAuth{Authenticated?}
    CheckAuth -->|No| LoginPage[Login Page]
    CheckAuth -->|Yes| CheckAccount{Account Selected?}
    
    CheckAccount -->|Yes| App[Main Application]
    CheckAccount -->|No| CheckTenant{Tenant Selected?}
    
    CheckTenant -->|No| TenantSelect[Tenant Selection]
    CheckTenant -->|Yes| CheckMultiAccount{Multiple Accounts?}
    
    CheckMultiAccount -->|Yes| AccountSelect[Account Selection]
    CheckMultiAccount -->|No| FinalLogin[Complete Login]
    
    LoginPage -->|Submit| Authenticate[POST /rest/session/authenticate]
    Authenticate -->|Success| CheckSingleTenant{Single Tenant?}
    CheckSingleTenant -->|Yes| AutoSelectTenant[Auto-select Tenant]
    CheckSingleTenant -->|No| TenantSelect
    AutoSelectTenant --> FetchTenant
    
    TenantSelect -->|Select| FetchTenant[GET /rest/app/tenantInfo]
    FetchTenant --> CheckMultiAccount
    
    AccountSelect -->|Select| FinalLogin
    FinalLogin --> ActivateSession[POST /rest/session/activate]
    ActivateSession --> App
```

## API Sequence Diagram

```mermaid
sequenceDiagram
    participant UI
    participant AuthEndpoint as /authenticate
    participant TenantEndpoint as /tenantInfo
    participant ActivateEndpoint as /activate
    participant SessionEndpoint as /session

    UI->>AuthEndpoint: POST {email, password}
    AuthEndpoint-->>UI: {id, name, email, role, tenants[]}
    
    Note over UI: User selects tenant<br/>(or auto-selected if single)
    
    UI->>TenantEndpoint: GET /tenantInfo/{tenantId}
    TenantEndpoint-->>UI: {id, tenantType, accounts[]}
    
    Note over UI: User selects account<br/>(or auto-selected if single/none)
    
    UI->>ActivateEndpoint: POST {tenantId, accountId?}
    ActivateEndpoint-->>UI: {sessionInfo}
    
    Note over UI: Session fully active
    
    UI->>SessionEndpoint: GET (for refresh/restore)
    SessionEndpoint-->>UI: {sessionInfo}
```

## API Endpoints

### POST /rest/session/authenticate

Validates user credentials and returns user information with available tenants.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "secret"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "user@example.com",
  "role": {
    "id": "ROLE_USER",
    "name": "User"
  },
  "tenants": [
    {
      "id": "1",
      "name": "Tenant A",
      "itemType": { "id": "ObjTenant", "name": "" }
    },
    {
      "id": "2", 
      "name": "Tenant B",
      "itemType": { "id": "ObjTenant", "name": "" }
    }
  ]
}
```

**Security:** This endpoint is publicly accessible (no authentication required).

**Side Effects:** 
- Creates an HTTP session
- Stores authentication in Spring Security context (without tenant/account)

---

### GET /rest/app/tenantInfo/{tenantId}

Returns tenant details including available accounts.

**Response (200 OK):**
```json
{
  "id": 1,
  "tenantType": {
    "id": "advisor",
    "name": "Advisor"
  },
  "accounts": [
    { "id": "1", "name": "Account A" },
    { "id": "2", "name": "Account B" }
  ]
}
```

**Security:** This endpoint is publicly accessible.

---

### POST /rest/session/activate

Activates the session with the selected tenant and optional account.

**Request:**
```json
{
  "tenantId": 1,
  "accountId": 2
}
```

**Response (200 OK):**
```json
{
  "applicationName": "FM Application",
  "applicationVersion": "1.0.0",
  "user": {
    "id": "1",
    "caption": "John Doe",
    "name": "John Doe",
    "email": "user@example.com",
    "role": { "id": "ROLE_USER", "name": "User" },
    "tenant": { "id": "1", "name": "Tenant A" }
  },
  "tenant": {
    "id": "1",
    "caption": "Tenant A",
    "name": "Tenant A",
    "tenantType": { "id": "advisor", "name": "Advisor" }
  },
  "account": {
    "id": "2",
    "caption": "Account B",
    "name": "Account B",
    "accountType": { "id": "standard", "name": "Standard" }
  },
  "locale": "de_CH",
  "applicationId": "fm",
  "availableApplications": ["fm"]
}
```

**Security:** Requires prior authentication via `/authenticate`.

**Side Effects:**
- Updates the Spring Security context with tenant/account
- Completes the session setup

---

### POST /rest/session/logout

Terminates the current session.

**Response (200 OK):** Empty body

**Side Effects:**
- Invalidates the HTTP session
- Clears the JSESSIONID cookie
- Clears the Spring Security context

---

### GET /rest/session/session

Returns current session information. Can be used to restore session state on page refresh.

**Response (200 OK):** Same as `/activate` response.

**Security:** Requires fully activated session (with tenant selected).

## Frontend State Machine

The frontend uses a Zustand store to manage session state through the following states:

```mermaid
stateDiagram-v2
    [*] --> close
    
    close --> pendingAuth: login()
    pendingAuth --> authenticated: authenticate success
    pendingAuth --> close: authenticate failure
    
    authenticated --> pendingOpen: completeLogin()
    authenticated --> close: logout()
    
    pendingOpen --> open: activate success
    pendingOpen --> authenticated: activate failure
    
    open --> close: logout()
```

### State Descriptions

| State | Description |
|-------|-------------|
| `close` | No active session, user not logged in |
| `pendingAuth` | Authentication request in progress |
| `authenticated` | Credentials validated, awaiting tenant/account selection |
| `pendingOpen` | Session activation in progress |
| `open` | Session fully active, user can access the application |

## Auto-Selection Logic

To streamline the user experience, the frontend automatically skips selection steps when possible:

1. **Single Tenant:** If the user has access to only one tenant, it is automatically selected
2. **No Accounts / Single Account:** If the tenant has zero or one account, selection is skipped and the session is activated immediately

```mermaid
flowchart TD
    AuthSuccess[Authentication Success] --> TenantCount{How many tenants?}
    
    TenantCount -->|0| Error[Error: No tenants]
    TenantCount -->|1| AutoTenant[Auto-select tenant]
    TenantCount -->|>1| ShowTenant[Show Tenant Selection]
    
    ShowTenant -->|User selects| FetchAccounts
    AutoTenant --> FetchAccounts[Fetch tenant accounts]
    
    FetchAccounts --> AccountCount{How many accounts?}
    
    AccountCount -->|0 or 1| AutoAccount[Auto-select or skip]
    AccountCount -->|>1| ShowAccount[Show Account Selection]
    
    ShowAccount -->|User selects| Activate
    AutoAccount --> Activate[Activate Session]
    
    Activate --> SessionOpen[Session Open]
```

## Session Persistence

The frontend persists session state in `sessionStorage`:

- `fm-ux-session-state` - Current state (`close`, `authenticated`, `open`)
- `fm-ux-session-info` - Full session info JSON (when state is `open`)

On page load, the `initSession()` function checks for stored state and restores the session if valid.

## Security Considerations

1. **Session Cookies:** The backend uses HTTP-only session cookies (`JSESSIONID`) for session management
2. **CORS:** The frontend sends requests with `withCredentials: true` to include cookies
3. **Pre-Session State:** After `/authenticate`, the user is authenticated but cannot access protected resources until `/activate` is called with a valid tenant
4. **401 Handling:** The frontend automatically redirects to login on 401 responses

## Legacy Endpoint

The original `/rest/session/login` endpoint is still available for backwards compatibility. It requires `tenantId` to be provided upfront and combines authentication + activation in a single call.

```json
{
  "email": "user@example.com",
  "password": "secret",
  "tenantId": 1,
  "accountId": 2
}
```

This endpoint is deprecated in favor of the two-phase `/authenticate` + `/activate` flow.
