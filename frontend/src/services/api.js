import request from '../utils/request';

export const getDashboard = () => request.get('/dashboard');

export const getRescuerPage = (params) => request.get('/rescuers/page', { params });
export const getRescuerDetail = (id) => request.get(`/rescuers/${id}`);
export const addRescuer = (data) => request.post('/rescuers', data);
export const updateRescuer = (data) => request.put('/rescuers', data);
export const deleteRescuer = (id) => request.delete(`/rescuers/${id}`);
export const getRescuerSkills = (id) => request.get(`/rescuers/${id}/skills`);
export const addRescuerSkill = (id, skillId, proficiency) => request.post(`/rescuers/${id}/skills`, null, { params: { skillId, proficiency } });
export const removeRescuerSkill = (rescuerSkillId) => request.delete(`/rescuers/skills/${rescuerSkillId}`);
export const updateRescuerStatus = (id, status) => request.put(`/rescuers/${id}/status`, null, { params: { status } });

export const getSkillList = () => request.get('/skills');
export const getSkillByType = (type) => request.get(`/skills/type/${type}`);

export const getTaskPage = (params) => request.get('/tasks/page', { params });
export const getTaskDetail = (id) => request.get(`/tasks/${id}`);
export const createTask = (data) => request.post('/tasks', data);
export const dispatchTask = (id) => request.post(`/tasks/${id}/dispatch`);
export const matchRescuers = (id) => request.get(`/tasks/${id}/match-rescuers`);
export const assignRescuer = (id, rescuerId, reason) => request.post(`/tasks/${id}/assign-rescuer`, null, { params: { rescuerId, reason } });
export const updateTaskStatus = (id, status) => request.put(`/tasks/${id}/status`, null, { params: { status } });
export const cancelTask = (id, reason) => request.post(`/tasks/${id}/cancel`, null, { params: { reason } });

export const updateTaskRescuerStatus = (id, status) => request.put(`/task-rescuers/${id}/status`, null, { params: { status } });
export const getTaskRescuers = (taskId) => request.get(`/task-rescuers/task/${taskId}`);

export const getEquipmentPage = (params) => request.get('/equipments/page', { params });
export const addEquipment = (data) => request.post('/equipments', data);
export const updateEquipment = (data) => request.put('/equipments', data);
export const deleteEquipment = (id) => request.delete(`/equipments/${id}`);
export const checkoutEquipment = (params) => request.post('/equipments/checkout', null, { params });
export const returnEquipment = (id) => request.post(`/equipments/return/${id}`);
export const getTaskEquipments = (taskId) => request.get(`/equipments/task/${taskId}`);

export const getTaskReview = (taskId) => request.get(`/reviews/task/${taskId}`);
export const saveTaskReview = (data) => request.post('/reviews', data);
export const getTaskTimelines = (taskId) => request.get(`/reviews/task/${taskId}/timelines`);
