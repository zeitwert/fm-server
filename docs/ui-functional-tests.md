# UI Functional Tests

This document explains how the functional testing infrastructure works for the `fm-ux` React application.

## Overview

The testing stack consists of three main technologies that work together:

```
┌─────────────────────────────────────────────────────────────────┐
│                         Your Test                               │
│  (describes user behavior: click, type, assert on screen)       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                  React Testing Library                          │
│  (renders components, provides queries like findByText)         │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Your React App                               │
│  (components make API calls via axios/fetch)                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   MSW (Mock Service Worker)                     │
│  (intercepts network requests, returns mock responses)          │
└─────────────────────────────────────────────────────────────────┘
```

## React Testing Library

**Philosophy**: Test your app the way users use it - by interacting with what's on screen, not implementation details.

### How It Works

React Testing Library renders your components into a virtual DOM (jsdom) and provides utilities to:

1. **Query the DOM** - Find elements the way a user would (by text, role, label)
2. **Simulate interactions** - Click, type, select (via `@testing-library/user-event`)
3. **Assert on results** - Check what's visible on screen

### Key Concepts

#### Queries

There are three types of queries, each with different behavior:

| Query Type | Returns | Throws if not found? | Waits? | Use When |
|------------|---------|---------------------|--------|----------|
| `getBy*` | Element | Yes | No | Element is already in DOM |
| `queryBy*` | Element or null | No | No | Checking element does NOT exist |
| `findBy*` | Promise<Element> | Yes (after timeout) | Yes | Element will appear after async operation |

```typescript
// Element is already there
const button = screen.getByRole('button', { name: /submit/i });

// Element might not exist (checking absence)
const error = screen.queryByText('Error message');
expect(error).not.toBeInTheDocument();

// Element will appear after API call
const welcomeText = await screen.findByText('Welcome', {}, { timeout: 5000 });
```

#### Query Priority

React Testing Library recommends queries in this order (most to least preferred):

1. **`getByRole`** - Accessible role (button, textbox, heading)
2. **`getByLabelText`** - Form fields with labels
3. **`getByPlaceholderText`** - Input placeholders
4. **`getByText`** - Visible text content
5. **`getByTestId`** - Last resort, `data-testid` attribute

```typescript
// Good - queries by accessibility role
screen.getByRole('button', { name: /sign in/i });
screen.getByRole('textbox', { name: /email/i });

// OK - queries by placeholder
screen.getByPlaceholderText('Email');

// Avoid if possible - relies on test-specific attribute
screen.getByTestId('submit-button');
```

#### User Interactions

Use `@testing-library/user-event` for realistic interactions:

```typescript
import userEvent from '@testing-library/user-event';

const user = userEvent.setup();

// Type into an input (fires all keyboard events)
await user.type(emailInput, 'test@example.com');

// Click a button
await user.click(submitButton);

// Clear and type
await user.clear(input);
await user.type(input, 'new value');

// Select from dropdown
await user.selectOptions(select, 'option-value');
```

#### Waiting for Async Operations

Use `waitFor` when you need to wait for state changes:

```typescript
import { waitFor } from '@testing-library/react';

// Wait for element to appear
await waitFor(() => {
  expect(screen.getByText('Success')).toBeInTheDocument();
});

// Wait for store state to change
await waitFor(() => {
  expect(useSessionStore.getState().state).toBe('open');
}, { timeout: 10000 });

// Or use findBy* which combines getBy* + waitFor
const element = await screen.findByText('Success', {}, { timeout: 10000 });
```

## MSW (Mock Service Worker)

**Purpose**: Intercept network requests at the network level and return mock responses. Your app code doesn't know it's being mocked.

### How It Works

1. MSW sets up request handlers that match URLs and HTTP methods
2. When your app makes a request (via axios, fetch, etc.), MSW intercepts it
3. MSW returns the mock response you defined
4. Your app processes it as if it came from a real server

```
App calls: POST /rest/session/authenticate
                    │
                    ▼
         MSW intercepts request
                    │
                    ▼
         Matches handler for POST /rest/session/authenticate
                    │
                    ▼
         Returns mock response: { id: '1', name: 'Test User', ... }
                    │
                    ▼
         App receives response and updates state
```

### Defining Handlers

Handlers are defined in `fm-ux/src/test/mocks/handlers.ts`:

```typescript
import { http, HttpResponse } from 'msw';

export const handlers = [
  // Match POST requests to /rest/session/authenticate
  http.post('/rest/session/authenticate', async ({ request }) => {
    // You can inspect the request body
    const body = await request.json();
    
    // Return different responses based on input
    if (body.password === 'invalid') {
      return HttpResponse.json(
        { errors: [{ detail: 'Invalid credentials' }] },
        { status: 401 }
      );
    }
    
    // Return success response
    return HttpResponse.json({
      id: '1',
      name: 'Test User',
      email: 'test@example.com',
      tenants: [{ id: '100', name: 'Test Tenant' }]
    });
  }),

  // Match GET requests with URL parameters
  http.get('/rest/app/tenantInfo/:tenantId', ({ params }) => {
    const { tenantId } = params;
    return HttpResponse.json({
      id: tenantId,
      accounts: [{ id: '1000', name: 'Default Account' }]
    });
  }),
];
```

### Server Lifecycle

The MSW server is set up in `fm-ux/src/test/setup.ts`:

```typescript
import { server } from './mocks/server';

// Start server before all tests
beforeAll(() => {
  server.listen({ onUnhandledRequest: 'error' });
});

// Reset handlers after each test (removes test-specific overrides)
afterEach(() => {
  server.resetHandlers();
});

// Close server after all tests
afterAll(() => {
  server.close();
});
```

### Overriding Handlers Per Test

You can override the default handlers for specific tests:

```typescript
import { server } from '../../test/mocks/server';
import { http, HttpResponse } from 'msw';

describe('error scenarios', () => {
  it('should handle server error', async () => {
    // Override just for this test
    server.use(
      http.post('/rest/session/authenticate', () => {
        return HttpResponse.json(
          { errors: [{ detail: 'Server error' }] },
          { status: 500 }
        );
      })
    );

    // ... test code that expects error handling
  });
  
  // After this test, server.resetHandlers() restores defaults
});
```

## Putting It Together

Here's how a complete test flows:

```typescript
import { describe, it, expect, beforeEach } from 'vitest';
import { renderApp, screen, waitFor } from '../../test/utils';
import { useSessionStore } from '../model/sessionStore';
import { SessionState } from '../model/types';

describe('Login', () => {
  beforeEach(() => {
    // Reset Zustand store state before each test
    useSessionStore.setState({
      state: SessionState.close,
      error: null,
      sessionInfo: null,
    });
  });

  it('should login successfully', async () => {
    // 1. Render the app at /login route
    const { user } = renderApp({ initialPath: '/login' });

    // 2. Wait for login form to appear (async because router needs to resolve)
    const emailInput = await screen.findByPlaceholderText('Email');
    const passwordInput = screen.getByPlaceholderText('Password');

    // 3. Simulate user typing credentials
    await user.type(emailInput, 'test@example.com');
    await user.type(passwordInput, 'password123');

    // 4. Click submit button
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    // 5. At this point:
    //    - App calls POST /rest/session/authenticate
    //    - MSW intercepts and returns mock user data
    //    - App updates session store
    //    - App calls POST /rest/session/activate
    //    - MSW intercepts and returns mock session
    //    - App updates store to 'open' state

    // 6. Assert on final state
    await waitFor(() => {
      expect(useSessionStore.getState().state).toBe(SessionState.open);
    }, { timeout: 10000 });
  });
});
```

## File Structure

```
fm-ux/src/test/
├── setup.ts              # Global setup
│   - Imports jest-dom matchers (toBeInTheDocument, etc.)
│   - Starts/stops MSW server
│   - Mocks browser APIs (ResizeObserver, matchMedia)
│   - Clears sessionStorage between tests
│
├── utils.tsx             # Custom render function
│   - Creates fresh QueryClient per test
│   - Sets up TanStack Router with memory history
│   - Returns user-event instance for interactions
│
└── mocks/
    ├── server.ts         # MSW server instance
    ├── handlers.ts       # Default API handlers
    └── fixtures.ts       # Reusable test data
```

## Common Patterns

### Testing Different User Scenarios

```typescript
// fixtures.ts - Define different user types
export const adminUser = { id: '1', role: 'admin', ... };
export const regularUser = { id: '2', role: 'user', ... };

// test file - Override handler to return specific user
it('admin can see admin panel', async () => {
  server.use(
    http.post('/rest/session/authenticate', () => 
      HttpResponse.json(adminUser)
    )
  );
  // ... test admin-specific features
});
```

### Testing Loading States

```typescript
it('shows loading spinner during login', async () => {
  const { user } = renderApp({ initialPath: '/login' });
  
  await user.type(await screen.findByPlaceholderText('Email'), 'test@example.com');
  await user.type(screen.getByPlaceholderText('Password'), 'pass');
  await user.click(screen.getByRole('button', { name: /sign in/i }));
  
  // Check loading state appears
  expect(screen.getByRole('button', { name: /sign in/i })).toBeDisabled();
  
  // Wait for completion
  await waitFor(() => {
    expect(useSessionStore.getState().state).toBe('open');
  });
});
```

### Testing Error Display

```typescript
it('displays error message on failed login', async () => {
  server.use(
    http.post('/rest/session/authenticate', () =>
      HttpResponse.json({ errors: [{ detail: 'Wrong password' }] }, { status: 401 })
    )
  );

  const { user } = renderApp({ initialPath: '/login' });
  
  await user.type(await screen.findByPlaceholderText('Email'), 'test@example.com');
  await user.type(screen.getByPlaceholderText('Password'), 'wrong');
  await user.click(screen.getByRole('button', { name: /sign in/i }));
  
  // Error should appear in the UI
  await screen.findByText(/wrong password/i);
});
```

## Debugging Tips

### See what's rendered

```typescript
import { screen } from '@testing-library/react';

// Print the current DOM
screen.debug();

// Print a specific element
screen.debug(screen.getByRole('form'));
```

### Check available roles

```typescript
// This will throw an error listing all available roles
screen.getByRole('banana');
// Error: Unable to find an accessible element with the role "banana"
// Here are the accessible roles:
//   button: [<button />, <button />]
//   textbox: [<input />]
//   ...
```

### Slow down tests for observation

```typescript
it('slow test', async () => {
  const { user } = renderApp({ initialPath: '/login' });
  
  await user.type(await screen.findByPlaceholderText('Email'), 'test@example.com');
  
  // Add a delay to observe state
  await new Promise(r => setTimeout(r, 2000));
  
  // Continue...
});
```
