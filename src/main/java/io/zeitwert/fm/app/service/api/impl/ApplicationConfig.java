
package io.zeitwert.fm.app.service.api.impl;

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
	private final NavigationAction DefaultAction = NavigationAction.builder().actionType(this.RouteActionType).build();
	private final Navigation DefaultNavigation = Navigation.builder().target(this.SelfTarget).action(this.DefaultAction).build();
	private final AppMenu EmptyMenu = AppMenu.builder().build();

	private final AppMenuAction accountAction = AppMenuAction.builder().id("account").name("Kunden").navigation(this.DefaultNavigation).icon("").build();
	private final ApplicationArea accountArea = ApplicationArea.builder().id("account").name("Kunden").icon("standard:account").path("account").component("account/ui/AccountArea").menu(this.EmptyMenu).menuAction(this.accountAction).build();

	private final AppMenuAction contactAction = AppMenuAction.builder().id("contact").name("Kontakte").navigation(this.DefaultNavigation).icon("").build();
	private final ApplicationArea contactArea = ApplicationArea.builder().id("contact").name("Kontakte").icon("standard:contact").path("contact").component("contact/ui/ContactArea").menu(this.EmptyMenu).menuAction(this.contactAction).build();

	private final AppMenuAction buildingAction = AppMenuAction.builder().id("building").name("Immobilien").navigation(this.DefaultNavigation).icon("").build();
	private final ApplicationArea buildingArea = ApplicationArea.builder().id("building").name("Immobilien").icon("custom:custom24").path("building").component("building/ui/BuildingArea").menu(this.EmptyMenu).menuAction(this.buildingAction).build();

	private final AppMenuAction documentAction = AppMenuAction.builder().id("document").name("Dokumente").navigation(this.DefaultNavigation).icon("").build();
	private final ApplicationArea documentArea = ApplicationArea.builder().id("document").name("Dokumente").icon("standard:document").path("document").component("document/ui/DocumentArea").menu(this.EmptyMenu).menuAction(this.documentAction).build();

	private final AppMenuAction homeAction = AppMenuAction.builder().id("home").name("Dashboard").navigation(this.DefaultNavigation).icon("").build();
	private final ApplicationArea homeArea = ApplicationArea.builder().id("home").name("Dashboard").icon("standard:home").path("home").component("home/ui/HomeArea").menu(this.EmptyMenu).menuAction(this.homeAction).build();

	private final AppMenuAction leadAction = AppMenuAction.builder().id("lead").name("Leads").navigation(this.DefaultNavigation).icon("").build();
	private final ApplicationArea leadArea = ApplicationArea.builder().id("lead").name("Leads").icon("standard:lead").path("lead").component("lead/ui/LeadArea").menu(this.EmptyMenu).menuAction(this.leadAction).build();

	private final AppMenuAction portfolioAction = AppMenuAction.builder().id("portfolio").name("Portfolios").navigation(this.DefaultNavigation).icon("").build();
	private final ApplicationArea portfolioArea = ApplicationArea.builder().id("portfolio").name("Portfolios").icon("standard:store_group").path("portfolio").component("portfolio/ui/PortfolioArea").menu(this.EmptyMenu).menuAction(this.portfolioAction).build();

	private final AppMenuAction taskAction = AppMenuAction.builder().id("task").name("Aufgaben").navigation(this.DefaultNavigation).icon("").build();
	private final ApplicationArea taskArea = ApplicationArea.builder().id("task").name("Aufgaben").icon("standard:task").path("task").component("task/ui/TaskArea").menu(this.EmptyMenu).menuAction(this.taskAction).build();

	private final AppMenuAction tenantAction = AppMenuAction.builder().id("tenant").name("Mandanten").navigation(this.DefaultNavigation).icon("").build();
	private final ApplicationArea tenantArea = ApplicationArea.builder().id("tenant").name("Mandanten").icon("standard:document").path("tenant").component("tenant/ui/TenantArea").menu(this.EmptyMenu).menuAction(this.tenantAction).build();

	private final AppMenuAction userAction = AppMenuAction.builder().id("user").name("Benutzer").navigation(this.DefaultNavigation).icon("").build();
	private final ApplicationArea userArea = ApplicationArea.builder().id("user").name("Benutzer").icon("standard:user").path("user").component("user/ui/UserArea").menu(this.EmptyMenu).menuAction(this.userAction).build();

	private final Application fmApp = Application.builder().id("fm").name("zeitwert: fm").icon("advise").description("Strategische Unterhaltsplanung").build();
	private final ApplicationInfo fmAppMenu = ApplicationInfo.builder().id("fmMenu").name("ZEitWERT: fm").areas(List.of(this.homeArea, this.portfolioArea, this.buildingArea, this.accountArea, this.contactArea)).defaultArea(this.homeArea.getId()).build();

	private final Application tenantAdminApp = Application.builder().id("tenantAdmin").name("ZEitWERT: admin").icon("config").description("Mandantenadministration").build();
	private final ApplicationInfo tenantAdminAppMenu = ApplicationInfo.builder().id("adminMenu").name("ZEitWERT: admin").areas(List.of(this.tenantArea, this.accountArea, this.userArea)).defaultArea(this.userArea.getId()).build();

	private final Application appAdminApp = Application.builder().id("appAdmin").name("ZEitWERT: appAdmin").icon("config").description("Applikationsadministration").build();
	private final ApplicationInfo appAdminAppMenu = ApplicationInfo.builder().id("appAdminMenu").name("ZEitWERT: appAdmin").areas(List.of(this.tenantArea, this.accountArea, this.userArea)).defaultArea(this.tenantArea.getId()).build();
	//@formatter:on

	final List<Application> AppAdminApplications = new ArrayList<>();
	final List<Application> AdminApplications = new ArrayList<>();
	final List<Application> UserApplications = new ArrayList<>();
	final Map<String, Application> ApplicationMap = new HashMap<>();
	final Map<String, ApplicationArea> Areas = new HashMap<>();
	final Map<String, ApplicationInfo> ApplicationMenus = new HashMap<>();

	public ApplicationConfig() {

		this.Areas.put(this.accountArea.getId(), this.accountArea);
		this.Areas.put(this.buildingArea.getId(), this.buildingArea);
		this.Areas.put(this.contactArea.getId(), this.contactArea);
		this.Areas.put(this.documentArea.getId(), this.documentArea);
		this.Areas.put(this.homeArea.getId(), this.homeArea);
		this.Areas.put(this.leadArea.getId(), this.leadArea);
		this.Areas.put(this.portfolioArea.getId(), this.portfolioArea);
		this.Areas.put(this.taskArea.getId(), this.taskArea);
		this.Areas.put(this.tenantArea.getId(), this.tenantArea);
		this.Areas.put(this.userArea.getId(), this.userArea);

		this.ApplicationMap.put(this.fmApp.getId(), this.fmApp);
		this.ApplicationMenus.put(this.fmApp.getId(), this.fmAppMenu);

		this.ApplicationMap.put(this.tenantAdminApp.getId(), this.tenantAdminApp);
		this.ApplicationMenus.put(this.tenantAdminApp.getId(), this.tenantAdminAppMenu);

		this.ApplicationMap.put(this.appAdminApp.getId(), this.appAdminApp);
		this.ApplicationMenus.put(this.appAdminApp.getId(), this.appAdminAppMenu);

		this.UserApplications.add(this.fmApp);

		this.AdminApplications.add(this.tenantAdminApp);

		this.AppAdminApplications.add(this.appAdminApp);

	}

}
