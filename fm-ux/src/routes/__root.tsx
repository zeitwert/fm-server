import { createRootRoute, Outlet } from '@tanstack/react-router';
import { ConfigProvider, Layout, Spin, Typography } from 'antd';
import { useEffect } from 'react';
import { useSessionStore } from '../session/model/sessionStore';
import { SessionState } from '../session/model/types';
import { LoginPage } from '../session/ui/LoginPage';
import { SelectionWizard } from '../session/ui/SelectionWizard';

const { Content } = Layout;
const { Title, Text } = Typography;

export const Route = createRootRoute({
  component: RootComponent,
});

function RootComponent() {
  const { state, initSession, needsTenantSelection, needsAccountSelection } = useSessionStore();

  // Initialize session on mount
  useEffect(() => {
    initSession();
  }, [initSession]);

  // Show login page when not authenticated
  if (state === SessionState.close) {
    return (
      <ConfigProvider>
        <LoginPage />
      </ConfigProvider>
    );
  }

  // Show loading during authentication
  if (state === SessionState.pendingAuth) {
    return (
      <ConfigProvider>
        <div
          style={{
            minHeight: '100vh',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          }}
        >
          <Spin size="large" />
        </div>
      </ConfigProvider>
    );
  }

  // Show tenant/account selection wizard
  if (state === SessionState.authenticated && (needsTenantSelection() || needsAccountSelection())) {
    return (
      <ConfigProvider>
        <SelectionWizard />
      </ConfigProvider>
    );
  }

  // Show loading during session initialization
  if (state === SessionState.pendingOpen) {
    return (
      <ConfigProvider>
        <div
          style={{
            minHeight: '100vh',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          }}
        >
          <Spin size="large" />
        </div>
      </ConfigProvider>
    );
  }

  // Show main application when session is open
  return (
    <ConfigProvider>
      <Layout style={{ minHeight: '100vh' }}>
        <Content
          style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            flexDirection: 'column',
            padding: '50px',
          }}
        >
          <Title>Hello World</Title>
          <Text type="secondary">FM-UX is running successfully!</Text>
          <Outlet />
        </Content>
      </Layout>
    </ConfigProvider>
  );
}
