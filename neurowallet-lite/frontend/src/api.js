import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth APIs
export const register = (email, password) => {
  return api.post('/auth/register', { email, password });
};

export const login = (email, password) => {
  return api.post('/auth/login', { email, password });
};

// Wallet APIs
export const getWallets = () => {
  return api.get('/wallets');
};

export const createWallet = (currency) => {
  return api.post('/wallets', { currency });
};

export const updateWallet = (id, amount) => {
  return api.put(`/wallets/${id}`, { amount });
};

export const deleteWallet = (id) => {
  return api.delete(`/wallets/${id}`);
};

// Transaction APIs
export const getTransactions = (params = {}) => {
  return api.get('/transactions', { params });
};

export const createTransaction = (data) => {
  return api.post('/transactions', data);
};

// Admin APIs
export const getAllUsers = () => {
  return api.get('/admin/users');
};

export const freezeWallet = (id) => {
  return api.put(`/admin/wallets/${id}/freeze`);
};

export const unfreezeWallet = (id) => {
  return api.put(`/admin/wallets/${id}/unfreeze`);
};

export const getAllTransactions = () => {
  return api.get('/admin/transactions');
};

// File Upload APIs
export const uploadFile = (file) => {
  const formData = new FormData();
  formData.append('file', file);
  return api.post('/files/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const getFile = (filename) => {
  return api.get(`/files/${filename}`, {
    responseType: 'blob',
  });
};

export default api;