import axios, { AxiosError, AxiosHeaders, type AxiosAdapter } from 'axios';
import { dispatchDemoRequest, DemoApiError } from './demoApi';
import { DEMO_MODE_KEY } from './demoStore';

export const isDemoBuild = () => import.meta.env.VITE_DEMO_MODE === 'true';
export const isDemoMode = () => isDemoBuild() || localStorage.getItem(DEMO_MODE_KEY) === 'true';

const networkAdapter = axios.getAdapter(axios.defaults.adapter);
const adapter: AxiosAdapter = async (config) => {
  if (!isDemoMode()) return networkAdapter(config);
  try {
    return {
      data: dispatchDemoRequest(config.method, config.url, config.data, config.params),
      status: 200,
      statusText: 'OK',
      headers: new AxiosHeaders(),
      config,
    };
  } catch (error) {
    if (!(error instanceof DemoApiError)) throw error;
    const response = {
      data: { message: error.message },
      status: error.status,
      statusText: 'Demo error',
      headers: new AxiosHeaders(),
      config,
    };
    throw new AxiosError(error.message, AxiosError.ERR_BAD_REQUEST, config, undefined, response);
  }
};

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api',
  adapter,
});
api.interceptors.request.use((config) => {
  const token = isDemoMode() ? null : localStorage.getItem('restaurant-token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export type ApiEntity = { id: number; name: string; description?: string; status?: string; price?: number; stock?: number; createdAt?: string };
