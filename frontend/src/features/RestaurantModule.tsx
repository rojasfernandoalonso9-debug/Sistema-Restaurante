import { lazy } from 'react';
const ModulePage=lazy(()=>import('./ModulePage').then(m=>({default:m.ModulePage})));
const ProductModule=lazy(()=>import('./ProductModule').then(m=>({default:m.ProductModule})));
const TableModule=lazy(()=>import('./TableModule').then(m=>({default:m.TableModule})));
const InventoryModule=lazy(()=>import('./InventoryModule').then(m=>({default:m.InventoryModule})));
const OrderModule=lazy(()=>import('./OrderModule').then(m=>({default:m.OrderModule})));
const SalesModule=lazy(()=>import('./SalesModule').then(m=>({default:m.SalesModule})));
const ReportsModule=lazy(()=>import('./ReportsModule').then(m=>({default:m.ReportsModule})));
export function RestaurantModule({title,search,canManage}:{title:string;search:string;canManage:boolean}){
 switch(title){case 'Productos':return <ProductModule search={search} canManage={canManage}/>;case 'Mesas':return <TableModule search={search} canManage={canManage}/>;case 'Inventario':return <InventoryModule search={search} canManage={canManage}/>;case 'Pedidos':return <OrderModule search={search}/>;case 'Ventas':return <SalesModule search={search}/>;case 'Reportes':return <ReportsModule/>;default:return <ModulePage title={title} search={search}/>;}
}
