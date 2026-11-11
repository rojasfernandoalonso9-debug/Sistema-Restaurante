import { FormEvent, useCallback, useEffect, useState } from 'react';
import { PackageOpen, Pencil, Plus, RefreshCw, Search, Trash2, X } from 'lucide-react';
import { api, type ApiEntity } from '../lib/api';
import { validateRecordPayload } from '../lib/validation';

type Field = { label: string; key: string; type?: string; required?: boolean };
const endpoints: Record<string,string> = { Pedidos:'orders', Mesas:'tables', Productos:'products', Inventario:'inventory', Ventas:'sales', Reportes:'reports', Personal:'users' };
const fields: Record<string,Field[]> = {
  Mesas:[{label:'Nombre o número',key:'name',required:true},{label:'Capacidad',key:'capacity',type:'number',required:true},{label:'Estado (AVAILABLE, OCCUPIED, RESERVED)',key:'status'}],
  Productos:[{label:'Nombre del producto',key:'name',required:true},{label:'Descripción',key:'description'},{label:'Precio (PEN)',key:'price',type:'number',required:true}],
  Inventario:[{label:'Nombre del insumo',key:'name',required:true},{label:'Descripción',key:'description'},{label:'Existencias',key:'stock',type:'number',required:true}],
  Personal:[{label:'Nombre completo',key:'name',required:true},{label:'Correo electrónico',key:'email',type:'email',required:true},{label:'Contraseña (mínimo 12 caracteres)',key:'password',type:'password'},{label:'Rol (ADMIN, MANAGER o STAFF)',key:'role',required:true}],
  Pedidos:[{label:'Descripción del pedido',key:'name',required:true},{label:'Mesa',key:'tableName'},{label:'Estado',key:'status'}],
  Ventas:[{label:'Referencia de venta',key:'name',required:true},{label:'Total (PEN)',key:'price',type:'number',required:true},{label:'Estado',key:'status'}],
  Reportes:[{label:'Nombre del reporte',key:'name',required:true},{label:'Descripción',key:'description'}],
};

export function ModulePage({ title, search }: { title:string; search:string }) {
  const [rows,setRows]=useState<ApiEntity[]>([]); const [loading,setLoading]=useState(true); const [error,setError]=useState('');
  const [modal,setModal]=useState(false); const [editing,setEditing]=useState<ApiEntity|null>(null); const [saving,setSaving]=useState(false);
  const endpoint=endpoints[title]??'reports'; const moduleFields=fields[title]??[{label:'Nombre',key:'name',required:true}];
  const visibleFields=moduleFields.filter(field=>field.type!=='password');
  const load=useCallback(()=>{setLoading(true);api.get<ApiEntity[]>(`/${endpoint}`).then(response=>{setRows(response.data);setError('')}).catch(()=>{setRows([]);setError('No se pudo conectar con el servicio. Verifica que el backend esté iniciado.')}).finally(()=>setLoading(false))},[endpoint]);
  useEffect(()=>{load()},[load]);
  const open=(row?:ApiEntity)=>{setEditing(row??null);setError('');setModal(true)};
  const submit=async(event:FormEvent<HTMLFormElement>)=>{
    event.preventDefault();const payload=Object.fromEntries(new FormData(event.currentTarget).entries());
    const validation=validateRecordPayload(payload);if(validation){setError(validation);return}
    if(title==='Personal'&&!editing&&String(payload.password??'').length<12){setError('La contraseña debe tener al menos 12 caracteres.');return}
    setSaving(true);
    try{if(editing)await api.put(`/${endpoint}/${editing.id}`,payload);else await api.post(`/${endpoint}`,payload);setModal(false);load()}
    catch{setError('No se pudo guardar. Revisa los datos, el rol y la conexión con el servidor.')}
    finally{setSaving(false)}
  };
  const remove=async(id:number)=>{if(!window.confirm('¿Eliminar este registro? Esta acción puede no ser reversible.'))return;try{await api.delete(`/${endpoint}/${id}`);load()}catch{setError('No se pudo eliminar. Comprueba permisos y dependencias.') }};
  const shown=rows.filter(row=>`${row.name??''} ${row.description??''} ${(row as unknown as Record<string,unknown>).email??''}`.toLowerCase().includes(search.toLowerCase()));
  const displayValue=(row:ApiEntity,key:string)=>{const value=(row as unknown as Record<string,unknown>)[key];if(value===undefined||value===null||value==='')return '—';if(key==='status')return <span className={`status-pill status-${String(value).toLowerCase()}`}>{String(value).replace(/_/g,' ')}</span>;if(key==='price')return `S/ ${Number(value).toFixed(2)}`;return String(value)};
  return <>
    <div className="page-heading"><div><div className="eyebrow">OPERACIÓN</div><h1>{title}</h1><p>Administra y consulta la información de {title.toLowerCase()}.</p></div><button className="button button-primary" onClick={()=>open()}><Plus size={17}/> Nuevo registro</button></div>
    <section className="panel module-panel"><div className="module-toolbar"><div className="module-summary"><strong>{rows.length}</strong> registros <span>•</span> Actualizado desde el servidor</div><div className="module-tools"><label className="table-search"><Search size={15}/><input placeholder={`Buscar ${title.toLowerCase()}...`} value={search} readOnly/></label><button className="icon-button" title="Actualizar" onClick={load}><RefreshCw size={16}/></button></div></div>
    {error&&!modal&&<div className="error-banner">{error}</div>}<div className="table-responsive"><table><thead><tr>{visibleFields.map(field=><th key={field.key}>{field.label.toUpperCase()}</th>)}<th>ACCIONES</th></tr></thead><tbody>
    {loading?<tr><td colSpan={visibleFields.length+1} className="empty-cell">Cargando información...</td></tr>:shown.length?shown.map(row=><tr key={row.id}>{visibleFields.map(field=><td key={field.key}>{displayValue(row,field.key)}</td>)}<td><div className="row-actions"><button aria-label="Editar" onClick={()=>open(row)}><Pencil size={15}/></button><button aria-label="Eliminar" onClick={()=>remove(row.id)}><Trash2 size={15}/></button></div></td></tr>):<tr><td colSpan={visibleFields.length+1} className="empty-cell"><PackageOpen size={22}/><strong>No hay registros todavía</strong><span>Agrega el primero con “Nuevo registro”.</span></td></tr>}
    </tbody></table></div><div className="table-bottom">Mostrando {shown.length} de {rows.length} registros</div></section>
    {modal&&<div className="modal-backdrop" onMouseDown={event=>{if(event.target===event.currentTarget)setModal(false)}}><section className="modal"><div className="modal-heading"><div><h2>{editing?'Editar':'Nuevo'} registro</h2><p>Completa los campos para {editing?'actualizar':'crear'} {title.toLowerCase()}.</p></div><button className="icon-button" aria-label="Cerrar" onClick={()=>setModal(false)}><X size={19}/></button></div>
    {error&&<div className="error-banner">{error}</div>}<form onSubmit={submit}><div className="form-grid">{moduleFields.map(field=><label className="form-field" key={field.key}>{field.label}<input name={field.key} type={field.type??'text'} required={field.required||(title==='Personal'&&field.key==='password'&&!editing)} min={field.type==='number'?0:undefined} step={field.type==='number'?'0.01':undefined} autoComplete={field.type==='password'?'new-password':undefined} defaultValue={(editing as unknown as Record<string,unknown>|null)?.[field.key] as string|number|undefined}/></label>)}</div><div className="modal-actions"><button type="button" className="button button-plain" onClick={()=>setModal(false)}>Cancelar</button><button className="button button-primary" disabled={saving}>{saving?'Guardando...':'Guardar registro'}</button></div></form></section></div>}
  </>;
}
