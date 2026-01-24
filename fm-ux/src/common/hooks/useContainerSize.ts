import { useState, useEffect, useRef, type RefObject } from "react";

interface ContainerSize {
	width: number;
	height: number;
}

export function useContainerSize<T extends HTMLElement>(): [RefObject<T | null>, ContainerSize] {
	const ref = useRef<T | null>(null);
	const [size, setSize] = useState<ContainerSize>({ width: 0, height: 0 });

	useEffect(() => {
		const element = ref.current;
		if (!element) return;

		const observer = new ResizeObserver((entries) => {
			const entry = entries[0];
			if (entry) {
				setSize({
					width: entry.contentRect.width,
					height: entry.contentRect.height,
				});
			}
		});

		observer.observe(element);
		return () => observer.disconnect();
	}, []);

	return [ref, size];
}
