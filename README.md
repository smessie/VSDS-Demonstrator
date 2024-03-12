# VSDS DEMONSTRATOR

The VSDS Demonstrator is a web application which can be used to demonstrate the ingestion/replication of Linked Data Event Streams.
The VSDS Demonstrator was built in the context of
the [VSDS project](https://vlaamseoverheid.atlassian.net/wiki/spaces/VSDSSTART/overview) in order to easily exchange
open data.

## Structure of the VSDS Demonstrator
In the middle of the diagram, you can see both _backend_ as _VUE.js_, which will be referred to as the frontend. 
Those two components are the components of the full stack application. The frontend makes requests to the backend, which communicate with both the PostgreSQL db as the Eclipse RDF4J graph db. 

At the left of the diagram, you can see a LDIO component, which is a LDIO-Orchestrator and will be further referred to as the data provider. The data provider will follow one or more Linked Data Event Streams, and will eventually not use only use the LdioHttpOut to POST the member to the backend, but also the LdioRepositoryMaterialiser to add the member to the graph db. 
![img.png](documentation/img.png)

## How To Run
We'll show you how to run the VSDS Demonstrator, both locally via Maven and Docker.

### Locally - Maven
To let the VSDS Demonstrator run successfully, there are some requirements.
1. In the [resources](backend/src/main/resources) folder in the backend, a `application.yml` file should be provided, which can look like the [following example](backend/examples/example-application.yaml)
2. An up and running instance of a PostgreSQL is needed, and it's connection details should be provided in the `application.yml` file.
3. An up and running instance of a [RDF4J Server and Workbench is needed](https://rdf4j.org/documentation/tools/server-workbench), it's connection details should also be provided in the `application.yaml` file.

If the following requirements are met, the VSDS Demonstrator can be started with following commando's:
```shell
mvn clean install
```
```shell
cd ./backend
```
```shell
cd mvn spring-boot:run
```

### Docker
There are 3 files where you can configure the dockerized application:

- [The demonstrator config files](#the-demonstrator-config-files)
- [The data provider config files](#the-data-provider-config-files)
- [The docker compose file](#the-docker-compose-file)

#### The Demonstrator Config Files

Runtime settings can be defined via `.env` files. Alternatively, an `application.yml` file can be mounted into the container.

The [`demonstrator.env`](./docker-compose/demonstrator.env) is an `.env` example file.

#### The Data Provider Config Files
The data provider is an instance of a LDIO-orchestrator, which also can be configured via an `.env` file or a mounted `application.yml` file. 
For this container, we chose to go with the second scenario. More information about how to configure the LDIO-Orchestrator 
can be found in the [LDIO Documentation](https://informatievlaanderen.github.io/VSDS-Linked-Data-Interactions/).

The [`data-provider.config.yml`](./docker-compose/data-provider.config.yml) is an `application.yml` example file.

#### The docker compose File
Modify the [`docker-compose.yml`](./docker-compose.yml) according to your needs. To start the containers, run the following commands.

Run the containers in the services
```shell
docker compose up
```

## Changes undertaken for the RINF LDES

- [one-time] Correctly wait upon depending services being started ([f60b775](https://github.com/smessie/VSDS-Demonstrator/commit/f60b775072ab9af1787807a098f5d01ea1a832e0)), map to correct port of process container ([9120f7b](https://github.com/smessie/VSDS-Demonstrator/commit/9120f7bba8fe0ea390b27f85041e276fb64d16a7)), update the `data-provider.config.yml` config to use the latest syntax ([da6a08b](https://github.com/smessie/VSDS-Demonstrator/commit/da6a08b2e47172b21aa50b8d040c9fe28e861db2)), make docker hostname work on Linux host systems ([dba50ea](https://github.com/smessie/VSDS-Demonstrator/commit/dba50ea3e6e3ffc876191179a9e21a58e56d1712))
- Browse the RINF LDES manually to get an idea of its members' shape (the predicates present)
- Configure the LDES in the `demonstrator.env`. Make sure to at least configure the `MEMBERTYPE`, `TIMESTAMPPATH`, `VERSIONPATH`, and `GEOLOCATIONPATH`. Additional properties can be configured with `PROPERTYPREDICATES_{PROPERTY}`. All values have to be literals. ([docker-compose/demonstrator.env](https://github.com/smessie/VSDS-Demonstrator/commit/3d56795db4daba1e449be10ac888351ff64bc261#diff-e0f0ff531333cebd439640ceb1b54642b83df91d09ce4ad3511f3a00141d4310))
- Register the LDES as stream in the frontend in the `streams.json`. ([frontend/streams.json](https://github.com/smessie/VSDS-Demonstrator/commit/3d56795db4daba1e449be10ac888351ff64bc261#diff-77ca91f85a6921942331ff392de711bb7877ed0304c739bbf1e536be54b7618e))
- Configure a pipeline for the LDES in the `data-provider.config.yml`. At least an input and output step are needed. More information about the configuration of the different steps can be found in [the documentation](https://informatievlaanderen.github.io/VSDS-Linked-Data-Interactions/ldio/ldio-inputs/ldio-ldes-client). ([docker-compose/data-provider.config.yml](https://github.com/smessie/VSDS-Demonstrator/commit/3d56795db4daba1e449be10ac888351ff64bc261#diff-519cd7ed3a9e57e666f94dcf911f52b25d632bd7fd75a6f688416786605e2515))
    - the type (`name`) of the input will most likely be `Ldio:LdesClient` if configuring an LDES.
    - the output is a `Ldio:RepositoryMaterialiser` with as `named-graph` the value used as `id` in the `streams.json` as configured in the previous step.
    - Additionally, the second output is a `Ldio:HttpOut` with as endpoint `http://host.docker.internal:8084/api/{id}/members`. This hostname points to the demonstrator service, the `{id}` is again the `id` value as used in the `streams.json` from the last step.
