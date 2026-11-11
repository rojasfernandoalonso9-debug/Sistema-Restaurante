import axios from 'axios';

export const api = axios.create({ baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api' });
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('restaurant-token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export type ApiEntity = { id: number; name: string; description?: string; status?: string; price?: number; stock?: number; createdAt?: string };
