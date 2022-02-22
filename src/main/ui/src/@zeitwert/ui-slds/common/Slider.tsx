import { default as BaseSlider } from "@salesforce/design-system-react/components/slider";
import _ from "lodash";
import { observer } from "mobx-react";
import React from "react";

interface SliderProps {
	label: string;
	min: number;
	max: number;
	step?: number;
	value?: number;
	defaultValue?: number;
	onInput?: (e: any, v: any) => void;
	onChange?: (e: any, v: any) => void;
}

@observer
export default class Slider extends React.Component<SliderProps> {
	ref?: HTMLDivElement;

	componentDidMount() {
		const { min, max, value, defaultValue } = this.props;
		this.setBubble(
			{
				value: value ? value : defaultValue,
				min: min,
				max: max
			},
			this.ref!.querySelector(".slds-slider__range")!,
			true
		);
	}

	render() {
		const { label, min, max, step, onInput, onChange } = this.props;
		return (
			<div ref={(ref: any) => (this.ref = ref)}>
				<BaseSlider
					label={label}
					min={min}
					max={max}
					step={step}
					value={this.props.value}
					defaultValue={this.props.defaultValue}
					// @ts-ignore
					onInput={(e, v) => {
						onInput && onInput(e, v);
						this.setBubble(
							{
								value: v.value,
								min: min,
								max: max
							},
							e.target
						);
					}}
					// @ts-ignore
					onChange={onChange}
				/>
			</div>
		);
	}

	private setBubble(range: any, bubble: HTMLDivElement, dflt = false) {
		const val = range.value ? range.value : 0;
		const min = range.min ? range.min : 0;
		const max = range.max ? range.max : 100;
		const newVal = Number(((val - min) * 100) / (max - min));
		bubble.setAttribute("data-after", val.toString());
		bubble.style.setProperty("--left", _.clamp(newVal, 0, 100) + "%");
		if (dflt) {
			bubble.style.setProperty("--right", _.clamp(newVal, 0, 100) + "%");
		}
	}
}
