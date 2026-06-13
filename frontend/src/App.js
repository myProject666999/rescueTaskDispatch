import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './layouts/MainLayout';
import Dashboard from './pages/Dashboard';
import RescuerList from './pages/RescuerList';
import TaskList from './pages/TaskList';
import TaskDetail from './pages/TaskDetail';
import EquipmentList from './pages/EquipmentList';

function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="rescuers" element={<RescuerList />} />
        <Route path="tasks" element={<TaskList />} />
        <Route path="tasks/:id" element={<TaskDetail />} />
        <Route path="equipments" element={<EquipmentList />} />
      </Route>
    </Routes>
  );
}

export default App;
