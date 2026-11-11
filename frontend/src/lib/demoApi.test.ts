import { beforeEach, describe, expect, it } from 'vitest';
import { dispatchDemoRequest } from './demoApi';
import { DEMO_STORAGE_KEY, resetDemoData, type DemoInventoryItem, type DemoOrder, type DemoProduct, type DemoSale } from './demoStore';

const stored = new Map<string, string>();
const storage: Storage = {
  get length() { return stored.size; },
  clear: () => stored.clear(),
  getItem: key => stored.get(key) ?? null,
  key: index => [...stored.keys()][index] ?? null,
  removeItem: key => stored.delete(key),
  setItem: (key, value) => { stored.set(key, String(value)); },
};

const request = (method: string, url: string, body?: unknown) => dispatchDemoRequest(method, url, body);

beforeEach(() => {
  stored.clear();
  Object.defineProperty(globalThis, 'localStorage', { configurable: true, value: storage });
});

describe('demo API', () => {
  it('seeds the catalog and persists product creation, edits and deletion', () => {
    const initial = request('GET', '/products') as DemoProduct[];
    expect(initial).toHaveLength(4);

    const created = request('POST', '/products', {
      name: 'Arroz con leche',
      description: 'Postre de prueba',
      price: 9.5,
      categoryId: 4,
      available: true,
    }) as DemoProduct;
    expect(created.id).toBe(5);

    request('PUT', `/products/${created.id}`, { ...created, name: 'Arroz con leche demo', price: 10 });
    const updated = (request('GET', '/products') as DemoProduct[]).find(product => product.id === created.id);
    expect(updated).toMatchObject({ name: 'Arroz con leche demo', price: 10 });
    expect(localStorage.getItem(DEMO_STORAGE_KEY)).toContain('Arroz con leche demo');

    request('DELETE', `/products/${created.id}`);
    expect((request('GET', '/products') as DemoProduct[]).find(product => product.id === created.id)?.available).toBe(false);
  });

  it('creates an order, updates its table and stock, records a fictitious sale, and recalculates reports', () => {
    const order = request('POST', '/orders', {
      tableId: 1,
      items: [{ productId: 1, quantity: 2 }],
    }) as DemoOrder;
    expect(order.total).toBe(57);
    expect((request('GET', '/tables') as { id: number; status: string }[]).find(table => table.id === 1)?.status).toBe('OCCUPIED');

    request('PATCH', `/orders/${order.id}/status`, { status: 'PREPARING' });
    request('PATCH', `/orders/${order.id}/status`, { status: 'READY' });
    request('PATCH', `/orders/${order.id}/status`, { status: 'DELIVERED' });
    expect((request('GET', '/tables') as { id: number; status: string }[]).find(table => table.id === 1)?.status).toBe('AVAILABLE');
    expect((request('GET', '/inventory') as DemoInventoryItem[]).find(item => item.id === 1)?.quantity).toBe(11.64);

    const sale = request('POST', `/sales/orders/${order.id}/checkout`, { paymentMethod: 'CASH' }) as DemoSale;
    expect(sale).toMatchObject({ orderId: order.id, total: 57, status: 'COMPLETED', cashier: 'Usuario DEMO' });
    expect((request('GET', '/sales') as DemoSale[])).toContainEqual(sale);

    const report = request('GET', '/reports/summary?period=DAILY') as { revenue: number; completedSales: number };
    expect(report.revenue).toBe(122.5);
    expect(report.completedSales).toBe(2);
    expect(request('GET', '/dashboard/metrics')).toMatchObject({ occupiedTables: 2, activeProducts: 4 });

    request('POST', `/sales/${sale.id}/void`, { reason: 'Prueba de anulación' });
    expect((request('GET', '/reports/summary?period=DAILY') as { revenue: number }).revenue).toBe(65.5);
  });

  it('lets demo users update table status, persist changes, and reset the seed data', () => {
    request('PUT', '/tables/2', { tableNumber: 'Mesa 2', capacity: 4, status: 'OCCUPIED' });
    expect((request('GET', '/tables') as { id: number; status: string }[]).find(table => table.id === 2)?.status).toBe('OCCUPIED');

    resetDemoData();
    expect((request('GET', '/tables') as { id: number; status: string }[]).find(table => table.id === 2)?.status).toBe('AVAILABLE');
    expect((request('GET', '/products') as DemoProduct[])).toHaveLength(4);
  });

  it('records inventory movements and exposes the updated stock and movement history', () => {
    const item = request('POST', '/inventory', {
      name: 'Aceite de prueba',
      unit: 'L',
      minimumQuantity: 1,
    }) as DemoInventoryItem;
    request('POST', `/inventory/${item.id}/movements`, { movementType: 'IN', quantity: 5, note: 'Carga de prueba' });
    request('POST', `/inventory/${item.id}/movements`, { movementType: 'OUT', quantity: 2, note: 'Uso de prueba' });

    expect((request('GET', '/inventory') as DemoInventoryItem[]).find(row => row.id === item.id)?.quantity).toBe(3);
    expect(request('GET', `/inventory/${item.id}/movements`)).toHaveLength(2);
  });

  it('blocks account and employee operations in demo mode', () => {
    expect(() => request('GET', '/users')).toThrow('deshabilitado en el modo DEMO');
    expect(() => request('POST', '/employees', { fullName: 'No permitido' })).toThrow('deshabilitado en el modo DEMO');
  });
});
