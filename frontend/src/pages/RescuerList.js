import React, { useEffect, useState } from 'react';
import dayjs from 'dayjs';
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  DatePicker,
  Tag,
  Space,
  Row,
  Col,
  Card,
  message,
  Popconfirm,
  Divider,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import {
  getRescuerPage,
  addRescuer,
  updateRescuer,
  deleteRescuer,
  getRescuerSkills,
  addRescuerSkill,
  updateRescuerStatus,
  getSkillList,
} from '../services/api';
import { AVAILABILITY_STATUS_MAP, PROFICIENCY_MAP } from '../constants';

const levelOptions = [
  { value: 'CAPTAIN', label: '队长' },
  { value: 'VICE_CAPTAIN', label: '副队长' },
  { value: 'MEMBER', label: '队员' },
];

const proficiencyOptions = Object.entries(PROFICIENCY_MAP).map(([v, l]) => ({ value: v, label: l }));

export default function RescuerList() {
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [list, setList] = useState([]);

  const [searchName, setSearchName] = useState('');
  const [searchStatus, setSearchStatus] = useState('');

  const [modalVisible, setModalVisible] = useState(false);
  const [skillModalVisible, setSkillModalVisible] = useState(false);
  const [currentRescuer, setCurrentRescuer] = useState(null);
  const [currentSkills, setCurrentSkills] = useState([]);
  const [allSkills, setAllSkills] = useState([]);

  const [form] = Form.useForm();
  const [skillForm] = Form.useForm();

  const loadList = async () => {
    setLoading(true);
    try {
      const res = await getRescuerPage({
        pageNum: page,
        pageSize,
        name: searchName,
        availabilityStatus: searchStatus,
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
  }, [searchName, searchStatus]);

  const openAdd = () => {
    setCurrentRescuer(null);
    form.resetFields();
    setModalVisible(true);
  };

  const openEdit = (record) => {
    setCurrentRescuer(record);
    form.setFieldsValue({
      ...record,
      joinDate: record.joinDate ? dayjs(record.joinDate.split(' ')[0]) : null,
    });
    setModalVisible(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    const submitData = {
      ...values,
      joinDate: values.joinDate ? values.joinDate.format('YYYY-MM-DD') : null,
    };
    if (currentRescuer) {
      await updateRescuer({ ...submitData, id: currentRescuer.id });
      message.success('更新成功');
    } else {
      await addRescuer(submitData);
      message.success('添加成功');
    }
    setModalVisible(false);
    loadList();
  };

  const handleDelete = async (id) => {
    await deleteRescuer(id);
    message.success('删除成功');
    loadList();
  };

  const openSkillModal = async (record) => {
    setCurrentRescuer(record);
    const res = await getRescuerSkills(record.id);
    setCurrentSkills(res.data || []);
    skillForm.resetFields();
    setSkillModalVisible(true);
  };

  const handleAddSkill = async () => {
    const { skillId, proficiency } = await skillForm.validateFields();
    await addRescuerSkill(currentRescuer.id, skillId, proficiency);
    message.success('添加技能成功');
    skillForm.resetFields();
    const res = await getRescuerSkills(currentRescuer.id);
    setCurrentSkills(res.data || []);
  };

  const handleChangeStatus = async (record, status) => {
    await updateRescuerStatus(record.id, status);
    message.success('状态已更新');
    loadList();
  };

  const columns = [
    { title: '姓名', dataIndex: 'name', width: 100 },
    { title: '手机号', dataIndex: 'phone', width: 130 },
    { title: '性别', dataIndex: 'gender', width: 60, render: (g) => (g === 1 ? '男' : '女') },
    { title: '年龄', dataIndex: 'age', width: 60 },
    {
      title: '状态',
      dataIndex: 'availabilityStatus',
      width: 120,
      render: (s, r) => (
        <Select
          size="small"
          value={s}
          style={{ width: 100 }}
          onChange={(val) => handleChangeStatus(r, val)}
        >
          {Object.entries(AVAILABILITY_STATUS_MAP).map(([v, c]) => (
            <Select.Option key={v} value={v}>
              {c.text}
            </Select.Option>
          ))}
        </Select>
      ),
    },
    {
      title: '级别',
      dataIndex: 'level',
      width: 100,
      render: (l) => {
        const map = { CAPTAIN: '队长', VICE_CAPTAIN: '副队长', MEMBER: '队员' };
        return map[l] || l;
      },
    },
    { title: '入队日期', dataIndex: 'joinDate', width: 120 },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 260,
      render: (_, r) => (
        <Space size="small">
          <Button size="small" type="link" onClick={() => openEdit(r)}>
            编辑
          </Button>
          <Button size="small" type="link" onClick={() => openSkillModal(r)}>
            技能
          </Button>
          <Popconfirm title="确定删除?" onConfirm={() => handleDelete(r.id)}>
            <Button size="small" type="link" danger>
              删除
            </Button>
          </Popconfirm>
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
              placeholder="搜索姓名"
              allowClear
              onSearch={setSearchName}
            />
          </Col>
          <Col span={6}>
            <Select
              placeholder="可调动状态"
              allowClear
              style={{ width: '100%' }}
              onChange={setSearchStatus}
            >
              {Object.entries(AVAILABILITY_STATUS_MAP).map(([v, c]) => (
                <Select.Option key={v} value={v}>
                  {c.text}
                </Select.Option>
              ))}
            </Select>
          </Col>
          <Col flex="auto">
            <div style={{ textAlign: 'right' }}>
              <Button type="primary" icon={<PlusOutlined />} onClick={openAdd}>
                新增队员
              </Button>
            </div>
          </Col>
        </Row>

        <Table
          loading={loading}
          columns={columns}
          dataSource={list}
          rowKey="id"
          scroll={{ x: 1000 }}
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
        title={currentRescuer ? '编辑队员' : '新增队员'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={handleSubmit}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item label="姓名" name="name" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="手机号" name="phone" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="性别" name="gender" rules={[{ required: true }]}>
                <Select>
                  <Select.Option value={1}>男</Select.Option>
                  <Select.Option value={2}>女</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="年龄" name="age">
                <InputNumber min={1} max={100} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="可调动状态" name="availabilityStatus" rules={[{ required: true }]}>
                <Select>
                  {Object.entries(AVAILABILITY_STATUS_MAP).map(([v, c]) => (
                    <Select.Option key={v} value={v}>
                      {c.text}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="级别" name="level" rules={[{ required: true }]}>
                <Select options={levelOptions} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="身份证号" name="idCard">
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="入队日期" name="joinDate">
                <DatePicker style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item label="住址" name="address">
                <Input />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item label="备注" name="remark">
                <Input.TextArea rows={3} />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>

      <Modal
        title={`技能管理 - ${currentRescuer?.name || ''}`}
        open={skillModalVisible}
        onCancel={() => setSkillModalVisible(false)}
        footer={null}
        width={640}
        destroyOnClose
      >
        <Space direction="vertical" style={{ width: '100%' }}>
          <Form form={skillForm} layout="inline">
            <Form.Item name="skillId" rules={[{ required: true, message: '请选择技能' }]}>
              <Select placeholder="选择技能" style={{ width: 180 }}>
                {allSkills.map((s) => (
                  <Select.Option key={s.id} value={s.id}>
                    [{s.skillType}] {s.skillName}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item name="proficiency" initialValue="INTERMEDIATE">
              <Select placeholder="熟练度" style={{ width: 140 }} options={proficiencyOptions} />
            </Form.Item>
            <Form.Item>
              <Button type="primary" onClick={handleAddSkill}>
                添加
              </Button>
            </Form.Item>
          </Form>
          <Divider style={{ margin: 0 }} />
          {currentSkills.length ? (
            <div>
              {currentSkills.map((s) => (
                <Tag key={s.id} style={{ marginBottom: 8 }}>
                  [{s.skillType}] {s.skillName}
                </Tag>
              ))}
            </div>
          ) : (
            <div style={{ color: '#999', textAlign: 'center', padding: 20 }}>暂无技能</div>
          )}
        </Space>
      </Modal>
    </div>
  );
}
