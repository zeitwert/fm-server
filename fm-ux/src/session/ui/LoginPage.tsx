import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Form, Input, Typography } from 'antd';
import { useSessionStore } from '../model/sessionStore';
import { SessionState } from '../model/types';

const { Title } = Typography;

interface LoginFormValues {
	email: string;
	password: string;
}

export function LoginPage() {

	const [form] = Form.useForm<LoginFormValues>();
	const { state, error, login, clearError } = useSessionStore();
	const isLoading = state === SessionState.pendingAuth;

	const handleSubmit = async (values: LoginFormValues) => {
		clearError();
		await login(values.email, values.password);
	};

	return (
		<div
			style={{
				minHeight: '100vh',
				display: 'flex',
				justifyContent: 'center',
				alignItems: 'center',
				background: 'white',
				padding: '20px',
			}}
		>
			<Card
				style={{
					width: '100%',
					maxWidth: 400,
					boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)',
				}}
			>
				<div style={{ textAlign: 'center', marginBottom: 32 }}>
					<Title level={2} style={{ marginBottom: 8 }}>
						Welcome
					</Title>
					<Typography.Text type="secondary">Sign in to continue</Typography.Text>
				</div>

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

				<Form form={form} layout="vertical" onFinish={handleSubmit} autoComplete="off">
					<Form.Item
						name="email"
						rules={[
							{ required: true, message: 'Please enter your email' },
							{ type: 'email', message: 'Please enter a valid email' },
						]}
					>
						<Input prefix={<UserOutlined />} placeholder="Email" size="large" autoFocus />
					</Form.Item>

					<Form.Item
						name="password"
						rules={[{ required: true, message: 'Please enter your password' }]}
					>
						<Input.Password prefix={<LockOutlined />} placeholder="Password" size="large" />
					</Form.Item>

					<Form.Item style={{ marginBottom: 0 }}>
						<Button type="primary" htmlType="submit" size="large" block loading={isLoading}>
							Sign In
						</Button>
					</Form.Item>
				</Form>
			</Card>
		</div>
	);

}
