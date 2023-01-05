/*

 ----------------------------------------------------------------------------
 | qewd-transform-json: Transform JSON using a template                     |
 |                                                                          |
 | Copyright (c) 2016-17 M/Gateway Developments Ltd,                        |
 | Redhill, Surrey UK.                                                      |
 | All rights reserved.                                                     |
 |                                                                          |
 | http://www.mgateway.com                                                  |
 | Email: rtweed@mgateway.com                                               |
 |                                                                          |
 |                                                                          |
 | Licensed under the Apache License, Version 2.0 (the "License");          |
 | you may not use this file except in compliance with the License.         |
 | You may obtain a copy of the License at                                  |
 |                                                                          |
 |     http://www.apache.org/licenses/LICENSE-2.0                           |
 |                                                                          |
 | Unless required by applicable law or agreed to in writing, software      |
 | distributed under the License is distributed on an "AS IS" BASIS,        |
 | WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
 | See the License for the specific language governing permissions and      |
 |  limitations under the License.                                          |
 ----------------------------------------------------------------------------

*/

import ObjectPath from "object-path";
import { DateFormat } from "./DateFormat";
import { requireThis } from "./Invariants";
import { TraversalState, traverse } from "./Object";

// function isNumeric(n: any) {
// 	return !isNaN(parseFloat(n)) && isFinite(n);
// }

const StandardHelpers = {
	index() {
		const self: any = this;
		return self.index + 1;
	},
	nvl(value: any, defaultValue: any) {
		if (value === "") {
			return defaultValue;
		}
		return value;
	},
	either(guard: any, value: any, defaultValue: any) {
		return guard ? value : defaultValue;
	},
	getDate(input: any) {
		if (!!input) {
			return new Date(input);
		}
		return new Date();
	},
	getTime(date: any) {
		if (!!date) {
			return new Date(date).getTime();
		}
		return "";
	},
	relativeTime(input: any) {
		if (!!input) {
			return DateFormat.relativeTime(new Date(), new Date(input));
		}
		return "";
	}
};

const SingleExprRegex = /^{{[\w\d.()[\]"]+}}$/;
const MultiExprRegex = /({{[\w\d.()[\]"]+}})/g;

export function transform(src: any, index: number, template: any, customHelpers: any = null) {
	let helpers = customHelpers || {};
	for (let fn in StandardHelpers) {
		if (!helpers[fn]) {
			helpers[fn] = StandardHelpers[fn];
		}
	}

	function evalExpr(expr: string, data: any) {
		const pureExpr = expr.substring(2, expr.length - 2);
		return ObjectPath.get(data, pureExpr) || "";
	}

	function getActualValue(expr: string, data: any) {
		if (SingleExprRegex.test(expr)) {
			return evalExpr(expr, data);
		} else if (MultiExprRegex.test(expr)) {
			let result = expr;
			const matches = expr.match(MultiExprRegex);
			if (!!matches) {
				for (let i = 0; i < matches.length; i++) {
					const value = evalExpr(matches[i], data);
					result = result.replace(matches[i], value.toString());
				}
			}
			return result;
		}
	}

	/*
	 * traverse will traverse each updated element again
	 * so we have to take care to do a no-op on already replaced elements
	 * especially relevant for arrays
	 */
	/* eslint-disable array-callback-return */
	return traverse(template).map((state: TraversalState, node: any) => {
		if (typeof node === "function") {
			state.update(node(src));
		} else if (Array.isArray(node)) {
			// check if we have an array with array expression in first element
			if (node.length > 0 && node[0] && typeof node[0] === "string" && node[0].indexOf("{{") >= 0) {
				let dataArr = getActualValue(node[0], src) || [];
				requireThis(Array.isArray(dataArr), "data is array [" + node[0] + "]", src);
				if (node.length === 2) {
					let itemTemplate = node[1];
					state.update(transformArray(dataArr, itemTemplate, helpers));
				} else {
					state.update(dataArr);
				}
			}
		} else if (typeof node === "string") {
			if (node.indexOf("{{") >= 0) {
				state.update(getActualValue(node, src));
			} else if (node.startsWith("=>")) {
				let fn = node.split("=>")[1];
				// fn = fn.replace(/ /g,"");
				fn = fn.trim(); // remove leading & trailing spaces
				let pieces = fn.split("(");
				let fnName = pieces[0];
				let argStr = pieces[1].split(")")[0];
				let args = argStr.split(",");
				let argArr: any[] = [];
				if (args) {
					args.forEach((arg: any) => {
						arg = arg.trim();
						if (arg === "null") {
							arg = undefined;
						} else if (arg[0] === "'" || arg[0] === '"') {
							arg = arg.slice(1, -1);
						} else if (isNaN(arg)) {
							let argx = "{{" + arg + "}}";
							try {
								arg = getActualValue(argx, src);
							} catch (err: any) {
								/* */
							}
						}
						argArr.push(arg);
					});
				}
				try {
					let result = helpers[fnName].bind({
						src: src,
						index: index,
						template: template
					})(...argArr);
					if (result === "<!delete>") {
						state.delete();
					} else {
						state.update(result);
					}
				} catch (err: any) {
					state.update("Error running: " + fnName + "(" + argArr + ")");
				}
			}
		}
	});
}

function transformArray(items: any[], itemTemplate: any, helpers: any): any[] {
	requireThis(Array.isArray(items), "array");
	return items.map((obj: any, index: number) => transform(obj, index, itemTemplate, helpers));
}
