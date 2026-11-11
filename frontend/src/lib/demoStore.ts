export const DEMO_STORAGE_KEY = 'restaurant-demo-data-v1';
export const DEMO_MODE_KEY = 'restaurant-demo-mode';

export type DemoCategory = { id: number; name: string; description: string; active: boolean };
export type DemoProduct = {
  id: number;
  name: string;
  description: string;
  price: number;
  categoryId: number;
  categoryName: string;
  imageUrl: string | null;
  available: boolean;
};
export type DemoTable = { id: number; tableNumber: string; capacity: number; status: 'AVAILABLE' | 'OCCUPIED' | 'RESERVED' };
export type DemoInventoryItem = { id: number; name: string; unit: string; quantity: number; minimumQuantity: number };
export type DemoMovement = { id: number; type: 'IN' | 'OUT'; quantity: number; note: string; user: string; createdAt: string };
export type DemoOrderItem = { productId: number; productName: string; quantity: number; unitPrice: number; subtotal: number };
export type DemoOrder = {
  id: number;
  tableId: number | null;
  tableName: string | null;
  status: 'PENDING' | 'PREPARING' | 'READY' | 'DELIVERED' | 'CANCELLED';
  total: number;
  createdAt: string;
  items: DemoOrderItem[];
};
export type DemoSale = {
  id: number;
  orderId: number;
  paymentMethod: 'CASH' | 'CARD' | 'TRANSFER' | 'OTHER';
  total: number;
  status: 'COMPLETED' | 'VOIDED';
  cashier: string;
  voidReason?: string;
  voidedAt?: string;
  createdAt: string;
};
export type DemoRecipeIngredient = { inventoryItemId: number; quantityPerProduct: number };

export type DemoData = {
  version: 1;
  categories: DemoCategory[];
  products: DemoProduct[];
  tables: DemoTable[];
  inventory: DemoInventoryItem[];
  movements: Record<number, DemoMovement[]>;
  recipes: Record<number, DemoRecipeIngredient[]>;
  orders: DemoOrder[];
  sales: DemoSale[];
};

const daysAgo = (days: number, hours = 12) => {
  const date = new Date();
  date.setDate(date.getDate() - days);
  date.setHours(hours, 0, 0, 0);
  return date.toISOString();
};

export function createInitialDemoData(): DemoData {
  const categories: DemoCategory[] = [
    { id: 1, name: 'Entradas', description: 'Para comenzar', active: true },
    { id: 2, name: 'Fondos', description: 'Platos principales', active: true },
    { id: 3, name: 'Bebidas', description: 'Bebidas de la casa', active: true },
    { id: 4, name: 'Postres', description: 'Dulces para compartir', active: true },
  ];
  const products: DemoProduct[] = [
    { id: 1, name: 'Lomo saltado', description: 'Lomo, cebolla, tomate y papas doradas', price: 28.5, categoryId: 2, categoryName: 'Fondos', imageUrl: null, available: true },
    { id: 2, name: 'Causa limeña', description: 'Papa amarilla, pollo y palta', price: 14.5, categoryId: 1, categoryName: 'Entradas', imageUrl: null, available: true },
    { id: 3, name: 'Chicha morada', description: 'Refresco de maíz morado', price: 8.5, categoryId: 3, categoryName: 'Bebidas', imageUrl: null, available: true },
    { id: 4, name: 'Cheesecake de maracuyá', description: 'Porción con salsa de maracuyá', price: 13, categoryId: 4, categoryName: 'Postres', imageUrl: null, available: true },
  ];
  const tables: DemoTable[] = [
    { id: 1, tableNumber: 'Mesa 1', capacity: 2, status: 'AVAILABLE' },
    { id: 2, tableNumber: 'Mesa 2', capacity: 4, status: 'AVAILABLE' },
    { id: 3, tableNumber: 'Mesa 3', capacity: 4, status: 'AVAILABLE' },
    { id: 4, tableNumber: 'Mesa 4', capacity: 6, status: 'OCCUPIED' },
    { id: 5, tableNumber: 'Mesa 5', capacity: 4, status: 'OCCUPIED' },
    { id: 6, tableNumber: 'Mesa 6', capacity: 2, status: 'RESERVED' },
  ];
  const inventory: DemoInventoryItem[] = [
    { id: 1, name: 'Carne de res', unit: 'kg', quantity: 12, minimumQuantity: 3 },
    { id: 2, name: 'Papa amarilla', unit: 'kg', quantity: 18, minimumQuantity: 5 },
    { id: 3, name: 'Cebolla roja', unit: 'kg', quantity: 1.5, minimumQuantity: 2 },
    { id: 4, name: 'Maíz morado', unit: 'kg', quantity: 7, minimumQuantity: 2 },
  ];
  const orders: DemoOrder[] = [
    { id: 1, tableId: 2, tableName: 'Mesa 2', status: 'DELIVERED', total: 65.5, createdAt: daysAgo(0, 11), items: [{ productId: 1, productName: 'Lomo saltado', quantity: 2, unitPrice: 28.5, subtotal: 57 }, { productId: 3, productName: 'Chicha morada', quantity: 1, unitPrice: 8.5, subtotal: 8.5 }] },
    { id: 2, tableId: null, tableName: null, status: 'DELIVERED', total: 29, createdAt: daysAgo(1, 15), items: [{ productId: 2, productName: 'Causa limeña', quantity: 2, unitPrice: 14.5, subtotal: 29 }] },
    { id: 3, tableId: 3, tableName: 'Mesa 3', status: 'DELIVERED', total: 27.5, createdAt: daysAgo(3, 13), items: [{ productId: 2, productName: 'Causa limeña', quantity: 1, unitPrice: 14.5, subtotal: 14.5 }, { productId: 4, productName: 'Cheesecake de maracuyá', quantity: 1, unitPrice: 13, subtotal: 13 }] },
    { id: 4, tableId: 1, tableName: 'Mesa 1', status: 'DELIVERED', total: 28.5, createdAt: daysAgo(0, 10), items: [{ productId: 1, productName: 'Lomo saltado', quantity: 1, unitPrice: 28.5, subtotal: 28.5 }] },
    { id: 5, tableId: 4, tableName: 'Mesa 4', status: 'PREPARING', total: 45.5, createdAt: daysAgo(0, 12), items: [{ productId: 1, productName: 'Lomo saltado', quantity: 1, unitPrice: 28.5, subtotal: 28.5 }, { productId: 3, productName: 'Chicha morada', quantity: 2, unitPrice: 8.5, subtotal: 17 }] },
    { id: 6, tableId: 5, tableName: 'Mesa 5', status: 'READY', total: 13, createdAt: daysAgo(0, 13), items: [{ productId: 4, productName: 'Cheesecake de maracuyá', quantity: 1, unitPrice: 13, subtotal: 13 }] },
  ];
  const sales: DemoSale[] = [
    { id: 1, orderId: 1, paymentMethod: 'CASH', total: 65.5, status: 'COMPLETED', cashier: 'Usuario DEMO', createdAt: daysAgo(0, 11) },
    { id: 2, orderId: 2, paymentMethod: 'TRANSFER', total: 29, status: 'COMPLETED', cashier: 'Usuario DEMO', createdAt: daysAgo(1, 15) },
    { id: 3, orderId: 3, paymentMethod: 'CASH', total: 27.5, status: 'COMPLETED', cashier: 'Usuario DEMO', createdAt: daysAgo(3, 13) },
  ];
  return {
    version: 1,
    categories,
    products,
    tables,
    inventory,
    movements: {
      1: [{ id: 1, type: 'IN', quantity: 12, note: 'Carga inicial demostrativa', user: 'Usuario DEMO', createdAt: daysAgo(4) }],
      2: [{ id: 2, type: 'IN', quantity: 18, note: 'Carga inicial demostrativa', user: 'Usuario DEMO', createdAt: daysAgo(4) }],
      3: [{ id: 3, type: 'IN', quantity: 1.5, note: 'Carga inicial demostrativa', user: 'Usuario DEMO', createdAt: daysAgo(4) }],
      4: [{ id: 4, type: 'IN', quantity: 7, note: 'Carga inicial demostrativa', user: 'Usuario DEMO', createdAt: daysAgo(4) }],
    },
    recipes: {
      1: [{ inventoryItemId: 1, quantityPerProduct: 0.18 }, { inventoryItemId: 2, quantityPerProduct: 0.2 }, { inventoryItemId: 3, quantityPerProduct: 0.1 }],
    },
    orders,
    sales,
  };
}

function isDemoData(value: unknown): value is DemoData {
  if (!value || typeof value !== 'object') return false;
  const data = value as Partial<DemoData>;
  return data.version === 1
    && Array.isArray(data.categories)
    && Array.isArray(data.products)
    && Array.isArray(data.tables)
    && Array.isArray(data.inventory)
    && Array.isArray(data.orders)
    && Array.isArray(data.sales)
    && typeof data.movements === 'object'
    && typeof data.recipes === 'object';
}

export function getDemoData(): DemoData {
  const stored = localStorage.getItem(DEMO_STORAGE_KEY);
  if (stored === null) {
    const initialData = createInitialDemoData();
    saveDemoData(initialData);
    return initialData;
  }
  let parsed: unknown;
  try {
    parsed = JSON.parse(stored);
  } catch {
    throw new Error('Los datos DEMO guardados no se pueden leer. Reinicia los datos de demostración.');
  }
  if (!isDemoData(parsed)) {
    throw new Error('Los datos DEMO guardados tienen un formato no compatible. Reinicia los datos de demostración.');
  }
  return parsed;
}

export function saveDemoData(data: DemoData): void {
  localStorage.setItem(DEMO_STORAGE_KEY, JSON.stringify(data));
}

export function updateDemoData<T>(update: (data: DemoData) => T): T {
  const data = getDemoData();
  const result = update(data);
  saveDemoData(data);
  return result;
}

export function resetDemoData(): void {
  saveDemoData(createInitialDemoData());
}
