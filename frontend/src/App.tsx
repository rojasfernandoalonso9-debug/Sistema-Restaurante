import { lazy, Suspense, useEffect, useMemo, useState } from 'react';
import { NavLink, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import {
  Activity,
  Bell,
  Boxes,
  ChefHat,
  ChevronDown,
  CircleDollarSign,
  ClipboardList,
  LayoutDashboard,
  LogOut,
  Menu,
  Moon,
  RotateCcw,
  Search,
  Settings,
  ShoppingBag,
  Table2,
  Users,
} from 'lucide-react';
import { LoginPage } from './features/LoginPage';
import { RestaurantModule } from './features/RestaurantModule';
import { isDemoBuild, isDemoMode, api } from './lib/api';
import { DEMO_MODE_KEY, resetDemoData } from './lib/demoStore';

const Dashboard = lazy(() => import('./features/DashboardContent').then(module => ({ default: module.DashboardContent })));
const PeopleModule = lazy(() => import('./features/PeopleModule').then(module => ({ default: module.PeopleModule })));

const links = [
  { label: 'Dashboard', path: '/', icon: LayoutDashboard },
  { label: 'Pedidos', path: '/pedidos', icon: ClipboardList },
  { label: 'Mesas', path: '/mesas', icon: Table2 },
  { label: 'Productos', path: '/productos', icon: ShoppingBag },
  { label: 'Inventario', path: '/inventario', icon: Boxes },
  { label: 'Ventas', path: '/ventas', icon: CircleDollarSign },
  { label: 'Reportes', path: '/reportes', icon: Activity },
  { label: 'Personal', path: '/personal', icon: Users },
];
const titles: Record<string, string> = {
  '/pedidos': 'Pedidos',
  '/mesas': 'Mesas',
  '/productos': 'Productos',
  '/inventario': 'Inventario',
  '/ventas': 'Ventas',
  '/reportes': 'Reportes',
  '/personal': 'Personal',
};

function NotificationCenter() {
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [items, setItems] = useState<{ label: string; to: string }[]>([]);
  const [error, setError] = useState('');

  const toggle = async () => {
    if (open) {
      setOpen(false);
      return;
    }
    setOpen(true);
    setLoading(true);
    setError('');
    try {
      const [inventory, orders] = await Promise.all([
        api.get<{ name: string; quantity: number; minimumQuantity: number }[]>('/inventory'),
        api.get<{ id: number; status: string }[]>('/orders'),
      ]);
      const alerts = inventory.data
        .filter(item => Number(item.quantity) <= Number(item.minimumQuantity))
        .map(item => ({ label: `Stock bajo: ${item.name} (${item.quantity})`, to: '/inventario' }));
      const active = orders.data.filter(order => ['PENDING', 'PREPARING', 'READY'].includes(order.status)).length;
      if (active > 0) alerts.push({ label: `${active} pedido${active === 1 ? ' activo' : 's activos'} por atender`, to: '/pedidos' });
      setItems(alerts);
    } catch {
      setError('No se pudieron consultar alertas.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="notification-wrap">
      <button className="icon-button notification" title="Notificaciones" aria-expanded={open} onClick={toggle}>
        <Bell size={18} />
        {items.length > 0 && <i />}
      </button>
      {open && (
        <div className="notification-popover">
          <div className="notification-title"><strong>Alertas</strong><button onClick={() => setOpen(false)} aria-label="Cerrar">×</button></div>
          {loading
            ? <p>Consultando inventario y pedidos…</p>
            : error
              ? <p>{error}</p>
              : items.length
                ? items.map((item, index) => <a href={item.to} key={`${item.to}-${index}`}>{item.label}</a>)
                : <p>Todo al día. No hay alertas pendientes.</p>}
        </div>
      )}
    </div>
  );
}

export default function App() {
  const [drawer, setDrawer] = useState(false);
  const [dark, setDark] = useState(false);
  const [query, setQuery] = useState('');
  const [token, setToken] = useState(localStorage.getItem('restaurant-token'));
  const [profile, setProfile] = useState(localStorage.getItem('restaurant-name') ?? 'Fernando Rojas');
  const [role, setRole] = useState(localStorage.getItem('restaurant-role') ?? 'STAFF');
  const [demoMode, setDemoMode] = useState(() => isDemoBuild() || isDemoMode());
  const location = useLocation();
  const navigate = useNavigate();
  const effectiveProfile = demoMode ? 'Usuario DEMO' : profile;
  const effectiveRole = demoMode ? 'DEMO' : role;
  const canManage = demoMode || effectiveRole === 'ADMIN' || effectiveRole === 'MANAGER';

  useEffect(() => {
    document.documentElement.dataset.theme = dark ? 'dark' : 'light';
  }, [dark]);

  useEffect(() => {
    if (location.pathname === '/demo') {
      if (!demoMode) {
        localStorage.setItem(DEMO_MODE_KEY, 'true');
        setDemoMode(true);
      }
      navigate('/', { replace: true });
    }
  }, [demoMode, location.pathname, navigate]);

  const title = useMemo(() => titles[location.pathname] ?? 'Dashboard', [location.pathname]);
  const logout = () => {
    localStorage.removeItem('restaurant-token');
    localStorage.removeItem('restaurant-name');
    localStorage.removeItem('restaurant-role');
    setToken(null);
  };
  const enterDemo = () => {
    localStorage.setItem(DEMO_MODE_KEY, 'true');
    setDemoMode(true);
    navigate('/', { replace: true });
  };
  const restartDemo = () => {
    if (!window.confirm('¿Reiniciar todos los datos de demostración? Se perderán los cambios guardados en este navegador.')) return;
    resetDemoData();
    window.location.reload();
  };

  if (!demoMode && !token) {
    return (
      <LoginPage
        onLogin={(newToken, name, newRole) => {
          localStorage.setItem('restaurant-token', newToken);
          localStorage.setItem('restaurant-name', name);
          localStorage.setItem('restaurant-role', newRole);
          setToken(newToken);
          setProfile(name);
          setRole(newRole);
        }}
        onDemo={enterDemo}
      />
    );
  }

  const initials = effectiveProfile.split(' ').map(part => part[0]).slice(0, 2).join('').toUpperCase();
  const visibleLinks = links.filter(({ path }) => path !== '/personal' || (!demoMode && canManage));
  const nav = (
    <>
      <div className="brand">
        <div className="brand-mark"><ChefHat size={21} /></div>
        <div><strong>Mesa &amp; Miga</strong><small>GESTIÓN RESTAURANTE</small></div>
      </div>
      <div className="workspace"><span className="workspace-dot" /> Restaurante principal <ChevronDown size={14} /></div>
      <div className="nav-label">MENÚ PRINCIPAL</div>
      <nav>
        {visibleLinks.map(({ label, path, icon: Icon }) => (
          <NavLink end={path === '/'} onClick={() => setDrawer(false)} className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`} to={path} key={path}>
            <Icon size={18} /><span>{label}</span>{label === 'Pedidos' && <span className="nav-count">—</span>}
          </NavLink>
        ))}
      </nav>
      <div className="sidebar-bottom">
        <div className="help-card">
          <div className="help-icon"><ChefHat size={17} /></div>
          <strong>¿Necesitas ayuda?</strong>
          <span>Consulta la guía técnica en la carpeta docs.</span>
          <button onClick={() => window.alert('Consulta docs/TECHNICAL.md para la configuración.')}>Ver documentación <span>→</span></button>
        </div>
        <button className="nav-link settings-link" onClick={() => window.alert('La configuración general se realiza mediante variables de entorno.')}><Settings size={18} /> Configuración</button>
        <div className="side-user">
          <div className="avatar">{initials}</div>
          <div><strong>{effectiveProfile}</strong><small>{demoMode ? 'Datos locales · sin servidor' : 'Sesión activa'}</small></div>
          {!demoMode && <button title="Cerrar sesión" onClick={logout}><LogOut size={17} /></button>}
        </div>
      </div>
    </>
  );

  return (
    <div className="app-shell">
      <aside className="sidebar">{nav}</aside>
      {drawer && <><button aria-label="Cerrar menú" className="drawer-shade" onClick={() => setDrawer(false)} /><aside className="mobile-sidebar">{nav}</aside></>}
      <main className="main-area">
        <header className="topbar">
          <button aria-label="Abrir menú" className="icon-button mobile-menu" onClick={() => setDrawer(true)}><Menu size={20} /></button>
          <div className="breadcrumb">Restaurante <span>/</span> <strong>{title}</strong></div>
          <div className="top-actions">
            <label className="search-box"><Search size={16} /><input placeholder="Buscar..." value={query} onChange={event => setQuery(event.target.value)} /><kbd>⌘ K</kbd></label>
            <button className="icon-button" title="Alternar tema" onClick={() => setDark(value => !value)}><Moon size={18} /></button>
            <NotificationCenter />
            {demoMode && <span className="demo-badge">DEMO</span>}
            <div className="top-profile">
              <div className="avatar">{initials}</div>
              <div><strong>{effectiveProfile}</strong><small>{effectiveRole}</small></div>
              {!demoMode && <button className="logout-top" title="Cerrar sesión" onClick={logout}><LogOut size={16} /></button>}
            </div>
          </div>
        </header>
        {demoMode && (
          <div className="demo-banner" role="status">
            <div><strong>Estás utilizando una versión de demostración</strong><span>Los cambios se guardan solo en este navegador. No se conectan al servidor ni procesan pagos reales.</span></div>
            <button className="button button-demo-reset" onClick={restartDemo}><RotateCcw size={15} /> Reiniciar datos de prueba</button>
          </div>
        )}
        <div className="page-content">
          <Suspense fallback={<div className="panel">Cargando módulo...</div>}>
            <Routes>
              <Route path="/" element={<Dashboard />} />
              {visibleLinks.filter(({ path }) => path !== '/').map(({ path, label }) => (
                <Route
                  key={path}
                  path={path}
                  element={label === 'Personal'
                    ? <PeopleModule search={query} />
                    : <RestaurantModule title={label} search={query} canManage={canManage} />}
                />
              ))}
              <Route path="*" element={<Dashboard />} />
            </Routes>
          </Suspense>
        </div>
        <footer className="footer">© 2026 Mesa &amp; Miga <span>•</span> Gestión inteligente para restaurantes</footer>
      </main>
    </div>
  );
}
