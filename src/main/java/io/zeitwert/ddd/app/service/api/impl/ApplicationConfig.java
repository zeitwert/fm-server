
package io.zeitwert.ddd.app.service.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.zeitwert.ddd.app.model.AppMenu;
import io.zeitwert.ddd.app.model.AppMenuAction;
import io.zeitwert.ddd.app.model.Application;
import io.zeitwert.ddd.app.model.ApplicationArea;
import io.zeitwert.ddd.app.model.ApplicationInfo;
import io.zeitwert.ddd.app.model.Navigation;
import io.zeitwert.ddd.app.model.NavigationAction;
import io.zeitwert.ddd.app.model.NavigationTarget;

class ApplicationConfig {

	//@formatter:off
	private final NavigationTarget SelfTarget = NavigationTarget.builder().applicationId("self").applicationAreaId("self").build();
	private final String RouteActionType = "route";
	private final NavigationAction DefaultAction = NavigationAction.builder().actionType(RouteActionType).build();
	private final Navigation DefaultNavigation = Navigation.builder().target(SelfTarget).action(DefaultAction).build();
	private final AppMenu EmptyMenu = AppMenu.builder().build();

	private final AppMenuAction accountAction = AppMenuAction.builder().id("account").name("Kunden").navigation(DefaultNavigation).icon("").build();
	private final ApplicationArea accountArea = ApplicationArea.builder().id("account").name("Kunden").icon("standard:account").path("account").component("AccountArea").menu(EmptyMenu).menuAction(accountAction).build();

	private final AppMenuAction buildingAction = AppMenuAction.builder().id("building").name("Immobilien").navigation(DefaultNavigation).icon("").build();
	private final ApplicationArea buildingArea = ApplicationArea.builder().id("building").name("Immobilien").icon("custom:custom24").path("building").component("BuildingArea").menu(EmptyMenu).menuAction(buildingAction).build();

	private final AppMenuAction buildingReportAction = AppMenuAction.builder().id("bldgReport").name("Auswertungen (TEST)").navigation(DefaultNavigation).icon("").build();
	private final ApplicationArea buildingReportArea = ApplicationArea.builder().id("bldgReport").name("Auswertungen (TEST)").icon("standard:agent_home").path("bldgReport").component("BuildingReportArea").menu(EmptyMenu).menuAction(buildingReportAction).build();

	private final AppMenuAction contactAction = AppMenuAction.builder().id("contact").name("Kontakte").navigation(DefaultNavigation).icon("").build();
	private final ApplicationArea contactArea = ApplicationArea.builder().id("contact").name("Kontakte").icon("standard:contact").path("contact").component("ContactArea").menu(EmptyMenu).menuAction(contactAction).build();

	private final AppMenuAction documentAction = AppMenuAction.builder().id("document").name("Dokumente").navigation(DefaultNavigation).icon("").build();
	private final ApplicationArea documentArea = ApplicationArea.builder().id("document").name("Dokumente").icon("standard:document").path("document").component("DocumentArea").menu(EmptyMenu).menuAction(documentAction).build();

	private final AppMenuAction homeAction = AppMenuAction.builder().id("home").name("Dashboard").navigation(DefaultNavigation).icon("").build();
	private final ApplicationArea homeArea = ApplicationArea.builder().id("home").name("Dashboard").icon("standard:home").path("home").component("HomeArea").menu(EmptyMenu).menuAction(homeAction).build();

	private final AppMenuAction leadAction = AppMenuAction.builder().id("lead").name("Leads").navigation(DefaultNavigation).icon("").build();
	private final ApplicationArea leadArea = ApplicationArea.builder().id("lead").name("Leads").icon("standard:lead").path("lead").component("LeadArea").menu(EmptyMenu).menuAction(leadAction).build();

	private final AppMenuAction portfolioAction = AppMenuAction.builder().id("portfolio").name("Portfolios").navigation(DefaultNavigation).icon("").build();
	private final ApplicationArea portfolioArea = ApplicationArea.builder().id("portfolio").name("Portfolios").icon("standard:store_group").path("portfolio").component("PortfolioArea").menu(EmptyMenu).menuAction(portfolioAction).build();

	private final AppMenuAction taskAction = AppMenuAction.builder().id("task").name("Aufgaben").navigation(DefaultNavigation).icon("").build();
	private final ApplicationArea taskArea = ApplicationArea.builder().id("task").name("Aufgaben").icon("standard:task").path("task").component("TaskArea").menu(EmptyMenu).menuAction(taskAction).build();

	private final AppMenuAction tenantAction = AppMenuAction.builder().id("tenant").name("Mandanten").navigation(DefaultNavigation).icon("").build();
	private final ApplicationArea tenantArea = ApplicationArea.builder().id("tenant").name("Mandanten").icon("standard:document").path("tenant").component("TenantArea").menu(EmptyMenu).menuAction(tenantAction).build();

	private final AppMenuAction userAction = AppMenuAction.builder().id("user").name("Benutzer").navigation(DefaultNavigation).icon("").build();
	private final ApplicationArea userArea = ApplicationArea.builder().id("user").name("Benutzer").icon("standard:user").path("user").component("UserArea").menu(EmptyMenu).menuAction(userAction).build();

	private final Application adminFmApp = Application.builder().id("adminFm").name("zeitwert: fm").icon("advise").description("Strategische Unterhaltsplanung").build();
	private final ApplicationInfo adminFmAppMenu = ApplicationInfo.builder().id("adminFm").name("zeitwert: fm").areas(List.of(homeArea, buildingArea, portfolioArea, accountArea)).defaultArea(homeArea.getId()).build();

	private final Application userFmApp = Application.builder().id("userFm").name("zeitwert: fm").icon("advise").description("Strategische Unterhaltsplanung").build();
	private final ApplicationInfo userFmAppMenu = ApplicationInfo.builder().id("userFm").name("zeitwert: fm").areas(List.of(homeArea, buildingArea, portfolioArea)).defaultArea(homeArea.getId()).build();

	private final Application adminAdminApp = Application.builder().id("adminAdmin").name("zeitwert: admin").icon("config").description("Applikationskonfiguration").build();
	private final ApplicationInfo adminAdminAppMenu = ApplicationInfo.builder().id("adminAdmin").name("zeitwert: admin").areas(List.of(userArea, tenantArea, documentArea)).defaultArea(userArea.getId()).build();
	//@formatter:on

	final List<Application> AdminApplications = new ArrayList<>();
	final List<Application> UserApplications = new ArrayList<>();
	final Map<String, Application> ApplicationMap = new HashMap<>();
	final Map<String, ApplicationArea> Areas = new HashMap<>();
	final Map<String, ApplicationInfo> ApplicationMenus = new HashMap<>();

	public ApplicationConfig() {

		Areas.put(accountArea.getId(), accountArea);
		Areas.put(buildingArea.getId(), buildingArea);
		Areas.put(buildingReportArea.getId(), buildingReportArea);
		Areas.put(contactArea.getId(), contactArea);
		Areas.put(documentArea.getId(), documentArea);
		Areas.put(homeArea.getId(), homeArea);
		Areas.put(leadArea.getId(), leadArea);
		Areas.put(portfolioArea.getId(), portfolioArea);
		Areas.put(taskArea.getId(), taskArea);
		Areas.put(tenantArea.getId(), tenantArea);
		Areas.put(userArea.getId(), userArea);

		ApplicationMap.put(adminFmApp.getId(), adminFmApp);
		ApplicationMenus.put(adminFmApp.getId(), adminFmAppMenu);

		ApplicationMap.put(userFmApp.getId(), userFmApp);
		ApplicationMenus.put(userFmApp.getId(), userFmAppMenu);

		ApplicationMap.put(adminAdminApp.getId(), adminAdminApp);
		ApplicationMenus.put(adminAdminApp.getId(), adminAdminAppMenu);

		AdminApplications.add(adminFmApp);
		AdminApplications.add(adminAdminApp);

		UserApplications.add(userFmApp);

	}

}
