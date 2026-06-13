export const TASK_STATUS_MAP = {
  PENDING: { text: '待分配', color: 'default' },
  DISPATCHED: { text: '已派发', color: 'processing' },
  IN_PROGRESS: { text: '进行中', color: 'warning' },
  COMPLETED: { text: '已完成', color: 'success' },
  CANCELLED: { text: '已取消', color: 'error' },
};

export const RESPONSE_STATUS_MAP = {
  NOTIFIED: { text: '已通知', color: 'default' },
  RESPONDED: { text: '已响应', color: 'processing' },
  DEPARTED: { text: '已出动', color: 'warning' },
  ARRIVED: { text: '已到场', color: 'success' },
  WITHDRAWN: { text: '已撤离', color: 'default' },
  REJECTED: { text: '已拒绝', color: 'error' },
};

export const AVAILABILITY_STATUS_MAP = {
  ON_DUTY: { text: '值班', color: 'success' },
  RESTING: { text: '休息', color: 'warning' },
  AWAY: { text: '不在本地', color: 'error' },
};

export const DANGER_LEVEL_MAP = {
  LOW: { text: '低', color: 'success' },
  NORMAL: { text: '中', color: 'warning' },
  HIGH: { text: '高', color: 'error' },
  EXTREME: { text: '极高', color: '#cf1322' },
};

export const TASK_TYPES = ['山地救援', '水域救援', '医疗救援', '综合救援'];

export const EQUIPMENT_CATEGORIES = ['绳索装备', '水域装备', '医疗装备', '通讯装备', '导航装备', '其他'];

export const PROFICIENCY_MAP = {
  BEGINNER: '初级',
  INTERMEDIATE: '中级',
  ADVANCED: '高级',
  EXPERT: '专家',
};

export const RESCUE_EFFECT_MAP = {
  SUCCESS: '成功',
  PARTIAL: '部分成功',
  FAILURE: '失败',
};
