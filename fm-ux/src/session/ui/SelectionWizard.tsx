import { ArrowLeftOutlined, BankOutlined, TeamOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Col, Row, Spin, Typography } from 'antd';
import { useSessionStore } from '../model/sessionStore';
import { Enumerated, TypedEnumerated } from '../model/types';

const { Title, Text } = Typography;

export function SelectionWizard() {
  const {
    userInfo,
    selectedTenant,
    tenantInfo,
    selectedAccount,
    error,
    selectTenant,
    selectAccount,
    completeLogin,
    clearError,
    logout,
  } = useSessionStore();

  // Determine current step
  const showTenantSelection = !selectedTenant && (userInfo?.tenants?.length ?? 0) > 0;
  const showAccountSelection =
    selectedTenant && (tenantInfo?.accounts?.length ?? 0) > 1 && !selectedAccount;
  const isLoading = selectedTenant && !tenantInfo && !showAccountSelection;

  const handleTenantSelect = async (tenant: TypedEnumerated) => {
    await selectTenant(tenant);
  };

  const handleAccountSelect = async (account: Enumerated) => {
    selectAccount(account);
    await completeLogin();
  };

  const handleBack = () => {
    // Reset to tenant selection by logging out and back in
    // In a more sophisticated implementation, we'd reset just the tenant selection
    logout();
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        padding: '20px',
      }}
    >
      <Card
        style={{
          width: '100%',
          maxWidth: 600,
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
        }}
      >
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <Title level={3} style={{ marginBottom: 8 }}>
            {showTenantSelection && 'Select Tenant'}
            {showAccountSelection && 'Select Account'}
            {isLoading && 'Loading...'}
          </Title>
          <Text type="secondary">
            {showTenantSelection && 'Choose the tenant you want to work with'}
            {showAccountSelection && 'Choose the account you want to access'}
          </Text>
        </div>

        {/* Error display */}
        {error && (
          <Alert
            message={error}
            type="error"
            showIcon
            closable
            onClose={clearError}
            style={{ marginBottom: 24 }}
          />
        )}

        {/* Loading state */}
        {isLoading && (
          <div style={{ textAlign: 'center', padding: '40px 0' }}>
            <Spin size="large" />
          </div>
        )}

        {/* Tenant Selection */}
        {showTenantSelection && (
          <Row gutter={[16, 16]}>
            {userInfo?.tenants.map((tenant) => (
              <Col xs={24} sm={12} key={tenant.id}>
                <Card
                  hoverable
                  onClick={() => handleTenantSelect(tenant)}
                  style={{
                    textAlign: 'center',
                    cursor: 'pointer',
                    border: '2px solid transparent',
                    transition: 'all 0.3s',
                  }}
                  bodyStyle={{ padding: '24px 16px' }}
                >
                  <TeamOutlined style={{ fontSize: 32, color: '#667eea', marginBottom: 12 }} />
                  <div>
                    <Text strong style={{ fontSize: 16 }}>
                      {tenant.name}
                    </Text>
                  </div>
                </Card>
              </Col>
            ))}
          </Row>
        )}

        {/* Account Selection */}
        {showAccountSelection && (
          <>
            <Row gutter={[16, 16]}>
              {tenantInfo?.accounts.map((account) => (
                <Col xs={24} sm={12} key={account.id}>
                  <Card
                    hoverable
                    onClick={() => handleAccountSelect(account)}
                    style={{
                      textAlign: 'center',
                      cursor: 'pointer',
                      border: '2px solid transparent',
                      transition: 'all 0.3s',
                    }}
                    bodyStyle={{ padding: '24px 16px' }}
                  >
                    <BankOutlined style={{ fontSize: 32, color: '#764ba2', marginBottom: 12 }} />
                    <div>
                      <Text strong style={{ fontSize: 16 }}>
                        {account.name}
                      </Text>
                    </div>
                  </Card>
                </Col>
              ))}
            </Row>

            {/* Back button */}
            <div style={{ marginTop: 24, textAlign: 'center' }}>
              <Button icon={<ArrowLeftOutlined />} onClick={handleBack}>
                Back to Login
              </Button>
            </div>
          </>
        )}

        {/* Logout option */}
        {showTenantSelection && (
          <div style={{ marginTop: 24, textAlign: 'center' }}>
            <Button type="link" onClick={logout}>
              Sign in with a different account
            </Button>
          </div>
        )}
      </Card>
    </div>
  );
}
