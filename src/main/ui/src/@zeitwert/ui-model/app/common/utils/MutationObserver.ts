
/**
 * Mutation Observer Helper function
 * //developer.mozilla.org/en-US/docs/Web/API/MutationObserver/observe
 * @param {string} sel The DOM selector to watch 
 * @param {object} opt MutationObserver options 
 * @param {function} cb Pass Mutation object to a callback function
 */
export const observeMutation = (sel: string, opt: MutationObserverInit | undefined, cb: (value: MutationRecord, index: number, array: MutationRecord[]) => void) => {
	const Obs = new MutationObserver((m) => [...m].forEach(cb));
	document.querySelectorAll(sel).forEach(el => Obs.observe(el, opt));
};
