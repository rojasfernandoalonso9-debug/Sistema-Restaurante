import {
  getDemoData,
  updateDemoData,
  type DemoCategory,
  type DemoInventoryItem,
  type DemoMovement,
  type DemoOrder,
  type DemoProduct,
  type DemoSale,
  type DemoTable,
} from './demoStore';

export class DemoApiError extends Error {
  constructor(message: string, readonly status: number) {
    super(message);
    this.name = 'DemoApiError';
  }
}

type RequestBody = Record<string, unknown>;

const reject = (message: string, status = 400): never => {
  throw new DemoApiError(message, status);
};
const nextId = (rows: { id: number }[]) => Math.max(0, ...rows.map(row => row.id)) + 1;
const bodyObject = (value: unknown): RequestBody => value && typeof value === 'object' ? value as RequestBody : {};
const positiveNumber = (value: unknown) => Number.isFinite(Number(value)) && Number(value) > 0;
const localDateKey = (date: Date) => {
  const local = new Date(date.getTime() - date.getTimezoneOffset() * 60_000);
  return local.toISOString().slice(0, 10);
};
const startOfDay = (date: Date) => new Date(date.getFullYear(), date.getMonth(), date.getDate());
const addDays = (date: Date, count: number) => {
  const result = new Date(date);
  result.setDate(result.getDate() + count);
  return result;
};
const dateInRange = (value: string, from: string, to: string) => {
  const date = localDateKey(new Date(value));
  return date >= from && date <= to;
};

function makeOrderItems(items: unknown, products: DemoProduct[]) {
  if (!Array.isArray(items) || items.length === 0) return reject('Agrega al menos un producto al pedido.');
  return items.map((value) => {
    const line = bodyObject(value);
    const product = products.find(item => item.id === Number(line.productId) && item.available);
    if (!product) return reject('Uno de los productos ya no está disponible.');
    const quantity = Number(line.quantity);
    if (!Number.isInteger(quantity) || quantity < 1 || quantity > 1000) return reject('La cantidad del producto no es válida.');
    return {
      productId: product.id,
      productName: product.name,
      quantity,
      unitPrice: product.price,
      subtotal: Math.round(product.price * quantity * 100) / 100,
    };
  });
}

function calculateReport(period: string) {
  const data = getDemoData();
  const today = startOfDay(new Date());
  let firstDay = today;
  if (period === 'WEEKLY') {
    const daysSinceMonday = (today.getDay() + 6) % 7;
    firstDay = addDays(today, -daysSinceMonday);
  } else if (period === 'MONTHLY') {
    firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
  } else if (period !== 'DAILY') {
    return reject('El periodo del reporte no es válido.');
  }
  const from = localDateKey(firstDay);
  const to = localDateKey(today);
  const sales = data.sales.filter(sale => sale.status === 'COMPLETED' && dateInRange(sale.createdAt, from, to));
  const revenue = sales.reduce((sum, sale) => sum + sale.total, 0);
  const quantities = new Map<number, { name: string; quantity: number; revenue: number }>();
  sales.forEach(sale => {
    const order = data.orders.find(item => item.id === sale.orderId);
    order?.items.forEach(item => {
      const current = quantities.get(item.productId) ?? { name: item.productName, quantity: 0, revenue: 0 };
      current.quantity += item.quantity;
      current.revenue += item.subtotal;
      quantities.set(item.productId, current);
    });
  });
  return {
    period,
    from,
    to,
    completedSales: sales.length,
    revenue: Math.round(revenue * 100) / 100,
    averageSale: sales.length ? Math.round((revenue / sales.length) * 100) / 100 : 0,
    bestSellingProducts: [...quantities.values()].sort((a, b) => b.quantity - a.quantity),
  };
}

function dashboardMetrics() {
  const data = getDemoData();
  const today = startOfDay(new Date());
  const todayKey = localDateKey(today);
  const weekStart = localDateKey(addDays(today, -6));
  const completedSales = data.sales.filter(sale => sale.status === 'COMPLETED');
  const todaySales = completedSales.filter(sale => localDateKey(new Date(sale.createdAt)) === todayKey);
  const weekSales = completedSales.filter(sale => dateInRange(sale.createdAt, weekStart, todayKey));
  const salesByDay = Array.from({ length: 7 }, (_, index) => {
    const date = addDays(today, index - 6);
    const key = localDateKey(date);
    const sales = completedSales.filter(item => localDateKey(new Date(item.createdAt)) === key);
    return {
      day: date.toLocaleDateString('es-PE', { weekday: 'short' }).replace('.', ''),
      sales: Math.round(sales.reduce((sum, item) => sum + item.total, 0) * 100) / 100,
    };
  });
  const productTotals = new Map<string, { name: string; quantity: number; revenue: number }>();
  weekSales.forEach(sale => {
    const order = data.orders.find(item => item.id === sale.orderId);
    order?.items.forEach(item => {
      const current = productTotals.get(item.productName) ?? { name: item.productName, quantity: 0, revenue: 0 };
      current.quantity += item.quantity;
      current.revenue += item.subtotal;
      productTotals.set(item.productName, current);
    });
  });
  const recentOrders = [...data.orders]
    .sort((a, b) => b.createdAt.localeCompare(a.createdAt))
    .slice(0, 8);
  return {
    salesByDay,
    todaySales: todaySales.reduce((sum, sale) => sum + sale.total, 0),
    weekTotal: weekSales.reduce((sum, sale) => sum + sale.total, 0),
    salesCount: todaySales.length,
    pendingOrders: data.orders.filter(order => ['PENDING', 'PREPARING', 'READY'].includes(order.status)).length,
    occupiedTables: data.tables.filter(table => table.status === 'OCCUPIED').length,
    activeProducts: data.products.filter(product => product.available).length,
    recentOrders,
    bestSellingProducts: [...productTotals.values()].sort((a, b) => b.quantity - a.quantity),
  };
}

function createOrderItems(data: ReturnType<typeof getDemoData>, value: unknown) {
  const items = makeOrderItems(value, data.products);
  return {
    items,
    total: Math.round(items.reduce((sum, item) => sum + item.subtotal, 0) * 100) / 100,
  };
}

function findActiveOrder(data: ReturnType<typeof getDemoData>, tableId: number, exceptOrderId?: number) {
  return data.orders.some(order =>
    order.id !== exceptOrderId
    && order.tableId === tableId
    && ['PENDING', 'PREPARING', 'READY'].includes(order.status),
  );
}

function consumeRecipeStock(data: ReturnType<typeof getDemoData>, order: DemoOrder) {
  const required = new Map<number, number>();
  order.items.forEach(item => {
    (data.recipes[item.productId] ?? []).forEach(ingredient => {
      required.set(
        ingredient.inventoryItemId,
        (required.get(ingredient.inventoryItemId) ?? 0) + ingredient.quantityPerProduct * item.quantity,
      );
    });
  });
  for (const [inventoryId, quantity] of required) {
    const item = data.inventory.find(entry => entry.id === inventoryId);
    if (!item || item.quantity < quantity) return reject(`Stock insuficiente para entregar el pedido: ${item?.name ?? 'insumo'}.`, 409);
  }
  const now = new Date().toISOString();
  for (const [inventoryId, quantity] of required) {
    const item = data.inventory.find(entry => entry.id === inventoryId)!;
    item.quantity = Math.round((item.quantity - quantity) * 1000) / 1000;
    const history = data.movements[inventoryId] ?? [];
    history.push({
      id: nextId(history),
      type: 'OUT',
      quantity: Math.round(quantity * 1000) / 1000,
      note: `Consumo de pedido #${order.id}`,
      user: 'Usuario DEMO',
      createdAt: now,
    });
    data.movements[inventoryId] = history;
  }
}

function finishOrder(data: ReturnType<typeof getDemoData>, order: DemoOrder) {
  if (order.tableId !== null && !findActiveOrder(data, order.tableId, order.id)) {
    const table = data.tables.find(item => item.id === order.tableId);
    if (table) table.status = 'AVAILABLE';
  }
}

export function dispatchDemoRequest(methodValue: string | undefined, urlValue: string | undefined, bodyValue?: unknown, paramsValue?: unknown): unknown {
  const method = (methodValue ?? 'GET').toUpperCase();
  const url = new URL(urlValue ?? '/', 'http://demo.local');
  const path = url.pathname.replace(/^\/api(?=\/|$)/, '') || '/';
  const params = bodyObject(paramsValue);
  const body = typeof bodyValue === 'string' ? bodyObject(JSON.parse(bodyValue)) : bodyObject(bodyValue);

  if (path === '/users' || path.startsWith('/users/') || path === '/employees' || path.startsWith('/employees/')) {
    return reject('El acceso a cuentas y personal está deshabilitado en el modo DEMO.', 403);
  }
  if (method === 'GET' && path === '/dashboard/metrics') return dashboardMetrics();
  if (method === 'GET' && path === '/reports/summary') return calculateReport(String(params.period ?? url.searchParams.get('period') ?? 'DAILY'));

  const data = getDemoData();
  if (path === '/categories') {
    if (method === 'GET') return data.categories;
    if (method === 'POST') {
      return updateDemoData(store => {
        const name = String(body.name ?? '').trim();
        if (!name) return reject('El nombre de la categoría es obligatorio.');
        if (store.categories.some(category => category.name.toLowerCase() === name.toLowerCase())) return reject('Ya existe una categoría con ese nombre.', 409);
        const category: DemoCategory = { id: nextId(store.categories), name, description: String(body.description ?? ''), active: true };
        store.categories.push(category);
        return category;
      });
    }
  }
  const categoryMatch = path.match(/^\/categories\/(\d+)$/);
  if (categoryMatch) {
    const id = Number(categoryMatch[1]);
    if (method === 'PUT') {
      return updateDemoData(store => {
        const category = store.categories.find(item => item.id === id);
        if (!category) return reject('No se encontró la categoría.', 404);
        category.name = String(body.name ?? category.name).trim();
        category.description = String(body.description ?? category.description);
        store.products.forEach(product => {
          if (product.categoryId === id) product.categoryName = category.name;
        });
        return category;
      });
    }
    if (method === 'DELETE') {
      return updateDemoData(store => {
        if (store.products.some(product => product.categoryId === id)) return reject('No se puede eliminar una categoría con productos asociados.', 409);
        store.categories = store.categories.filter(category => category.id !== id);
        return null;
      });
    }
  }

  if (path === '/products') {
    if (method === 'GET') return data.products;
    if (method === 'POST') {
      return updateDemoData(store => {
        const name = String(body.name ?? '').trim();
        const category = store.categories.find(item => item.id === Number(body.categoryId) && item.active);
        const price = Number(body.price);
        if (!name || !category || !Number.isFinite(price) || price < 0) return reject('Completa nombre, categoría y precio válido.');
        const product: DemoProduct = {
          id: nextId(store.products),
          name,
          description: String(body.description ?? ''),
          price,
          categoryId: category.id,
          categoryName: category.name,
          imageUrl: body.imageUrl ? String(body.imageUrl) : null,
          available: body.available !== false,
        };
        store.products.push(product);
        return product;
      });
    }
  }
  const recipeMatch = path.match(/^\/products\/(\d+)\/recipe$/);
  if (recipeMatch) {
    const productId = Number(recipeMatch[1]);
    if (method === 'GET') {
      return (data.recipes[productId] ?? []).map(line => {
        const item = data.inventory.find(inventoryItem => inventoryItem.id === line.inventoryItemId);
        return { ...line, name: item?.name ?? '', unit: item?.unit ?? '' };
      });
    }
    if (method === 'PUT') {
      const ingredients = Array.isArray(body.ingredients) ? body.ingredients : [];
      return updateDemoData(store => {
        if (!store.products.some(product => product.id === productId)) return reject('No se encontró el producto.', 404);
        const recipe = ingredients.map(value => {
          const ingredient = bodyObject(value);
          const inventoryItemId = Number(ingredient.inventoryItemId);
          if (!store.inventory.some(item => item.id === inventoryItemId) || !positiveNumber(ingredient.quantityPerProduct)) {
            return reject('Cada insumo debe existir y tener una cantidad mayor que cero.');
          }
          return { inventoryItemId, quantityPerProduct: Number(ingredient.quantityPerProduct) };
        });
        if (new Set(recipe.map(item => item.inventoryItemId)).size !== recipe.length) return reject('No repitas insumos en la receta.');
        store.recipes[productId] = recipe;
        return recipe;
      });
    }
  }
  const productMatch = path.match(/^\/products\/(\d+)$/);
  if (productMatch) {
    const id = Number(productMatch[1]);
    if (method === 'PUT') {
      return updateDemoData(store => {
        const product = store.products.find(item => item.id === id);
        if (!product) return reject('No se encontró el producto.', 404);
        const category = store.categories.find(item => item.id === Number(body.categoryId ?? product.categoryId) && item.active);
        const price = Number(body.price ?? product.price);
        if (!category || !Number.isFinite(price) || price < 0) return reject('La categoría o el precio no son válidos.');
        Object.assign(product, {
          name: String(body.name ?? product.name).trim(),
          description: String(body.description ?? product.description),
          price,
          categoryId: category.id,
          categoryName: category.name,
          imageUrl: body.imageUrl ? String(body.imageUrl) : null,
          available: body.available === undefined ? product.available : body.available === true,
        });
        return product;
      });
    }
    if (method === 'DELETE') {
      return updateDemoData(store => {
        const product = store.products.find(item => item.id === id);
        if (!product) return reject('No se encontró el producto.', 404);
        product.available = false;
        return null;
      });
    }
  }

  if (path === '/tables') {
    if (method === 'GET') return data.tables;
    if (method === 'POST') {
      return updateDemoData(store => {
        const tableNumber = String(body.tableNumber ?? '').trim();
        const capacity = Number(body.capacity);
        if (!tableNumber || !Number.isInteger(capacity) || capacity < 1) return reject('El nombre de mesa y una capacidad válida son obligatorios.');
        if (store.tables.some(table => table.tableNumber.toLowerCase() === tableNumber.toLowerCase())) return reject('Ya existe una mesa con ese nombre.', 409);
        const status = ['AVAILABLE', 'OCCUPIED', 'RESERVED'].includes(String(body.status)) ? body.status as DemoTable['status'] : 'AVAILABLE';
        const table: DemoTable = { id: nextId(store.tables), tableNumber, capacity, status };
        store.tables.push(table);
        return table;
      });
    }
  }
  const tableMatch = path.match(/^\/tables\/(\d+)$/);
  if (tableMatch) {
    const id = Number(tableMatch[1]);
    if (method === 'PUT') {
      return updateDemoData(store => {
        const table = store.tables.find(item => item.id === id);
        if (!table) return reject('No se encontró la mesa.', 404);
        const tableNumber = String(body.tableNumber ?? table.tableNumber).trim();
        const capacity = Number(body.capacity ?? table.capacity);
        if (!tableNumber || !Number.isInteger(capacity) || capacity < 1) return reject('El nombre de mesa y la capacidad no son válidos.');
        if (store.tables.some(item => item.id !== id && item.tableNumber.toLowerCase() === tableNumber.toLowerCase())) return reject('Ya existe una mesa con ese nombre.', 409);
        const status = String(body.status ?? table.status);
        if (!['AVAILABLE', 'OCCUPIED', 'RESERVED'].includes(status)) return reject('El estado de mesa no es válido.');
        if (findActiveOrder(store, id) && status !== 'OCCUPIED') return reject('La mesa tiene un pedido activo y no se puede liberar.', 409);
        Object.assign(table, { tableNumber, capacity, status });
        return table;
      });
    }
    if (method === 'DELETE') {
      return updateDemoData(store => {
        const table = store.tables.find(item => item.id === id);
        if (table?.status === 'OCCUPIED' || store.orders.some(order => order.tableId === id && ['PENDING', 'PREPARING', 'READY'].includes(order.status))) {
          return reject('No se puede eliminar una mesa ocupada o con pedidos activos.', 409);
        }
        store.tables = store.tables.filter(item => item.id !== id);
        return null;
      });
    }
  }

  if (path === '/inventory') {
    if (method === 'GET') return data.inventory;
    if (method === 'POST') {
      return updateDemoData(store => {
        const name = String(body.name ?? '').trim();
        const unit = String(body.unit ?? '').trim();
        const minimumQuantity = Number(body.minimumQuantity);
        if (!name || !unit || !Number.isFinite(minimumQuantity) || minimumQuantity < 0) return reject('Nombre, unidad y stock mínimo son obligatorios.');
        const item: DemoInventoryItem = { id: nextId(store.inventory), name, unit, quantity: 0, minimumQuantity };
        store.inventory.push(item);
        store.movements[item.id] = [];
        return item;
      });
    }
  }
  const inventoryMovementMatch = path.match(/^\/inventory\/(\d+)\/movements$/);
  if (inventoryMovementMatch) {
    const id = Number(inventoryMovementMatch[1]);
    if (method === 'GET') return data.movements[id] ?? [];
    if (method === 'POST') {
      return updateDemoData(store => {
        const item = store.inventory.find(entry => entry.id === id);
        if (!item) return reject('No se encontró el insumo.', 404);
        const type = String(body.movementType ?? '');
        const quantity = Number(body.quantity);
        if (!['IN', 'OUT'].includes(type) || !positiveNumber(quantity)) return reject('El tipo y la cantidad del movimiento no son válidos.');
        if (type === 'OUT' && item.quantity < quantity) return reject('Stock insuficiente para registrar la salida.', 409);
        item.quantity = Math.round((item.quantity + (type === 'IN' ? quantity : -quantity)) * 1000) / 1000;
        const history = store.movements[id] ?? [];
        const movement: DemoMovement = {
          id: nextId(history),
          type: type as DemoMovement['type'],
          quantity,
          note: String(body.note ?? ''),
          user: 'Usuario DEMO',
          createdAt: new Date().toISOString(),
        };
        history.push(movement);
        store.movements[id] = history;
        return movement;
      });
    }
  }
  const inventoryMatch = path.match(/^\/inventory\/(\d+)$/);
  if (inventoryMatch) {
    const id = Number(inventoryMatch[1]);
    if (method === 'PUT') {
      return updateDemoData(store => {
        const item = store.inventory.find(entry => entry.id === id);
        if (!item) return reject('No se encontró el insumo.', 404);
        item.name = String(body.name ?? item.name).trim();
        item.unit = String(body.unit ?? item.unit).trim();
        const minimumQuantity = Number(body.minimumQuantity ?? item.minimumQuantity);
        if (!item.name || !item.unit || !Number.isFinite(minimumQuantity) || minimumQuantity < 0) return reject('Los datos del insumo no son válidos.');
        item.minimumQuantity = minimumQuantity;
        return item;
      });
    }
    if (method === 'DELETE') {
      return updateDemoData(store => {
        if ((store.movements[id] ?? []).length > 0 || Object.values(store.recipes).some(recipe => recipe.some(line => line.inventoryItemId === id))) {
          return reject('No se puede eliminar un insumo con historial o recetas asociadas.', 409);
        }
        store.inventory = store.inventory.filter(item => item.id !== id);
        delete store.movements[id];
        return null;
      });
    }
  }

  if (path === '/orders') {
    if (method === 'GET') return data.orders;
    if (method === 'POST') {
      return updateDemoData(store => {
        const tableId = body.tableId === null || body.tableId === undefined || body.tableId === '' ? null : Number(body.tableId);
        const table = tableId === null ? null : store.tables.find(item => item.id === tableId);
        if (tableId !== null && (!table || table.status === 'OCCUPIED')) return reject('La mesa no está disponible.', 409);
        const { items, total } = createOrderItems(store, body.items);
        const order: DemoOrder = {
          id: nextId(store.orders),
          tableId,
          tableName: table?.tableNumber ?? null,
          status: 'PENDING',
          total,
          createdAt: new Date().toISOString(),
          items,
        };
        store.orders.push(order);
        if (table) table.status = 'OCCUPIED';
        return order;
      });
    }
  }
  const orderItemsMatch = path.match(/^\/orders\/(\d+)\/items$/);
  if (orderItemsMatch && method === 'PUT') {
    const id = Number(orderItemsMatch[1]);
    return updateDemoData(store => {
      const order = store.orders.find(item => item.id === id);
      if (!order || !['PENDING', 'PREPARING'].includes(order.status)) return reject('Solo se pueden editar pedidos pendientes o en preparación.', 409);
      const tableId = body.tableId === null || body.tableId === undefined || body.tableId === '' ? null : Number(body.tableId);
      const table = tableId === null ? null : store.tables.find(item => item.id === tableId);
      if (tableId !== null && (!table || (table.status === 'OCCUPIED' && order.tableId !== tableId))) return reject('La mesa no está disponible.', 409);
      const { items, total } = createOrderItems(store, body.items);
      const oldTableId = order.tableId;
      order.tableId = tableId;
      order.tableName = table?.tableNumber ?? null;
      order.items = items;
      order.total = total;
      if (oldTableId !== null && oldTableId !== tableId && !findActiveOrder(store, oldTableId, order.id)) {
        const oldTable = store.tables.find(item => item.id === oldTableId);
        if (oldTable) oldTable.status = 'AVAILABLE';
      }
      if (table) table.status = 'OCCUPIED';
      return order;
    });
  }
  const orderStatusMatch = path.match(/^\/orders\/(\d+)\/status$/);
  if (orderStatusMatch && method === 'PATCH') {
    const id = Number(orderStatusMatch[1]);
    return updateDemoData(store => {
      const order = store.orders.find(item => item.id === id);
      if (!order) return reject('No se encontró el pedido.', 404);
      const allowed: Record<DemoOrder['status'], DemoOrder['status'][]> = {
        PENDING: ['PREPARING', 'CANCELLED'],
        PREPARING: ['READY', 'CANCELLED'],
        READY: ['DELIVERED', 'CANCELLED'],
        DELIVERED: [],
        CANCELLED: [],
      };
      const status = String(body.status ?? '') as DemoOrder['status'];
      if (!allowed[order.status].includes(status)) return reject('La transición de estado no está permitida.', 409);
      if (status === 'DELIVERED') consumeRecipeStock(store, order);
      order.status = status;
      if (status === 'DELIVERED' || status === 'CANCELLED') finishOrder(store, order);
      return order;
    });
  }

  if (path === '/sales') {
    if (method === 'GET') return data.sales;
  }
  const checkoutMatch = path.match(/^\/sales\/orders\/(\d+)\/checkout$/);
  if (checkoutMatch && method === 'POST') {
    const orderId = Number(checkoutMatch[1]);
    return updateDemoData(store => {
      const order = store.orders.find(item => item.id === orderId);
      if (!order || order.status !== 'DELIVERED') return reject('Solo se pueden cobrar pedidos entregados.', 409);
      if (store.sales.some(sale => sale.orderId === orderId)) return reject('El pedido ya tiene una venta registrada.', 409);
      const paymentMethod = String(body.paymentMethod ?? 'CASH');
      if (!['CASH', 'CARD', 'TRANSFER', 'OTHER'].includes(paymentMethod)) return reject('El método de pago no es válido.');
      const sale: DemoSale = {
        id: nextId(store.sales),
        orderId,
        paymentMethod: paymentMethod as DemoSale['paymentMethod'],
        total: order.total,
        status: 'COMPLETED',
        cashier: 'Usuario DEMO',
        createdAt: new Date().toISOString(),
      };
      store.sales.push(sale);
      return sale;
    });
  }
  const voidMatch = path.match(/^\/sales\/(\d+)\/void$/);
  if (voidMatch && method === 'POST') {
    const id = Number(voidMatch[1]);
    return updateDemoData(store => {
      const sale = store.sales.find(item => item.id === id);
      const reason = String(body.reason ?? '').trim();
      if (!sale || sale.status !== 'COMPLETED' || !reason) return reject('Indica el motivo de una venta completada para anularla.', 409);
      sale.status = 'VOIDED';
      sale.voidReason = reason;
      sale.voidedAt = new Date().toISOString();
      return sale;
    });
  }

  return reject(`La operación ${method} ${path} no está disponible en modo DEMO.`, 404);
}
