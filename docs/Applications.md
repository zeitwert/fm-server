
# zeitwert Tenants, Users, Roles, Applications & Functionality

## Kernel Tenant

The Kernel Tenant is the container in which to centrally maintain Organisational Entities (Tenants, Users, Accounts) and Reference Data (f.ex. Baukostenindex).

### Roles

- __app_admin__ Application Admin

### Users

- k@zeitwert.io app_admin, (internal) Kernel User, owner of migrated objects
- admin@zeitwert.io app_admin, Application Administrator (for Tenants, Users, Accounts)

### Application Admin Application

- Tenant Management: Add/edit/deactivate all Tenants of the Platform
- User Management: Add/edit/deactivate Users for all Tenants
- Account Management: Add/Edit/Deactivate Accounts for all Tenants


## Community Tenant (Gemeinde mit 1 Account)

A Community Tenant holds exactly one Account which corresponds to the Community.
This Account must be set up when the Tenant is set up from the App Admin (admin@zeitwert.io).
Since we keep some reference data on the Account (f.ex. Discount Rate, Images), an Admin needs to be able to edit Tenant and Account data, as well as manage (add/edit/deactivate) Users.

### Roles

- __admin__ Tenant Administrator (Admin Application)
- __super_user__ Facility Management Super User (f.ex. Import/Export of Buildings)
- __user__ Normal User
- __readOnly__ Read-Only User (typically prospect which is allowed to look at data)

### Users

- admin@community.ch Tenant Admin (for Tenant, Users, Account)
- x.y@community.ch Tenant User

### Tenant Admin Application

- Tenant Management: edit Community Tenant
- User Management: Add/edit/deactivate Users of Community Tenant
- Account Management: Edit Account of Community Tenant

### Facility Management Application

- Facility Management
- Account Read-Only


## Advisor Tenant

An Advisor Tenant can contain multiple Accounts, which correspond to the different customers of the Advisor.
Just like with the Community Tenant, we need to support an Tenant Admin to edit Tenant data, as well as manage (add/edit/deactivate) Users and Accounts.

### Roles

- __admin__ Tenant Administrator (Admin Application)
- __super_user__ Facility Management Super User (f.ex. Import/Export of Buildings)
- __user__ Normal User
- __readOnly__ Read-Only User (typically prospect which is allowed to look at data)

### Users

- admin@community.ch Tenant Admin (for Tenant, Users, Account)
- x.y@community.ch Tenant User

### Tenant Admin Application

- Tenant Management: edit Advisor Tenant
- User Management: Add/edit/deactivate Users of Advisor Tenant
- Account Management: Add/edit/deactivate Accounts of Advisor Tenant

### Facility Management Application

- Facility Management
- Account Read-Only
