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
import { Card, Table, Button, Space, Modal, Typography, Empty, message, Pagination } from "antd";
import type { TableProps } from "antd";
import { PlusOutlined } from "@ant-design/icons";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { PreviewDrawer } from "./PreviewDrawer";
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
	/** Display name for the entity type (plural, e.g., "Immobilien") */
	entityLabel: string;
	/** Display name for the entity type (singular, e.g., "Immobilie") */
	entityLabelSingular: string;
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
		entityLabel,
		entityLabelSingular,
		icon,
		queryKey,
		queryFn,
		columns,
		rowKey = "id",
		canCreate = false,
		CreateForm,
		PreviewComponent,
		onRowClick,
		getDetailPath,
		selectable = false,
		bulkActions = [],
		headerActions,
	} = props;
	const { t } = useTranslation("common");
	const navigate = useNavigate();

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

	// -------------------------------------------------------------------------
	// Handlers
	// -------------------------------------------------------------------------

	const handleRowClick = (record: T) => {
		if (onRowClick) {
			onRowClick(record);
		} else if (getDetailPath) {
			navigate({ to: getDetailPath(record) });
		} else if (PreviewComponent) {
			setPreviewId(record.id);
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
			message.error(err.detail || `Fehler: ${err.message}`);
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
		? {
			selectedRowKeys,
			onChange: (keys: Key[]) => setSelectedRowKeys(keys),
		}
		: undefined;

	return (
		<div
			ref={containerRef}
			style={{
				display: "flex",
				flexDirection: "column",
				height: "100%",
				overflow: "hidden",
			}}
		>
			{/* Header */}
			<Card
				style={{
					marginBottom: 0,
					background: "#f5f5f5",
					borderBottomLeftRadius: 0,
					borderBottomRightRadius: 0,
				}}
				styles={{
					body: {
						padding: "8px 16px",
					},
				}}
			>
				<div
					ref={headerRef}
					style={{
						display: "flex",
						justifyContent: "space-between",
						alignItems: "center",
					}}
				>
					<Space>
						{icon && <span style={{ fontSize: 24, color: "#1677ff" }}>{icon}</span>}
						<div>
							<Title level={4} style={{ margin: 0 }}>
								{entityLabel}
							</Title>
							<Text type="secondary">
								{items.length} {items.length === 1 ? entityLabelSingular : entityLabel}
							</Text>
						</div>
					</Space>

					<Space>
						{/* Bulk actions */}
						{selectedRowKeys.length > 0 && bulkActions.length > 0 && (
							<Space>
								<Text type="secondary">{selectedRowKeys.length} ausgewählt</Text>
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
							</Space>
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
								{t("create")} {entityLabelSingular}
							</Button>
						)}
					</Space>
				</div>
			</Card>

			{/* Table */}
			<Card
				style={{
					flex: 1,
					minHeight: 0,
					display: "flex",
					flexDirection: "column",
					borderTopLeftRadius: 0,
					borderTopRightRadius: 0,
					margin: 0,
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
						description={`Fehler beim Laden der ${entityLabel}`}
						image={Empty.PRESENTED_IMAGE_SIMPLE}
					/>
				) : (
					<div
						style={{
							flex: 1,
							minHeight: 0,
							display: "flex",
							flexDirection: "column",
							overflow: "hidden",
							position: "relative",
						}}
						className="items-page-table-wrapper"
					>
						<div
							style={{
								flex: 1,
								minHeight: 0,
								overflow: "auto",
							}}
						>
							<Table<T>
								columns={columns}
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
						<div
							style={{
								padding: "16px",
								borderTop: "1px solid #f0f0f0",
								display: "flex",
								justifyContent: "flex-end",
								flexShrink: 0,
								background: "#ffffff",
							}}
						>
							<Pagination
								current={currentPage}
								total={items.length}
								pageSize={pageSize}
								showSizeChanger
								showTotal={(total, range) => `${range[0]}-${range[1]} von ${total}`}
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
					title={`${entityLabelSingular} erstellen`}
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
