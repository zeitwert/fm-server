import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface ShellStore {
	// State
	sidebarCollapsed: boolean;

	// Actions
	toggleSidebar: () => void;
	setSidebarCollapsed: (collapsed: boolean) => void;
}

export const useShellStore = create<ShellStore>()(
	persist(
		(set) => ({
			// Initial state
			sidebarCollapsed: false,

			// Actions
			toggleSidebar: () => {
				set((state) => ({ sidebarCollapsed: !state.sidebarCollapsed }));
			},

			setSidebarCollapsed: (collapsed: boolean) => {
				set({ sidebarCollapsed: collapsed });
			},
		}),
		{
			name: 'shell-storage',
		}
	)
);
