/**
 * ItemsPage - Configuration-based component for entity list views.
 *
 * Provides a standardized list view with:
 * - Ant Design Table with sorting and selection
 * - Create modal with React Hook Form
 * - Preview drawer for quick entity preview
 * - Bulk actions for selected items
 * - Header with title, icon, and custom actions
 */

import { useState, useMemo, useRef } from "react";
import { Card, Table, Button, Modal, Typography, Empty, message, Pagination } from "antd";
import type { TableProps } from "antd";
import { EyeOutlined, PlusOutlined } from "@ant-design/icons";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { PreviewDrawer } from "./PreviewDrawer";
import { useStyles } from "../../hooks/useStyles";
import type { ColumnType, Key } from "antd/es/table/interface";
import type { ReactNode } from "react";

const { Title, Text } = Typography;

// ============================================================================
// Types
// ============================================================================

export interface CreateFormProps {
	/** Callback to invoke on successful creation */
	onSuccess: () => void;
	/** Callback to invoke when the user cancels */
	onCancel: () => void;
}

export interface BulkAction<T> {
	/** Unique key for the action */
	key: string;
	/** Display label */
	label: string;
	/** Optional icon */
	icon?: ReactNode;
	/** Whether this is a dangerous action (shows red button) */
	danger?: boolean;
	/** Action handler receiving selected items */
	action: (selectedItems: T[]) => Promise<void>;
}

export interface ItemsPageProps<T extends { id: string }> {
	// Entity configuration
	/** Entity type identifier (e.g., "building", "contact") */
	entityType: string;
	/** Icon for the entity type */
	icon?: ReactNode;

	// Data configuration
	/** TanStack Query key for the list */
	queryKey: string[];
	/** Function to fetch the list of entities */
	queryFn: () => Promise<T[]>;

	// Table configuration
	/** Ant Design table columns */
	columns: ColumnType<T>[];
	/** Row key extractor (defaults to "id") */
	rowKey?: string | ((record: T) => string);
	/** Default sort field */
	defaultSortField?: string;
	/** Default sort order */
	defaultSortOrder?: "ascend" | "descend";

	// Creation
	/** Whether users can create new entities */
	canCreate?: boolean;
	/** Function to create a new entity */
	createMutationFn?: (data: Partial<T>) => Promise<T>;
	/** Form component for creating entities */
	CreateForm?: React.ComponentType<CreateFormProps>;
	/** Callback after successful creation */
	onAfterCreate?: (entity: T) => void;
	/** Default values for the create form */
	defaultCreateValues?: Partial<T>;

	// Preview (optional side panel)
	/** Component for previewing an entity */
	PreviewComponent?: React.ComponentType<{ id: string; onClose: () => void }>;
	/** Whether to show a preview column (defaults to true when PreviewComponent is provided) */
	showPreviewColumn?: boolean;

	// Navigation
	/** Callback when a row is clicked */
	onRowClick?: (record: T) => void;
	/** Function to get the detail page path for a record */
	getDetailPath?: (record: T) => string;

	// Bulk actions
	/** Whether rows are selectable */
	selectable?: boolean;
	/** Available bulk actions for selected rows */
	bulkActions?: BulkAction<T>[];

	// Custom header actions
	/** Additional action buttons for the header */
	headerActions?: ReactNode;
}

// ============================================================================
// Component
// ============================================================================

export function ItemsPage<T extends { id: string }>(props: ItemsPageProps<T>) {
	const {
		entityType,
		icon,
		queryKey,
		queryFn,
		columns,
		rowKey = "id",
		canCreate = false,
		CreateForm,
		PreviewComponent,
		showPreviewColumn,
		onRowClick,
		getDetailPath,
		selectable = false,
		bulkActions = [],
		headerActions,
	} = props;
	const { t } = useTranslation();
	const navigate = useNavigate();
	const { styles, token } = useStyles();

	// -------------------------------------------------------------------------
	// Derived values
	// -------------------------------------------------------------------------

	const entityLabelSingular = t(`${entityType}:label.entity`);

	// -------------------------------------------------------------------------
	// State
	// -------------------------------------------------------------------------

	const [isCreateOpen, setIsCreateOpen] = useState(false);
	const [previewId, setPreviewId] = useState<string | null>(null);
	const [selectedRowKeys, setSelectedRowKeys] = useState<Key[]>([]);
	const containerRef = useRef<HTMLDivElement>(null);
	const headerRef = useRef<HTMLDivElement>(null);
	const [currentPage, setCurrentPage] = useState(1);
	const [pageSize, setPageSize] = useState(20);

	// -------------------------------------------------------------------------
	// Data fetching
	// -------------------------------------------------------------------------

	const {
		data: items = [],
		isLoading,
		isError,
	} = useQuery({
		queryKey,
		queryFn,
	});

	// -------------------------------------------------------------------------
	// Derived data
	// -------------------------------------------------------------------------

	const selectedItems = useMemo(
		() => items.filter((item) => selectedRowKeys.includes(item.id)),
		[items, selectedRowKeys]
	);

	// Build columns with optional preview icon in the first column
	const finalColumns = useMemo(() => {
		if (!PreviewComponent || showPreviewColumn === false) {
			return columns;
		}

		const [first, ...rest] = columns;

		// Enhance the first column to include the preview icon on the right
		const firstColumnWithPreview: ColumnType<T> = {
			...first,
			render: (value, record, index) => {
				// Get the original render content
				const originalContent = first.render ? first.render(value, record, index) : value;

				return (
					<div
						style={{
							display: "flex",
							justifyContent: "space-between",
							alignItems: "center",
							gap: 8,
						}}
					>
						<span style={{ overflow: "hidden", textOverflow: "ellipsis" }}>{originalContent}</span>
						<Button
							type="text"
							size="small"
							icon={<EyeOutlined />}
							onClick={(e) => {
								e.stopPropagation();
								setPreviewId(record.id);
							}}
							aria-label="common:preview"
							style={{ flexShrink: 0 }}
						/>
					</div>
				);
			},
		};

		return [firstColumnWithPreview, ...rest];
	}, [columns, PreviewComponent, showPreviewColumn]);

	// -------------------------------------------------------------------------
	// Handlers
	// -------------------------------------------------------------------------

	const handleRowClick = (record: T) => {
		if (onRowClick) {
			onRowClick(record);
		} else if (getDetailPath) {
			navigate({ to: getDetailPath(record) });
		}
	};

	// Table change handler is available for future sorting/filtering implementation
	const handleTableChange: TableProps<T>["onChange"] = () => {
		// Sorting state management can be added here when needed
	};

	const handleBulkAction = async (action: BulkAction<T>) => {
		try {
			await action.action(selectedItems);
			setSelectedRowKeys([]);
		} catch (error) {
			const err = error as Error & { detail?: string };
			message.error(err.detail || t("common:message.error", { message: err.message }));
		}
	};

	// Pagination logic
	const paginatedItems = useMemo(() => {
		const start = (currentPage - 1) * pageSize;
		const end = start + pageSize;
		return items.slice(start, end);
	}, [items, currentPage, pageSize]);

	// -------------------------------------------------------------------------
	// Render
	// -------------------------------------------------------------------------

	const rowSelection = selectable
		? { selectedRowKeys, onChange: (keys: Key[]) => setSelectedRowKeys(keys) }
		: undefined;

	return (
		<div
			ref={containerRef}
			className="af-flex-column af-full-height"
			style={{ overflow: "hidden" }}
		>
			{/* Header */}
			<Card
				className="af-card-header-connected"
				styles={{
					body: {
						padding: `${token.paddingXS}px ${token.padding}px`,
					},
				}}
			>
				{/* Main header row */}
				<div
					ref={headerRef}
					style={{
						display: "flex",
						justifyContent: "space-between",
						alignItems: "center",
						marginTop: 12,
					}}
				>
					<div style={{ display: "flex", alignItems: "center", gap: 8 }}>
						{icon && <span style={styles.primaryIcon}>{icon}</span>}
						<Title level={4} style={{ margin: 0, lineHeight: "24px" }}>
							{entityLabelSingular}
						</Title>
					</div>

					<div style={{ display: "flex", alignItems: "center", gap: 8 }}>
						{/* Bulk actions */}
						{selectedRowKeys.length > 0 && bulkActions.length > 0 && (
							<>
								<Text type="secondary">
									{t("common:label.selectedCount", { count: selectedRowKeys.length })}
								</Text>
								{bulkActions.map((action) => (
									<Button
										key={action.key}
										icon={action.icon}
										danger={action.danger}
										onClick={() => handleBulkAction(action)}
									>
										{action.label}
									</Button>
								))}
							</>
						)}

						{/* Custom header actions */}
						{headerActions}

						{/* Create button */}
						{canCreate && CreateForm && (
							<Button
								type="primary"
								icon={<PlusOutlined />}
								onClick={() => setIsCreateOpen(true)}
								aria-label="common:create"
							>
								{t("common:action.createEntity", { entity: entityLabelSingular })}
							</Button>
						)}
					</div>
				</div>
				{/* Count row */}
				<Text type="secondary" style={{ marginLeft: 32 }}>
					{t(`${entityType}:label.entityCount`, { count: items.length })}
				</Text>
			</Card>

			{/* Table */}
			<Card
				className="af-card-body-connected"
				style={{
					flex: 1,
					minHeight: 0,
					display: "flex",
					flexDirection: "column",
				}}
				styles={{
					body: {
						flex: 1,
						minHeight: 0,
						display: "flex",
						flexDirection: "column",
						padding: 0,
						overflow: "hidden",
					},
				}}
			>
				{isError ? (
					<Empty
						description={t("common:message.loadError", { entity: entityLabelSingular })}
						image={Empty.PRESENTED_IMAGE_SIMPLE}
					/>
				) : (
					<div
						className="items-page-table-wrapper af-flex-column"
						style={{
							flex: 1,
							minHeight: 0,
							overflow: "hidden",
							position: "relative",
						}}
					>
						<div
							style={{
								flex: 1,
								minHeight: 0,
								overflow: "auto",
							}}
						>
							<Table<T>
								columns={finalColumns}
								dataSource={paginatedItems}
								rowKey={rowKey}
								loading={isLoading}
								rowSelection={rowSelection}
								onChange={handleTableChange}
								sortDirections={["ascend", "descend"]}
								pagination={false}
								onRow={(record) => ({
									onClick: () => handleRowClick(record),
									style: { cursor: getDetailPath || PreviewComponent ? "pointer" : undefined },
								})}
							/>
						</div>
						<div style={styles.paginationFooter}>
							<Pagination
								current={currentPage}
								total={items.length}
								pageSize={pageSize}
								showSizeChanger
								showTotal={(total, range) =>
									t("common:message.paginationRange", { start: range[0], end: range[1], total })
								}
								pageSizeOptions={["10", "20", "50", "100"]}
								onChange={(page, size) => {
									setCurrentPage(page);
									setPageSize(size);
								}}
							/>
						</div>
					</div>
				)}
			</Card>

			{/* Create Modal */}
			{CreateForm && (
				<Modal
					open={isCreateOpen}
					title={t("common:action.createEntity", { entity: entityLabelSingular })}
					onCancel={() => setIsCreateOpen(false)}
					footer={null}
					destroyOnHidden
				>
					<CreateForm
						onSuccess={() => setIsCreateOpen(false)}
						onCancel={() => setIsCreateOpen(false)}
					/>
				</Modal>
			)}

			{/* Preview Drawer */}
			{PreviewComponent && previewId && (
				<PreviewDrawer
					open={!!previewId}
					title={entityLabelSingular}
					onClose={() => setPreviewId(null)}
				>
					<PreviewComponent id={previewId} onClose={() => setPreviewId(null)} />
				</PreviewDrawer>
			)}
		</div>
	);
}
