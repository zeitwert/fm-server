# Zeitwert UI

The Zeitwert Web UI.

## Status

[![Netlify Status](https://api.netlify.com/api/v1/badges/e2da500f-9988-4151-ad39-6875da561f10/deploy-status)](https://app.netlify.com/sites/zeitwert-ui/deploys)

## Installation

The application is created with create-react-app.

Build tools are webpack, yarn.

-   Install node.js (currently used: node v14)
-   Install yarn

More details on the original [create-react-app readme](https://github.com/facebook/create-react-app#readme)

## Development

Zeitwert UI is a single page TypeScript, React, Mobx-State-Tree application and can be built with yarn.

Some commands:

-   `yarn start` starts the webpack development server

-   `yarn test` starts the test runner (based on Jest)

-   `yarn build` creates the production build in directory ./build

### Direct link to @zeitwert repos for more efficient development

For a more efficient development, it is possible to link the zeitwert-ui-forms repository into this package:

**Delete library node_modules**:

-   Delete existing folder in node_modules

**Symbolic link to source folder**:

-   create folder `@zeitwert` in `src` folder
-   create symbolic link `forms` to src folder of zeitwert-forms

**Windows** (in `src\@zeitwert` folder):

-   `mklink /D forms "..\..\..\zeitwert-ui-forms\src"`

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

The zeitwert UI is deployed embedded in Spring Boot server, mapped from [https://www.zeitwert.fm](https://www.zeitwert.fm).
