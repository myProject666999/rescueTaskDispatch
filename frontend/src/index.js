import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/lib/locale/zh_CN';
import 'antd/dist/antd.css';
import App from './App';
import './index.css';

if (process.env.NODE_ENV === 'development') {
  const suppressWarnings = [
    'Support for defaultProps will be removed',
  ];

  const originalWarn = console.warn;
  const originalError = console.error;

  const shouldSuppress = (args) => {
    return suppressWarnings.some((keyword) =>
      args.some((arg) => typeof arg === 'string' && arg.includes(keyword))
    );
  };

  console.warn = function filteredWarn(...args) {
    if (shouldSuppress(args)) return;
    originalWarn.apply(console, args);
  };

  console.error = function filteredError(...args) {
    if (shouldSuppress(args)) return;
    originalError.apply(console, args);
  };
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <ConfigProvider locale={zhCN}>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </ConfigProvider>
);
