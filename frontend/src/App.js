import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Typography } from 'antd';
import {
  CalendarOutlined,
  TeamOutlined,
  HomeOutlined,
    MedicineBoxOutlined
} from '@ant-design/icons';
import AppointmentsPage from './pages/AppointmentsPage';
import DoctorsPage from './pages/DoctorsPage';
import PatientsPage from './pages/PatientsPage';
import ClinicsPage from './pages/ClinicsPage';

const { Header, Content, Footer } = Layout;
const { Text } = Typography;

function App() {
    const navigate = useNavigate();
    const location = useLocation();

    // Определяем активную вкладку на основе текущего URL
    const getSelectedKey = () => {
        const path = location.pathname;
        if (path === '/') return ['appointments'];
        if (path === '/patients') return ['patients'];
        if (path === '/clinics') return ['clinics'];
        if (path === '/doctors') return ['doctors'];
        return ['appointments'];
    };

  return (
      <Layout style={{ minHeight: '100vh', background: '#f0f2f5' }}>
        <Header style={{
          position: 'sticky',
          top: 0,
          zIndex: 10,
          width: '100%',
          display: 'flex',
          alignItems: 'center',
          background: '#001529',
          padding: '0 24px'
        }}>
          <div
              style={{
                color: 'white',
                marginRight: '24px',
                fontSize: '18px',
                fontWeight: 'bold',
                display: 'flex',
                alignItems: 'center',
                cursor: 'pointer'
              }}
              onClick={() => navigate('/')}
          >
            <MedicineBoxOutlined style={{ fontSize: '24px', marginRight: '8px' }} />
            МедЗапись
          </div>

          <Menu
              theme="dark"
              mode="horizontal"
              selectedKeys={getSelectedKey()}
              style={{
                flex: 1,
                background: 'transparent',
                borderBottom: 'none'
              }}
          >
            <Menu.Item key="appointments" icon={<CalendarOutlined />}>
              <Link to="/">Записи</Link>
            </Menu.Item>
            <Menu.Item key="patients" icon={<TeamOutlined />}>
              <Link to="/patients">Пациенты</Link>
            </Menu.Item>
            <Menu.Item key="clinics" icon={<HomeOutlined />}>
              <Link to="/clinics">Клиники</Link>
            </Menu.Item>
            <Menu.Item key="doctors" icon={<MedicineBoxOutlined />}>
              <Link to="/doctors">Врачи</Link>
            </Menu.Item>
          </Menu>
        </Header>

        <Content style={{
          padding: '24px',
          position: 'relative',
          zIndex: 1,
          marginTop: '-1px'
        }}>
          <div style={{
            background: '#ffffff',
            padding: '24px',
            borderRadius: '8px',
            minHeight: 'calc(100vh - 134px)',
            boxShadow: '0 1px 2px rgba(0,0,0,0.1)'
          }}>
            <Routes>
              <Route path="/" element={<AppointmentsPage />} />
              <Route path="/patients" element={<PatientsPage />} />
              <Route path="/clinics" element={<ClinicsPage />} />
              <Route path="/doctors" element={<DoctorsPage />} />
            </Routes>
          </div>
        </Content>

        <Footer style={{
          textAlign: 'center',
          background: '#f0f2f5',
          padding: '16px 50px',
          position: 'relative',
          zIndex: 1
        }}>
          <Text type="secondary">
            © {new Date().getFullYear()} Система записи на приём к врачу
          </Text>
        </Footer>
      </Layout>
  );
}

function AppWrapper() {
  return (
      <Router>
        <App />
      </Router>
  );
}

export default AppWrapper;