export function deepFind(obj: any, path: any): string | undefined {
	var paths = path.split("."),
		current = obj,
		i;

	for (i = 0; i < paths.length; ++i) {
		if (current[paths[i]] === undefined) {
			return undefined;
		} else {
			current = current[paths[i]];
		}
	}
	return current;
}

export function valueByPath(o: any, path: string) {
	// regex:
	// - convert indices to properties
	// - then strip a possible leading dot
	// - then split by dots
	var pathElements = path
		.replace(/\[(\w+)\]/g, ".$1")
		.replace(/^\./, "")
		.split(".");
	for (var i = 0; i < pathElements.length; ++i) {
		var key = pathElements[i];
		if (key in o) {
			o = o[key];
		} else {
			return undefined;
		}
	}
	return o;
}

export function traverse(obj: any): Traversal {
	return new TraversalImpl(obj);
}

export interface Traversal {
	map(cb: TraversalCallback): any;

	forEach(cb: TraversalCallback): any;
}

export type TraversalCallback = (state: TraversalState, node: any) => void;

export interface TraversalState {
	node: any;
	origNode: any;
	path: any[];
	parent: any;
	parents: any[];
	key: string;
	isRoot: boolean;
	notRoot: boolean | null;
	isLeaf: boolean | null;
	notLeaf: boolean | null;
	level: number;
	circular: boolean | null;
	isFirst?: any;
	isLast?: any;
	update: (value: any, stopHere?: boolean) => void;
	delete: (stopHere?: boolean) => void;
	remove: (stopHere?: boolean) => void;
	keys: string[] | null;
	before: (f: Function) => void;
	pre: (f: Function) => void;
	post: (f: Function) => void;
	after: (f: Function) => void;
	stop: () => void;
	block: () => void;
}

class TraversalImpl implements Traversal {
	value: any = null;

	constructor(value: any) {
		this.value = value;
	}

	map(cb: TraversalCallback) {
		return walk(this.value, cb, true);
	}

	forEach(cb: TraversalCallback) {
		this.value = walk(this.value, cb, false);
		return this.value;
	}
}

interface Modifiers {
	before?: Function;
	pre?: Function;
	post?: Function;
	after?: Function;
}

function walk(root: any, cb: TraversalCallback, immutable: boolean) {
	let path: any[] = [];
	let parents: any[] = [];
	let alive = true;

	return (function walker(origNode: any) {
		let node = immutable ? copy(origNode) : origNode;
		let modifiers: Modifiers = {};
		let keepGoing = true;

		let state: TraversalState = {
			node: node,
			origNode: origNode,
			path: ([] as any[]).concat(path),
			parent: parents[parents.length - 1],
			parents: parents,
			key: path.slice(-1)[0],
			isRoot: path.length === 0,
			notRoot: null,
			isLeaf: null,
			notLeaf: null,
			level: path.length,
			circular: null,
			update: (x: any, stopHere: boolean = false) => {
				if (!state.isRoot) {
					state.parent.node[state.key] = x;
				}
				state.node = x;
				if (stopHere) {
					keepGoing = false;
				}
			},
			delete: (stopHere: boolean = false) => {
				delete state.parent.node[state.key];
				if (stopHere) {
					keepGoing = false;
				}
			},
			remove: (stopHere: boolean = false) => {
				if (Array.isArray(state.parent.node)) {
					state.parent.node.splice(state.key, 1);
				} else {
					delete state.parent.node[state.key];
				}
				if (stopHere) {
					keepGoing = false;
				}
			},
			keys: null as string[] | null,
			before: (f: Function) => {
				modifiers.before = f;
			},
			pre: (f: Function) => {
				modifiers.pre = f;
			},
			post: (f: Function) => {
				modifiers.post = f;
			},
			after: (f: Function) => {
				modifiers.after = f;
			},
			stop: () => {
				alive = false;
			},
			block: () => {
				keepGoing = false;
			}
		};

		if (!alive) {
			return state;
		}

		function updateState() {
			if (typeof state.node === "object" && state.node !== null) {
				if (!state.keys || state.origNode !== state.node) {
					state.keys = Object.keys(state.node);
				}
				state.isLeaf = state.keys.length === 0;
				for (let i = 0; i < parents.length; i++) {
					if (parents[i].node_ === origNode) {
						state.circular = parents[i];
						break;
					}
				}
			} else {
				state.isLeaf = true;
				state.keys = null;
			}
			state.notLeaf = !state.isLeaf;
			state.notRoot = !state.isRoot;
		}

		updateState();

		// use return values to update if defined
		let ret = cb(state, state.node);
		if (ret !== undefined && state.update) {
			state.update(ret);
		}

		if (modifiers.before) {
			modifiers.before.call(state, state.node);
		}

		if (!keepGoing) {
			return state;
		}

		if (typeof state.node === "object" && state.node !== null && !state.circular) {
			parents.push(state);
			updateState();
			forEach(state.keys, function (key: string, i: number) {
				path.push(key);
				if (modifiers.pre) {
					modifiers.pre.call(state, state.node[key], key);
				}
				let child = walker(state.node[key]);
				if (immutable && Object.hasOwnProperty.call(state.node, key)) {
					state.node[key] = child.node;
				}
				child.isLast = i === state.keys!.length - 1;
				child.isFirst = i === 0;
				if (modifiers.post) {
					modifiers.post.call(state, child);
				}
				path.pop();
			});
			parents.pop();
		}

		if (modifiers.after) {
			modifiers.after.call(state, state.node);
		}

		return state;
	})(root).node;
}

function copy(src: any) {
	if (typeof src === "object" && src !== null) {
		let dst: any = undefined;
		if (Array.isArray(src)) {
			dst = [];
		} else if (isDate(src)) {
			dst = new Date(src.getTime ? src.getTime() : src);
		} else if (isRegExp(src)) {
			dst = new RegExp(src);
		} else if (isError(src)) {
			dst = { message: src.message };
		} else if (isBoolean(src)) {
			dst = Boolean(src);
		} else if (isNumber(src)) {
			dst = Number(src);
		} else if (isString(src)) {
			dst = String(src);
		} else if (Object.create && Object.getPrototypeOf) {
			dst = Object.create(Object.getPrototypeOf(src));
		} else if (src.constructor === Object) {
			dst = {};
		} else {
			let proto = (src.constructor && src.constructor.prototype) || src.__proto__ || {};
			let T = function () {
				/**/
			};
			T.prototype = proto;
			dst = new (T as any)();
		}
		forEach(Object.keys(src), function (key: string) {
			dst[key] = src[key];
		});
		return dst;
	} else {
		return src;
	}
}

function toS(obj: any): string {
	return Object.prototype.toString.call(obj);
}
function isDate(obj: any) {
	return toS(obj) === "[object Date]";
}
function isRegExp(obj: any) {
	return toS(obj) === "[object RegExp]";
}
function isError(obj: any) {
	return toS(obj) === "[object Error]";
}
function isBoolean(obj: any) {
	return toS(obj) === "[object Boolean]";
}
function isNumber(obj: any) {
	return toS(obj) === "[object Number]";
}
function isString(obj: any) {
	return toS(obj) === "[object String]";
}

let forEach = function (xs: any, fn: Function) {
	if (xs.forEach) {
		return xs.forEach(fn);
	} else {
		for (let i = 0; i < xs.length; i++) {
			fn(xs[i], i, xs);
		}
	}
};

forEach(Object.keys(TraversalImpl.prototype), function (key: any) {
	traverse[key] = function (obj: any) {
		let args = [].slice.call(arguments, 1);
		let t = new TraversalImpl(obj);
		return t[key].apply(t, args);
	};
});
