# zeitwert Application Server

## Modularisation

1. by Functional Module

2. by Layer

- adapter
  - api (adapters for incoming/driving ports)
    - topic
  - spi (adapters for outgoing/driven ports)
    - topic
- model (domain model)
- service
  - api (service, incoming/driven port)
    - topic
  - spi (outgoing/driving port)
    - topic

example:

    meeting
      adapter
        api
          calendar
            sync
          rest
          jsonapi
          websocket
        spi
          calendar
      model
      service
        api
        spi
          calendar

## Technology Stack

### Flyway

Trigger manual migration `mvnw flyway:migrate`

### jOOQ

**Code Generation**
Generated source code of jOOQ is checked in under `src/main/java/io/zeitwert/[module]/[component]/db` (according to definition in jooq XML config files).

Sources can be generated (after database has been set up through flyway) with `mvnw generate-sources`;

Generating sources for only a single component can be done like this: `mvnw jooq-codegen:generate@jooq-codegen-crm-contact`.

**Subclassing Records (instead of manual proxying)**
UpdatableRecord cannot be subclassed, since instantiation seems to be hardcoded.

An option could be to hook into the code generator, so that our xxBase classes could be generated (would need to be investigated whether that would then still work with Crank).

**Enumeration Loading**
When loading enum domains from DB in the `@PostConstruct` method, it must be guaranteed that flyway migrations have done their work. This can be achieved by specifying a corresponding dependency: `@DependsOn({ "flyway", "flywayInitializer" })`.

### Crank (io.crnk)

Does not work with interfaces, resource definition must be a (abstract) class.
So our JsonApi Repositories work with the bottom level implementation classes (e.g. DocLeadImpl) instead of the corresponding interfaces.

### Updating dependencies

Using [versions-maven-plugin](https://www.mojohaus.org/versions-maven-plugin/) library.

`mvnw versions:display-dependency-updates`

Displays newer versions of dependencies.

## Heroku deployment

First you need to install the [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli).

Login into the container registry (only once):

`heroku login`
`heroku container:login`

Create authorization key:

`heroku authorizations:create`

Attach the current application:

`heroku git:remote -a zeitwert`

To show logs from application:

`heroku logs --tail`

Also you can run the docker image locally:

`docker-compose -f docker/docker-compose.yml up`
