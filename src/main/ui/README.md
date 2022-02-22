# Comunas UI

The Comunas Web UI.

## Status

[![Netlify Status](https://api.netlify.com/api/v1/badges/e2da500f-9988-4151-ad39-6875da561f10/deploy-status)](https://app.netlify.com/sites/comunas-ui/deploys)

## Installation

The application is created with create-react-app.

Build tools are webpack, yarn.

-   Install node.js (currently used: node v14)
-   Install yarn

More details on the original [create-react-app readme](https://github.com/facebook/create-react-app#readme)

## Development

Comunas UI is a single page TypeScript, React, Mobx-State-Tree application and can be built with yarn.

Some commands:

-   `yarn start` starts the webpack development server

-   `yarn test` starts the test runner (based on Jest)

-   `yarn build` creates the production build in directory ./build

### Direct link to @finadvise repos for more efficient development

For a more efficient development, it is possible to link the finadvise-ui-forms repository into this package:

**Delete library node_modules**:

-   Delete existing folder in node_modules

**Symbolic link to source folder**:

-   create folder `@finadvise` in `src` folder
-   create symbolic link `forms` to src folder of finadvise-forms

**Windows** (in `src\@finadvise` folder):

-   `mklink /D forms "..\..\..\finadvise-ui-forms\src"`

### Salesforce Lightning React

In order to keep up with SLDS changes, we need to update assets.

https://github.com/salesforce/design-system-react/blob/master/docs/create-react-app-2x.md#copy-over-salesforce-lightning-static-asset

```sh
cp node_modules/@salesforce-ux/design-system/assets/styles/salesforce-lightning-design-system.min.css public/
cp node_modules/@salesforce-ux/design-system/assets/styles/salesforce-lightning-design-system.min.css plugin/outlook/assets/styles
cp -r node_modules/@salesforce-ux/design-system/assets/icons public/assets
cp -r node_modules/@salesforce-ux/design-system/assets/icons plugin/outlook/assets
cp -r node_modules/\@salesforce/design-system-react/assets/images public/assets
cp -r node_modules/\@salesforce/design-system-react/assets/images plugin/outlook/assets
```

## Deployment

The finadvise UI is deployed to [Netlify](https://app.netlify.com/sites/comunas-ui) upon pushing to Bitbucket, running at [https://comunas-ui.netlify.com](https://comunas-ui.netlify.com), and mapped from [https://www.comunas.fm](https://www.comunas.fm).

## Docker (INACCURATE)

Docker setup according to [http://mherman.org/blog/2017/12/07/dockerizing-a-react-app](http://mherman.org/blog/2017/12/07/dockerizing-a-react-app) for both a development and a production build.

### Development Build

docker container prune
docker rmi comunas/comunas-ui-dev
docker rmi comunas/comunas-ui-dev:0.0.1-SNAPSHOT

docker build -f docker/dev.Dockerfile -t comunas/comunas-ui-dev:0.0.1-SNAPSHOT .
docker tag comunas/comunas-ui-dev:0.0.1-SNAPSHOT comunas/comunas-ui-dev:latest

docker run -it -v ${PWD}:/usr/src/app -v /usr/src/app/node_modules -p 3000:3000 --rm comunas/comunas-ui-dev

### Production Build

docker container prune
docker rmi comunas/comunas-ui
docker rmi comunas/comunas-ui:0.0.1-SNAPSHOT

docker build -f docker/prod.Dockerfile -t comunas/comunas-ui:0.0.1-SNAPSHOT .
docker tag comunas/comunas-ui:0.0.1-SNAPSHOT comunas/comunas-ui:latest

docker run --name comunas_ui -it -p 3000:80 --rm comunas/comunas-ui:0.0.1-SNAPSHOT

docker-compose -f docker/prod.docker-compose.yml up -d --build
