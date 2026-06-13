import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Descriptions,
  Tag,
  Button,
  Row,
  Col,
  Card,
  Timeline,
  Table,
  Modal,
  Form,
  Input,
  Select,
  InputNumber,
  message,
  Space,
  List,
  Statistic,
  Popover,
} from 'antd';
import { ArrowLeftOutlined, TeamOutlined, ToolOutlined, EditOutlined } from '@ant-design/icons';
import {
  getTaskDetail,
  updateTaskRescuerStatus,
  getTaskRescuers,
  matchRescuers,
  assignRescuer,
  checkoutEquipment,
  getEquipmentPage,
  returnEquipment,
  getTaskEquipments,
  getTaskReview,
  saveTaskReview,
} from '../services/api';
import {
  TASK_STATUS_MAP,
  DANGER_LEVEL_MAP,
  RESPONSE_STATUS_MAP,
  RESCUE_EFFECT_MAP,
  EQUIPMENT_CATEGORIES,
} from '../constants';

export default function TaskDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [detail, setDetail] = useState(null);
  const [loading, setLoading] = useState(false);

  const [equipModalVisible, setEquipModalVisible] = useState(false);
  const [equipList, setEquipList] = useState([]);
  const [selectedEquip, setSelectedEquip] = useState(null);
  const [equipQty, setEquipQty] = useState(1);

  const [reviewModalVisible, setReviewModalVisible] = useState(false);
  const [reviewForm] = Form.useForm();

  const [assignModalVisible, setAssignModalVisible] = useState(false);
  const [matchedRescuers, setMatchedRescuers] = useState([]);

  const loadDetail = async () => {
    setLoading(true);
    try {
      const res = await getTaskDetail(id);
      setDetail(res.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDetail();
  }, [id]);

  const task = detail?.task;
  const rescuers = detail?.rescuers || [];
  const equipments = detail?.equipments || [];
  const timelines = detail?.timelines || [];
  const review = detail?.review;

  const statusCounts = rescuers.reduce((acc, r) => {
    const s = r.taskRescuer?.responseStatus;
    if (s) acc[s] = (acc[s] || 0) + 1;
    return acc;
  }, {});

  const handleUpdateRescuerStatus = async (taskRescuerId, status) => {
    await updateTaskRescuerStatus(taskRescuerId, status);
    message.success('状态已更新');
    loadDetail();
  };

  const openEquipModal = async () => {
    const res = await getEquipmentPage({ pageNum: 1, pageSize: 50 });
    setEquipList(res.data.records || []);
    setSelectedEquip(null);
    setEquipQty(1);
    setEquipModalVisible(true);
  };

  const handleCheckout = async () => {
    if (!selectedEquip) {
      message.warning('请选择装备');
      return;
    }
    await checkoutEquipment({
      taskId: id,
      equipmentId: selectedEquip,
      quantity: equipQty,
      operatorName: '调度员',
    });
    message.success('装备出库成功');
    setEquipModalVisible(false);
    loadDetail();
  };

  const handleReturnEquip = async (taskEquipId) => {
    await returnEquipment(taskEquipId);
    message.success('归还成功');
    loadDetail();
  };

  const openReviewModal = () => {
    reviewForm.setFieldsValue(review || {});
    setReviewModalVisible(true);
  };

  const handleSaveReview = async () => {
    const values = await reviewForm.validateFields();
    await saveTaskReview({ ...values, taskId: id, reviewerName: '调度员' });
    message.success('复盘保存成功');
    setReviewModalVisible(false);
    loadDetail();
  };

  const openAssignModal = async () => {
    const res = await matchRescuers(id);
    setMatchedRescuers(res.data || []);
    setAssignModalVisible(true);
  };

  const handleAssign = async (rescuerId) => {
    await assignRescuer(id, rescuerId, '手动分配');
    message.success('分配成功');
    setAssignModalVisible(false);
    loadDetail();
  };

  const rescuerColumns = [
    { title: '队员', dataIndex: ['rescuer', 'name'], width: 80 },
    { title: '手机号', dataIndex: ['rescuer', 'phone'], width: 130 },
    {
      title: '响应状态',
      dataIndex: ['taskRescuer', 'responseStatus'],
      width: 100,
      render: (s) => {
        const cfg = RESPONSE_STATUS_MAP[s];
        return <Tag color={cfg?.color}>{cfg?.text}</Tag>;
      },
    },
    {
      title: '预计到达(分钟)',
      dataIndex: ['taskRescuer', 'estimatedArrival'],
      width: 110,
    },
    { title: '响应时间', dataIndex: ['taskRescuer', 'responseTime'], width: 160 },
    { title: '出动时间', dataIndex: ['taskRescuer', 'departTime'], width: 160 },
    { title: '到场时间', dataIndex: ['taskRescuer', 'arriveTime'], width: 160 },
    { title: '撤离时间', dataIndex: ['taskRescuer', 'withdrawTime'], width: 160 },
    {
      title: '操作',
      width: 260,
      render: (_, r) => {
        const tr = r.taskRescuer;
        const status = tr?.responseStatus;
        const buttons = [];
        if (status === 'NOTIFIED') {
          buttons.push(
            <Button size="small" type="link" onClick={() => handleUpdateRescuerStatus(tr.id, 'RESPONDED')}>
              响应
            </Button>
          );
          buttons.push(
            <Button size="small" type="link" danger onClick={() => handleUpdateRescuerStatus(tr.id, 'REJECTED')}>
              拒绝
            </Button>
          );
        }
        if (status === 'RESPONDED') {
          buttons.push(
            <Button size="small" type="link" onClick={() => handleUpdateRescuerStatus(tr.id, 'DEPARTED')}>
              出动
            </Button>
          );
        }
        if (status === 'DEPARTED') {
          buttons.push(
            <Button size="small" type="link" onClick={() => handleUpdateRescuerStatus(tr.id, 'ARRIVED')}>
              到场
            </Button>
          );
        }
        if (status === 'ARRIVED') {
          buttons.push(
            <Button size="small" type="link" onClick={() => handleUpdateRescuerStatus(tr.id, 'WITHDRAWN')}>
              撤离
            </Button>
          );
        }
        return <Space size="small">{buttons}</Space>;
      },
    },
  ];

  const equipColumns = [
    {
      title: '装备名称',
      dataIndex: ['equipment', 'equipName'],
      render: (v, r) => r.equipmentId || v,
    },
    { title: '数量', dataIndex: 'quantity', width: 80 },
    { title: '领用人', dataIndex: 'operatorName', width: 100 },
    { title: '出库时间', dataIndex: 'checkoutTime', width: 160 },
    { title: '归还时间', dataIndex: 'returnTime', width: 160 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (s) => {
        const map = {
          CHECKED_OUT: { text: '已出库', color: 'warning' },
          RETURNED: { text: '已归还', color: 'success' },
          DAMAGED: { text: '损坏', color: 'error' },
          LOST: { text: '丢失', color: 'error' },
        };
        return <Tag color={map[s]?.color}>{map[s]?.text}</Tag>;
      },
    },
    {
      title: '操作',
      width: 80,
      render: (_, r) =>
        r.status === 'CHECKED_OUT' ? (
          <Button size="small" type="link" onClick={() => handleReturnEquip(r.id)}>
            归还
          </Button>
        ) : null,
    },
  ];

  // 补全equipments中的装备名称
  const renderEquipments = equipments.map((e) => ({
    ...e,
    equipment: { equipName: e.equipmentId ? '' : e.equipment?.equipName || '' },
  }));

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)}>
              返回
            </Button>
            <h2 style={{ margin: 0 }}>
              {task?.taskTitle}
              <Tag style={{ marginLeft: 12 }} color={TASK_STATUS_MAP[task?.status]?.color}>
                {TASK_STATUS_MAP[task?.status]?.text}
              </Tag>
              {task?.dangerLevel && (
                <Tag style={{ marginLeft: 8 }} color={DANGER_LEVEL_MAP[task?.dangerLevel]?.color}>
                  {DANGER_LEVEL_MAP[task?.dangerLevel]?.text}
                </Tag>
              )}
            </h2>
          </Space>
        </div>

        <Descriptions bordered size="small" column={3}>
          <Descriptions.Item label="任务编号">{task?.taskNo}</Descriptions.Item>
          <Descriptions.Item label="任务类型">{task?.taskType}</Descriptions.Item>
          <Descriptions.Item label="被困人数">{task?.victimCount}</Descriptions.Item>
          <Descriptions.Item label="求救地点" span={3}>
            {task?.location}
          </Descriptions.Item>
          <Descriptions.Item label="报警人">{task?.reporterName} / {task?.reporterPhone}</Descriptions.Item>
          <Descriptions.Item label="创建时间">{task?.createdTime}</Descriptions.Item>
          <Descriptions.Item label="派发时间">{task?.dispatchTime}</Descriptions.Item>
          <Descriptions.Item label="求救人状况" span={3}>
            {task?.victimInfo || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="险情描述" span={3}>
            {task?.description || '-'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Row gutter={[16, 16]}>
        <Col span={16}>
          <Card
            title={
              <Space>
                <TeamOutlined />
                参与队员 ({rescuers.length})
                <Button size="small" type="primary" onClick={openAssignModal}>
                  分配队员
                </Button>
              </Space>
            }
            style={{ marginBottom: 16 }}
          >
            <Row gutter={[8, 8]} style={{ marginBottom: 12 }}>
              {Object.entries(RESPONSE_STATUS_MAP).map(([k, c]) => (
                <Col span={4} key={k}>
                  <div style={{ padding: 8, background: '#fafafa', borderRadius: 6, textAlign: 'center' }}>
                    <div style={{ fontSize: 20, fontWeight: 'bold', color: c.color === 'error' ? '#ff4d4f' : '#1890ff' }}>
                      {statusCounts[k] || 0}
                    </div>
                    <div style={{ fontSize: 12, color: '#666' }}>{c.text}</div>
                  </div>
                </Col>
              ))}
            </Row>
            <Table
              size="small"
              columns={rescuerColumns}
              dataSource={rescuers}
              rowKey={(r) => r.taskRescuer?.id}
              pagination={false}
              scroll={{ x: 1100 }}
            />
          </Card>

          <Card
            title={
              <Space>
                <ToolOutlined />
                装备清单
                <Button size="small" type="primary" onClick={openEquipModal}>
                  装备出库
                </Button>
              </Space>
            }
          >
            <Table
              size="small"
              columns={equipColumns}
              dataSource={equipments}
              rowKey="id"
              pagination={false}
            />
          </Card>
        </Col>

        <Col span={8}>
          <Card title="复盘信息" style={{ marginBottom: 16 }} extra={<Button size="small" onClick={openReviewModal} icon={<EditOutlined />}>编辑</Button>}>
            {review ? (
              <Descriptions column={1} size="small">
                <Descriptions.Item label="救援效果">
                  <Tag color={review.rescueEffect === 'SUCCESS' ? 'success' : review.rescueEffect === 'PARTIAL' ? 'warning' : 'error'}>
                    {RESCUE_EFFECT_MAP[review.rescueEffect] || '-'}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="任务总结">{review.summary || '-'}</Descriptions.Item>
                <Descriptions.Item label="经验教训">{review.experience || '-'}</Descriptions.Item>
                <Descriptions.Item label="存在问题">{review.problems || '-'}</Descriptions.Item>
                <Descriptions.Item label="改进建议">{review.improvements || '-'}</Descriptions.Item>
                <Descriptions.Item label="伤亡情况">{review.casualtySituation || '-'}</Descriptions.Item>
                <Descriptions.Item label="复盘人 / 时间">
                  {review.reviewerName || '-'} / {review.reviewTime || '-'}
                </Descriptions.Item>
              </Descriptions>
            ) : (
              <div style={{ color: '#999', textAlign: 'center', padding: 30 }}>暂无复盘，点击右上角编辑</div>
            )}
          </Card>

          <Card title="任务时间线">
            <Timeline
              items={timelines
                .slice()
                .sort((a, b) => new Date(b.eventTime) - new Date(a.eventTime))
                .map((t) => ({
                  color: t.eventType?.includes('CANCEL') || t.eventType?.includes('REJECT')
                    ? 'red'
                    : t.eventType?.includes('COMPLETE') || t.eventType?.includes('ARRIVED')
                    ? 'green'
                    : t.eventType?.includes('DISPATCH') || t.eventType?.includes('DEPARTED') || t.eventType?.includes('CHECKOUT')
                    ? 'blue'
                    : 'gray',
                  children: (
                    <div>
                      <div style={{ fontWeight: 'bold' }}>{t.eventTitle}</div>
                      <div style={{ color: '#666', fontSize: 12 }}>{t.eventDetail}</div>
                      <div style={{ color: '#999', fontSize: 12, marginTop: 2 }}>{t.eventTime}</div>
                    </div>
                  ),
                }))}
            />
          </Card>
        </Col>
      </Row>

      <Modal title="装备出库" open={equipModalVisible} onCancel={() => setEquipModalVisible(false)} onOk={handleCheckout}>
        <Form layout="vertical">
          <Form.Item label="选择装备" required>
            <Select
              value={selectedEquip}
              onChange={(v) => setSelectedEquip(v)}
              showSearch
              optionFilterProp="children"
              filterOption={(input, option) => option.children.toLowerCase().includes(input.toLowerCase())}
            >
              {equipList.map((e) => (
                <Select.Option key={e.id} value={e.id}>
                  [{e.category}] {e.equipName} ({e.equipCode}) - 可用 {e.availableQuantity}
                  {e.unit}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item label="数量">
            <InputNumber min={1} value={equipQty} onChange={setEquipQty} style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="任务复盘"
        open={reviewModalVisible}
        onCancel={() => setReviewModalVisible(false)}
        onOk={handleSaveReview}
        width={640}
        destroyOnClose
      >
        <Form form={reviewForm} layout="vertical">
          <Form.Item label="救援效果" name="rescueEffect">
            <Select>
              {Object.entries(RESCUE_EFFECT_MAP).map(([v, l]) => (
                <Select.Option key={v} value={v}>
                  {l}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item label="任务总结" name="summary">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item label="经验教训" name="experience">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item label="存在问题" name="problems">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item label="改进建议" name="improvements">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item label="人员伤亡情况" name="casualtySituation">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="匹配可用队员"
        open={assignModalVisible}
        onCancel={() => setAssignModalVisible(false)}
        footer={null}
        width={640}
      >
        {matchedRescuers.length ? (
          <Table
            size="small"
            rowKey="id"
            pagination={false}
            dataSource={matchedRescuers}
            columns={[
              { title: '姓名', dataIndex: 'name' },
              { title: '手机', dataIndex: 'phone' },
              {
                title: '级别',
                dataIndex: 'level',
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
