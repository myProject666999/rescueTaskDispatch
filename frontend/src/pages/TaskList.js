import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  Select,
  InputNumber,
  Tag,
  Space,
  Row,
  Col,
  message,
  Popconfirm,
  Cascader,
} from 'antd';
import {
  PlusOutlined,
  EyeOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import {
  getTaskPage,
  createTask,
  dispatchTask,
  matchRescuers,
  assignRescuer,
  updateTaskStatus,
  cancelTask,
  getSkillList,
} from '../services/api';
import { TASK_STATUS_MAP, DANGER_LEVEL_MAP, TASK_TYPES } from '../constants';

const dangerOptions = Object.entries(DANGER_LEVEL_MAP).map(([v, c]) => ({ value: v, label: c.text }));

export default function TaskList() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [list, setList] = useState([]);

  const [searchStatus, setSearchStatus] = useState('');
  const [searchType, setSearchType] = useState('');
  const [searchKw, setSearchKw] = useState('');

  const [modalVisible, setModalVisible] = useState(false);
  const [matchModalVisible, setMatchModalVisible] = useState(false);
  const [currentTask, setCurrentTask] = useState(null);
  const [matchedRescuers, setMatchedRescuers] = useState([]);
  const [allSkills, setAllSkills] = useState([]);

  const [form] = Form.useForm();

  const loadList = async () => {
    setLoading(true);
    try {
      const res = await getTaskPage({
        pageNum: page,
        pageSize,
        status: searchStatus,
        taskType: searchType,
        keyword: searchKw,
      });
      setList(res.data.records || []);
      setTotal(res.data.total || 0);
    } finally {
      setLoading(false);
    }
  };

  const loadAllSkills = async () => {
    const res = await getSkillList();
    setAllSkills(res.data || []);
  };

  useEffect(() => {
    loadList();
    loadAllSkills();
  }, [page, pageSize]);

  useEffect(() => {
    setPage(1);
    loadList();
  }, [searchStatus, searchType, searchKw]);

  const groupedSkills = allSkills.reduce((acc, s) => {
    (acc[s.skillType] = acc[s.skillType] || []).push({ value: s.id, label: s.skillName });
    return acc;
  }, {});
  const cascaderOptions = Object.entries(groupedSkills).map(([t, children]) => ({
    value: t,
    label: t,
    children,
  }));

  const openAdd = () => {
    setCurrentTask(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleCreate = async () => {
    const values = await form.validateFields();
    const skillIds = (values.skillIds || []).map((arr) => arr[arr.length - 1]).filter(Boolean);
    await createTask({ ...values, skillIds });
    message.success('任务创建成功');
    setModalVisible(false);
    loadList();
  };

  const handleDispatch = async (record) => {
    try {
      await dispatchTask(record.id);
      message.success('派发成功，已通知对应队员');
      loadList();
    } catch (e) {
      message.error(e.message);
    }
  };

  const openMatch = async (record) => {
    setCurrentTask(record);
    const res = await matchRescuers(record.id);
    setMatchedRescuers(res.data || []);
    setMatchModalVisible(true);
  };

  const handleAssign = async (rescuerId) => {
    await assignRescuer(currentTask.id, rescuerId, '手动分配');
    message.success('分配成功');
    setMatchModalVisible(false);
    loadList();
  };

  const handleUpdateStatus = async (record, status) => {
    await updateTaskStatus(record.id, status);
    message.success('状态已更新');
    loadList();
  };

  const handleCancel = (record) => {
    Modal.confirm({
      title: '确认取消任务?',
      icon: <ExclamationCircleOutlined />,
      content: (
        <Form.Item label="取消原因">
          <Input.TextArea id="cancelReason" rows={3} />
        </Form.Item>
      ),
      onOk: async () => {
        const reason = document.getElementById('cancelReason')?.value || '';
        await cancelTask(record.id, reason);
        message.success('任务已取消');
        loadList();
      },
    });
  };

  const columns = [
    { title: '任务编号', dataIndex: 'taskNo', width: 160 },
    { title: '任务标题', dataIndex: 'taskTitle' },
    { title: '任务类型', dataIndex: 'taskType', width: 100 },
    {
      title: '险情等级',
      dataIndex: 'dangerLevel',
      width: 100,
      render: (s) => {
        const cfg = DANGER_LEVEL_MAP[s];
        return <Tag color={typeof cfg?.color === 'string' && cfg.color.startsWith('#') ? cfg.color : cfg?.color}>{cfg?.text}</Tag>;
      },
    },
    { title: '求救地点', dataIndex: 'location' },
    { title: '被困人数', dataIndex: 'victimCount', width: 90 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (s) => {
        const cfg = TASK_STATUS_MAP[s];
        return <Tag color={cfg?.color}>{cfg?.text}</Tag>;
      },
    },
    { title: '创建时间', dataIndex: 'createdTime', width: 160 },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 320,
      render: (_, r) => (
        <Space size="small">
          <Button size="small" type="link" onClick={() => navigate(`/tasks/${r.id}`)}>
            详情
          </Button>
          {r.status === 'PENDING' && (
            <>
              <Button size="small" type="link" onClick={() => openMatch(r)}>
                匹配队员
              </Button>
              <Button size="small" type="link" onClick={() => handleDispatch(r)}>
                派发
              </Button>
            </>
          )}
          {(r.status === 'DISPATCHED' || r.status === 'PENDING') && (
            <Button size="small" type="link" onClick={() => handleUpdateStatus(r, 'IN_PROGRESS')}>
              开始
            </Button>
          )}
          {r.status === 'IN_PROGRESS' && (
            <Button size="small" type="link" onClick={() => handleUpdateStatus(r, 'COMPLETED')}>
              完成
            </Button>
          )}
          {r.status !== 'COMPLETED' && r.status !== 'CANCELLED' && (
            <Popconfirm title="取消任务?" onConfirm={() => handleCancel(r)}>
              <Button size="small" type="link" danger>
                取消
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Card>
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={8}>
            <Input.Search
              placeholder="搜索编号/标题/地点"
              allowClear
              onSearch={setSearchKw}
            />
          </Col>
          <Col span={5}>
            <Select
              placeholder="状态"
              allowClear
              style={{ width: '100%' }}
              onChange={setSearchStatus}
            >
              {Object.entries(TASK_STATUS_MAP).map(([v, c]) => (
                <Select.Option key={v} value={v}>
                  {c.text}
                </Select.Option>
              ))}
            </Select>
          </Col>
          <Col span={5}>
            <Select
              placeholder="任务类型"
              allowClear
              style={{ width: '100%' }}
              onChange={setSearchType}
            >
              {TASK_TYPES.map((t) => (
                <Select.Option key={t} value={t}>
                  {t}
                </Select.Option>
              ))}
            </Select>
          </Col>
          <Col flex="auto">
            <div style={{ textAlign: 'right' }}>
              <Button type="primary" icon={<PlusOutlined />} onClick={openAdd}>
                录入任务
              </Button>
            </div>
          </Col>
        </Row>

        <Table
          loading={loading}
          columns={columns}
          dataSource={list}
          rowKey="id"
          scroll={{ x: 1300 }}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (t) => `共 ${t} 条`,
            onChange: (p, ps) => {
              setPage(p);
              setPageSize(ps);
            },
          }}
        />
      </Card>

      <Modal
        title="录入任务"
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={handleCreate}
        width={720}
        destroyOnClose
        okText="创建"
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item label="任务标题" name="taskTitle" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="任务类型" name="taskType" rules={[{ required: true }]}>
                <Select>
                  {TASK_TYPES.map((t) => (
                    <Select.Option key={t} value={t}>
                      {t}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="险情等级" name="dangerLevel" rules={[{ required: true }]}>
                <Select options={dangerOptions} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="被困人数" name="victimCount" initialValue={1}>
                <InputNumber min={1} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item label="求救地点" name="location" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="报警人姓名" name="reporterName">
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="报警人电话" name="reporterPhone">
                <Input />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item label="求救人状况" name="victimInfo">
                <Input.TextArea rows={2} />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item label="险情描述" name="description">
                <Input.TextArea rows={3} />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item label="所需技能" name="skillIds">
                <Cascader
                  options={cascaderOptions}
                  multiple
                  showSearch
                  placeholder="可多选"
                  style={{ width: '100%' }}
                />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>

      <Modal
        title={`匹配队员 - ${currentTask?.taskTitle || ''}`}
        open={matchModalVisible}
        onCancel={() => setMatchModalVisible(false)}
        footer={null}
        width={720}
      >
        {matchedRescuers.length ? (
          <Table
            size="small"
            dataSource={matchedRescuers}
            rowKey="id"
            pagination={false}
            columns={[
              { title: '姓名', dataIndex: 'name', width: 100 },
              { title: '手机号', dataIndex: 'phone', width: 140 },
              { title: '性别', dataIndex: 'gender', width: 60, render: (g) => (g === 1 ? '男' : '女') },
              {
                title: '级别',
                dataIndex: 'level',
                width: 100,
                render: (l) => ({ CAPTAIN: '队长', VICE_CAPTAIN: '副队长', MEMBER: '队员' })[l] || l,
              },
              {
                title: '操作',
                width: 80,
                render: (_, r) => (
                  <Button size="small" type="link" onClick={() => handleAssign(r.id)}>
                    分配
                  </Button>
                ),
              },
            ]}
          />
        ) : (
          <div style={{ color: '#999', textAlign: 'center', padding: 30 }}>暂无匹配的可用队员</div>
        )}
      </Modal>
    </div>
  );
}
