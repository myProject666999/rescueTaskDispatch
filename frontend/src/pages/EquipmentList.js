import React, { useEffect, useState } from 'react';
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  Tag,
  Row,
  Col,
  Card,
  message,
  Space,
  Popconfirm,
  Progress,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { getEquipmentPage, addEquipment, updateEquipment, deleteEquipment } from '../services/api';
import { EQUIPMENT_CATEGORIES } from '../constants';

const categoryColorMap = {
  绳索装备: 'geekblue',
  水域装备: 'blue',
  医疗装备: 'red',
  通讯装备: 'purple',
  导航装备: 'cyan',
  其他: 'default',
};

export default function EquipmentList() {
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [list, setList] = useState([]);

  const [searchCat, setSearchCat] = useState('');
  const [searchKw, setSearchKw] = useState('');

  const [modalVisible, setModalVisible] = useState(false);
  const [current, setCurrent] = useState(null);
  const [form] = Form.useForm();

  const loadList = async () => {
    setLoading(true);
    try {
      const res = await getEquipmentPage({
        pageNum: page,
        pageSize,
        category: searchCat,
        keyword: searchKw,
      });
      setList(res.data.records || []);
      setTotal(res.data.total || 0);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadList();
  }, [page, pageSize]);

  useEffect(() => {
    setPage(1);
    loadList();
  }, [searchCat, searchKw]);

  const openAdd = () => {
    setCurrent(null);
    form.resetFields();
    setModalVisible(true);
  };

  const openEdit = (r) => {
    setCurrent(r);
    form.setFieldsValue(r);
    setModalVisible(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    if (current) {
      await updateEquipment({ ...values, id: current.id });
      message.success('已更新');
    } else {
      await addEquipment(values);
      message.success('已新增');
    }
    setModalVisible(false);
    loadList();
  };

  const handleDelete = async (id) => {
    await deleteEquipment(id);
    message.success('删除成功');
    loadList();
  };

  const columns = [
    { title: '装备编号', dataIndex: 'equipCode', width: 140 },
    { title: '装备名称', dataIndex: 'equipName', width: 160 },
    {
      title: '分类',
      dataIndex: 'category',
      width: 110,
      render: (c) => <Tag color={categoryColorMap[c] || 'default'}>{c}</Tag>,
    },
    { title: '规格型号', dataIndex: 'specification' },
    {
      title: '库存状态',
      width: 200,
      render: (_, r) => {
        const percent = r.totalQuantity ? Math.round((r.availableQuantity / r.totalQuantity) * 100) : 0;
        const color = percent > 60 ? '#52c41a' : percent > 30 ? '#faad14' : '#ff4d4f';
        return (
          <div style={{ width: 160 }}>
            <Progress percent={percent} showInfo={false} size="small" strokeColor={color} />
            <div style={{ fontSize: 12, marginTop: -4 }}>
              可用 <b style={{ color }}>{r.availableQuantity}</b> / {r.totalQuantity}
              {r.unit}
            </div>
          </div>
        );
      },
    },
    { title: '单位', dataIndex: 'unit', width: 60 },
    { title: '存放位置', dataIndex: 'location', width: 120 },
    {
      title: '操作',
      fixed: 'right',
      width: 150,
      render: (_, r) => (
        <Space size="small">
          <Button size="small" type="link" onClick={() => openEdit(r)}>
            编辑
          </Button>
          <Popconfirm title="确认删除?" onConfirm={() => handleDelete(r.id)}>
            <Button size="small" type="link" danger>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Card>
      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col span={8}>
          <Input.Search placeholder="搜索名称/编号" allowClear onSearch={setSearchKw} />
        </Col>
        <Col span={6}>
          <Select
            placeholder="装备分类"
            allowClear
            style={{ width: '100%' }}
            onChange={setSearchCat}
          >
            {EQUIPMENT_CATEGORIES.map((c) => (
              <Select.Option key={c} value={c}>
                {c}
              </Select.Option>
            ))}
          </Select>
        </Col>
        <Col flex="auto">
          <div style={{ textAlign: 'right' }}>
            <Button type="primary" icon={<PlusOutlined />} onClick={openAdd}>
              新增装备
            </Button>
          </div>
        </Col>
      </Row>

      <Table
        loading={loading}
        columns={columns}
        dataSource={list}
        rowKey="id"
        scroll={{ x: 1100 }}
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

      <Modal
        title={current ? '编辑装备' : '新增装备'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={handleSubmit}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item label="装备编号" name="equipCode" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="装备名称" name="equipName" rules={[{ required: true }]}>
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="分类" name="category" rules={[{ required: true }]}>
                <Select>
                  {EQUIPMENT_CATEGORIES.map((c) => (
                    <Select.Option key={c} value={c}>
                      {c}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="单位" name="unit" initialValue="套">
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="总数量" name="totalQuantity" initialValue={0}>
                <InputNumber min={0} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="可用数量" name="availableQuantity" initialValue={0}>
                <InputNumber min={0} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item label="规格型号" name="specification">
                <Input />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item label="存放位置" name="location">
                <Input />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item label="描述" name="description">
                <Input.TextArea rows={2} />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    </Card>
  );
}
