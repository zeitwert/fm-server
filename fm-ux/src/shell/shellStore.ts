import { create } from "zustand";
import { persist } from "zustand/middleware";

interface ShellStore {
	// State
	sidebarCollapsed: boolean;
	rightPanelCollapsed: boolean;

	// Actions
	toggleSidebar: () => void;
	setSidebarCollapsed: (collapsed: boolean) => void;
	toggleRightPanel: () => void;
	setRightPanelCollapsed: (collapsed: boolean) => void;
}

export const useShellStore = create<ShellStore>()(
	persist(
		(set) => ({
			// Initial state
			sidebarCollapsed: false,
			rightPanelCollapsed: false,

			// Actions
			toggleSidebar: () => {
				set((state) => ({ sidebarCollapsed: !state.sidebarCollapsed }));
			},

			setSidebarCollapsed: (collapsed: boolean) => {
				set({ sidebarCollapsed: collapsed });
			},

			toggleRightPanel: () => {
				set((state) => ({ rightPanelCollapsed: !state.rightPanelCollapsed }));
			},

			setRightPanelCollapsed: (collapsed: boolean) => {
				set({ rightPanelCollapsed: collapsed });
			},
		}),
		{
			name: "shell-storage",
		}
	)
);
