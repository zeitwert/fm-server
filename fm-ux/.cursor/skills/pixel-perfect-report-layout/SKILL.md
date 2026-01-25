---
name: pixel-perfect-report-layout
description: Apply pixel-perfect CSS positioning for HTML print reports matching original Aspose Word templates. Use when comparing report screenshots, adjusting report element positions, or when the user shows original vs current report layouts.
---

# Pixel-Perfect Report Layout

This skill enables precise CSS positioning to match original Aspose Word template layouts for print reports.

## Page Dimensions

**A4 Landscape:**
- Page: 297mm × 210mm
- Margins: 15mm top, 20mm right/bottom/left
- Content area: 257mm × 175mm

**Conversion:**
- 1mm = 2.834647 points (PostScript/PDF standard)
- To convert Aspose points to CSS mm: `mm = points / 2.834647`

## Analysis Workflow

When the user provides original vs current screenshots:

1. **Identify each distinct element** in the original layout
2. **Measure positions** relative to content area edges (top/bottom/left/right)
3. **Calculate sizes** using the point-to-mm conversion
4. **Create individual CSS classes** for each element

## CSS Approach

### Each Element Gets Its Own Class

```css
/* Element naming pattern: .{page}-{element-name} */
.cover-account-name { ... }
.cover-building-name { ... }
.cover-photo-container { ... }
.cover-strategic-label { ... }
.cover-report-type { ... }
```

### Use Absolute Positioning

```css
.cover-element {
    position: absolute;
    top: 0;      /* or bottom: Xmm */
    left: 0;     /* or right: Xmm */
    /* ... font properties */
}
```

### Container Must Be Relative

```css
.cover-page-body {
    position: relative;
    width: 100%;
    height: 100%;
}
```

## Measurement Reference

### From Aspose Source Code

Check `DocumentGenerationServiceImpl.kt` for original measurements:

| Element | Aspose Value | CSS Value |
|---------|-------------|-----------|
| Cover Photo | 400×230pt | 141×81mm |
| Cover Photo Top | 170pt | 60mm |
| Location Map | 360×360pt | 127×127mm |

### Visual Estimation from Screenshots

When Aspose values aren't available, estimate from screenshot proportions:

1. Content height = 175mm
2. Measure element's vertical position as percentage
3. Convert: `position_mm = (percentage / 100) × 175mm`

## Font Styling

Always explicitly set font properties:

```css
.element {
    font-size: 16pt;
    font-style: normal;    /* or italic */
    font-weight: 400;      /* 400=normal, 700=bold */
    color: #333;           /* or #666 for gray */
    margin: 0;
    line-height: 1.2;
}
```

## Common Positions

### Top-aligned elements
```css
top: 0;           /* First element */
top: 8mm;         /* Below first element */
```

### Bottom-aligned elements
```css
bottom: 35mm;     /* Higher label */
bottom: 20mm;     /* Lower label */
```

### Right-aligned elements (photos, maps)
```css
right: 0;
top: 45mm;        /* Adjust based on Aspose or visual match */
```

## Iteration Process

1. Make CSS changes
2. User provides new screenshot
3. Compare with original
4. Adjust positions in mm increments
5. Repeat until pixel-perfect

## Files to Modify

- **CSS**: `areas/{entity}/ui/report/{reportType}/report.css`
- **Components**: `areas/{entity}/ui/report/{reportType}/sections/*.tsx`
- **Constants**: `areas/{entity}/utils/evaluationUtils.ts`
