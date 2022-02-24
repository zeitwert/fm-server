import { reportService } from "../..";
import { Datamart } from "../Datamart";
import { Layout } from "../Layout";
import { Template } from "../Template";

it("has defined providers", () => {
	expect(reportService.provider("api")).toBeDefined();
});

it("has defined datamarts", () => {
	const datamartPromise = reportService.datamart("meeting.meetings");

	test("datamart", (done) => {
		datamartPromise.then((datamart: Datamart | undefined) => {
			expect(datamart).toBeDefined();
			expect(datamart!.provider).toBeDefined();
			expect(datamart!.id).toBe("meeting.meetings");
			expect(datamart!.name).toBeDefined();
			expect(datamart!.params).toBeDefined();
			done();
		});
	});
});

it("has defined layout", () => {
	const layoutPromise = reportService.layout("meeting.meetings.default");

	test("layout", (done) => {
		layoutPromise.then((layout: Layout | undefined) => {
			expect(layout).toBeDefined();
			expect(layout!.datamart).toBeDefined();
			expect(layout!.id).toBe("meeting.meetings.default");
			expect(layout!.name).toBeDefined();
			expect(layout!.layout).toBeDefined();
			done();
		});
	});
});

it("has defined template", () => {
	const templatePromise = reportService.template("meeting.meetings.my-open");

	test("template", (done) => {
		templatePromise.then((template: Template | undefined) => {
			expect(template).toBeDefined();
			expect(template!.datamart).toBeDefined();
			expect(template!.id).toBe("meeting.meetings.my-open");
			expect(template!.name).toBeDefined();
			expect(template!.layout).toBeDefined();
			done();
		});
	});
});

it("can execute a template", () => {
	// Cannot execute fetch on nodejs / Jest environment
	/*
		const url = "http://ec2-34-232-210-118.compute-1.amazonaws.com:8080/api/cases?query=model:Address%20Change%20state:open&view=light";
		console.log("execute", url);
		Http.fetch(url)
			.then((response: Response): any => {
				console.log("response", response);
				return response.json();
			})
			.then((data: any) {
				console.log("data", data);
			})
			.catch((reason: any) {
				console.log("crashed", reason);
			});
	*/
	/*
	const templatePromise = ReportEngine.template("e1:cases.my-open");
	templatePromise
		.then((template: Template): Promise<any> => {
			return ReportEngine.execute(template);
		})
		.then((result: any) {
			console.log("executed", result);
		})
		.catch((reason: any) {
			expect(false).toBe(true);
		});
	*/
});
